package com.example.mobilebanking;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.util.Random;

public class Register extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        Button registerBtn = findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    private void registerUser() {
        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etIdNo = findViewById(R.id.etIdNo);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etTel = findViewById(R.id.etTel);



        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String idNo = etIdNo.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String tel = etTel.getText().toString();


        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required!");
            etFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Last Name is required!");
            etLastName.requestFocus();
            return;
        }
        if (idNo.isEmpty()) {
            etIdNo.setError("ID Number is Required");
            etIdNo.requestFocus();
            return;
        }
        if (idNo.length() < 8) {
            etIdNo.setError("Incomplete ID!!");
            etIdNo.requestFocus();
            return;
        }
        if (idNo.length() > 8) {
            etIdNo.setError("Incorrect ID!");
            etIdNo.requestFocus();
            return;
        }
        if (tel.isEmpty()) {
            etTel.setError("Phone Number is Required");
            etTel.requestFocus();
            return;
        }
        if (tel.length() < 10) {
            etTel.setError("Incomplete Contact!");
            etTel.requestFocus();
            return;
        }
        if (tel.length() > 10) {
            etTel.setError("Incorrect Contact!");
            etTel.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required!");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Provide valid email!");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 8) {
            etPassword.setError("Min password length is 8 characters!");
            etPassword.requestFocus();
            return;
        }


        /*
           MD5 Encryption to hash password provided into a 16bit character
         */
        byte[] md5Input = etPassword.getText().toString().getBytes();
        BigInteger md5Data = null;

        try {
            md5Data = new BigInteger(1, MD5.encryptMD5(md5Input));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*
        Encrypted password passed to variable md5Str and stored in the Database
         */
        String md5Str = md5Data.toString(16);

        //Pass the level value as a Client by default
        String level = "Client";

        //Generate 8-digit code
        String accNo = generateRandomCode();

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Upload profile picture
                        if (task.isSuccessful()) {

                            //Store user bio data
                            String uid = task.getResult().getUser().getUid();
                            User user = new User(firstName, lastName, idNo, email, tel, md5Str,level, accNo);
                            FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(Register.this, "Registration Success", Toast.LENGTH_LONG).show();
                                            finish();
                                            //startActivity(new Intent(Register.this, Login.class));
                                        }
                                    });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    // Method to generate a random 8-digit code
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

}