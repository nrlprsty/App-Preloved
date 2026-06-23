package com.example.app_preloved;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProdukActivity extends AppCompatActivity {


    private EditText etNamaProduk, etHarga, etDeskripsi;
    private Spinner spinnerKategori;
    private Button btnSimpan;
    private ImageView btnBack, ivPreviewFoto;
    private LinearLayout layoutFoto;
    private ImageView icTambahFoto;
    private TextView tvTambahFoto;

    private FirebaseFirestore db;
    private Uri fotoUriBaru = null;
    private String fotoBase64Lama = null;
    private String productId;

    // AP-14: launcher untuk ganti foto
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    fotoUriBaru = result.getData().getData();
                    ivPreviewFoto.setImageURI(fotoUriBaru);
                    ivPreviewFoto.setVisibility(View.VISIBLE);
                    icTambahFoto.setVisibility(View.GONE);
                    tvTambahFoto.setVisibility(View.GONE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_produk);

        db = FirebaseFirestore.getInstance();

        // AP-14: inisialisasi view
        etNamaProduk    = findViewById(R.id.etNamaProduk);
        etHarga         = findViewById(R.id.etHarga);
        etDeskripsi     = findViewById(R.id.etDeskripsi);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        btnSimpan       = findViewById(R.id.btnSimpan);
        btnBack         = findViewById(R.id.btnBack);
        layoutFoto      = findViewById(R.id.layoutFoto);
        ivPreviewFoto   = findViewById(R.id.ivPreviewFoto);
        icTambahFoto    = findViewById(R.id.icTambahFoto);
        tvTambahFoto    = findViewById(R.id.tvTambahFoto);

        // AP-14: setup spinner kategori
        setupSpinnerKategori();

        // AP-14: ambil productId dari Intent
        productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        // AP-14: listener ganti foto
        layoutFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        // AP-14: tombol simpan
        btnSimpan.setOnClickListener(v -> updateProduk());



        // AP-14: load data produk dari Firestore
        loadDataProduk();
    }

    // AP-14: setup pilihan kategori
    private void setupSpinnerKategori() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.kategori_produk,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapter);
    }

    // AP-14: ambil data produk dari Firestore dan isi ke form
    private void loadDataProduk() {
        db.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etNamaProduk.setText(doc.getString("nama"));
                        Long harga = doc.getLong("harga");
                        etHarga.setText(harga != null ? String.valueOf(harga) : "");
                        etDeskripsi.setText(doc.getString("deskripsi"));
                        String kategori = doc.getString("kategori");
                        setSpinnerToValue(kategori);
                        fotoBase64Lama = doc.getString("foto");
                        tampilkanPreviewFotoLama();
                    } else {
                        Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data produk", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // AP-14: set spinner ke nilai kategori produk
    private void setSpinnerToValue(String kategori) {
        if (kategori == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinnerKategori.getAdapter();
        int position = adapter.getPosition(kategori);
        if (position >= 0) spinnerKategori.setSelection(position);
    }

    // AP-14: tampilkan preview foto lama dari Base64
    private void tampilkanPreviewFotoLama() {
        if (fotoBase64Lama != null && !fotoBase64Lama.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(fotoBase64Lama, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                ivPreviewFoto.setImageBitmap(bitmap);
                ivPreviewFoto.setVisibility(View.VISIBLE);
                icTambahFoto.setVisibility(View.GONE);
                tvTambahFoto.setVisibility(View.GONE);
            } catch (Exception e) {
                // biarkan placeholder default
            }
        }
    }

    // AP-14: konversi foto baru ke Base64
    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) return null;
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    // AP-14: validasi dan update data produk ke Firestore
    private void updateProduk() {
        String nama      = etNamaProduk.getText().toString().trim();
        String hargaStr  = etHarga.getText().toString().trim();
        String kategori  = spinnerKategori.getSelectedItem().toString();
        String deskripsi = etDeskripsi.getText().toString().trim();

        // AP-14: validasi nama
        if (TextUtils.isEmpty(nama)) {
            etNamaProduk.setError("Nama produk wajib diisi");
            etNamaProduk.requestFocus();
            return;
        }

        // AP-14: validasi harga
        if (TextUtils.isEmpty(hargaStr)) {
            etHarga.setError("Harga wajib diisi");
            etHarga.requestFocus();
            return;
        }

        // AP-14: validasi kategori
        if (kategori.equals("Pilih Kategori")) {
            Toast.makeText(this, "Pilih kategori produk dulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // AP-14: validasi format harga
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

        // AP-14: tentukan foto yang dipakai (baru atau lama)
        String fotoBase64Final;
        if (fotoUriBaru != null) {
            fotoBase64Final = uriToBase64(fotoUriBaru);
            if (fotoBase64Final == null) {
                Toast.makeText(this,
                        "Format foto tidak sesuai. Pilih foto lain.",
                        Toast.LENGTH_LONG).show();
                btnSimpan.setEnabled(true);
                btnSimpan.setText("Simpan Perubahan");
                return;
            }
        } else {
            fotoBase64Final = fotoBase64Lama;
        }

        // AP-14: data yang diupdate ke Firestore
        Map<String, Object> produk = new HashMap<>();
        produk.put("nama",      nama);
        produk.put("harga",     harga);
        produk.put("kategori",  kategori);
        produk.put("deskripsi", deskripsi);
        produk.put("foto",      fotoBase64Final);

        // AP-14: update ke Firestore
        db.collection("products")
                .document(productId)
                .update(produk)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Produk berhasil diperbarui!",
                            Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Perubahan");
                    new AlertDialog.Builder(this)
                            .setTitle("Gagal Memperbarui")
                            .setMessage("Terjadi kesalahan sistem. Coba lagi.")
                            .setPositiveButton("Coba Lagi", (d, w) -> d.dismiss())
                            .setCancelable(false)
                            .show();
                });
    }

}