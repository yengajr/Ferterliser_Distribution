package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
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

    Dialog dialog;

    private ArrayList<String> orderList;
    private ArrayList<LocationClass> itemsArrayList;
    ArrayAdapter<String> adapter;

    String locationName,locationPrice;

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

        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();
        dialog.show();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        orderList = new ArrayList<>();
        itemsArrayList = new ArrayList<>();

 // Set the adapter for the AutoCompleteTextView
         adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, orderList);
        autoCompleteTextView.setAdapter(adapter);

        // Set the click listener for the AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected item
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Set the selected item to the AutoCompleteTextView
            autoCompleteTextView.setText(selectedItem);
            //store the name of the location
            location = selectedItem;

            for (LocationClass locationClass : itemsArrayList) {
                if (locationClass.getLocation().equals(location)) {
                    deliveryFees.setText(locationClass.getPrice());
                }
            }

        });


        //Proceed button click listener
        proceed.setOnClickListener(v -> {
            emailAddress = customerEmail.getText().toString();
            phoneNumber = Objects.requireNonNull(customerPhone.getText()).toString();
            deliveryCharge = deliveryFees.getText().toString();


            //validate phone number
            if (phoneNumber.length() != 10) {
                customerPhone.setError("Invalid phone number");
                customerPhone.requestFocus();
            } else if (autoCompleteTextView.getText().toString().isEmpty()) {
                autoCompleteTextView.setError("Select a location");
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

        // get name and email of user from the data base
        getUser_Details();

        //get locations from the database
        getLocations();
    }

    private void getUser_Details() {
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
    }

    private void getLocations() {

        db.collection("Destinations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (int i = 0; i < Objects.requireNonNull(task.getResult()).size(); i++) {
                            String location = task.getResult().getDocuments().get(i).getString("location");
                            String price = task.getResult().getDocuments().get(i).getString("price");
                            LocationClass locationClass = new LocationClass(location, price);
                            itemsArrayList.add(locationClass);
                            // Add the location to the list
                            orderList.add(location);

                        }
                        Collections.sort(orderList);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(CustomerDetails.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }
}