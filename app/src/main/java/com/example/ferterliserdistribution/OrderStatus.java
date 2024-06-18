package com.example.ferterliserdistribution;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderStatus extends AppCompatActivity {

    CheckBox placed,accepted,processed, shipped, delivered;
    TextView placedText,acceptedText,processedText, shippedText, deliveredText,orderIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the views
        placed = findViewById(R.id.placed);
        accepted = findViewById(R.id.accepted);
        processed = findViewById(R.id.processed);
        shipped = findViewById(R.id.dispatched);
        delivered = findViewById(R.id.delivered);
        placedText = findViewById(R.id.order_placed);
        acceptedText = findViewById(R.id.order_accepted);
        processedText = findViewById(R.id.order_processed);
        shippedText = findViewById(R.id.order_shipped);
        deliveredText = findViewById(R.id.order_delivered);
        orderIdText = findViewById(R.id.order_id);

        //get details from previous activity
        String orderId = getIntent().getStringExtra("orderId");
        String orderDate = getIntent().getStringExtra("orderDate");
        String orderStatus = getIntent().getStringExtra("orderStatus");
        String orderName = getIntent().getStringExtra("orderName");
        String orderAddress = getIntent().getStringExtra("orderAddress");

        //set values
        switch (orderStatus){
            case "0":
                placed.setChecked(true);
                accepted.setChecked(false);
                processed.setChecked(false);
                shipped.setChecked(false);
                delivered.setChecked(false);

                placedText.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "1":
                placed.setChecked(true);
                accepted.setChecked(true);
                processed.setChecked(false);
                shipped.setChecked(false);
                delivered.setChecked(false);

                placedText.setBackgroundColor(getResources().getColor(R.color.green));
                acceptedText.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "2":
                placed.setChecked(true);
                accepted.setChecked(true);
                processed.setChecked(true);
                shipped.setChecked(false);
                delivered.setChecked(false);

                placedText.setBackgroundColor(getResources().getColor(R.color.green));
                acceptedText.setBackgroundColor(getResources().getColor(R.color.green));
                processedText.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "3":
                placed.setChecked(true);
                accepted.setChecked(true);
                processed.setChecked(true);
                shipped.setChecked(true);
                delivered.setChecked(false);

                placedText.setBackgroundColor(getResources().getColor(R.color.green));
                acceptedText.setBackgroundColor(getResources().getColor(R.color.green));
                processedText.setBackgroundColor(getResources().getColor(R.color.green));
                shippedText.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "4":
                placed.setChecked(true);
                accepted.setChecked(true);
                processed.setChecked(true);
                shipped.setChecked(true);
                delivered.setChecked(true);

                //change textView background color
                placedText.setBackgroundColor(getResources().getColor(R.color.green));
                acceptedText.setBackgroundColor(getResources().getColor(R.color.green));
                processedText.setBackgroundColor(getResources().getColor(R.color.green));
                shippedText.setBackgroundColor(getResources().getColor(R.color.green));
                deliveredText.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            default:
                placed.setChecked(false);
                accepted.setChecked(false);
                processed.setChecked(false);
                shipped.setChecked(false);
                delivered.setChecked(false);
                break;
        }

        //set text
        orderIdText.setText(orderId);

    }
}