package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryAddition extends AppCompatActivity {

    ShapeableImageView shapeableImageView;
    TextInputEditText itemName, itemPrice, itemQuantity;
    TextView addCategory;
    Button save, discard ;
    Spinner category;

    Uri imageUri;
    String imageUrl;

    private FirebaseAuth mAuth;

    Dialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        imageUri = data.getData();
        shapeableImageView.setImageURI(imageUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory_addition);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shapeableImageView = findViewById(R.id.ImageView);
        itemName = findViewById(R.id.item_name);
        itemPrice = findViewById(R.id.price);
        itemQuantity = findViewById(R.id.quantity);
        save = findViewById(R.id.add);
        discard = findViewById(R.id.discard);
        addCategory = findViewById(R.id.add_category);
        category = findViewById(R.id.category);


        //dialog to show progress
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Saving item")
                .setMessage("Please wait")
                .create();

        shapeableImageView.setOnClickListener(
                v -> ImagePicker.with(this)
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        // .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
        );

        mAuth = FirebaseAuth.getInstance();
        Categories();


        save.setOnClickListener(view -> {
            //get user input
            String name = itemName.getText().toString().trim();
            String price = itemPrice.getText().toString().trim();
            String quantity = itemQuantity.getText().toString().trim();


            //save details to firestore
            if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || imageUri == null) {
                Toast.makeText(InventoryAddition.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }  else
            {
                uploadProfilePicture(imageUri);
            }

        });

        discard.setOnClickListener(v -> {
            //move to item add activity
            startActivity(new Intent(InventoryAddition.this, InventoryAddition.class));

        });

        addCategory.setOnClickListener(v -> {
            //add category
            showAddCategoryDialog();
        });
    }

    private void Categories() {
        FirebaseUser user1 = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Products").document("Fertilizer").collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String categoryName = document.getString("name");
                        categories.add(categoryName);
                    }

                    // For example, if you have a spinner:
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(InventoryAddition.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    category.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Log.e("Error", e.getMessage());
                });
    }

    // Add a new category to Firestore
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InventoryAddition.this);
        builder.setTitle("Add Category");
        final EditText input = new EditText(InventoryAddition.this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString();
            if (!categoryName.isEmpty()) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user1 = mAuth.getCurrentUser();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Add the new category to Firestore
                Map<String, Object> category = new HashMap<>();
                category.put("name", categoryName);
                db.collection("Products").document("Fertilizer").collection("Categories")
                        .add(category)
                        .addOnSuccessListener(aVoid -> {
                            // Category added successfully
                            Log.d("TAG", "Category added: " + categoryName);
                            // Refresh the categories list
                            Categories();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("Error", "Error adding category", e);
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Upload the profile picture to Firebase Storage
    private void uploadProfilePicture(Uri imageUri) {

        dialog.show();
        // Initialize Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("shop/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update imageUrl variable with the download URL
                        imageUrl = uri.toString();
                        // After getting the download URL, proceed to save user details in Firestore
                        saveProduct_Details();
                    }).addOnFailureListener(e -> {
                        // Handle the failure to get the download URL
                        Toast.makeText(InventoryAddition.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to upload the image
                    Toast.makeText(InventoryAddition.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                });
    }


    // Save item details to Firestore
    public void saveProduct_Details(){
        //get selected category
        String selectedCategory = category.getSelectedItem().toString();

        //save details to firestore
        String name = itemName.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String quantity = itemQuantity.getText().toString().trim();

        FirebaseUser user1 = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", price);
        item.put("quantity", quantity);
        item.put("category", selectedCategory);
        item.put("image", imageUrl);
        db.collection("Products").document("Fertilizer").collection("Available").add(item)
                .addOnSuccessListener(documentReference -> {
                    String itemId = documentReference.getId(); // Get the generated unique ID
                    item.put("itemId", itemId); // Store the ID in the item object
                    db.collection("Products").document("Fertilizer").collection("Available").document(itemId)
                            .set(item) // Update the item with the generated ID
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(InventoryAddition.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                startActivity(new Intent(InventoryAddition.this, AdminDashboard.class));
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(InventoryAddition.this, "Error updating item", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InventoryAddition.this, "Error adding item", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

    }


}