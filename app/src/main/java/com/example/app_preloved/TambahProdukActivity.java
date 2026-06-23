package com.example.app_preloved;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TambahProdukActivity extends AppCompatActivity {

    private EditText etNamaProduk, etHarga, etDeskripsi;
    private Spinner spinnerKategori;
    private Button btnSimpan;
    private ImageView btnBack;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        db = FirebaseFirestore.getInstance();

        etNamaProduk    = findViewById(R.id.etNamaProduk);
        etHarga         = findViewById(R.id.etHarga);
        etDeskripsi     = findViewById(R.id.etDeskripsi);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        btnSimpan       = findViewById(R.id.btnSimpan);
        btnBack         = findViewById(R.id.btnBack);

        setupSpinnerKategori();

        btnBack.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> simpanProduk());
    }

    private void setupSpinnerKategori() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.kategori_produk,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapter);
    }

    private void simpanProduk() {
        String nama      = etNamaProduk.getText().toString().trim();
        String hargaStr  = etHarga.getText().toString().trim();
        String kategori  = spinnerKategori.getSelectedItem().toString();
        String deskripsi = etDeskripsi.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            etNamaProduk.setError("Nama produk wajib diisi");
            etNamaProduk.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(hargaStr)) {
            etHarga.setError("Harga wajib diisi");
            etHarga.requestFocus();
            return;
        }

        if (kategori.equals("Pilih Kategori")) {
            Toast.makeText(this, "Pilih kategori produk dulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        long harga;
        try {
            harga = Long.parseLong(hargaStr);
        } catch (NumberFormatException e) {
            etHarga.setError("Harga tidak valid");
            etHarga.requestFocus();
            return;
        }

        btnSimpan.setEnabled(false);
        btnSimpan.setText("Menyimpan...");

        String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> produk = new HashMap<>();
        produk.put("nama",      nama);
        produk.put("harga",     harga);
        produk.put("kategori",  kategori);
        produk.put("deskripsi", deskripsi);
        produk.put("foto",      "");
        produk.put("sellerId",  sellerId);
        produk.put("status",    "Available");

        db.collection("products")
                .add(produk)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this,
                            "Produk berhasil ditambahkan!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Produk");
                    new AlertDialog.Builder(this)
                            .setTitle("Gagal Menyimpan")
                            .setMessage("Terjadi kesalahan sistem. Coba lagi.")
                            .setPositiveButton("Coba Lagi", (d, w) -> d.dismiss())
                            .setCancelable(false)
                            .show();
                });
    }
}