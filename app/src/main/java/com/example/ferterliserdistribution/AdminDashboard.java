package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class AdminDashboard extends AppCompatActivity {

    TextView textView1, username;
    FloatingActionButton floatingActionButton;
    LinearLayout linearLayout;
    ImageButton imageButton;
    RecyclerView recyclerView;
    Admin_ItemsAdapter admin_itemsAdapter;

    ArrayList<Product> productArrayList;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Dialog dialog;
    String userId;

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
        setContentView(R.layout.activity_admin_dashboard);

        //views binding
        textView1 = findViewById(R.id.itemManagement);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.fab);
        linearLayout = findViewById(R.id.linear_Layout);
        username = findViewById(R.id.user_name);
        imageButton = findViewById(R.id.menu_button);

        //firebase initialisation
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

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

        imageButton.setOnClickListener(v -> {
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
        CurrentUserDetails();
    }

    //method to get user details
    private void CurrentUserDetails() {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                username.setText(task.getResult().getString("name"));
            }
        });
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