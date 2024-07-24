package com.example.ferterliserdistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class Register extends AppCompatActivity {

    TextInputEditText name, email, password, confirm_password;
    Button register;
    TextView login;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String user2;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.role_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //initialize the TextInputEditText
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        //dialog to show progress
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Signing up")
                .setMessage("Please wait")
                .create();

//set on click listener to the register button
        register.setOnClickListener(v -> {
            //get the values from the TextInputEditText
            String nameValue = Objects.requireNonNull(name.getText()).toString().trim();
            String emailValue = Objects.requireNonNull(email.getText()).toString().trim();
            String passwordValue = Objects.requireNonNull(password.getText()).toString().trim();
            String confirmPasswordValue = Objects.requireNonNull(confirm_password.getText()).toString().trim();
            String roleValue = spinner.getSelectedItem().toString().trim();
            location = "Null";
            String truckNoValue = "Null";

            //validate the values
            if (nameValue.isEmpty() || emailValue.isEmpty() || passwordValue.isEmpty() || confirmPasswordValue.isEmpty()) {
                //show a toast message
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            //check if the password and confirm password are the same
            else if (!passwordValue.equals(confirmPasswordValue)) {
                //show a toast message
                Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                dialog.show();
                //create with email and password
                mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user1 = mAuth.getCurrentUser();
                                assert user1 != null;
                                 user2 = user1.getUid();

                                // Add user details to firestore
                                db.collection("users").document(user2) // Using "users" collection
                                        .set(new User(nameValue, emailValue, passwordValue, roleValue,user2,location,truckNoValue)) // Include roleValue
                                        .addOnSuccessListener(aVoid -> {
                                            dialog.dismiss();
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(Register.this, "Sign up successful.",
                                                    Toast.LENGTH_SHORT).show();

                                            // Redirect to login page
                                            startActivity(new Intent(Register.this, Login.class));
                                        })
                                        .addOnFailureListener(e -> {
                                            dialog.dismiss();
                                            Toast.makeText(Register.this, "Sign up failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                dialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Register.this, "Sign up failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        //set on click listener to the login textview
        login.setOnClickListener(v -> {
            //redirect to login page
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });
    }
}