package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private ImageView btnLogout;
    private TextView tvNamaUser;
    private ImageView btnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.btnLogout);
        tvNamaUser = findViewById(R.id.tvNamaUser); // pastikan ada id ini di XML

        // Ambil email dari Intent (dikirim saat login)
        String email = getIntent().getStringExtra("email");
        if (email != null && tvNamaUser != null) {
            tvNamaUser.setText(email);
        }

        // Tombol Logout → tampilkan dialog konfirmasi
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LogoutActivity.class);
            startActivity(intent);
        });

        btnDashboard = findViewById(R.id.btnDashboard);
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        });
    }
}