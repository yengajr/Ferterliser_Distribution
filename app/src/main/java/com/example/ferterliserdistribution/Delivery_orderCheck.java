package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Delivery_orderCheck extends AppCompatActivity {

    RecyclerView recyclerView;

    Order_ProcessingAdapter order_processingAdapter;
    ArrayList<Order> orderArrayList;
    FloatingActionButton fab;

    FirebaseFirestore db;
    Dialog dialog;
    String location, userId;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_order_check);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();

        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        fab.setOnClickListener(v -> {
            //clear the arraylist
            orderArrayList.clear();
            dialog.show();
            GetOrders();
        });

        //initialize the database
        db = FirebaseFirestore.getInstance();

        orderArrayList = new ArrayList<>();
        order_processingAdapter = new Order_ProcessingAdapter(this, orderArrayList);
        recyclerView.setAdapter(order_processingAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetOrders();
    }

    private void CurrentUserDetails() {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                location = task.getResult().getString("name");
            }
        });
    }

    private void GetOrders() {
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
                    //add orders whose served value is 1
                    if (Objects.equals(order.getServed(), "3") && Objects.equals(order.getOrderAddress(), location)){
                        orderArrayList.add(order);
                    }
                }
                order_processingAdapter.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));
    }
}