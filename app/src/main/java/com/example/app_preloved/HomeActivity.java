package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView btnDashboard, btnChat, btnTambah, btnNotif, btnProduk;
    private TextView tvNamaUser, tvEmptyHome;
    private RecyclerView rvProdukHome;
    private EditText etCariProduk;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private ProdukAdapter adapter;
    private final List<Produk> daftarProduk = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        btnDashboard = findViewById(R.id.btnDashboard);
        btnChat      = findViewById(R.id.btnChat);
        btnTambah    = findViewById(R.id.btnTambah);
        btnNotif     = findViewById(R.id.btnNotif);
        btnProduk    = findViewById(R.id.btnProduk);
        tvNamaUser   = findViewById(R.id.tvNamaUser);
        tvEmptyHome  = findViewById(R.id.tvEmptyHome);
        rvProdukHome = findViewById(R.id.rvProdukHome);
        etCariProduk = findViewById(R.id.etCariProduk);

        // Setup RecyclerView 2 kolom
        rvProdukHome.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProdukAdapter(daftarProduk, produk -> {
            Toast.makeText(this, produk.getNama(), Toast.LENGTH_SHORT).show();
            // nanti bisa ke DetailProdukActivity
        });
        rvProdukHome.setAdapter(adapter);

        // Ambil nama user
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvNamaUser.setText(document.getString("nama"));
                    }
                });

        // Tombol navigasi
        btnDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        });

        btnTambah.setOnClickListener(v -> {
            startActivity(new Intent(this, TambahProdukActivity.class));
        });

        btnProduk.setOnClickListener(v -> {
            startActivity(new Intent(this, DaftarProdukSayaActivity.class));
        });

        btnChat.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur chat belum tersedia", Toast.LENGTH_SHORT).show();
        });

        btnNotif.setOnClickListener(v -> {
            Toast.makeText(this, "Belum ada notifikasi", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSemuaProduk();
    }

    private void loadSemuaProduk() {
        db.collection("products")
                .whereEqualTo("status", "Available")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    daftarProduk.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Produk produk = new Produk();
                        produk.setId(doc.getId());
                        produk.setNama(doc.getString("nama"));
                        Long harga = doc.getLong("harga");
                        produk.setHarga(harga != null ? harga : 0);
                        produk.setKategori(doc.getString("kategori"));
                        produk.setDeskripsi(doc.getString("deskripsi"));
                        produk.setFoto(doc.getString("foto"));
                        produk.setSellerId(doc.getString("sellerId"));
                        produk.setStatus(doc.getString("status"));
                        daftarProduk.add(produk);
                    }
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat produk", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEmptyState() {
        if (daftarProduk.isEmpty()) {
            tvEmptyHome.setVisibility(View.VISIBLE);
            rvProdukHome.setVisibility(View.GONE);
        } else {
            tvEmptyHome.setVisibility(View.GONE);
            rvProdukHome.setVisibility(View.VISIBLE);
        }
    }
}