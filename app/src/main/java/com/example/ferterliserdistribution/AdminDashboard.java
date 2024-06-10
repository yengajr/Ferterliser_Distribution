package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class AdminDashboard extends AppCompatActivity {

    TextView textView1;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    Admin_ItemsAdapter admin_itemsAdapter;

    ArrayList<Product> productArrayList;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        //views binding
        textView1 = findViewById(R.id.itemManagement);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.fab);

        //firebase initialisation
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //dialog initialisation
        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();

        dialog.show();

        //adding new item
        floatingActionButton.setOnClickListener(v -> {
            // move to inventory_addition activity
            startActivity(new Intent(AdminDashboard.this, InventoryAddition.class));
        });

        textView1.setOnClickListener(v -> {
            // move to item_management activity
            startActivity(new Intent(AdminDashboard.this, AdminNavigation.class));
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //arraylist initialisation
        productArrayList = new ArrayList<>();

        //adapter initialisation
        admin_itemsAdapter = new Admin_ItemsAdapter(this, productArrayList);
        recyclerView.setAdapter(admin_itemsAdapter);

        //method to get items from the database
        getItems();
    }

    private void getItems() {
            db.collection("Products").document("Fertilizer").collection("Available").addSnapshotListener((new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        return;
                    }
                    assert value != null;
                    productArrayList.addAll(value.toObjects(Product.class));
                    admin_itemsAdapter.notifyDataSetChanged();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }));
    }
}