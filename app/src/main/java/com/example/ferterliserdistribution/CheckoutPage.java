package com.example.ferterliserdistribution;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckoutPage extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView totalTextView;
    ReceiptAdapter receiptAdapter;
    ArrayList<Product> cartItems = new ArrayList<>();
    Button checkoutButton;

    //firebase initialisations
    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    String  emailAddress,phoneNumber,orderAddress;
    Dialog dialog;
    String currentDate, currentTime;
    ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_page);

        recyclerView = findViewById(R.id.recyclerView);
        totalTextView = findViewById(R.id.totalPrice);
        checkoutButton = findViewById(R.id.checkoutButton);
        productArrayList = new ArrayList<>();


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        //get values from previous activity
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String location = intent.getStringExtra("location");
        String deliveryFees = intent.getStringExtra("deliveryFees");

        //toast delivery charge
        Toast.makeText(this, deliveryFees, Toast.LENGTH_SHORT).show();

        //progress dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Processing Payment")
                .setMessage("Please wait")
                .create();
        //getting current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = dateFormat.format(new Date());

        //get current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        currentTime = timeFormat.format(new Date());



        //checkout button click to call the checkout method
        checkoutButton.setOnClickListener(v -> {

            emailAddress = email;
            phoneNumber = phone;
            orderAddress = location;

                customerOrders();
                updateQuantities();
                soldProducts();
        });


        // Fetch item details from Product_Cart class
        Map<Product, Integer> selectedProductsMap = Product_Cart.getInstance().getSelectedProducts();
        for (Map.Entry<Product, Integer> entry : selectedProductsMap.entrySet()) {
            Product product = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                cartItems.add(product);
            }
        }

        // Initialize CartAdapter with fetched item details
        receiptAdapter = new ReceiptAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(receiptAdapter);

        // Calculate total price of all items in the cart
        double totalPrice = Product_Cart.getInstance().calculateTotalPrice();
        //add the delivery charge to the total price
        double total = totalPrice + Double.parseDouble(deliveryFees);
        //convert the total price to string
        String totalPrice1 = String.valueOf(total);

        totalTextView.setText(totalPrice1);

    }

    private void soldProducts() {
        for (final Product product : cartItems) {
            Map<String, Object> soldData = new HashMap<>();
            soldData.put("productName", product.getName());
            soldData.put("itemCount", product.getItemCount());
            soldData.put("paymentNumber", phoneNumber);
            soldData.put("totalPrice", product.getItemTotal());

            db.collection("SalesReport")
                    .document(currentDate)
                    .collection("Items")
                    .add(soldData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Order saved successfully
                            Log.d(TAG, "Order saved successfully with ID: " + documentReference.getId());
                            //updateQuantities();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save order
                            Log.w(TAG, "Error saving order", e);
                            //dismiss the progress dialog
                            dialog.dismiss();
                        }
                    });
        }
    }


    //method to save the order to the database
    private void customerOrders() {

        for (final Product product : cartItems) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("productName", product.getName());
            orderData.put("itemCount", product.getItemCount());
            orderData.put("paymentNumber", phoneNumber);
            orderData.put("emailAddress", emailAddress);
            orderData.put("orderDate", currentDate);
            orderData.put("productCategory", product.getCategory());
            orderData.put("served", "0");
            orderData.put("orderTime", currentTime);
            orderData.put("orderAddress", orderAddress);
            orderData.put("distributor", "none");

            db.collection("Orders")
                    .document("Customer")
                    .collection("order")
                    .add(orderData)
                    .addOnSuccessListener(documentReference -> {

                        String itemId = documentReference.getId(); // Get the generated unique ID
                    orderData.put("itemId", itemId);

                    db.collection("Orders")
                            .document("Customer")
                            .collection("order")
                            .document(itemId)
                            .set(orderData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Order saved successfully
                                    Log.d(TAG, "Order saved successfully with ID: " + itemId);
                                    //updateQuantities();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to save order
                                    Log.w(TAG, "Error saving order", e);
                                    //dismiss the progress dialog
                                    dialog.dismiss();
                                }
                            });
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save order
                            Log.w(TAG, "Error saving order", e);
                            //dismiss the progress dialog
                            dialog.dismiss();
                        }
                    });
        }
    }

    //checkout method to save the order to the database
    private void updateQuantities() {

        // Save order to the database
        for (final Product product : cartItems) {
            final String remainingQuantity = product.getRemainingQuantity();
            final String soldQuantity = product.getSoldQuantity();

            // Update quantity in Firestore
            db.collection("Products")
                    .document("Fertilizer")
                    .collection("Available")
                    .document(product.getItemId())
                    .update("quantity", remainingQuantity, "soldQuantity", soldQuantity)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Quantity updated successfully
                            Log.d(TAG, "Quantity updated for product: " + product.getItemId());
                            //toast message
                            // Toast.makeText(CheckoutPage.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                            //dismiss the progress dialog
                            dialog.dismiss();
                            // Move to the home page
                            startActivity(new Intent(CheckoutPage.this, PurchaseCompletion.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update quantity
                            Log.w(TAG, "Error updating quantity for product: " + product.getItemId(), e);

                            //dismiss the progress dialog
                            dialog.dismiss();
                        }
                    });
        }

        Product_Cart.getInstance().getSelectedProducts().clear();
        receiptAdapter.notifyDataSetChanged();

    }
}