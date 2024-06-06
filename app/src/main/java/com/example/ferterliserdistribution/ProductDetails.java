package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ProductDetails extends AppCompatActivity {

    TextView productName, productPrice, productQuantity, productId, productCategory, productDescription,ratings;
    ShapeableImageView productImage;
    Button addToCart;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        productName = findViewById(R.id.name);
        productPrice = findViewById(R.id.price);
        productQuantity = findViewById(R.id.quantity);
        productId = findViewById(R.id.id);
        productCategory = findViewById(R.id.category);
        productDescription = findViewById(R.id.description);
        productImage = findViewById(R.id.user_card_image);
        addToCart = findViewById(R.id.add_to_cart);
        ratings = findViewById(R.id.ratings);


        // Get the product details from the intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("productName");
        String price = intent.getStringExtra("productPrice");
        String quantity = intent.getStringExtra("productQuantity");
        String id = intent.getStringExtra("productId");
        String category = intent.getStringExtra("productCategory");
        String description = intent.getStringExtra("productDescription");
        String image = intent.getStringExtra("productImage");
        String rating = intent.getStringExtra("productRating");

        //create a new product object
        // Create a product object with the details
        product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setItemId(id);
        product.setCategory(category);
        product.setDescription(description);
        product.setImage(image);
        product.setSoldQuantity(rating);


        // Set the product details to the views
        productName.setText(name);
        productPrice.setText(price);
        productQuantity.setText(quantity);
        productId.setText(id);
        productCategory.setText(category);
        productDescription.setText(description);
        ratings.setText(rating);
        Glide.with(this).load(image).placeholder(R.drawable.blue_circle).into(productImage);


        // Set the click listener for the add to cart button
                addToCart.setOnClickListener(v -> {
                    int quantity1 = Integer.parseInt(productQuantity.getText().toString());
                    if (quantity1 == 0) {
                        Toast.makeText(v.getContext(), "Out of stock", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int count = 1;

                    // Check if the quantity is not exceeded
                    if (count > quantity1) {
                        Toast.makeText(v.getContext(), "No more " + productName.getText().toString()+" available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Check if the product already exists in the cart
                    if (Product_Cart.getInstance().checkExistingItem(productId.getText().toString())) {
                        Toast.makeText(v.getContext(), "Item already in cart", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        // Add the selected product to the cart
                        product.setItemTotal(String.valueOf(Double.parseDouble(product.getPrice()) * count));
                        product.setItemCount(String.valueOf(count));
                        Product_Cart.getInstance().addToCart(product, count);
                        Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();

                        //move back userDashboard
                        Intent intent1 = new Intent(ProductDetails.this, UserDashboard.class);
                        startActivity(intent1);
                    }
                });

    }

}
