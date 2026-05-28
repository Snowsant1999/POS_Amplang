package com.kelompok3.posamplang.activities.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.kelompok3.posamplang.models.User;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etSandi, etKonfirmasi;
    private com.google.android.material.textfield.TextInputLayout tilNama, tilEmail, tilSandi, tilKonfirmasi;
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
        
        tilNama = findViewById(R.id.tilNamaLengkap);
        tilEmail = findViewById(R.id.tilEmailDaftar);
        tilSandi = findViewById(R.id.tilPasswordDaftar);
        tilKonfirmasi = findViewById(R.id.tilKonfirmasiPassword);
        
        btnDaftar = findViewById(R.id.btnDaftarSekarang);
        tvMasuk = findViewById(R.id.tvMasuk);

        setupTextWatchers();

        btnDaftar.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String sandi = etSandi.getText().toString().trim();
            String konfirmasi = etKonfirmasi.getText().toString().trim();

            tilNama.setErrorEnabled(false);
            tilEmail.setErrorEnabled(false);
            tilSandi.setErrorEnabled(false);
            tilKonfirmasi.setErrorEnabled(false);

            if (TextUtils.isEmpty(nama)) {
                tilNama.setError("Nama tidak boleh kosong");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                tilEmail.setError("Email tidak boleh kosong");
                return;
            }
            if (sandi.length() < 8) {
                tilSandi.setError("Sandi minimal 8 karakter");
                return;
            }
            if (!sandi.equals(konfirmasi)) {
                tilKonfirmasi.setError("Sandi tidak cocok");
                return;
            }

            // Proses ke Database (Background Thread)
            com.kelompok3.posamplang.database.AppDatabase db = com.kelompok3.posamplang.database.AppDatabase.getInstance(this);
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                // Cek apakah email sudah terdaftar
                com.kelompok3.posamplang.models.User existingUser = db.userDao().getByEmail(email);
                
                if (existingUser != null) {
                    runOnUiThread(() -> tilEmail.setError("Email sudah digunakan"));
                    return;
                }
                
                try {
                    // Simpan user baru (Role diset 1 agar tidak error foreign key jika role 2 belum ada)
                    User newUser = new com.kelompok3.posamplang.models.User(1, "-", nama, email, sandi, "-");
                    db.userDao().insert(newUser);
                    
                    runOnUiThread(() -> {
                        showStatusDialog();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Gagal mendaftar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        tvMasuk.setOnClickListener(v -> finish());
    }

    private void showStatusDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status_auth);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        int width = (int)(480 * getResources().getDisplayMetrics().density);
        dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView ivIcon   = dialog.findViewById(R.id.ivStatusIcon);
        TextView tvTitle   = dialog.findViewById(R.id.tvStatusTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvStatusMessage);
        Button btnAction   = dialog.findViewById(R.id.btnStatusAction);

        ivIcon.setImageResource(R.mipmap.berhasil_foreground);
        tvTitle.setText("Pendaftaran Berhasil!");
        tvMessage.setText("Akun Anda berhasil dibuat. Anda sudah bisa login sekarang.");
        btnAction.setText("Login Sekarang");
        btnAction.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0B429")));

        btnAction.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        dialog.show();
    }

    private void setupTextWatchers() {
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilNama.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilSandi.setErrorEnabled(false);
                tilKonfirmasi.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etNama.addTextChangedListener(tw);
        etEmail.addTextChangedListener(tw);
        etSandi.addTextChangedListener(tw);
        etKonfirmasi.addTextChangedListener(tw);
    }
}