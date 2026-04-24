package com.kelompok3.posamplang.activities.dashboard;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kelompok3.posamplang.R;

import com.kelompok3.posamplang.parent.BaseActivity;

/**
 * MainActivity — Halaman Dashboard utama aplikasi POS Amplang.
 *
 * Berfungsi sebagai pusat navigasi ke semua modul:
 * Kasir, Stok/Produk, Supplier, Laporan, dan Pengaturan.
 *
 * TODO: Tambahkan statistik ringkasan (total transaksi hari ini, stok menipis, dll.)
 *       setelah implementasi Room Database selesai.
 */
public class MainActivity extends BaseActivity {

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupSidebar(R.id.btn_nav_dashboard);
    }

    // -------------------------------------------------------------------------
    // Inisialisasi
    // -------------------------------------------------------------------------

    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}