package com.kelompok3.posamplang.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.auth.LoginActivity;
import com.kelompok3.posamplang.activities.produk.ProdukListActivity;
import com.kelompok3.posamplang.activities.transaksi.KasirActivity;

/**
 * MainActivity — Halaman Dashboard utama aplikasi POS Amplang.
 *
 * Berfungsi sebagai pusat navigasi ke semua modul:
 * Kasir, Stok/Produk, Supplier, Laporan, dan Pengaturan.
 *
 * TODO: Tambahkan statistik ringkasan (total transaksi hari ini, stok menipis, dll.)
 *       setelah implementasi Room Database selesai.
 */
public class MainActivity extends AppCompatActivity {

    // -------------------------------------------------------------------------
    // Views — Navigasi Sidebar
    // -------------------------------------------------------------------------

    private LinearLayout btnNavDashboard;
    private LinearLayout btnNavKasir;
    private LinearLayout btnNavStok;
    private LinearLayout btnNavSupplier;
    private LinearLayout btnNavLaporan;
    private LinearLayout btnNavPengaturan;
    private LinearLayout btnNavLogout;

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        initViews();
        setupNavigationListeners();
    }

    // -------------------------------------------------------------------------
    // Inisialisasi
    // -------------------------------------------------------------------------

    /** Mengatur padding agar konten tidak tertutup system bars. */
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

    /** Menghubungkan variabel ke elemen UI dari layout. */
    private void initViews() {
        btnNavDashboard  = findViewById(R.id.btn_nav_dashboard);
        btnNavKasir      = findViewById(R.id.btn_nav_kasir);
        btnNavStok       = findViewById(R.id.btn_nav_stok);
        btnNavSupplier   = findViewById(R.id.btn_nav_supplier);
        btnNavLaporan    = findViewById(R.id.btn_nav_laporan);
        btnNavPengaturan = findViewById(R.id.btn_nav_pengaturan);
        btnNavLogout     = findViewById(R.id.btn_nav_logout);
    }

    /** Mendaftarkan semua listener navigasi sidebar. */
    private void setupNavigationListeners() {
        // Dashboard — sudah berada di halaman ini, tidak perlu navigasi
        btnNavDashboard.setOnClickListener(v -> navigateTo(MainActivity.class));

        // Modul yang sudah tersedia
        btnNavKasir.setOnClickListener(v -> navigateTo(KasirActivity.class));
        btnNavStok.setOnClickListener(v  -> navigateTo(ProdukListActivity.class));

        // Modul yang belum diimplementasikan
        // TODO: Ganti dengan intent ke SupplierActivity, LaporanActivity, PengaturanActivity
        //       setelah modul-modul tersebut selesai dibuat.
        btnNavSupplier.setOnClickListener(v   -> showComingSoon("Supplier"));
        btnNavLaporan.setOnClickListener(v    -> showComingSoon("Laporan"));
        btnNavPengaturan.setOnClickListener(v -> showComingSoon("Pengaturan"));

        // Logout — kembali ke LoginActivity dan hapus semua activity dari back stack
        btnNavLogout.setOnClickListener(v -> handleLogout());
    }

    // -------------------------------------------------------------------------
    // Navigasi & Aksi
    // -------------------------------------------------------------------------

    /**
     * Navigasi ke activity tertentu menggunakan explicit intent.
     *
     * @param targetClass Class activity tujuan.
     */
    private void navigateTo(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * Menampilkan pesan bahwa fitur belum tersedia.
     *
     * @param moduleName Nama modul yang dituju.
     */
    private void showComingSoon(String moduleName) {
        Toast.makeText(this, "Modul " + moduleName + " belum tersedia", Toast.LENGTH_SHORT).show();
    }

    /** Keluar dari sesi dan kembali ke halaman Login. */
    private void handleLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        // Hapus semua activity dari back stack agar pengguna tidak bisa kembali ke dashboard
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}