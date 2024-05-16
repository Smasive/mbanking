package com.example.mobilebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        // Check if user is signed in
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        progressBar = findViewById(R.id.progressBar);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Clear EditText fields
        etEmail.setText("");
        etPassword.setText("");

        Button loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               authenticateUser();
            }
        });

        TextView txtRegister = findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, Register.class));
            }
        });
        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, PasswordReset.class));
            }
        });
    }
    private void authenticateUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //Focus to ensure no box is left empty or erroneous info is entered
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
            etPassword.setError("Incorrect Password!");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            FirebaseDatabase fDb = FirebaseDatabase.getInstance();
                            fDb.getReference().child("Users").child(uid).child("level")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String level = snapshot.getValue(String.class);
                                            if (level.matches("Client")) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Login.this, "Welcome!!", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(Login.this, Main.class));
                                            } else {
                                                /*
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Login.this, "Welcome!!", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(Login.this, ClientDash.class));

                                                */

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}