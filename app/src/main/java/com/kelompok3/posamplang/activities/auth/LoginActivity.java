package com.kelompok3.posamplang.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnMasuk, btnBatal;
    private TextView tvDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halamanlogin);

        // Inisialisasi View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        btnBatal = findViewById(R.id.btnBatal);
        tvDaftar = findViewById(R.id.tvDaftar);

        // Logika Tombol Masuk
        btnMasuk.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Kata sandi tidak boleh kosong");
                return;
            }

            // Simulasi Login Gagal sesuai gambar
            if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                navigateTo("com.kelompok3.posamplang.activities.dashboard.MainActivity");
            } else {
                // Pindah ke Halaman Login Gagal
                Intent intent = new Intent(LoginActivity.this, LoginGagalActivity.class);
                startActivity(intent);
            }
        });

        // Logika Tombol Batal
        btnBatal.setOnClickListener(v -> {
            etEmail.setText("");
            etPassword.setText("");
        });

        // Logika Daftar Sekarang
        tvDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
        private void navigateTo(String className) {
            try {
                Class<?> targetClass = Class.forName(className);
                Intent intent = new Intent(this, targetClass);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } catch (ClassNotFoundException e) {
                android.widget.Toast.makeText(this, "Halaman belum tersedia", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
}