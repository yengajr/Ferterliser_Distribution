package com.example.ferterliserdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class CustomerDetails extends AppCompatActivity {

    TextView customerEmail, customerName, deliveryFees;
    AutoCompleteTextView autoCompleteTextView;
    Button proceed;
    TextInputEditText customerPhone;

    //firebase initialisations
    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    String  emailAddress,phoneNumber,location,deliveryCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        // view binding
        customerEmail = findViewById(R.id.customer_email);
        customerName = findViewById(R.id.customer_name);
        customerPhone = findViewById(R.id.customer_phone);
        proceed = findViewById(R.id.proceed);
         autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        deliveryFees = findViewById(R.id.delivery_cost);


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        // Get the array of locations from strings.xml
        String[] locations = getResources().getStringArray(R.array.locations_array);

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, locations);
        autoCompleteTextView.setAdapter(adapter);

        // Set up the item click listener
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLocation = (String) parent.getItemAtPosition(position);
            String deliveryFee;
            switch (selectedLocation) {
                case "Kisumu":
                    deliveryFee = "200.0";
                    break;
                case "Nairobi":
                    deliveryFee = "300.0";
                    break;
                case "Mombasa":
                    deliveryFee = "400.0";
                    break;
                default:
                    deliveryFee = "0.0";
                    break;
            }
            //set text to the delivery fee
            deliveryFees.setText(deliveryFee);
        });

        // get name and email of user from the data base
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String name = documentSnapshot.getString("name");
                        customerEmail.setText(email);
                        customerName.setText(name);
                    }
                });

        //Proceed button click listener
        proceed.setOnClickListener(v -> {
            emailAddress = customerEmail.getText().toString();
            phoneNumber = Objects.requireNonNull(customerPhone.getText()).toString();
            location = autoCompleteTextView.getText().toString();
            deliveryCharge = deliveryFees.getText().toString();
            // Do something with the data

            //validate phone number
            if (phoneNumber.length() != 10) {
                customerPhone.setError("Invalid phone number");
                customerPhone.requestFocus();
            } else if (location.isEmpty()) {
                autoCompleteTextView.setError("Location is required");
                autoCompleteTextView.requestFocus();
            }
            else {
                //pass the data to the next activity
                Intent intent = new Intent(CustomerDetails.this, CheckoutPage.class);
                intent.putExtra("email", emailAddress);
                intent.putExtra("phone", phoneNumber);
                intent.putExtra("location", location);
                intent.putExtra("deliveryFees", deliveryCharge);
                startActivity(intent);
            }
        });
    }
}