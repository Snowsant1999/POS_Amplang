package com.kelompok3.posamplang.parent;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.auth.LoginActivity;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;
import com.kelompok3.posamplang.activities.laporan.LaporanHarianActivity;
import com.kelompok3.posamplang.activities.laporan.LaporanHarianDetailActivity;
import com.kelompok3.posamplang.activities.transaksi.KasirActivity;
import com.kelompok3.posamplang.activities.produk.ProdukListActivity;
import com.kelompok3.posamplang.activities.supplier.SupplierListActivity;
import com.kelompok3.posamplang.activities.pengaturan.PengaturanActivity;
import com.kelompok3.posamplang.utils.FixedViewportScaler;
import com.kelompok3.posamplang.utils.StoreSettings;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    
    /**
     * Inisialisasi Sidebar dan tandai menu yang aktif.
     * @param activeMenuId ID dari layout menu yang ingin diberi warna merah (misal: R.id.btn_nav_dashboard)
     */
    protected void setupSidebar(int activeMenuId) {
        FixedViewportScaler.apply(this);
        applySidebarStoreName();

        // Inisialisasi semua button dari layout_sidebar.xml
        LinearLayout btnDashboard = findViewById(R.id.btn_nav_dashboard);
        LinearLayout btnKasir      = findViewById(R.id.btn_nav_kasir);
        LinearLayout btnStok       = findViewById(R.id.btn_nav_stok);
        LinearLayout btnSupplier   = findViewById(R.id.btn_nav_supplier);
        LinearLayout btnLaporan    = findViewById(R.id.btn_nav_laporan);
        LinearLayout btnSetting    = findViewById(R.id.btn_nav_pengaturan);
        LinearLayout btnLogout     = findViewById(R.id.btn_nav_logout);

        // Tandai menu yang aktif dengan warna merah
        if (activeMenuId != 0) {
            LinearLayout activeLayout = findViewById(activeMenuId);
            if (activeLayout != null) {
                // Gunakan setBackgroundResource untuk mengambil warna dari colors.xml
                activeLayout.setBackgroundResource(R.color.active_red);
            }
        }

        // Logika Navigasi (Klik Menu)
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(v -> navigateTo(MainActivity.class));
        }

        if (btnStok != null) {
            btnStok.setOnClickListener(v -> navigateTo(ProdukListActivity.class));
        }

        if (btnSupplier != null) {
            btnSupplier.setOnClickListener(v -> navigateTo(SupplierListActivity.class));
        }

        if (btnLaporan != null) {
            btnLaporan.setOnClickListener(v -> navigateTo(LaporanHarianDetailActivity.class));
        }

        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> navigateTo(PengaturanActivity.class));
        }

        if (btnKasir != null) {
            btnKasir.setOnClickListener(v -> navigateTo(KasirActivity.class));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> handleLogout());
        }
    }

    protected void applySidebarStoreName() {
        TextView tvPrimary = findViewById(R.id.tv_sidebar_store_primary);
        TextView tvSecondary = findViewById(R.id.tv_sidebar_store_secondary);
        if (tvPrimary == null || tvSecondary == null) {
            return;
        }

        String storeName = StoreSettings.get(this).name.trim();
        if (storeName.isEmpty()) {
            storeName = StoreSettings.DEFAULT_NAME;
        }
        String[] parts = storeName.split("\\s+", 2);
        tvPrimary.setText(parts[0].toUpperCase(Locale.ROOT));
        tvSecondary.setText(parts.length > 1 ? parts[1].toUpperCase(Locale.ROOT) : "");
    }

    /**
     * Helper untuk pindah activity agar tidak duplikat dan lebih efisien.
     */
    protected void navigateTo(Class<?> targetClass) {
        if (this.getClass() == targetClass) return;

        Intent intent = new Intent(this, targetClass);
        // Memastikan jika activity sudah ada di background, dia akan ditarik ke depan
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        // Menghilangkan animasi agar sidebar terasa persisten/tidak bergerak
        overridePendingTransition(0, 0);
    }

    /**
     * Logika untuk keluar dari akun
     */
    protected void handleLogout() {
        Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        // Hapus semua history activity sebelumnya agar tidak bisa "back" lagi
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        // Menghilangkan animasi
        overridePendingTransition(0, 0);
    }
}
