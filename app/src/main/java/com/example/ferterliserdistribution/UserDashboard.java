package com.example.ferterliserdistribution;

import static java.util.Locale.filter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class UserDashboard extends AppCompatActivity {

    TextView username, announcementText;
    ImageButton btn_navigation, btn_cart, btn_logout;
    TextView cart_count,current_total1;
    RecyclerView recyclerView;
    ShapeableImageView announcementImage;
    SearchView searchView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    ItemsAdapter itemsAdapter;
    ArrayList<Product> productArrayList;
    ArrayList<AnnouncementClass> announcementArrayList;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Dialog dialog;
    String userId, imageUrl, announcementText1;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 2000; // 2 second

    // Override the onBackPressed method

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.user_name);
        announcementText = findViewById(R.id.announcementText);
        recyclerView = findViewById(R.id.recyclerView);
        announcementImage = findViewById(R.id.user_card_image);
        searchView = findViewById(R.id.searchView);
        mySwipeRefreshLayout = findViewById(R.id.main);
        cart_count = findViewById(R.id.cart_count);
        btn_cart = findViewById(R.id.cart1);
        current_total1 = findViewById(R.id.current_total);

        announcementArrayList = new ArrayList<>();

        // Initialize firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Initialize the dialog
        //dialog
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Fetching Data...")
                .setMessage("Please wait")
                .create();

        dialog.show();

        // Set the texts
        announcementText.setText("announcementText1");


        announcementImage.setOnClickListener(v -> {
            //log out
            mAuth.signOut();
                startActivity(new Intent(UserDashboard.this, Login.class));
                finish();

        });

        // Set the cart button listener
        btn_cart.setOnClickListener(v -> {
            // Move to the cart page
            startActivity(new Intent(UserDashboard.this, Items_Cart.class));
        });

        //click listener for current_total1
        current_total1.setOnClickListener(v -> {
            // Move to the cart page
            startActivity(new Intent(UserDashboard.this, Items_Cart.class));
        });

        // Set the swipe refresh layout listener
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            //set the adapter again
            itemsAdapter = new ItemsAdapter(UserDashboard.this, productArrayList);
            recyclerView.setAdapter(itemsAdapter);
            mySwipeRefreshLayout.setRefreshing(false);



            //check if cart is empty
            if (Product_Cart.getInstance().getSelectedProducts().size() == 0) {
                //make cart count invisible
                cart_count.setVisibility(View.INVISIBLE);

            } else {
                //make cart count visible
                cart_count.setVisibility(View.VISIBLE);
                //set the cart count
                cart_count.setText(String.valueOf(Product_Cart.getInstance().getSelectedProducts().size()));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // Set the recycler view layout manager
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize the product array list
        productArrayList = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(this, productArrayList);
        recyclerView.setAdapter(itemsAdapter);

        // Get the data from firebase
        EventChangeListener();
        CurrentUserDetails();
        AnnouncementData();

        // Set the cart count
        cart_count.setText(String.valueOf(Product_Cart.getInstance().getSelectedProducts().size()));

    }
    //get announcement data and set the image
    private void AnnouncementData() {

        //get Announcements using AnnouncementClass and announcementArrayList
        db.collection("Announcements").addSnapshotListener((value, error) -> {
            if (error != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                return;
            }
            assert value != null;
            announcementArrayList.addAll(value.toObjects(AnnouncementClass.class));

            //compare values in Number and display details of that with largest number
            int max = 0;
            for (int i = 0; i < announcementArrayList.size(); i++) {
                if (announcementArrayList.get(i).getNumber() > max) {
                    max = announcementArrayList.get(i).getNumber();
                    imageUrl = announcementArrayList.get(i).getImageUrl();
                    announcementText1 = announcementArrayList.get(i).getAnnouncement();

                    //set the values
                    announcementText.setText(announcementText1);
                    Glide.with(UserDashboard.this).load(imageUrl).into(announcementImage);

                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    // Filter the list of products
    private void filter(String text) {
        ArrayList<Product> filteredList = new ArrayList<>();
        for (Product item : productArrayList) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        itemsAdapter.filterList(filteredList);
    }

    //method to get user details
    private void CurrentUserDetails() {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                username.setText(task.getResult().getString("name"));
            }
        });
    }

    //method to fetch data
    private void EventChangeListener() {
        db.collection("Products").document("Fertilizer").collection("Available").addSnapshotListener((new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }
                assert value != null;
                productArrayList.addAll(value.toObjects(Product.class));
                itemsAdapter.notifyDataSetChanged();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }));

    }
}