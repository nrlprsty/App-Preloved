package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Icon ☰ kanan atas → tutup Dashboard, kembali ke Home
        findViewById(R.id.btn_close).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.stay);
        });

        // Menu Home → kembali ke Home
        findViewById(R.id.menu_home).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.stay);
        });

        // Menu Profil → buka ProfilActivity
        findViewById(R.id.menu_profil).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfilActivity.class));
        });

        // Menu Pengaturan
        findViewById(R.id.menu_pengaturan).setOnClickListener(v -> {
            // TODO: startActivity ke PengaturanActivity
        });

        // Menu Logout
        findViewById(R.id.menu_logout).setOnClickListener(v -> {
            startActivity(new Intent(this, LogoutActivity.class));
        });


    }
}