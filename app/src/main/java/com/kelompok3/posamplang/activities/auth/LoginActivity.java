package com.kelompok3.posamplang.activities.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.User;
import com.kelompok3.posamplang.utils.FixedViewportScaler;

import java.util.concurrent.Executors;

// Halaman untuk proses autentikasi pengguna
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private com.google.android.material.textfield.TextInputLayout tilEmail, tilPassword;
    private Button btnMasuk;
    private Button btnBatal;
    private TextView tvDaftar, tvLupaSandi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);
        FixedViewportScaler.apply(this);

        initViews();
        setupClickListeners();
    }


    // Menghubungkan variabel dengan komponen di layout
    private void initViews() {
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tilEmail   = findViewById(R.id.tilEmail);
        tilPassword= findViewById(R.id.tilPassword);
        btnMasuk   = findViewById(R.id.btnMasuk);
        btnBatal   = findViewById(R.id.btnBatal);
        tvDaftar   = findViewById(R.id.tvDaftar);
        tvLupaSandi= findViewById(R.id.tvLupaSandi);
        
        setupTextWatchers();
    }

    private void setupTextWatchers() {
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setErrorEnabled(false);
                tilPassword.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etEmail.addTextChangedListener(tw);
        etPassword.addTextChangedListener(tw);
    }


    // Menentukan aksi ketika tombol diklik
    private void setupClickListeners() {
        btnMasuk.setOnClickListener(v -> handleLogin());
        btnBatal.setOnClickListener(v -> clearForm());
        tvDaftar.setOnClickListener(v -> navigateToRegister());
        tvLupaSandi.setOnClickListener(v -> {
            startActivity(new Intent(this, LupaSandiActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    // Proses validasi form
    private void handleLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilEmail.setErrorEnabled(false);
        tilPassword.setErrorEnabled(false);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email wajib diisi");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password wajib diisi");
            etPassword.requestFocus();
            return;
        }

        // Proses autentikasi melalui database (Background Thread)
        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().login(email, password);
            
            // Kembali ke Main Thread untuk update UI
            runOnUiThread(() -> {
                if (user != null) {
                    onLoginSuccess();
                } else {
                    onLoginFailed();
                }
            });
        });
    }

    // Aksi jika login berhasil
    private void onLoginSuccess() {
        showStatusDialog(
            true,
            "Login Berhasil!",
            "Selamat datang kembali! Anda berhasil masuk ke sistem.",
            "Masuk Sekarang",
            () -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        );
    }

    // Aksi jika login gagal — tampilkan dialog langsung tanpa pindah halaman
    private void onLoginFailed() {
        showStatusDialog(
            false,
            "Login Gagal!",
            "Email atau kata sandi yang Anda masukkan salah. Silakan coba lagi atau gunakan fitur Lupa Sandi.",
            "Coba Lagi",
            () -> {
                etPassword.setText("");
                tilEmail.setErrorEnabled(false);
                tilPassword.setErrorEnabled(false);
                etPassword.requestFocus();
            }
        );
    }

    // Menampilkan popup dialog status (sukses/gagal)
    private void showStatusDialog(boolean isSuccess, String title, String message, String btnLabel, Runnable onAction) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status_auth);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(FixedViewportScaler.responsiveDialogWidth(this, 480),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView ivIcon    = dialog.findViewById(R.id.ivStatusIcon);
        TextView tvTitle    = dialog.findViewById(R.id.tvStatusTitle);
        TextView tvMessage  = dialog.findViewById(R.id.tvStatusMessage);
        Button   btnAction  = dialog.findViewById(R.id.btnStatusAction);

        tvTitle.setText(title);
        tvMessage.setText(message);
        btnAction.setText(btnLabel);

        if (isSuccess) {
            ivIcon.setImageResource(R.mipmap.berhasil_foreground);
            btnAction.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0B429")));
        } else {
            ivIcon.setImageResource(android.R.drawable.ic_dialog_alert);
            btnAction.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E53935")));
        }

        btnAction.setOnClickListener(v -> {
            dialog.dismiss();
            if (onAction != null) onAction.run();
        });

        dialog.show();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
