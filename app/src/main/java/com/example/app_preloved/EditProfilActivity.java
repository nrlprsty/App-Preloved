package com.example.app_preloved;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfilActivity extends AppCompatActivity {

    EditText etNama, etTelepon, etAlamat, etEmail;
    TextView errNama, errTelepon, errAlamat, errEmail;
    Button btnSimpan, btnBatal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        etNama    = findViewById(R.id.et_nama);
        etTelepon = findViewById(R.id.et_telepon);
        etAlamat  = findViewById(R.id.et_alamat);
        etEmail   = findViewById(R.id.et_email);

        errNama    = findViewById(R.id.err_nama);
        errTelepon = findViewById(R.id.err_telepon);
        errAlamat  = findViewById(R.id.err_alamat);
        errEmail   = findViewById(R.id.err_email);

        btnSimpan = findViewById(R.id.btn_simpan);
        btnBatal  = findViewById(R.id.btn_batal);

        // Isi field dengan data sebelumnya
        Intent intent = getIntent();
        etNama.setText(intent.getStringExtra("nama"));
        etTelepon.setText(intent.getStringExtra("telepon"));
        etAlamat.setText(intent.getStringExtra("alamat"));
        etEmail.setText(intent.getStringExtra("email"));

        // Hapus error saat user mulai mengetik
        etNama.addTextChangedListener(new SimpleTextWatcher(() -> errNama.setVisibility(View.GONE)));
        etTelepon.addTextChangedListener(new SimpleTextWatcher(() -> errTelepon.setVisibility(View.GONE)));
        etAlamat.addTextChangedListener(new SimpleTextWatcher(() -> errAlamat.setVisibility(View.GONE)));
        etEmail.addTextChangedListener(new SimpleTextWatcher(() -> errEmail.setVisibility(View.GONE)));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        btnBatal.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            if (validasiInput()) {
                simpanData();
            }
        });
    }

    private boolean validasiInput() {
        boolean valid = true;

        String nama    = etNama.getText().toString().trim();
        String telepon = etTelepon.getText().toString().trim();
        String alamat  = etAlamat.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();

        // Validasi Nama
        if (TextUtils.isEmpty(nama)) {
            errNama.setText("Nama lengkap tidak boleh kosong");
            errNama.setVisibility(View.VISIBLE);
            valid = false;
        } else if (nama.length() < 3) {
            errNama.setText("Nama minimal 3 karakter");
            errNama.setVisibility(View.VISIBLE);
            valid = false;
        }

        // Validasi Telepon
        if (TextUtils.isEmpty(telepon)) {
            errTelepon.setText("Nomor telepon tidak boleh kosong");
            errTelepon.setVisibility(View.VISIBLE);
            valid = false;
        } else if (!telepon.startsWith("+62") && !telepon.startsWith("08")) {
            errTelepon.setText("Nomor harus diawali +62 atau 08");
            errTelepon.setVisibility(View.VISIBLE);
            valid = false;
        } else if (telepon.length() < 10) {
            errTelepon.setText("Nomor telepon tidak valid");
            errTelepon.setVisibility(View.VISIBLE);
            valid = false;
        }

        // Validasi Alamat
        if (TextUtils.isEmpty(alamat)) {
            errAlamat.setText("Alamat tidak boleh kosong");
            errAlamat.setVisibility(View.VISIBLE);
            valid = false;
        }

        // Validasi Email
        if (TextUtils.isEmpty(email)) {
            errEmail.setText("Email tidak boleh kosong");
            errEmail.setVisibility(View.VISIBLE);
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errEmail.setText("Format email tidak valid");
            errEmail.setVisibility(View.VISIBLE);
            valid = false;
        }

        return valid;
    }

    private void simpanData() {
        // Simulasi E1 - Kegagalan Sistem (misal random gagal)
        boolean sistemGagal = false; // ganti true untuk test error

        if (sistemGagal) {
            // E1 – Tampilkan dialog kegagalan sistem
            new AlertDialog.Builder(this)
                    .setTitle("Gagal Menyimpan")
                    .setMessage("Terjadi kesalahan sistem. Data tidak diperbarui. Silakan coba kembali.")
                    .setPositiveButton("Coba Lagi", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Batal", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
            return;
        }

        // Sukses → kirim data ke ProfilActivity
        Intent result = new Intent(this, ProfilActivity.class);
        result.putExtra("profil_diperbarui", true);
        result.putExtra("nama",    etNama.getText().toString().trim());
        result.putExtra("telepon", etTelepon.getText().toString().trim());
        result.putExtra("alamat",  etAlamat.getText().toString().trim());
        result.putExtra("email",   etEmail.getText().toString().trim());
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}