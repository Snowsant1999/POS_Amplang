package com.kelompok3.posamplang.activities.laporan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.LaporanHarianAdapter;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.LaporanHarian;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.ExcelReportExporter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class LaporanHarianActivity extends BaseActivity implements LaporanHarianAdapter.Listener {

    private final List<LaporanHarian> laporanList = new ArrayList<>();
    private AppDatabase database;
    private RecyclerView rvLaporan;
    private TextView tvEmpty;
    private TextView tvInfo;
    private LaporanHarianAdapter adapter;
    private LaporanHarian selectedExport;

    private final ActivityResultLauncher<String> excelLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), uri -> {
                if (uri != null && selectedExport != null) {
                    writeExcel(uri, selectedExport);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_harian);
        setupSidebar(R.id.btn_nav_laporan);
        database = AppDatabase.getInstance(this);

        rvLaporan = findViewById(R.id.rvLaporanHarian);
        tvEmpty = findViewById(R.id.tvEmptyLaporan);
        tvInfo = findViewById(R.id.tvInfoLaporan);
        rvLaporan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanHarianAdapter(laporanList, this);
        rvLaporan.setAdapter(adapter);
        findViewById(R.id.btnKembaliLaporanAktif).setOnClickListener(v ->
                startActivity(new Intent(this, LaporanHarianDetailActivity.class)));
        loadReports();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (database != null) {
            loadReports();
        }
    }

    private void loadReports() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<LaporanHarian> results = database.laporanHarianDao().getAll();
            runOnUiThread(() -> {
                laporanList.clear();
                laporanList.addAll(results);
                adapter.notifyDataSetChanged();
                boolean empty = laporanList.isEmpty();
                tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                rvLaporan.setVisibility(empty ? View.GONE : View.VISIBLE);
                tvInfo.setText("Menampilkan " + laporanList.size() + " data laporan");
            });
        });
    }

    @Override
    public void onLihatClick(LaporanHarian laporan) {
        Intent intent = new Intent(this, LaporanHarianDetailActivity.class);
        intent.putExtra(LaporanHarianDetailActivity.EXTRA_LAPORAN_ID, laporan.getId_laporan());
        startActivity(intent);
    }

    @Override
    public void onPrintClick(LaporanHarian laporan) {
        selectedExport = laporan;
        String date = new SimpleDateFormat("yyyyMMdd", Locale.US)
                .format(new Date(laporan.getTanggal_laporan()));
        excelLauncher.launch("Laporan_Keuangan_" + date + ".xlsx");
    }

    @Override
    public void onDeleteClick(LaporanHarian laporan) {
        Executors.newSingleThreadExecutor().execute(() -> {
            database.laporanHarianDao().delete(laporan);
            runOnUiThread(() -> {
                Toast.makeText(this, "Laporan harian telah dihapus.", Toast.LENGTH_SHORT).show();
                loadReports();
            });
        });
    }

    private void writeExcel(Uri uri, LaporanHarian laporan) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ExcelReportExporter.write(this, uri, laporan);
                runOnUiThread(() -> Toast.makeText(this,
                        "Laporan Excel berhasil disimpan.", Toast.LENGTH_SHORT).show());
            } catch (Exception exception) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Gagal menyimpan file Excel.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
