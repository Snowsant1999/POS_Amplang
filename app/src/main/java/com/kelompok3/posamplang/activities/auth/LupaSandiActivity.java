package com.kelompok3.posamplang.activities.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class LupaSandiActivity extends AppCompatActivity {

    private EditText etEmailReset;
    private Button btnKirimReset;
    private TextView tvKembaliLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halamanlogingagal1);

        etEmailReset = findViewById(R.id.etEmailReset);
        btnKirimReset = findViewById(R.id.btnKirimReset);
        tvKembaliLogin = findViewById(R.id.tvKembaliLogin);

        btnKirimReset.setOnClickListener(v -> {
            String email = etEmailReset.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmailReset.setError("Email tidak boleh kosong");
                return;
            }

            // Simulasi pengiriman link reset
            Toast.makeText(this, "Link reset telah dikirim ke: " + email, Toast.LENGTH_LONG).show();
        });

        tvKembaliLogin.setOnClickListener(v -> {
            // Kembali ke halaman login
            finish();
            overridePendingTransition(0, 0);
        });
    }
}