package com.example.ferterliserdistribution;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.ferterliserdistribution.Model.AccessToken;
import com.example.ferterliserdistribution.Model.MpesaRequest;
import com.example.ferterliserdistribution.Model.STKCallbackResponse;
import com.example.ferterliserdistribution.Model.STKPush;
import com.example.ferterliserdistribution.Services.DarajaApiClient;
import com.example.ferterliserdistribution.databinding.ActivityCheckoutPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    String  emailAddress,phoneNumber,orderAddress,totalPrice1;
    Dialog dialog;
    String currentDate, currentTime;
    ArrayList<Product> productArrayList;

    private String token,encodedPassword,timestamp;
    private DarajaApiClient mApiClient;
    private ActivityCheckoutPageBinding binding;

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

        // Initialize Daraja API client
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true);

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

            // Perform STK push
           performSTKPush();


                dialog.show();
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

        //convert to integer
        int total1 = (int) total;

        //convert to string
        totalPrice1 = String.valueOf(total1);

        totalTextView.setText(totalPrice1);
        getAccessToken();

    }

    // method for getting access token
    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Set auth token if request is successful
                    mApiClient.setAuthToken(response.body().accessToken);

                    // Store token for later use
                    token = response.body().accessToken;
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Timber.e(t, "Failed to get access token");
            }
        });
    }

    public void performSTKPush() {

        // Generate encoded password for authentication
        timestamp = Utils.getTimestamp();
        String toEncode = "174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + timestamp;

        // Encode password using Base64
        byte[] byteArray = toEncode.getBytes(StandardCharsets.UTF_8);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedPassword = Base64.getEncoder().encodeToString(byteArray);
        } else {
            encodedPassword = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
        }

        // Create STKPush object with required parameters
        STKPush stkPush = new STKPush(
                "174379",  // BusinessShortCode
                encodedPassword,  // Password
                timestamp,  // Timestamp
                "CustomerPayBillOnline",  // TransactionType
                Integer.parseInt(totalPrice1),  // Amount
                "254715798225",  // PartyA
                "174379",  // PartyB
                Utils.sanitizePhoneNumber(phoneNumber),  // PhoneNumber


                "https://mydomain.com/path",  // CallBackURL
                "Fertilizer Distribution",  // AccountReference
                "Payment of X"  // TransactionDesc
        );

        // Disable access token retrieval after first call
        mApiClient.setGetAccessToken(false);

        // Make API call to perform STK push
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {

                // Dismiss progress dialog after API call completes

                try {
                    if (response.isSuccessful()) {

                        // Log success message if request is successful
                        Timber.d("post submitted to API. %s", response.body());

                        // Start checking transaction status
                        assert response.body() != null;
                        checkTransactionStatus(response.body().getCheckoutRequestID());

                        // Display success message
                        Toast.makeText(CheckoutPage.this, "Request sent. Please complete payment on your phone.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.errorBody() != null) {

                            // Log error message if request fails
                            Timber.e("Response %s", response.errorBody().string());

                            // Display error message
                            Toast.makeText(CheckoutPage.this, "Error", Toast.LENGTH_SHORT).show();

                            //dismiss the progress dialog
                            dialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    Timber.e(e, "Error processing response");

                    //dismiss the progress dialog
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {

                // Dismiss progress dialog if request fails
                dialog.dismiss();
                Timber.e(t, "Request failed");

            }
        });
    }

    public void checkTransactionStatus(String checkoutRequestID) {
        // Simulate checking transaction status with a delayed task
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call MpesaRequest to get transaction status
                MpesaRequest mpesaRequest = new MpesaRequest();

                //get access token from responce body

                mpesaRequest.sendRequest("174379", encodedPassword, timestamp, checkoutRequestID, token, new MpesaRequest.MpesaRequestCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Handle the response
                        Log.d("Transaction status response: %s", response);
                        // Parse the response and handle transaction status
                        //convert response data to dictionary
                        Gson gson = new Gson();
                        STKCallbackResponse stkCallbackResponse = gson.fromJson(response, STKCallbackResponse.class);

                        //if result code is 0, transaction is successful
                        if (stkCallbackResponse.getResultCode().equals("0")) {
                            // Handle successful transaction
                            System.out.println("Transaction successful");


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    customerOrders();
                                    updateQuantities();
                                    soldProducts();
                                }
                            });


                        } else {
                            // Handle failed transaction
                            System.out.println("Transaction failed");

                            //dismiss the progress dialog
                            dialog.dismiss();

                            //call looper prepare and toast message
                            Looper.prepare();
                            Toast.makeText(CheckoutPage.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                            //display the error
                            Toast.makeText(CheckoutPage.this, stkCallbackResponse.getResultDesc(), Toast.LENGTH_SHORT).show();
                            Looper.loop();

                        }

                    }

                    @Override
                    public void onFailure(String error) {
                        Timber.e("Failed to get transaction status: %s", error);

                        //dismiss the progress dialog
                        dialog.dismiss();

                    }
                });
            }
        }, 30000); // Check after 30 seconds
    }

    private void soldProducts() {
        for (final Product product : cartItems) {
            Map<String, Object> soldData = new HashMap<>();
            soldData.put("productName", product.getName());
            soldData.put("itemCount", product.getItemCount());
            soldData.put("paymentNumber", phoneNumber);
            soldData.put("totalPrice", product.getItemTotal());
            soldData.put("orderDate", currentDate);

            db.collection("SalesReport")
                    .document("currentDate")
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
        Map<String, Object> orderData = new HashMap<>();
        List<Map<String, Object>> productsList = new ArrayList<>();

        for (final Product product : cartItems) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("productName", product.getName());
            productData.put("itemCount", product.getItemCount());
            productData.put("productCategory", product.getCategory());
            productData.put("cumulativePrice", product.getItemTotal());
            productsList.add(productData);
        }

        orderData.put("products", productsList);
        orderData.put("paymentNumber", phoneNumber);
        orderData.put("emailAddress", emailAddress);
        orderData.put("orderDate", currentDate);
        orderData.put("orderTime", currentTime);
        orderData.put("orderAddress", orderAddress);
        orderData.put("served", "0");
        orderData.put("distributor", "none");

        db.collection("Orders")
                .document("Customer")
                .collection("order")
                .add(orderData)
                .addOnSuccessListener(documentReference -> {
                    String orderId = documentReference.getId(); // Get the generated unique ID
                    orderData.put("orderId", orderId);

                    db.collection("Orders")
                            .document("Customer")
                            .collection("order")
                            .document(orderId)
                            .set(orderData)
                            .addOnSuccessListener(aVoid -> {
                                // Order saved successfully
                                Log.d(TAG, "Order saved successfully with ID: " + orderId);
                                // You can update quantities or perform other actions here
                            })
                            .addOnFailureListener(e -> {
                                // Failed to save order
                                Log.w(TAG, "Error saving order", e);
                                // Dismiss the progress dialog if you have one
                                dialog.dismiss();
                            });
                })
                .addOnFailureListener(e -> {
                    // Failed to save order
                    Log.w(TAG, "Error saving order", e);
                    // Dismiss the progress dialog if you have one
                    dialog.dismiss();
                });
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