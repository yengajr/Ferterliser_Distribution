package com.example.ferterliserdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class DistributorDashboard extends AppCompatActivity {

    TextView welcomeText,tittleText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_distributor_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeText = findViewById(R.id.welcomeMessage);
        tittleText = findViewById(R.id.appTitle);

        mAuth = FirebaseAuth.getInstance();

        //welome click
        welcomeText.setOnClickListener(v -> {
            //logout
            mAuth.signOut();
            startActivity(new Intent(DistributorDashboard.this, Login.class));
        });

        //title click
        tittleText.setOnClickListener(v -> {
            //open profile
            startActivity(new Intent(DistributorDashboard.this, Distributor_OrderCheck.class));
        });
    }
}