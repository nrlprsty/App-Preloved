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
    private Button btnSimpan, btnHapus;
    private ImageView btnBack, ivPreviewFoto;
    private LinearLayout layoutFoto;
    private ImageView icTambahFoto;
    private TextView tvTambahFoto;

    private FirebaseFirestore db;
    private Uri fotoUriBaru = null;
    private String fotoBase64Lama = null;
    private String productId;

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

        etNamaProduk    = findViewById(R.id.etNamaProduk);
        etHarga         = findViewById(R.id.etHarga);
        etDeskripsi     = findViewById(R.id.etDeskripsi);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        btnSimpan       = findViewById(R.id.btnSimpan);
        btnHapus        = findViewById(R.id.btnHapus);
        btnBack         = findViewById(R.id.btnBack);
        layoutFoto      = findViewById(R.id.layoutFoto);
        ivPreviewFoto   = findViewById(R.id.ivPreviewFoto);
        icTambahFoto    = findViewById(R.id.icTambahFoto);
        tvTambahFoto    = findViewById(R.id.tvTambahFoto);

        setupSpinnerKategori();

        productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        layoutFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        btnSimpan.setOnClickListener(v -> updateProduk());
        btnHapus.setOnClickListener(v -> konfirmasiHapus());

        loadDataProduk();
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

    // Story 7, langkah 4: Sistem menampilkan data produk saat ini
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

    private void setSpinnerToValue(String kategori) {
        if (kategori == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinnerKategori.getAdapter();
        int position = adapter.getPosition(kategori);
        if (position >= 0) {
            spinnerKategori.setSelection(position);
        }
    }

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
                // Biarkan tampilan default placeholder foto jika gagal decode
            }
        }
    }

    // Story 6 (di-include): Unggah Foto Produk
    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
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

    // Story 7: Edit Produk
    private void updateProduk() {
        String nama      = etNamaProduk.getText().toString().trim();
        String hargaStr   = etHarga.getText().toString().trim();
        String kategori   = spinnerKategori.getSelectedItem().toString();
        String deskripsi  = etDeskripsi.getText().toString().trim();

        // A1 - Data Tidak Valid
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

        String fotoBase64Final;

        if (fotoUriBaru != null) {
            fotoBase64Final = uriToBase64(fotoUriBaru);

            if (fotoBase64Final == null) {
                // A1 - Format File Tidak Sesuai (Story 6)
                Toast.makeText(this, "Format foto tidak sesuai atau ukuran terlalu besar. Pilih foto lain.", Toast.LENGTH_LONG).show();
                btnSimpan.setEnabled(true);
                btnSimpan.setText("Simpan Perubahan");
                return;
            }
        } else {
            fotoBase64Final = fotoBase64Lama;
        }

        Map<String, Object> produk = new HashMap<>();
        produk.put("nama", nama);
        produk.put("harga", harga);
        produk.put("kategori", kategori);
        produk.put("deskripsi", deskripsi);
        produk.put("foto", fotoBase64Final);

        db.collection("products")
                .document(productId)
                .update(produk)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Produk berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // E1 - Kegagalan Sistem
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Perubahan");

                    new AlertDialog.Builder(this)
                            .setTitle("Gagal Memperbarui")
                            .setMessage("Terjadi kesalahan sistem. Data produk tidak diperbarui. Silakan coba kembali.")
                            .setPositiveButton("Coba Lagi", (dialog, which) -> dialog.dismiss())
                            .setCancelable(false)
                            .show();
                });
    }

    // Story 8: Hapus Produk
    private void konfirmasiHapus() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Produk")
                .setMessage("Yakin ingin menghapus produk ini? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> hapusProduk())
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void hapusProduk() {
        btnHapus.setEnabled(false);

        db.collection("products")
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnHapus.setEnabled(true);
                    new AlertDialog.Builder(this)
                            .setTitle("Gagal Menghapus")
                            .setMessage("Terjadi kesalahan sistem. Produk tidak terhapus. Silakan coba kembali.")
                            .setPositiveButton("Coba Lagi", (dialog, which) -> dialog.dismiss())
                            .show();
                });
    }
}