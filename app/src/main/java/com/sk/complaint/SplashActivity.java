package com.sk.complaint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the splash layout
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // Already logged in → check role
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(auth.getUid()).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                Boolean approved = doc.getBoolean("approved");
                                String role = doc.getString("role");

                                if (approved == null || !approved) {
                                    Toast.makeText(this, "Wait for admin approval!", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(this, LoginActivity.class));
                                } else {
                                    if ("admin".equals(role)) {
                                        startActivity(new Intent(this, AdminDashboardActivity.class));
                                    } else if ("user".equals(role)) {
                                        startActivity(new Intent(this, UserDashboardActivity.class));
                                    } else if ("authority".equals(role)) {
                                        startActivity(new Intent(this, AuthorityDashboardActivity.class));
                                    } else if ("department".equals(role)) {
                                        startActivity(new Intent(this, DepartmentDashboardActivity.class));
                                    }
                                }
                                finish();
                            }
                        });
            } else {
                // Not logged in
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000); // 3 seconds
    }
}
