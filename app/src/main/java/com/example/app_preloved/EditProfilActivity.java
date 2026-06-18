package com.example.app_preloved;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfilActivity extends AppCompatActivity {

    EditText etNama, etTelepon, etAlamat, etEmail;
    Button btnSimpan, btnBatal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        etNama    = findViewById(R.id.et_nama);
        etTelepon = findViewById(R.id.et_telepon);
        etAlamat  = findViewById(R.id.et_alamat);
        etEmail   = findViewById(R.id.et_email);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBatal  = findViewById(R.id.btn_batal);

        Intent intent = getIntent();
        etNama.setText(intent.getStringExtra("nama"));
        etTelepon.setText(intent.getStringExtra("telepon"));
        etAlamat.setText(intent.getStringExtra("alamat"));
        etEmail.setText(intent.getStringExtra("email"));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("nama",    etNama.getText().toString().trim());
            result.putExtra("telepon", etTelepon.getText().toString().trim());
            result.putExtra("alamat",  etAlamat.getText().toString().trim());
            result.putExtra("email",   etEmail.getText().toString().trim());
            setResult(Activity.RESULT_OK, result);
            finish();
        });

        btnBatal.setOnClickListener(v -> finish());
    }
}