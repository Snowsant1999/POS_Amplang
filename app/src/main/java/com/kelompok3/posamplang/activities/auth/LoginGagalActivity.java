package com.kelompok3.posamplang.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class LoginGagalActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnMasuk, btnBatal;
    private TextView tvLupaSandi, tvDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_gagal);

        etEmail = findViewById(R.id.etEmailGagal);
        etPassword = findViewById(R.id.etPasswordGagal);
        btnMasuk = findViewById(R.id.btnMasukGagal);
        btnBatal = findViewById(R.id.btnBatalGagal);
        tvLupaSandi = findViewById(R.id.tvLupaSandi);
        tvDaftar = findViewById(R.id.tvDaftarGagal);

        // Simulasi input salah dari gambar
        etPassword.setText("sherid");

        btnMasuk.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sandi masih salah!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBatal.setOnClickListener(v -> {
            // Kembali ke halaman login normal
            finish();
            overridePendingTransition(0, 0);
        });

        tvLupaSandi.setOnClickListener(v -> {
            Intent intent = new Intent(this, LupaSandiActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        tvDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }
}