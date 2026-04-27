package com.kelompok3.posamplang.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;


// Halaman untuk proses autentikasi pengguna
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnMasuk;
    private Button btnBatal;
    private TextView tvDaftar;

    private static final String DEMO_EMAIL    = "admin@gmail.com";
    private static final String DEMO_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);

        initViews();
        setupClickListeners();
    }


    // Menghubungkan variabel dengan komponen di layout
    private void initViews() {
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnMasuk   = findViewById(R.id.btnMasuk);
        btnBatal   = findViewById(R.id.btnBatal);
        tvDaftar   = findViewById(R.id.tvDaftar);
    }


    // Menentukan aksi ketika tombol diklik
    private void setupClickListeners() {
        btnMasuk.setOnClickListener(v -> handleLogin());
        btnBatal.setOnClickListener(v -> clearForm());
        tvDaftar.setOnClickListener(v -> navigateToRegister());
    }

    // Memvalidasi input dan memproses login
    private void handleLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input kosong
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Kata sandi tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Proses autentikasi
        if (email.equals(DEMO_EMAIL) && password.equals(DEMO_PASSWORD)) {
            onLoginSuccess();
        } else {
            onLoginFailed();
        }
    }

    // Aksi jika login berhasil
    private void onLoginSuccess() {
        Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        // Hapus LoginActivity dari back stack agar pengguna tidak bisa kembali ke layar login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Aksi jika login gagal
    private void onLoginFailed() {
        Intent intent = new Intent(this, LoginGagalActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Mengosongkan isian form
    private void clearForm() {
        etEmail.setText("");
        etPassword.setText("");
        etEmail.requestFocus();
    }
    // Pindah ke halaman register
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}