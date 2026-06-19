package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private ImageView btnLogout, btnDashboard;
    private TextView tvNamaUser;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        btnLogout    = findViewById(R.id.btnLogout);
        btnDashboard = findViewById(R.id.btnDashboard);
        tvNamaUser   = findViewById(R.id.tvNamaUser);

        // Ambil nama dari Firestore
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nama = document.getString("nama");
                        tvNamaUser.setText(nama);
                    }
                });

        // Tombol Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LogoutActivity.class);
            startActivity(intent);
        });

        // Tombol Dashboard
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        });
    }
}