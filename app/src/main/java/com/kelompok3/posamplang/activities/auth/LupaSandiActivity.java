package com.kelompok3.posamplang.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.User;

import java.util.concurrent.Executors;

public class LupaSandiActivity extends AppCompatActivity {

    private EditText etEmailReset, etSandiBaru, etSandiKonfirmasi;
    private Button btnAturUlang;
    private TextView tvKembaliLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);
        EdgeToEdge.enable(this);

        etEmailReset = findViewById(R.id.etEmailReset);
        etSandiBaru = findViewById(R.id.etSandiBaru);
        etSandiKonfirmasi = findViewById(R.id.etSandiKonfirmasi);
        btnAturUlang = findViewById(R.id.btnAturUlang);
        tvKembaliLogin = findViewById(R.id.tvKembaliLogin);

        btnAturUlang.setOnClickListener(v -> handleResetPassword());
        tvKembaliLogin.setOnClickListener(v -> finish());
    }

    private void handleResetPassword() {
        String email = etEmailReset.getText().toString().trim();
        String sandiBaru = etSandiBaru.getText().toString().trim();
        String konfirmasi = etSandiKonfirmasi.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmailReset.setError("Email tidak boleh kosong");
            etEmailReset.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sandiBaru)) {
            etSandiBaru.setError("Sandi baru tidak boleh kosong");
            etSandiBaru.requestFocus();
            return;
        }

        if (sandiBaru.length() < 8) {
            etSandiBaru.setError("Sandi minimal 8 karakter");
            etSandiBaru.requestFocus();
            return;
        }

        if (!sandiBaru.equals(konfirmasi)) {
            etSandiKonfirmasi.setError("Konfirmasi sandi tidak cocok");
            etSandiKonfirmasi.requestFocus();
            return;
        }

        // Proses ganti sandi ke Database di Background Thread
        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            // Cari user berdasarkan email
            User user = db.userDao().getByEmail(email);

            runOnUiThread(() -> {
                if (user == null) {
                    etEmailReset.setError("Email tidak terdaftar di sistem");
                    etEmailReset.requestFocus();
                } else {
                    // Email ditemukan, update sandi
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.userDao().updatePassword(email, sandiBaru);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Sandi Berhasil Diperbarui", Toast.LENGTH_SHORT).show();
                            
                            // Pindah ke halaman sukses reset sandi
                            Intent intent = new Intent(this, SandiBerhasilActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    });
                }
            });
        });
    }
}
