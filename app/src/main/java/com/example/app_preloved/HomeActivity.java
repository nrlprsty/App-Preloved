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

    private ImageView btnDashboard, btnChat, btnTambah, btnNotif, btnProfil;
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

        // Inisialisasi view
        btnDashboard = findViewById(R.id.btnDashboard);
        btnChat      = findViewById(R.id.btnChat);
        btnTambah    = findViewById(R.id.btnTambah);
        btnNotif     = findViewById(R.id.btnNotif);
        btnProfil    = findViewById(R.id.btnProfil);
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

        // Tombol Dashboard (hamburger menu)
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        });

        // Tombol Chat
        btnChat.setOnClickListener(v -> {
            // Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            // startActivity(intent);
        });

        // Tombol Tambah Produk
        btnTambah.setOnClickListener(v -> {
            // Intent intent = new Intent(HomeActivity.this, TambahProdukActivity.class);
            // startActivity(intent);
        });

        // Tombol Notifikasi
        btnNotif.setOnClickListener(v -> {
            // Intent intent = new Intent(HomeActivity.this, NotifikasiActivity.class);
            // startActivity(intent);
        });

        // Tombol Profil
        btnProfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfilActivity.class);
            startActivity(intent);
        });
    }
}