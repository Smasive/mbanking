package com.example.mobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity {

    // Timer and TimerTask for tracking user activity
    private Timer inactivityTimer;
    private TimerTask inactivityTimerTask;
    private final long INACTIVITY_DELAY = 120000; // 2 Minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startInactivityTimer();

        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();


        // Check if user is signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){

            // Redirects to Login
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        TextView txtFirstName = findViewById(R.id.txtFirstName);
        TextView txtAccNo = findViewById(R.id.txtAccNo);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null){
                    txtFirstName.setText(""+user.firstName);
                    txtAccNo.setText(""+user.accNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetInactivityTimer();
    }

    private void startInactivityTimer() {
        inactivityTimer = new Timer();
        inactivityTimerTask = new TimerTask() {
            @Override
            public void run() {
                signOutUser();
            }
        };
        inactivityTimer.schedule(inactivityTimerTask, INACTIVITY_DELAY);
    }

    private void resetInactivityTimer() {
        inactivityTimerTask.cancel();
        inactivityTimerTask = new TimerTask() {
            @Override
            public void run() {
                signOutUser();
            }
        };
        inactivityTimer.schedule(inactivityTimerTask, INACTIVITY_DELAY);
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        showToast("You have been signed out due to inactivity.");
        startActivity(new Intent(Main.this, Login.class));
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Main.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
