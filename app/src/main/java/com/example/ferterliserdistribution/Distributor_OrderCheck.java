package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Distributor_OrderCheck extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView nameSpace,logOut;
    Order_ProcessingAdapter1 order_processingAdapter;
    ArrayList<Order> orderArrayList;
    FloatingActionButton fab;
    Button All, Pending, Completed, Cancelled;

    FirebaseFirestore db;
    Dialog dialog;
    String  userId;

    private FirebaseAuth mAuth;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 2000; // 2 second

    // Override the onBackPressed method
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_distributor_order_check);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        nameSpace = findViewById(R.id.distributor_name);
        logOut = findViewById(R.id.logout);

        All = findViewById(R.id.button_all);
        Pending = findViewById(R.id.button_pending);
        Completed = findViewById(R.id.button_completed);
        Cancelled = findViewById(R.id.button_cancel);

        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();

        dialog.show();

        fab.setOnClickListener(v -> {
            //clear the arraylist
            orderArrayList.clear();
            dialog.show();
            GetOrders();
        });

        logOut.setOnClickListener(v -> {
            //logout
            mAuth.signOut();
            startActivity(new Intent(Distributor_OrderCheck.this, Login.class));
        });

        All.setOnClickListener(v -> {
            //clear the arraylist
            orderArrayList.clear();
            dialog.show();
            GetOrders();
        });

        Pending.setOnClickListener(v -> {
            //clear the arraylist
            orderArrayList.clear();
            dialog.show();
            GetpendingOrders();
        });

        Completed.setOnClickListener(v -> {
            //clear the arraylist
            orderArrayList.clear();
            dialog.show();
            GetcompletedOrders();
        });

        Cancelled.setOnClickListener(v -> {
            //clear the arraylist
            orderArrayList.clear();
            dialog.show();
            GetcancelledOrders();
        });

        //initialize the database
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                nameSpace.setText("Welcome " + task.getResult().getString("name"));
            }

        });

        orderArrayList = new ArrayList<>();
        order_processingAdapter = new Order_ProcessingAdapter1(this, orderArrayList);
        recyclerView.setAdapter(order_processingAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetOrders();
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
                orderArrayList.clear();

                for (Order order : value.toObjects(Order.class)) {
                    //add orders whose served value is 2, 3 or 4
                    if (order.served.equals("3") || order.served.equals("4") || order.served.equals("5")) {
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

    private void GetpendingOrders() {
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
                orderArrayList.clear();

                for (Order order : value.toObjects(Order.class)) {
                    //add orders whose served value is 2, 3 or 4
                    if (order.served.equals("3")) {
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

    private void GetcompletedOrders() {
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
                orderArrayList.clear();

                for (Order order : value.toObjects(Order.class)) {
                    //add orders whose served value is 2, 3 or 4
                    if (order.served.equals("4")) {
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

    private void GetcancelledOrders() {
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
                orderArrayList.clear();

                for (Order order : value.toObjects(Order.class)) {
                    //add orders whose served value is 2, 3 or 4
                    if (order.served.equals("5")) {
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