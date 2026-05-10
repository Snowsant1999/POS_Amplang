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

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etSandi, etKonfirmasi;
    private Button btnDaftar;
    private TextView tvMasuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNama = findViewById(R.id.etNamaLengkap);
        etEmail = findViewById(R.id.etEmailDaftar);
        etSandi = findViewById(R.id.etPasswordDaftar);
        etKonfirmasi = findViewById(R.id.etKonfirmasiPassword);
        btnDaftar = findViewById(R.id.btnDaftarSekarang);
        tvMasuk = findViewById(R.id.tvMasuk);

        btnDaftar.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String sandi = etSandi.getText().toString().trim();
            String konfirmasi = etKonfirmasi.getText().toString().trim();

            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama tidak boleh kosong");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }
            if (sandi.length() < 8) {
                etSandi.setError("Sandi minimal 8 karakter");
                return;
            }
            if (!sandi.equals(konfirmasi)) {
                etKonfirmasi.setError("Sandi tidak cocok");
                return;
            }

            // Proses ke Database (Background Thread)
            com.kelompok3.posamplang.database.AppDatabase db = com.kelompok3.posamplang.database.AppDatabase.getInstance(this);
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                // Cek apakah email sudah terdaftar
                com.kelompok3.posamplang.models.User existingUser = db.userDao().getByEmail(email);
                
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        etEmail.setError("Email sudah terdaftar!");
                        etEmail.requestFocus();
                    });
                    return;
                }
                
                try {
                    // Simpan user baru (Role diset 1 agar tidak error foreign key jika role 2 belum ada)
                    com.kelompok3.posamplang.models.User newUser = new com.kelompok3.posamplang.models.User(1, "-", nama, email, sandi, "-");
                    db.userDao().insert(newUser);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show();
                        
                        // Lanjut ke halaman daftar berhasil
                        Intent intent = new Intent(this, com.kelompok3.posamplang.activities.auth.DaftarBerhasilActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Gagal mendaftar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        tvMasuk.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }
}