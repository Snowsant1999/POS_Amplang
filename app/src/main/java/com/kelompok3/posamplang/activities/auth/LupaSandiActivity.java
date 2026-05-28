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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.User;
import com.kelompok3.posamplang.utils.FixedViewportScaler;

import java.util.concurrent.Executors;

public class LupaSandiActivity extends AppCompatActivity {

    private EditText etEmailReset, etSandiBaru, etSandiKonfirmasi;
    private com.google.android.material.textfield.TextInputLayout tilEmailReset, tilSandiBaru, tilSandiKonfirmasi;
    private Button btnAturUlang;
    private TextView tvKembaliLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);
        EdgeToEdge.enable(this);
        FixedViewportScaler.apply(this);

        etEmailReset = findViewById(R.id.etEmailReset);
        etSandiBaru = findViewById(R.id.etSandiBaru);
        etSandiKonfirmasi = findViewById(R.id.etSandiKonfirmasi);
        
        tilEmailReset = findViewById(R.id.tilEmailReset);
        tilSandiBaru = findViewById(R.id.tilSandiBaru);
        tilSandiKonfirmasi = findViewById(R.id.tilSandiKonfirmasi);
        btnAturUlang = findViewById(R.id.btnAturUlang);
        tvKembaliLogin = findViewById(R.id.tvKembaliLogin);

        btnAturUlang.setOnClickListener(v -> handleResetPassword());
        tvKembaliLogin.setOnClickListener(v -> finish());
        
        setupTextWatchers();
    }

    private void setupTextWatchers() {
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmailReset.setErrorEnabled(false);
                tilSandiBaru.setErrorEnabled(false);
                tilSandiKonfirmasi.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etEmailReset.addTextChangedListener(tw);
        etSandiBaru.addTextChangedListener(tw);
        etSandiKonfirmasi.addTextChangedListener(tw);
    }

    private void handleResetPassword() {
        String email = etEmailReset.getText().toString().trim();
        String sandiBaru = etSandiBaru.getText().toString().trim();
        String konfirmasi = etSandiKonfirmasi.getText().toString().trim();

        // Bersihkan error sebelumnya secara total
        tilEmailReset.setErrorEnabled(false);
        tilSandiBaru.setErrorEnabled(false);
        tilSandiKonfirmasi.setErrorEnabled(false);

        if (TextUtils.isEmpty(email)) {
            tilEmailReset.setError("Email tidak boleh kosong");
            etEmailReset.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sandiBaru)) {
            tilSandiBaru.setError("Sandi baru tidak boleh kosong");
            etSandiBaru.requestFocus();
            return;
        }

        if (sandiBaru.length() < 8) {
            tilSandiBaru.setError("Sandi minimal 8 karakter");
            etSandiBaru.requestFocus();
            return;
        }

        if (!sandiBaru.equals(konfirmasi)) {
            tilSandiKonfirmasi.setError("Konfirmasi sandi tidak cocok");
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
                    tilEmailReset.setError("Email tidak terdaftar di sistem");
                    etEmailReset.requestFocus();
                } else {
                    // Email ditemukan, update sandi
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.userDao().updatePassword(email, sandiBaru);
                        runOnUiThread(() -> showSandiBerhasilDialog());
                    });
                }
            });
        });
    }

    private void showSandiBerhasilDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status_auth);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(FixedViewportScaler.responsiveDialogWidth(this, 480),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView ivIcon   = dialog.findViewById(R.id.ivStatusIcon);
        TextView tvTitle   = dialog.findViewById(R.id.tvStatusTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvStatusMessage);
        Button btnAction   = dialog.findViewById(R.id.btnStatusAction);

        ivIcon.setImageResource(R.mipmap.berhasil_foreground);
        tvTitle.setText("Sandi Berhasil Diubah!");
        tvMessage.setText("Sandi Anda telah berhasil diperbarui. Silakan masuk ulang menggunakan sandi baru Anda.");
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
}
