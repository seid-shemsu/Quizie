package com.seid.quizie.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seid.quizie.Constants;
import com.seid.quizie.MainActivity;
import com.seid.quizie.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    private CardView layout_login, layout_verify;
    private EditText phone, code;
    private Button fab_login, fab_verify;
    private ProgressBar progress_login, progress_verify;
    private String verificationId;
    private FirebaseAuth mAuth;
    private String phone_number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        layout_login = findViewById(R.id.login_card);
        layout_verify = findViewById(R.id.code_card);
        fab_login = findViewById(R.id.login);
        fab_verify = findViewById(R.id.verify);
        phone = findViewById(R.id.phone);
        phone.requestFocus();
        code = findViewById(R.id.code);
        progress_login = findViewById(R.id.login_progress);
        progress_verify = findViewById(R.id.verify_progress);

        fab_login.setOnClickListener(v -> {
            String num = phone.getText().toString();
            if (checkPhone(num)) {
                if (num.startsWith("0"))
                    num = "+251" + num.substring(1);
                else if (num.startsWith("251"))
                    num = "+" + num;
                phone_number = num.substring(1);
                sendVerificationCode(num);
                fab_login.setVisibility(View.GONE);
                progress_login.setVisibility(View.VISIBLE);
            } else
                phone.setError("check your input");
        });
        fab_verify.setOnClickListener(v -> {
            String code = this.code.getText().toString();
            if (code.isEmpty() || code.length() < 6) {
                this.code.setError("Enter code...");
                this.code.requestFocus();
                return;
            }
            verifyCode(code);
        });
    }

    private Boolean checkPhone(String number) {
        if (!number.startsWith("0") && !number.startsWith("+251") && !number.startsWith("251"))
            return false;
        if (number.startsWith("0")) {
            if (number.length() != 10)
                return false;
        }
        if (number.startsWith("+251")) {
            if (number.length() != 13)
                return false;
        }
        if (number.startsWith("251"))
            return number.length() == 12;
        return true;
    }

    private void verifyCode(String code) {
        progress_verify.setVisibility(View.VISIBLE);
        fab_verify.setVisibility(View.GONE);
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            e.printStackTrace();
            this.code.setError("Invalid code...");
            progress_verify.setVisibility(View.GONE);
            fab_verify.setVisibility(View.VISIBLE);
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference(Constants.USERS).child(phone_number)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                                        if (!snapshot.hasChild("phone")) {
                                            startActivity(new Intent(Login.this, Register.class).putExtra("phone", phone_number));
                                            finish();
                                        } else {
                                            sp.edit().putBoolean("auth", true)
                                                    .putString("phone", phone_number)
                                                    .apply();
                                            if (snapshot.hasChild("email"))
                                                sp.edit().putString("email", snapshot.child("email").getValue().toString()).apply();
                                            if (snapshot.hasChild("name"))
                                                sp.edit().putString("name", snapshot.child("name").getValue().toString()).apply();

                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            finish();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    } else {
                        if (Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).contains("invalid")) {
                            code.setError("Invalid code...");
                            progress_verify.setVisibility(View.GONE);
                            fab_verify.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            code.requestFocus();
            verificationId = s;
            layout_login.setVisibility(View.GONE);
            layout_verify.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Login.this.code.setText(code);
                verifyCode(code);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}