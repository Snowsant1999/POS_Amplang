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

    private EditText etSandiBaru, etSandiKonfirmasi;
    private Button btnAturUlang;
    private TextView tvKembaliLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halamanloginatursandi);

        etSandiBaru = findViewById(R.id.etSandiBaru);
        etSandiKonfirmasi = findViewById(R.id.etSandiKonfirmasi);
        btnAturUlang = findViewById(R.id.btnAturUlang);
        tvKembaliLogin = findViewById(R.id.tvKembaliLoginAtur);

        btnAturUlang.setOnClickListener(v -> {
            String sandiBaru = etSandiBaru.getText().toString().trim();
            String konfirmasi = etSandiKonfirmasi.getText().toString().trim();

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

            // Berhasil
            Toast.makeText(this, "Sandi Berhasil Diperbarui", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke login
        });

        tvKembaliLogin.setOnClickListener(v -> finish());
    }
}