package com.example.app_preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    TextView tvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvAvatar = findViewById(R.id.tv_avatar_dashboard);

        loadUserInisial();

        findViewById(R.id.btn_close).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.stay);
        });

        findViewById(R.id.menu_home).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.stay);
        });

        findViewById(R.id.menu_profil).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfilActivity.class));
        });

        findViewById(R.id.menu_pengaturan).setOnClickListener(v -> {
            // TODO
        });

        findViewById(R.id.menu_logout).setOnClickListener(v -> {
            startActivity(new Intent(this, LogoutActivity.class));
        });
    }

    private void loadUserInisial() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String nama = doc.getString("nama");
                    if (nama != null && !nama.isEmpty()) {
                        tvAvatar.setText(String.valueOf(nama.charAt(0)).toUpperCase());
                    }
                });
    }
}