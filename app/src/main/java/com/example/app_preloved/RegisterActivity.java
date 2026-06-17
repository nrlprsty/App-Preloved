package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    Button btnDaftar;
    ImageView btnBack;
    TextView txtMasuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnDaftar.setOnClickListener(v -> {
            Intent intent =
                    new Intent(RegisterActivity.this,
                            SuccessActivity.class);

            startActivity(intent);
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {finish();});

        txtMasuk = findViewById(R.id.txtMasuk);
        txtMasuk.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}