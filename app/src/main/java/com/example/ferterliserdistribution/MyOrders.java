package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MyOrders extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noOrders;
    FloatingActionButton fab;
    OrdersAdapter ordersAdapter1;
    ArrayList<Order> orderArrayList;
    FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    Dialog dialog;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.orders_recycler);
        noOrders = findViewById(R.id.my_orders_title);
        fab = findViewById(R.id.refresh_orders);

        //initialize the database
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        orderArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersAdapter1 = new OrdersAdapter(this, orderArrayList);
        recyclerView.setAdapter(ordersAdapter1);


        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();

        dialog.show();

        //textview click listener
        fab.setOnClickListener(v -> {
            dialog.show();
            orderArrayList.clear();
            //get orders
            getOrders();
        });

        get_UserDetails();

    }
    private void get_UserDetails() {
        //get phone number from user details in the database
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        email = documentSnapshot.getString("email");
                        getOrders();
                    }
                });
    }

    private void getOrders() {
        db.collection("Orders").document("Customer").collection("order").addSnapshotListener((new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }
                assert value != null;
                for (Order order : value.toObjects(Order.class)) {
                    //check if emailAddress is equal to email
                    if (order.getEmailAddress().equals(email)) {
                        orderArrayList.add(order);
                    }
                }
               // Collections.reverse(orderArrayList);
                orderArrayList.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                ordersAdapter1.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));
    }
}