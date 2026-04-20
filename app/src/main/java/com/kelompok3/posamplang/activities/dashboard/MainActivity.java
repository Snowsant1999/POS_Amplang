package com.kelompok3.posamplang.activities.dashboard;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kelompok3.posamplang.R;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnNavDashboard, btnNavStok, btnNavSupplier, btnNavKasir, btnNavLaporan, btnNavPengaturan, btnNavLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initNavigation();
        setupNavigationListeners();

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void initNavigation() {
        btnNavDashboard = findViewById(R.id.btn_nav_dashboard);
        btnNavStok = findViewById(R.id.btn_nav_stok);
        btnNavSupplier = findViewById(R.id.btn_nav_supplier);
        btnNavKasir = findViewById(R.id.btn_nav_kasir);
        btnNavLaporan = findViewById(R.id.btn_nav_laporan);
        btnNavPengaturan = findViewById(R.id.btn_nav_pengaturan);
        btnNavLogout = findViewById(R.id.btn_nav_logout);
    }

    private void setupNavigationListeners() {
        // Dashboard is current activity
        btnNavKasir.setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.transaksi.KasirActivity"));
        btnNavStok.setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.stok.StokActivity"));
        btnNavSupplier.setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.supplier.SupplierActivity"));
        btnNavLaporan.setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.laporan.LaporanActivity"));
        btnNavPengaturan.setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.pengaturan.PengaturanActivity"));

        btnNavLogout.setOnClickListener(v -> finish());
    }

    private void navigateTo(String className) {
        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(this, targetClass);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (ClassNotFoundException e) {
            android.widget.Toast.makeText(this, "Halaman belum tersedia", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}