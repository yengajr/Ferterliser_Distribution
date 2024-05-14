package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    TextInputEditText email, password;
    Button login;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        TextView btn=findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(v -> {

            // move to register activity
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);

        });


        db = FirebaseFirestore.getInstance();

        //dialog to show progress
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Signing in")
                .setMessage("Please wait")
                .create();


        login.setOnClickListener(v -> {
            //get the values from the TextInputEditText
            String emailValue = Objects.requireNonNull(email.getText()).toString().trim();
            String passwordValue = Objects.requireNonNull(password.getText()).toString().trim();

            //validate the values
            if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                //show a toast message
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                dialog.show();
                mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    //get current user id
                                    String uid = user.getUid();

                                    //get user details from firestore
                                    db.collection("users").document(uid).get().addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    //get user role
                                                    String role = task1.getResult().getString("roleValue");
                                                    if (role != null) {
                                                        switch (role) {
                                                            case "Admin":
                                                                //redirect to admin dashboard
                                                                startActivity(new Intent(Login.this, AdminDashboard.class));
                                                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                                break;
                                                            case "Distributor":
                                                                //redirect to distributor dashboard
                                                                startActivity(new Intent(Login.this, DistributorDashboard.class));
                                                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                                break;
                                                            case "User":
                                                                //redirect to user dashboard
                                                                startActivity(new Intent(Login.this, UserDashboard.class));
                                                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                                break;

                                                            case "Delivery":
                                                                //redirect to delivery dashboard
                                                                startActivity(new Intent(Login.this, DeliveryDashboard.class));
                                                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                                break;

                                                            default:
                                                                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                        }
                                                }
                                            }
                                            });
                                }
                            } else {
                                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

            }

            });
    }
}