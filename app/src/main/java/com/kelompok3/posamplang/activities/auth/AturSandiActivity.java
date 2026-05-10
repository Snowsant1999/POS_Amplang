package com.kelompok3.posamplang.activities.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class AturSandiActivity extends AppCompatActivity {

    private EditText etEmailReset, etSandiBaru, etSandiKonfirmasi;
    private Button btnAturUlang;
    private TextView tvKembaliLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atur_sandi);

        etEmailReset = findViewById(R.id.etEmailReset);
        etSandiBaru = findViewById(R.id.etSandiBaru);
        etSandiKonfirmasi = findViewById(R.id.etSandiKonfirmasi);
        btnAturUlang = findViewById(R.id.btnAturUlang);
        tvKembaliLogin = findViewById(R.id.tvKembaliLoginAtur);

        btnAturUlang.setOnClickListener(v -> {
            String email = etEmailReset.getText().toString().trim();
            String sandiBaru = etSandiBaru.getText().toString().trim();
            String konfirmasi = etSandiKonfirmasi.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmailReset.setError("Email tidak boleh kosong");
                return;
            }

            if (TextUtils.isEmpty(sandiBaru)) {
                etSandiBaru.setError("Sandi baru tidak boleh kosong");
                return;
            }

            if (sandiBaru.length() < 8) {
                etSandiBaru.setError("Sandi minimal 8 karakter");
                return;
            }

            if (!sandiBaru.equals(konfirmasi)) {
                etSandiKonfirmasi.setError("Konfirmasi sandi tidak cocok");
                return;
            }

            // Proses ganti sandi ke Database
            com.kelompok3.posamplang.database.AppDatabase db = com.kelompok3.posamplang.database.AppDatabase.getInstance(this);
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                // Cari user berdasarkan email
                com.kelompok3.posamplang.models.User user = db.userDao().getByEmail(email);

                runOnUiThread(() -> {
                    if (user == null) {
                        etEmailReset.setError("Email tidak terdaftar di sistem");
                        etEmailReset.requestFocus();
                    } else {
                        // Email ditemukan, update sandi di background thread
                        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                            db.userDao().updatePassword(email, sandiBaru);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Sandi Berhasil Diperbarui", Toast.LENGTH_SHORT).show();
                                
                                // Pindah ke halaman sukses reset sandi
                                android.content.Intent intent = new android.content.Intent(this, SandiBerhasilActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        });
                    }
                });
            });
        });

        tvKembaliLogin.setOnClickListener(v -> finish());
    }
}