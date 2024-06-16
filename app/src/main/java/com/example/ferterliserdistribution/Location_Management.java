package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Location_Management extends AppCompatActivity {

    ImageButton back;
    ListView listView;
    LinearLayout linearLayout;
    TextInputEditText location,price;
    Button add;
    TextView addLocation,no_of_destinations;

    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    Dialog dialog;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> orderList;
    private ArrayList<LocationClass> itemsArrayList;

    String locationName,locationPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_management);

        back = findViewById(R.id.back);
        listView = findViewById(R.id.location_list);
        linearLayout = findViewById(R.id.linear_Layout);
        location = findViewById(R.id.location);
        price = findViewById(R.id.price);
        add = findViewById(R.id.add);
        addLocation = findViewById(R.id.add_txt);
        no_of_destinations = findViewById(R.id.no_of_destination);

        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();


        orderList = new ArrayList<>();
        itemsArrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderList);
        listView.setAdapter(adapter);


        back.setOnClickListener(v -> {
            finish();
        });

        //add location
        addLocation.setOnClickListener(v -> {
            //male linear layout visible
            linearLayout.setVisibility(LinearLayout.VISIBLE);
        });

        add.setOnClickListener(v -> {

            dialog.show();

            // Add location
            locationName = Objects.requireNonNull(location.getText()).toString().toUpperCase().trim();
            locationPrice = Objects.requireNonNull(price.getText()).toString().trim();

            //validation
            if (locationName.isEmpty()){
                location.setError("Location is required");
                dialog.dismiss();
                return;
            } else if (locationPrice.isEmpty()){
                price.setError("Price is required");
                dialog.dismiss();
                return;
            } else {
                addLocation();
            }

        });
        //method data from database using the date
        getData();
    }

    private void addLocation() {

        Map<String,Object> locationMap = new HashMap<>();
        locationMap.put("location",locationName);
        locationMap.put("price",locationPrice);

        db.collection("Destinations")
                .add(locationMap)
                .addOnSuccessListener(documentReference -> {
                    // Announcement posted successfully
                    Toast.makeText(Location_Management.this, "Destination added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    //finish();
                    startActivity(new Intent(Location_Management.this, AdminDashboard.class));
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to post the announcement
                    Toast.makeText(Location_Management.this, "Failed to post announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
    }

    private void getData() {

        db.collection("Destinations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                            String location = queryDocumentSnapshots.getDocuments().get(i).getString("location");
                            String price = queryDocumentSnapshots.getDocuments().get(i).getString("price");

                            LocationClass locationClass = new LocationClass(location,price);
                            itemsArrayList.add(locationClass);
                            orderList.add(location + "\nKsh:"+price);
                        }

                        Collections.sort(orderList);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();

                        no_of_destinations.setText("We are now available in " + orderList.size()+ " Locations");
                    } else {
                        dialog.dismiss();
                        Toast.makeText(Location_Management.this, "No data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to get the size of the array
                    Toast.makeText(Location_Management.this, "Failed to get announcement size: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}