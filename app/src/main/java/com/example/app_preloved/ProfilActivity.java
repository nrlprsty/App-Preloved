package com.example.app_preloved;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    TextView tvNama, tvTelepon, tvAlamat, tvEmail, tvNamaHeader, tvAvatar;
    /*LinearLayout bannerSukses;*/

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
        tvAvatar     = findViewById(R.id.tv_avatar);
        /*bannerSukses = findViewById(R.id.banner_sukses);*/

        updateAvatar(tvNama.getText().toString());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_edit).setOnClickListener(v -> {
            Intent editIntent = new Intent(this, EditProfilActivity.class);
            editIntent.putExtra("nama",    tvNama.getText().toString());
            editIntent.putExtra("telepon", tvTelepon.getText().toString());
            editIntent.putExtra("alamat",  tvAlamat.getText().toString());
            editIntent.putExtra("email",   tvEmail.getText().toString());
            startActivityForResult(editIntent, REQUEST_EDIT);
        });
    }

    private void updateAvatar(String nama) {
        if (nama != null && !nama.isEmpty()) {
            tvAvatar.setText(String.valueOf(nama.charAt(0)).toUpperCase());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK && data != null) {
            String namaBaru = data.getStringExtra("nama");
            tvNama.setText(namaBaru);
            tvTelepon.setText(data.getStringExtra("telepon"));
            tvAlamat.setText(data.getStringExtra("alamat"));
            tvEmail.setText(data.getStringExtra("email"));
            tvNamaHeader.setText(data.getStringExtra("nama"));

            updateAvatar(namaBaru);

            /*// Tampilkan banner sukses
            bannerSukses.setVisibility(View.VISIBLE);
            // Sembunyikan otomatis setelah 3 detik
            new android.os.Handler().postDelayed(() -> {
                bannerSukses.setVisibility(View.INVISIBLE);
            }, 1500);*/

            // Tampilkan popup sukses di tengah
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setView(getLayoutInflater().inflate(R.layout.dialog_sukses, null))
                    .setCancelable(false)
                    .create();

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Posisi di tengah atas
            dialog.getWindow().setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL);
            dialog.getWindow().getAttributes().y = 150; // jarak dari atas

            dialog.show();
            // Paksa ukuran dialog sesuai konten
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );

            new android.os.Handler().postDelayed(() -> {
                dialog.dismiss();
            }, 2000);
        }
    }
}