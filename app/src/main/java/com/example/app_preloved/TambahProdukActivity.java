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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TambahProdukActivity extends AppCompatActivity {

    private EditText etNamaProduk, etHarga, etDeskripsi;
    private Spinner spinnerKategori;
    private Button btnSimpan;
    private ImageView btnBack, ivPreviewFoto;
    private LinearLayout layoutFoto;
    private ImageView icTambahFoto;
    private TextView tvTambahFoto;

    private FirebaseFirestore db;
    private Uri fotoUri = null;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    fotoUri = result.getData().getData();
                    ivPreviewFoto.setImageURI(fotoUri);
                    ivPreviewFoto.setVisibility(View.VISIBLE);
                    icTambahFoto.setVisibility(View.GONE);
                    tvTambahFoto.setVisibility(View.GONE);
                }
            });

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
        layoutFoto      = findViewById(R.id.layoutFoto);
        ivPreviewFoto   = findViewById(R.id.ivPreviewFoto);
        icTambahFoto    = findViewById(R.id.icTambahFoto);
        tvTambahFoto    = findViewById(R.id.tvTambahFoto);

        setupSpinnerKategori();

        btnBack.setOnClickListener(v -> finish());

        layoutFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

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

    // Story 6: Unggah Foto Produk - validasi format & ukuran, convert ke Base64
    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                // A1 - Format file tidak sesuai
                return null;
            }

            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    // Story 5: Tambah Produk
    private void simpanProduk() {
        String nama      = etNamaProduk.getText().toString().trim();
        String hargaStr   = etHarga.getText().toString().trim();
        String kategori   = spinnerKategori.getSelectedItem().toString();
        String deskripsi  = etDeskripsi.getText().toString().trim();

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
        if (fotoUri == null) {
            Toast.makeText(this, "Pilih foto produk dulu!", Toast.LENGTH_SHORT).show();
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

        String fotoBase64 = uriToBase64(fotoUri);
        if (fotoBase64 == null) {
            // A1 - Format File Tidak Sesuai
            Toast.makeText(this, "Format foto tidak sesuai atau ukuran terlalu besar. Pilih foto lain.", Toast.LENGTH_LONG).show();
            btnSimpan.setEnabled(true);
            btnSimpan.setText("Simpan Produk");
            return;
        }

        String sellerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "unknown";

        Map<String, Object> produk = new HashMap<>();
        produk.put("nama", nama);
        produk.put("harga", harga);
        produk.put("kategori", kategori);
        produk.put("deskripsi", deskripsi);
        produk.put("foto", fotoBase64);
        produk.put("sellerId", sellerId);
        produk.put("status", "Available");

        db.collection("products")
                .add(produk)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // E1 - Kegagalan Sistem
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Produk");

                    new AlertDialog.Builder(this)
                            .setTitle("Gagal Menyimpan")
                            .setMessage("Terjadi kesalahan sistem. Produk tidak tersimpan. Silakan coba kembali.")
                            .setPositiveButton("Coba Lagi", (dialog, which) -> dialog.dismiss())
                            .setCancelable(false)
                            .show();
                });
    }
}