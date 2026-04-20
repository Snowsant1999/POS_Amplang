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
import com.kelompok3.posamplang.activities.dashboard.MainActivity;

/**
 * LoginActivity — Halaman autentikasi pengguna.
 *
 * TODO: Ganti validasi hardcoded dengan autentikasi dari Room Database
 *       setelah implementasi AppDatabase dan UserDao selesai.
 */
public class LoginActivity extends AppCompatActivity {

    // -------------------------------------------------------------------------
    // Views
    // -------------------------------------------------------------------------

    private EditText etEmail;
    private EditText etPassword;
    private Button btnMasuk;
    private Button btnBatal;
    private TextView tvDaftar;

    // -------------------------------------------------------------------------
    // Kredensial sementara (HARDCODED — hanya untuk tahap prototyping)
    // TODO: Hapus ini dan ganti dengan query ke UserDao
    // -------------------------------------------------------------------------

    private static final String DEMO_EMAIL    = "admin@gmail.com";
    private static final String DEMO_PASSWORD = "admin123";

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halamanlogin);

        initViews();
        setupClickListeners();
    }

    // -------------------------------------------------------------------------
    // Inisialisasi
    // -------------------------------------------------------------------------

    /** Menghubungkan variabel ke elemen UI dari layout. */
    private void initViews() {
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnMasuk   = findViewById(R.id.btnMasuk);
        btnBatal   = findViewById(R.id.btnBatal);
        tvDaftar   = findViewById(R.id.tvDaftar);
    }

    /** Mendaftarkan semua listener untuk elemen interaktif. */
    private void setupClickListeners() {
        btnMasuk.setOnClickListener(v -> handleLogin());
        btnBatal.setOnClickListener(v -> clearForm());
        tvDaftar.setOnClickListener(v -> navigateToRegister());
    }

    // -------------------------------------------------------------------------
    // Logic Utama
    // -------------------------------------------------------------------------

    /** Memvalidasi input dan memproses percobaan login. */
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

    /** Dipanggil ketika login berhasil. */
    private void onLoginSuccess() {
        Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        // Hapus LoginActivity dari back stack agar pengguna tidak bisa kembali ke layar login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /** Dipanggil ketika login gagal. */
    private void onLoginFailed() {
        Intent intent = new Intent(this, LoginGagalActivity.class);
        startActivity(intent);
    }

    /** Mengosongkan semua field input. */
    private void clearForm() {
        etEmail.setText("");
        etPassword.setText("");
        etEmail.requestFocus();
    }

    /** Navigasi ke halaman pendaftaran akun baru. */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}