package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.util.Objects;

public class Order_ProcessingPage extends AppCompatActivity {

    TextView orderId, orderDate, orderStatus, orderQuantity, orderTime, orderName,orderPhone,orderAddress,orderProcess;
    String id, date, status, quantity, time, name, phone, address,deliveryPerson;
    FirebaseFirestore db;
    Dialog dialog;

    private FirebaseAuth mAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_processing_page);

        //initialize the views
        orderId = findViewById(R.id.id);
        orderDate = findViewById(R.id.date);
        orderStatus = findViewById(R.id.status);
        orderQuantity = findViewById(R.id.count);
        orderTime = findViewById(R.id.time);
        orderName = findViewById(R.id.name);
        orderPhone = findViewById(R.id.phone);
        orderAddress = findViewById(R.id.address);
        orderProcess = findViewById(R.id.process);

        //get the data from the intent
        id = getIntent().getStringExtra("orderId");
        date = getIntent().getStringExtra("orderDate");
        status = getIntent().getStringExtra("orderStatus");
        quantity = getIntent().getStringExtra("orderCount");
        time = getIntent().getStringExtra("orderTime");
        name = getIntent().getStringExtra("orderName");
        phone = getIntent().getStringExtra("orderPayment");
        address = getIntent().getStringExtra("orderAddress");

        //firebase initialisation
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //get user details
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
               //store the name in string deliveryPerson
                deliveryPerson = task.getResult().getString("name");
            }

        });

        //dialog initialisation
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Finishing up")
                .setMessage("Please wait")
                .create();

        //set the data to the views
        orderId.setText(MessageFormat.format("Order ID: {0}", id));
        orderDate.setText(date);
        orderQuantity.setText(quantity);
        orderTime.setText(time);
        orderName.setText(name);
        orderPhone.setText(phone);
        orderAddress.setText(address);

        //hide button if status is 4 and 5
        if (status.equals("4") || status.equals("5")){
            orderProcess.setVisibility(View.GONE);
        }
        else{
            orderProcess.setVisibility(View.VISIBLE);
        }

        //Check status value and set the text
        switch (status){
            case "0":
                orderStatus.setText("Placed");
                break;
            case "1":
                orderStatus.setText("Accepted");
                break;
            case "2":
                orderStatus.setText("Processed");
                break;
            case "3":
                orderStatus.setText("Dispatched");
                break;
            case "4":
                orderStatus.setText("Delivered");
                break;
            default:
                orderStatus.setText("Cancelled");
                break;
        }

        //change the color of the status text
        switch (status){
            case "0":
                orderStatus.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "1":
                orderStatus.setTextColor(getResources().getColor(R.color.magenta));
                break;
            case "2":
                orderStatus.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case "3":
                orderStatus.setTextColor(getResources().getColor(R.color.orange));
                break;
            case "4":
                orderStatus.setTextColor(getResources().getColor(R.color.green));
                break;
            default:
                orderStatus.setTextColor(getResources().getColor(R.color.red));
                break;
        }

        switch (status){
            case "0":
                orderProcess.setText("ACCEPT");
                break;
            case "1":
                orderProcess.setText("PROCESS");
                break;
            case "2":
                orderProcess.setText("DISPATCH");
                break;
            case "3":
                orderProcess.setText("Confirm Delivery");
                break;
            default:
                orderProcess.setText("CANCEL");
                break;
        }

        //orderProcess click listener
        orderProcess.setOnClickListener(v -> {
            //call method to update the status
            UpdateStatus();
        });

    }
    private void UpdateStatus() {

        dialog.show();
        //update status
        if (status.equals("3")) {
            db.collection("Orders").document("Customer").collection("order").document(id).update("served", String.valueOf(Integer.parseInt(status) + 1), "distributor", deliveryPerson)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                        } else {
                            //toast message
                            Toast.makeText(Order_ProcessingPage.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }
        else{
            db.collection("Orders").document("Customer").collection("order").document(id).update("served", String.valueOf(Integer.parseInt(status) + 1))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                        }
                        else {
                            //toast message
                            Toast.makeText(Order_ProcessingPage.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }
    }
}