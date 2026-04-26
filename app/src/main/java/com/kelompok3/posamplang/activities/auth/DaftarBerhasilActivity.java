package com.kelompok3.posamplang.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class DaftarBerhasilActivity extends AppCompatActivity {

    private Button btnLoginSekarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_berhasil);

        btnLoginSekarang = findViewById(R.id.btnLoginSekarangDaftar);

        btnLoginSekarang.setOnClickListener(v -> {
            // Kembali ke halaman login utama dan hapus history pendaftaran
            Intent intent = new Intent(DaftarBerhasilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}