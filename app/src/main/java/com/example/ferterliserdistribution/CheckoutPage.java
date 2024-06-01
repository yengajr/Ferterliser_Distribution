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
    TextView totalTextView,customerDetails,balance;
    TextInputEditText email;
    TextInputLayout emailHolder;
    ReceiptAdapter receiptAdapter;
    ArrayList<Product> cartItems = new ArrayList<>();
    RadioGroup radioGroup;
    LinearLayout linearLayout2, linearLayout3;
    Button checkoutButton;
    EditText amountPaid;

    //firebase initialisations
    private FirebaseFirestore db ;
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    String paymentMethod, emailAdress;
    Dialog dialog;
    String currentDate;
    ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_page);

        recyclerView = findViewById(R.id.recyclerView);
        totalTextView = findViewById(R.id.totalPrice);
        customerDetails = findViewById(R.id.customerDetails);
        email = findViewById(R.id.phone_edit_text);
        emailHolder = findViewById(R.id.textField_phone);
        radioGroup = findViewById(R.id.paymentMethod);
        checkoutButton = findViewById(R.id.checkoutButton);
        linearLayout2 = findViewById(R.id.linear_Layout2);
        linearLayout3 = findViewById(R.id.linear_Layout3);
        amountPaid = findViewById(R.id.cashAmount);
        balance = findViewById(R.id.balance);
        productArrayList = new ArrayList<>();


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        //get phone number from user details in the database
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String phone = documentSnapshot.getString("email");
                        emailHolder.getEditText().setText(phone);
                    }
                });


        //progress dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Processing Payment")
                .setMessage("Please wait")
                .create();
        //getting current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = dateFormat.format(new Date());

        //getting the payment method selected by the user
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cash) {
                Toast.makeText(this, "Cash", Toast.LENGTH_SHORT).show();

                checkoutButton.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);

                //automatically calculate the balance when the user types the amount paid
                amountPaid.setOnKeyListener((v, keyCode, event) -> {
                    //if its empty set do not do the calculation
                    if (amountPaid.getText().toString().isEmpty()) {
                        balance.setText("");
                    }
                    else {
                        double total = Double.parseDouble(totalTextView.getText().toString());
                        double paid = Double.parseDouble(amountPaid.getText().toString());
                        double balance1 = paid - total;
                        balance.setText(String.valueOf(balance1));

                        if (balance1 < 0) {
                            balance.setTextColor(getResources().getColor(R.color.red));
                            checkoutButton.setVisibility(View.GONE);
                        } else {
                            balance.setTextColor(getResources().getColor(R.color.green));
                            checkoutButton.setVisibility(View.VISIBLE);

                        }
                    }
                    return false;
                });

                //checkout button click to call the checkout method
                checkoutButton.setOnClickListener(v -> {
                    CashCheckout();

                });

            } else if (checkedId == R.id.mpesa) {
                Toast.makeText(this, "Mpesa", Toast.LENGTH_SHORT).show();

                //make linear layout 2 and 3 invisible
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                checkoutButton.setVisibility(View.VISIBLE);


                //clear the balance text view and amount paid edit text
                balance.setText("");
                amountPaid.setText("");

                //checkout button click to call the checkout method
                checkoutButton.setOnClickListener(v -> {
                    MpesaCheckout();

                });

            }
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
        //convert the total price to string
        String totalPrice1 = String.valueOf(totalPrice);

        totalTextView.setText(totalPrice1);


    }

    private void MpesaCheckout() {
        //updateQuantities();
        //get phone number and name and payment method selected
        emailAdress = Objects.requireNonNull(email.getText()).toString();
        paymentMethod = "";
        if (radioGroup.getCheckedRadioButtonId() == R.id.cash) {
            paymentMethod = "Cash";
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.mpesa) {
            paymentMethod = "Mpesa";
        }

        //validate the phone number and name and call the checkout method
        if (emailAdress.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            //make the customer details visible
            emailHolder.setVisibility(View.VISIBLE);
        } else {
            customerOrders();
            updateQuantities();
            soldProducts();

            //toast message for payment method
            Toast.makeText(this, "Payment method selected is: " + paymentMethod, Toast.LENGTH_SHORT).show();
        }
    }

    private void CashCheckout() {
        //updateQuantities();
        //get phone number and name and payment method selected
        emailAdress = Objects.requireNonNull(email.getText()).toString();
        paymentMethod = "";
        if (radioGroup.getCheckedRadioButtonId() == R.id.cash) {
            paymentMethod = "Cash";
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.mpesa) {
            paymentMethod = "Mpesa";
        }


        //validate the phone number and name and call the checkout method
        if (emailAdress.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            //make the customer details visible
            emailHolder.setVisibility(View.VISIBLE);
        } else {
            customerOrders();
            updateQuantities();
            soldProducts();

            //toast message for payment method
            Toast.makeText(this, "Payment method selected is: " + paymentMethod, Toast.LENGTH_SHORT).show();
        }
    }

    private void soldProducts() {
        for (final Product product : cartItems) {
            Map<String, Object> soldData = new HashMap<>();
            soldData.put("productName", product.getName());
            soldData.put("itemCount", product.getItemCount());
            soldData.put("paymentMethod", paymentMethod);
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
            orderData.put("paymentMethod", paymentMethod);
            orderData.put("emailAddress", emailAdress);
            orderData.put("orderDate", currentDate);
            orderData.put("productCategory", product.getCategory());
            orderData.put("served", 0);

            db.collection("Orders")
                    .document("Customer")
                    .collection(emailAdress)
                    .add(orderData)
                    .addOnSuccessListener(documentReference -> {

                        String itemId = documentReference.getId(); // Get the generated unique ID
                    orderData.put("itemId", itemId);

                    db.collection("Orders")
                            .document("Customer")
                            .collection(emailAdress)
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