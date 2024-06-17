package com.example.ferterliserdistribution;

import android.app.Dialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Location_Update extends AppCompatActivity {

    TextView userId, userName, userEmail, userRole, userLocation;
    String userIdValue, userNameValue, userEmailValue, userRoleValue, userLocationValue,userPasswordValue;
    AutoCompleteTextView autoCompleteTextView;
    Button updateLocationButton;

    private ArrayList<String> orderList;
    private ArrayList<LocationClass> itemsArrayList;
    ArrayAdapter<String> adapter;

    private FirebaseFirestore db ;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_update);


        //initialize the views
        userId = findViewById(R.id.identifier_field);
        userName = findViewById(R.id.name_field);
        userEmail = findViewById(R.id.email_field);
        userRole = findViewById(R.id.role_field);
        userLocation = findViewById(R.id.location_field);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        updateLocationButton = findViewById(R.id.update);

        //get the data from the intent
        userIdValue = getIntent().getStringExtra("userId");
        userNameValue = getIntent().getStringExtra("userName");
        userEmailValue = getIntent().getStringExtra("userEmail");
        userRoleValue = getIntent().getStringExtra("userRole");
        userLocationValue = getIntent().getStringExtra("userLocation");
        userPasswordValue = getIntent().getStringExtra("userPassword");

        //set the data to the views
        userId.setText(userIdValue);
        userName.setText(userNameValue);
        userEmail.setText(userEmailValue);
        userRole.setText(userRoleValue);
        userLocation.setText(userLocationValue);

        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();
        dialog.show();

        //initialize the firebase
        db = FirebaseFirestore.getInstance();

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
            userLocationValue = selectedItem;
            userLocation.setText(userLocationValue);

        });

        //update click listener
        updateLocationButton.setOnClickListener(v -> {

            //check if autocomplete textview is empty
            if (autoCompleteTextView.getText().toString().isEmpty()) {
                autoCompleteTextView.setError("Select a location");
                autoCompleteTextView.requestFocus();

            }
            else {
                //update the location
                updateLocation();
            }
        });


        //get the locations
        getLocations();
    }

    private void updateLocation() {
        dialog.show();
        db.collection("users")
                .document(userIdValue)
                .update("location", userLocationValue)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    Toast.makeText(Location_Update.this, "Location updated successfully", Toast.LENGTH_SHORT).show();

                    //move to location assignment
                    finish();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(Location_Update.this, "Error updating location", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Location_Update.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }
}