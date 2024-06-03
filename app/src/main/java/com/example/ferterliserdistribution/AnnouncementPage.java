package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AnnouncementPage extends AppCompatActivity {

    TextInputEditText announcement;
    Button post;
    ShapeableImageView imageView;
    String announcementText, imageUrl;
    Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    Dialog dialog;
    int size;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        imageUri = data.getData();
        imageView.setImageURI(imageUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_page);

        announcement = findViewById(R.id.announcement);
        post = findViewById(R.id.submit);
        imageView = findViewById(R.id.user_card_image);

        //firebase initialisation
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //dialog to show progress
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Saving item")
                .setMessage("Please wait")
                .create();

        imageView.setOnClickListener(
                v -> ImagePicker.with(this)
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        // .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
        );

        post.setOnClickListener(v -> {
            // Get the announcement text
            announcementText = Objects.requireNonNull(announcement.getText()).toString();

            //increament size by one
            size = size + 1;

            // Check if the announcement text is empty
            if (announcementText.isEmpty()) {
                announcement.setError("Please enter an announcement");
                return;
            }
            else if (imageUri == null) {
                Toast.makeText(AnnouncementPage.this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                dialog.show();
                // Upload the profile picture
                uploadProfilePicture(imageUri);
            }
        });

        //get size of announcement array
        db.collection("Announcements")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Get the size of the array
                     size = queryDocumentSnapshots.size();

                })
                .addOnFailureListener(e -> {
                    // Handle the failure to get the size of the array
                    Toast.makeText(AnnouncementPage.this, "Failed to get announcement size: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
    private void uploadProfilePicture(Uri imageUri) {

        // Initialize Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("Announcement/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update imageUrl variable with the download URL
                        imageUrl = uri.toString();
                        // After getting the download URL, proceed to save user details in Firestore
                        postAnnouncement();
                    }).addOnFailureListener(e -> {
                        // Handle the failure to get the download URL
                        Toast.makeText(AnnouncementPage.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to upload the image
                    Toast.makeText(AnnouncementPage.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                });
    }

    private void postAnnouncement() {

        Map<String, Object> announcement = new HashMap<>();
        announcement.put("announcement", announcementText);
        announcement.put("imageUrl", imageUrl);
        announcement.put("Number",size);

        db.collection("Announcements")
                .add(announcement)
                .addOnSuccessListener(documentReference -> {
                    // Announcement posted successfully
                    Toast.makeText(AnnouncementPage.this, "Announcement posted successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    //finish();
                    startActivity(new Intent(AnnouncementPage.this, AdminDashboard.class));
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to post the announcement
                    Toast.makeText(AnnouncementPage.this, "Failed to post announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
    }
}