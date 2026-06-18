package com.example.app_preloved;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ImageView btnBack;

    // Kredensial dummy untuk validasi
    private static final String VALID_EMAIL = "user@email.com";
    private static final String VALID_PASSWORD = "12345";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi View
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        btnBack    = findViewById(R.id.btnBack);

        // Tombol Back → kembali ke activity sebelumnya
        btnBack.setOnClickListener(v -> finish());

        // Tombol Login → validasi input
        btnLogin.setOnClickListener(v -> {
            String email    = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Cek field kosong
            if (TextUtils.isEmpty(email)) {
                etUsername.setError("Email tidak boleh kosong");
                etUsername.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password tidak boleh kosong");
                etPassword.requestFocus();
                return;
            }

            // Validasi kredensial
            if (email.equals(VALID_EMAIL) && password.equals(VALID_PASSWORD)) {
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                // Pindah ke HomeActivity (buat activity ini jika belum ada)
                Intent intent = new Intent(LoginActivity.this, com.example.app_preloved.HomeActivity.class);
                intent.putExtra("email", email); // kirim data email ke halaman berikutnya
                startActivity(intent);
                finish(); // tutup LoginActivity agar tidak bisa back ke sini
           } else {
                Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}