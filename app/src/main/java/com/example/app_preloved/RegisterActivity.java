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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    EditText etNama, etEmail, etHp, etPassword, etKonfirmasi;
    TextView errorNama, errorEmail, errorHp, errorPassword, errorKonfirmasi;
    Button btnDaftar;
    ImageView btnBack;
    TextView txtMasuk;

    ImageView btnTogglePassword, btnToggleKonfirmasi;
    boolean isPasswordVisible = false;
    boolean isKonfirmasiVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        // EditText
        etNama       = findViewById(R.id.etNama);
        etEmail      = findViewById(R.id.etEmail);
        etHp         = findViewById(R.id.etHp);
        etPassword   = findViewById(R.id.etPassword);
        etKonfirmasi = findViewById(R.id.etKonfirmasi);

        // Error TextView
        errorNama       = findViewById(R.id.errorNama);
        errorEmail      = findViewById(R.id.errorEmail);
        errorHp         = findViewById(R.id.errorHp);
        errorPassword   = findViewById(R.id.errorPassword);
        errorKonfirmasi = findViewById(R.id.errorKonfirmasi);

        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleKonfirmasi = findViewById(R.id.btnToggleKonfirmasi);

        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Sembunyikan password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = false;
            } else {
                // Tampilkan password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            // Jaga cursor tetap di akhir
            etPassword.setSelection(etPassword.getText().length());
        });

        btnToggleKonfirmasi.setOnClickListener(v -> {
            if (isKonfirmasiVisible) {
                etKonfirmasi.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnToggleKonfirmasi.setImageResource(R.drawable.ic_eye);
                isKonfirmasiVisible = false;
            } else {
                etKonfirmasi.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnToggleKonfirmasi.setImageResource(R.drawable.ic_eye);
                isKonfirmasiVisible = true;
            }
            etKonfirmasi.setSelection(etKonfirmasi.getText().length());
        });

        btnDaftar = findViewById(R.id.btnDaftar);
        btnDaftar.setOnClickListener(v -> {
            validasiForm();
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {finish();});

        txtMasuk = findViewById(R.id.txtMasuk);
        txtMasuk.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void validasiForm() {

        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String hp = etHp.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String konfirmasi = etKonfirmasi.getText().toString().trim();

        // Nama kosong
        if(TextUtils.isEmpty(nama)){
            errorNama.setText("Nama wajib diisi");
            errorNama.setVisibility(View.VISIBLE);
            etNama.requestFocus();
            return;
        }else{
            errorNama.setVisibility(View.GONE);
        }

        // Email kosong
        if(TextUtils.isEmpty(email)){
            errorEmail.setText("Email wajib diisi");
            errorEmail.setVisibility(View.VISIBLE);
            etEmail.requestFocus();
            return;
        }

        // Format email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorEmail.setText("Format email tidak valid");
            errorEmail.setVisibility(View.VISIBLE);
            etEmail.requestFocus();
            return;
        }else{
            errorEmail.setVisibility(View.GONE);
        }

        // Email sudah terdaftar
        String emailTerdaftar = "admin@gmail.com";

        if(email.equalsIgnoreCase(emailTerdaftar)){
            errorEmail.setText("Email ini sudah terdaftar");
            errorEmail.setVisibility(View.VISIBLE);
            etEmail.requestFocus();

            // Proses registrasi dibatalkan
            return;
        }else{
            errorEmail.setVisibility(View.GONE);
        }

        // HP kosong
        if(TextUtils.isEmpty(hp)){
            errorHp.setText("Nomor HP wajib diisi");
            errorHp.setVisibility(View.VISIBLE);
            etHp.requestFocus();
            return;
        }else{
            errorHp.setVisibility(View.GONE);
        }

        // Password kosong
        if(TextUtils.isEmpty(password)){
            errorPassword.setText("Password wajib diisi");
            errorPassword.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
            return;
        }


        // Minimal 6 karakter
        if(password.length() < 6){
            errorPassword.setText("Password minimal 6 karakter");
            errorPassword.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
            return;
        }else{
            errorPassword.setVisibility(View.GONE);
        }

        // Konfirmasi password
        if(!password.equals(konfirmasi)){
            errorKonfirmasi.setText("Password tidak sama");
            errorKonfirmasi.setVisibility(View.VISIBLE);
            etKonfirmasi.requestFocus();
            return;
        }else{
            errorKonfirmasi.setVisibility(View.GONE);
        }

        Toast.makeText(this,
                 "Registrasi Berhasil",
              Toast.LENGTH_SHORT).show();

        Intent intent =
                new Intent(RegisterActivity.this,
                        SuccessActivity.class);

        startActivity(intent);
    }
}