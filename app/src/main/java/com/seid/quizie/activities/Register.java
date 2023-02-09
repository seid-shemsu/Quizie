package com.seid.quizie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.seid.quizie.Constants;
import com.seid.quizie.MainActivity;
import com.seid.quizie.R;
import com.seid.quizie.models.User;

public class Register extends AppCompatActivity {
    private EditText first_name, last_name, email;
    private Button register;
    private String phone = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            phone = getIntent().getExtras().getString("phone");
        } catch (Exception e) {
            e.printStackTrace();
        }

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);

        register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            if (isValid()) {
                User user = new User();
                user.setName(first_name.getText().toString() + " " + last_name.getText().toString());
                user.setEmail(email.getText().toString());
                user.setPhone(phone);
                user.setPoints(0);
                FirebaseDatabase.getInstance().getReference(Constants.USERS)
                        .child(phone)
                        .setValue(user)
                        .addOnSuccessListener(unused -> {
                            getSharedPreferences("user", MODE_PRIVATE).edit()
                                    .putString("phone", phone)
                                    .putString("name", first_name.getText().toString() + " " + last_name.getText().toString())
                                    .putString("email", email.getText().toString())
                                    .apply();
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Something is not right. Please try again later", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        FirebaseDatabase.getInstance().getReference("user_games")
                .child(phone)
                .child("id1").setValue(false);

    }

    private boolean isValid() {
        if (first_name.getText().toString().isEmpty()) {
            first_name.setError("First name is required");
            return false;
        }
        if (first_name.getText().toString().isEmpty()) {
            first_name.setError("Last name is required");
            return false;
        }
        if (first_name.getText().toString().length() < 2) {
            first_name.setError("Too short first name");
            return false;
        }
        if (first_name.getText().toString().length() < 2) {
            first_name.setError("Too short last name");
            return false;
        }
        if (phone.isEmpty()) {
            return false;
        }
        return true;
    }
}