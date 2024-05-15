package com.example.ferterliserdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboard extends AppCompatActivity {

    TextView textView,textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.appTitle);
        textView1 = findViewById(R.id.itemManagement);

        textView.setOnClickListener(v -> {
            // move to inventory_addition activity
            startActivity(new Intent(AdminDashboard.this, InventoryAddition.class));

        });

        textView1.setOnClickListener(v -> {
            // move to item_management activity
            startActivity(new Intent(AdminDashboard.this, InventoryManagement.class));
        });
    }
}