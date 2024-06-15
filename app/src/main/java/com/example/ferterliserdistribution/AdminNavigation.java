package com.example.ferterliserdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class AdminNavigation extends AppCompatActivity {

    LinearLayout home, orders, sales,logout,announcement,inventory;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation);

        home = findViewById(R.id.linear_Layout_home);
        orders = findViewById(R.id.linear_Layout_order);
        sales = findViewById(R.id.linear_Layout_sales);
        logout = findViewById(R.id.linear_Layout_logout);
        announcement = findViewById(R.id.linear_Layout_announcement);
        inventory = findViewById(R.id.linear_Layout_inventory);

        mAuth = FirebaseAuth.getInstance();

        home.setOnClickListener(v -> {
            startActivity(new Intent(AdminNavigation.this, AdminDashboard.class));
        });

        orders.setOnClickListener(v -> {
            startActivity(new Intent(AdminNavigation.this, Admin_OrderCheck.class));
        });

        sales.setOnClickListener(v -> {
           // startActivity(new Intent(AdminNavigation.this, AdminSales.class));
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(AdminNavigation.this, Login.class));
            finish();
        });

        announcement.setOnClickListener(v -> {
            startActivity(new Intent(AdminNavigation.this, AnnouncementPage.class));
        });

        inventory.setOnClickListener(v -> {
            startActivity(new Intent(AdminNavigation.this, InventoryManagement.class));
        });

    }
}