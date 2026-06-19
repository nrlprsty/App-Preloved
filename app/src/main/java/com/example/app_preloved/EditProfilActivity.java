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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfilActivity extends AppCompatActivity {

    EditText etNama, etTelepon, etAlamat, etEmail;
    TextView errNama, errTelepon, errAlamat, errEmail;
    TextView tvNamaAvatar, tvAvatarEdit;
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

        tvNamaAvatar = findViewById(R.id.tv_nama_avatar);
        tvAvatarEdit  = findViewById(R.id.tv_avatar_edit);

        btnSimpan = findViewById(R.id.btn_simpan);
        btnBatal  = findViewById(R.id.btn_batal);

        loadDataFromFirestore();

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

    private void loadDataFromFirestore() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nama = doc.getString("nama");
                        etNama.setText(nama);
                        etTelepon.setText(doc.getString("telepon"));
                        etAlamat.setText(doc.getString("alamat"));
                        etEmail.setText(doc.getString("email"));

                        tvNamaAvatar.setText(nama);

                        if (nama != null && !nama.isEmpty()) {
                            tvAvatarEdit.setText(String.valueOf(nama.charAt(0)).toUpperCase());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("DEBUG_PROFIL", "Gagal ambil data", e);
                });
    }

    private boolean validasiInput() {
        boolean valid = true;

        String nama    = etNama.getText().toString().trim();
        String telepon = etTelepon.getText().toString().trim();
        String alamat  = etAlamat.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            errNama.setText("Nama lengkap tidak boleh kosong");
            errNama.setVisibility(View.VISIBLE);
            valid = false;
        } else if (nama.length() < 3) {
            errNama.setText("Nama minimal 3 karakter");
            errNama.setVisibility(View.VISIBLE);
            valid = false;
        }

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

        if (TextUtils.isEmpty(alamat)) {
            errAlamat.setText("Alamat tidak boleh kosong");
            errAlamat.setVisibility(View.VISIBLE);
            valid = false;
        }

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("nama", etNama.getText().toString().trim());
        data.put("telepon", etTelepon.getText().toString().trim());
        data.put("alamat", etAlamat.getText().toString().trim());
        data.put("email", etEmail.getText().toString().trim());

        btnSimpan.setEnabled(false);

        db.collection("users")
                .document(uid)
                .set(data)
                .addOnSuccessListener(unused -> {

                    Intent result = new Intent();
                    result.putExtra("profil_diperbarui", true);
                    result.putExtra("nama", etNama.getText().toString().trim());
                    result.putExtra("telepon", etTelepon.getText().toString().trim());
                    result.putExtra("alamat", etAlamat.getText().toString().trim());
                    result.putExtra("email", etEmail.getText().toString().trim());

                    setResult(Activity.RESULT_OK, result);
                    finish();
                })

                .addOnFailureListener(e -> {

                    btnSimpan.setEnabled(true);

                    new AlertDialog.Builder(this)
                            .setTitle("Gagal Menyimpan")
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();

                });
    }
}