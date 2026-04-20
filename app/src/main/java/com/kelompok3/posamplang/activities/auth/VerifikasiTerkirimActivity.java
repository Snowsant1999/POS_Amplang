package com.kelompok3.posamplang.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class VerifikasiTerkirimActivity extends AppCompatActivity {

    private Button btnKembaliLogin;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halamanlogingagal2);

        btnKembaliLogin = findViewById(R.id.btnKembaliLogin);
        tvDescription = findViewById(R.id.tvDescription);

        // Contoh cara menerima email dari activity sebelumnya (opsional)
        String email = getIntent().getStringExtra("EMAIL");
        if (email != null && !email.isEmpty()) {
            String text = "Link Reset Sandi sudah dikirim ke " + email + ". Silahkan cek email Anda dan ikuti instruksi untuk mengatur ulang sandi.";
            tvDescription.setText(text);
        }

        btnKembaliLogin.setOnClickListener(v -> {
            // Kembali ke halaman login dan membersihkan stack activity
            Intent intent = new Intent(VerifikasiTerkirimActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}