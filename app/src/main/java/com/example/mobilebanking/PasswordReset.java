package com.example.mobilebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.math.BigInteger;

public class PasswordReset extends AppCompatActivity {

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() !=null){
            finish();
            return;
        }


        Button updateBtn=findViewById(R.id.btnUpdatePassword);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void updatePassword(){

        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);


        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if(password.isEmpty()){
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return;
        }
        if(password.length() < 8){
            etPassword.setError("Min password length is 8 characters!");
            etPassword.requestFocus();
            return;
        }
        if(confirmPassword.isEmpty()){
            etConfirmPassword.setError("Password is required!");
            etConfirmPassword.requestFocus();
            return;
        }
        if(!confirmPassword.equals(password)){
            etConfirmPassword.setError("Password does not match!");
            etConfirmPassword.requestFocus();
            return;
        }


         //  MD5 Encryption to hash password provided into a 16bit character

        byte[] md5Input = etConfirmPassword.getText().toString().getBytes();
        BigInteger md5Data = null;

        try {
            md5Data = new BigInteger(1,MD5.encryptMD5(md5Input));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*
        Encrypted password passed to variable md5Str and stored in the Database
         */
        String md5Str = md5Data.toString(16);

        progressBar.setVisibility(View.VISIBLE);


        // Get the current FirebaseUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Change the user's password in Firebase Authentication
            user.updatePassword(md5Str)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update the new password in Firebase Realtime Database
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference usersDB = db.getReference("Users");
                                DatabaseReference userDB = usersDB.child(user.getUid());

                                userDB.child("password").setValue(md5Str);

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordReset.this, Login.class));

                                // Now, the new password is stored in Firebase Realtime Database under the user's node
                            } else {
                                // Password update failed in Firebase Authentication
                                // You can show an error message to the user
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // The user is not authenticated, handle this scenario as needed
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

    }


}