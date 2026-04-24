package com.kelompok3.posamplang.activities.laporan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;

/**
 * LaporanActivity — Halaman laporan penjualan.
 *
 * TODO: Implementasikan tampilan laporan harian, mingguan, dan bulanan
 *       menggunakan data dari PesananDao dan DetailPesananDao.
 */
public class LaporanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        setContentView(R.layout.activity_main);
        
        setupSidebar(R.id.btn_nav_laporan);
        
        Toast.makeText(this, "Modul Laporan sedang dalam pengembangan", Toast.LENGTH_SHORT).show();
    }
}
