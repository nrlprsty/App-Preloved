package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etNama, etEmail, etHp, etPassword, etKonfirmasi;
    TextView errorNama, errorEmail, errorHp, errorPassword, errorKonfirmasi;
    Button btnDaftar;
    ImageView btnBack, btnTogglePassword, btnToggleKonfirmasi;
    TextView txtMasuk;
    boolean isPasswordVisible = false;
    boolean isKonfirmasiVisible = false;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        etNama       = findViewById(R.id.etNama);
        etEmail      = findViewById(R.id.etEmail);
        etHp         = findViewById(R.id.etHp);
        etPassword   = findViewById(R.id.etPassword);
        etKonfirmasi = findViewById(R.id.etKonfirmasi);

        errorNama       = findViewById(R.id.errorNama);
        errorEmail      = findViewById(R.id.errorEmail);
        errorHp         = findViewById(R.id.errorHp);
        errorPassword   = findViewById(R.id.errorPassword);
        errorKonfirmasi = findViewById(R.id.errorKonfirmasi);

        btnTogglePassword   = findViewById(R.id.btnTogglePassword);
        btnToggleKonfirmasi = findViewById(R.id.btnToggleKonfirmasi);

        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isPasswordVisible = false;
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnToggleKonfirmasi.setOnClickListener(v -> {
            if (isKonfirmasiVisible) {
                etKonfirmasi.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isKonfirmasiVisible = false;
            } else {
                etKonfirmasi.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isKonfirmasiVisible = true;
            }
            etKonfirmasi.setSelection(etKonfirmasi.getText().length());
        });

        btnDaftar = findViewById(R.id.btnDaftar);
        btnDaftar.setOnClickListener(v -> validasiForm());

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        txtMasuk = findViewById(R.id.txtMasuk);
        txtMasuk.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void validasiForm() {
        String nama       = etNama.getText().toString().trim();
        String email      = etEmail.getText().toString().trim();
        String hp         = etHp.getText().toString().trim();
        String password   = etPassword.getText().toString().trim();
        String konfirmasi = etKonfirmasi.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            errorNama.setText("Nama wajib diisi");
            errorNama.setVisibility(View.VISIBLE);
            return;
        } else { errorNama.setVisibility(View.GONE); }

        if (TextUtils.isEmpty(email)) {
            errorEmail.setText("Email wajib diisi");
            errorEmail.setVisibility(View.VISIBLE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorEmail.setText("Format email tidak valid");
            errorEmail.setVisibility(View.VISIBLE);
            return;
        } else { errorEmail.setVisibility(View.GONE); }

        if (TextUtils.isEmpty(hp)) {
            errorHp.setText("Nomor HP wajib diisi");
            errorHp.setVisibility(View.VISIBLE);
            return;
        } else { errorHp.setVisibility(View.GONE); }

        if (TextUtils.isEmpty(password)) {
            errorPassword.setText("Password wajib diisi");
            errorPassword.setVisibility(View.VISIBLE);
            return;
        }
        if (password.length() < 6) {
            errorPassword.setText("Password minimal 6 karakter");
            errorPassword.setVisibility(View.VISIBLE);
            return;
        } else { errorPassword.setVisibility(View.GONE); }

        if (!password.equals(konfirmasi)) {
            errorKonfirmasi.setText("Password tidak sama");
            errorKonfirmasi.setVisibility(View.VISIBLE);
            return;
        } else { errorKonfirmasi.setVisibility(View.GONE); }

        // Semua valid → daftar ke Firebase
        btnDaftar.setEnabled(false);
        btnDaftar.setText("Mendaftar...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Simpan data profil ke Firestore
                    String uid = authResult.getUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("nama",    nama);
                    user.put("email",   email);
                    user.put("telepon", hp);
                    user.put("alamat",  "");

                    db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                startActivity(new Intent(this, SuccessActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnDaftar.setEnabled(true);
                                btnDaftar.setText("Daftar");
                                Toast.makeText(this,
                                        "Gagal simpan profil: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnDaftar.setEnabled(true);
                    btnDaftar.setText("Daftar");
                    errorEmail.setText("Pendaftaran gagal: " + e.getMessage());
                    errorEmail.setVisibility(View.VISIBLE);
                });
    }
}