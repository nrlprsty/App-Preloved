package com.example.app_preloved;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    TextView tvNama, tvTelepon, tvAlamat, tvEmail, tvNamaHeader;
    LinearLayout bannerSukses;

    static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        tvNama       = findViewById(R.id.tv_nama);
        tvTelepon    = findViewById(R.id.tv_telepon);
        tvAlamat     = findViewById(R.id.tv_alamat);
        tvEmail      = findViewById(R.id.tv_email);
        tvNamaHeader = findViewById(R.id.tv_nama_header);
        bannerSukses = findViewById(R.id.banner_sukses);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_edit).setOnClickListener(v -> {
            Intent editIntent = new Intent(this, com.example.app_preloved.EditProfilActivity.class);
            editIntent.putExtra("nama",    tvNama.getText().toString());
            editIntent.putExtra("telepon", tvTelepon.getText().toString());
            editIntent.putExtra("alamat",  tvAlamat.getText().toString());
            editIntent.putExtra("email",   tvEmail.getText().toString());
            startActivityForResult(editIntent, REQUEST_EDIT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK && data != null) {
            // Update data
            tvNama.setText(data.getStringExtra("nama"));
            tvTelepon.setText(data.getStringExtra("telepon"));
            tvAlamat.setText(data.getStringExtra("alamat"));
            tvEmail.setText(data.getStringExtra("email"));
            tvNamaHeader.setText(data.getStringExtra("nama"));

            // Tampilkan banner sukses
            bannerSukses.setVisibility(View.VISIBLE);
        }
    }
}