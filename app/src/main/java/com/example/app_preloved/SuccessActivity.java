package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SuccessActivity extends AppCompatActivity {
    Button btnLoginSekarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success);

        btnLoginSekarang = findViewById(R.id.btnLoginSekarang);
        btnLoginSekarang.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        });
    }
}
