package com.example.ferterliserdistribution;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class ProductDetails extends AppCompatActivity {

    TextView productName, productPrice, productQuantity, productId, productCategory, productDescription;
    ShapeableImageView productImage;
    Button addToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productName = findViewById(R.id.name);
        productPrice = findViewById(R.id.price);
        productQuantity = findViewById(R.id.quantity);
        productId = findViewById(R.id.id);
        productCategory = findViewById(R.id.category);
        productDescription = findViewById(R.id.description);
        productImage = findViewById(R.id.user_card_image);
        addToCart = findViewById(R.id.add_to_cart);

        // Get product info from intent
        String name = getIntent().getStringExtra("productName");
        String price = getIntent().getStringExtra("productPrice");
        String quantity = getIntent().getStringExtra("productQuantity");
        String id = getIntent().getStringExtra("productId");
        String category = getIntent().getStringExtra("productCategory");
        String description = getIntent().getStringExtra("productDescription");

        // Set product info

        productName.setText(name);
        productPrice.setText(price);
        productQuantity.setText(quantity);
        productId.setText(id);
        productCategory.setText(category);
        productDescription.setText(description);
        Glide.with(this).load(getIntent().getStringExtra("productImage")).placeholder(R.drawable.blue_circle).into(productImage);

        // Add to cart button click listener
        addToCart.setOnClickListener(v -> {
            // Get product info
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setItemId(id);
            product.setCategory(category);
            product.setDescription(description);
            product.setImage(getIntent().getStringExtra("productImage"));

            int count = 3;

            // Add product to cart
            Product_Cart.getInstance().addToCart(product, count);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });


    }
}
