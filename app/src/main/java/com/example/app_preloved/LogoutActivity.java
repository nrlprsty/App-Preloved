package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_preloved.MainActivity;

public class LogoutActivity extends AppCompatActivity {

    private Button btnBatal, btnYa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi tombol
        btnBatal = findViewById(R.id.btnBatal);
        btnYa    = findViewById(R.id.btnYa);

        // Tombol Batal → kembali ke HomeActivity
        btnBatal.setOnClickListener(v -> finish());

        // Tombol Ya → logout, kembali ke MainActivity & bersihkan back stack
        btnYa.setOnClickListener(v -> {
            Intent intent = new Intent(LogoutActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}