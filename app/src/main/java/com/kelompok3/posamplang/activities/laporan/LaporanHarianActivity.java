package com.kelompok3.posamplang.activities.laporan;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.LaporanHarianAdapter;
import com.kelompok3.posamplang.models.LaporanHarian;
import com.kelompok3.posamplang.parent.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class LaporanHarianActivity extends BaseActivity {

    private RecyclerView rvLaporanHarian;
    private LaporanHarianAdapter adapter;
    private List<LaporanHarian> laporanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laporan_harian);

        setupSidebar(R.id.btn_nav_laporan);

        rvLaporanHarian = findViewById(R.id.rvLaporanHarian);
        rvLaporanHarian.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi data dummy sesuai gambar
        laporanList = new ArrayList<>();
        laporanList.add(new LaporanHarian("10/04/2026", 0, 0, 0, 0, 0, 0, 0));
        laporanList.add(new LaporanHarian("11/04/2026", 0, 0, 0, 0, 0, 0, 0));
        laporanList.add(new LaporanHarian("12/04/2026", 0, 0, 0, 0, 0, 0, 0));
        laporanList.add(new LaporanHarian("13/04/2026", 0, 0, 0, 0, 0, 0, 0));

        adapter = new LaporanHarianAdapter(laporanList);
        
        // Menambahkan fungsi klik tombol
        adapter.setOnItemClickListener(new LaporanHarianAdapter.OnItemClickListener() {
            @Override
            public void onLihatClick(int position) {
                android.content.Intent intent = new android.content.Intent(LaporanHarianActivity.this, DataLaporanActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPrintClick(int position) {
                Toast.makeText(LaporanHarianActivity.this, "Mencetak Laporan: " + laporanList.get(position).getTanggal(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(int position) {
                android.content.Intent intent = new android.content.Intent(LaporanHarianActivity.this, EditLaporanActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                Toast.makeText(LaporanHarianActivity.this, "Hapus Laporan: " + laporanList.get(position).getTanggal(), Toast.LENGTH_SHORT).show();
            }
        });

        rvLaporanHarian.setAdapter(adapter);
    }
}