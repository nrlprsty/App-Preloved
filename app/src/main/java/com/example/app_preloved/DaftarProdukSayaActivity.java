package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DaftarProdukSayaActivity extends AppCompatActivity {

    private RecyclerView rvProdukSaya;
    private TextView tvEmptyState;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private ProdukAdapter adapter;
    private final List<Produk> daftarProduk = new ArrayList<>();

    private static final int REQUEST_EDIT_PRODUK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_produk_saya);

        db = FirebaseFirestore.getInstance();

        rvProdukSaya = findViewById(R.id.rvProdukSaya);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack      = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rvProdukSaya.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukAdapter(daftarProduk, produk -> {
            // Tap kartu -> buka Edit Produk (Story 7), yang juga punya tombol Hapus (Story 8)
            Intent intent = new Intent(DaftarProdukSayaActivity.this, EditProdukActivity.class);
            intent.putExtra("productId", produk.getId());
            startActivityForResult(intent, REQUEST_EDIT_PRODUK);
        });
        rvProdukSaya.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProdukSaya();
    }

    // Story 9: Sebagai pengguna, saya ingin melihat daftar produk yang saya jual
    private void loadProdukSaya() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("products")
                .whereEqualTo("sellerId", uid)
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
                    Toast.makeText(this, "Gagal memuat produk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEmptyState() {
        if (daftarProduk.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvProdukSaya.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvProdukSaya.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PRODUK && resultCode == RESULT_OK) {
            loadProdukSaya(); // refresh list setelah edit atau hapus
        }
    }
}