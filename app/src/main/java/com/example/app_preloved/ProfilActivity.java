package com.example.app_preloved;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilActivity extends AppCompatActivity {

    TextView tvNama, tvTelepon, tvAlamat, tvEmail, tvNamaHeader, tvAvatar;
    static final int REQUEST_EDIT = 1;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();
        uid   = mAuth.getCurrentUser().getUid();

        tvNama       = findViewById(R.id.tv_nama);
        tvTelepon    = findViewById(R.id.tv_telepon);
        tvAlamat     = findViewById(R.id.tv_alamat);
        tvEmail      = findViewById(R.id.tv_email);
        tvNamaHeader = findViewById(R.id.tv_nama_header);
        tvAvatar     = findViewById(R.id.tv_avatar);

        // Ambil data dari Firestore
        ambilDataProfil();

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

    private void ambilDataProfil() {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nama    = document.getString("nama");
                        String telepon = document.getString("telepon");
                        String alamat  = document.getString("alamat");
                        String email   = document.getString("email");

                        tvNama.setText(nama);
                        tvTelepon.setText(telepon);
                        tvAlamat.setText(alamat);
                        tvEmail.setText(email);
                        tvNamaHeader.setText(nama);
                        updateAvatar(nama);
                    }
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
            tvNamaHeader.setText(namaBaru);
            updateAvatar(namaBaru);

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setView(getLayoutInflater().inflate(R.layout.dialog_sukses, null))
                    .setCancelable(false)
                    .create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL);
            dialog.getWindow().getAttributes().y = 150;
            dialog.show();
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            new android.os.Handler().postDelayed(dialog::dismiss, 2000);
        }
    }
}