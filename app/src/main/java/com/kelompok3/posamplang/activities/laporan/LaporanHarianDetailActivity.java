package com.kelompok3.posamplang.activities.laporan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.LaporanHarian;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.ExcelReportExporter;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class LaporanHarianDetailActivity extends BaseActivity {

    public static final String EXTRA_LAPORAN_ID = "LAPORAN_ID";

    private AppDatabase database;
    private LaporanHarian displayedReport;
    private MaterialButton btnSimpan;

    private final ActivityResultLauncher<String> excelLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), uri -> {
                if (uri != null && displayedReport != null) {
                    writeExcel(uri, displayedReport);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_laporan);
        setupSidebar(R.id.btn_nav_laporan);
        database = AppDatabase.getInstance(this);

        btnSimpan = findViewById(R.id.btnSimpanLaporan);
        findViewById(R.id.btnLihatLaporan).setOnClickListener(v -> {
            startActivity(new Intent(this, LaporanHarianActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.btnPrint).setOnClickListener(v -> exportDisplayedReport());
        btnSimpan.setOnClickListener(v -> saveCurrentReport());

        int reportId = getIntent().getIntExtra(EXTRA_LAPORAN_ID, -1);
        if (reportId == -1) {
            loadTodayReport();
        } else {
            loadSavedReport(reportId);
        }
    }

    private void loadTodayReport() {
        long start = startOfDay(System.currentTimeMillis());
        long end = start + 86_400_000L - 1;
        Executors.newSingleThreadExecutor().execute(() -> {
            double pemasukan = database.pembayaranDao().getTotalLunasPeriode(start, end);
            double pengeluaran = database.detailStokRequestDao().getTotalPembelianSelesaiPeriode(start, end);
            double tunai = database.pembayaranDao().getTotalLunasPeriodeByMetode("Tunai", start, end);
            double online = Math.max(0, pemasukan - tunai);
            double tertunda = database.pembayaranDao().getTotalTertundaPeriode(start, end);
            int transaksi = database.pembayaranDao().countLunasPeriode(start, end);
            LaporanHarian report = new LaporanHarian(start, System.currentTimeMillis(),
                    pemasukan, pengeluaran, pemasukan - pengeluaran, tunai, online,
                    tertunda, transaksi);
            runOnUiThread(() -> {
                displayedReport = report;
                bindReport(false);
            });
        });
    }

    private void loadSavedReport(int reportId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            LaporanHarian report = database.laporanHarianDao().getById(reportId);
            runOnUiThread(() -> {
                if (report == null) {
                    Toast.makeText(this, "Laporan tersimpan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                displayedReport = report;
                bindReport(true);
            });
        });
    }

    private void bindReport(boolean saved) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"));
        ((TextView) findViewById(R.id.tvJudulLaporan)).setText(saved
                ? "LAPORAN KEUANGAN TERSIMPAN" : "LAPORAN KEUANGAN HARIAN");
        ((TextView) findViewById(R.id.tvTanggalLaporan)).setText((saved ? "Tanggal laporan: " : "Tanggal saat ini: ")
                + dateFormat.format(new Date(displayedReport.getTanggal_laporan())));
        ((TextView) findViewById(R.id.tvPemasukan)).setText(FormatUtils.formatRupiah(displayedReport.getPemasukan()));
        ((TextView) findViewById(R.id.tvPengeluaran)).setText(FormatUtils.formatRupiah(displayedReport.getPengeluaran()));
        ((TextView) findViewById(R.id.tvSaldoBersih)).setText(FormatUtils.formatRupiah(displayedReport.getSaldo_bersih()));
        ((TextView) findViewById(R.id.tvMetodeTunai)).setText(FormatUtils.formatRupiah(displayedReport.getPembayaran_tunai()));
        ((TextView) findViewById(R.id.tvMetodeOnline)).setText(FormatUtils.formatRupiah(displayedReport.getPembayaran_online()));
        ((TextView) findViewById(R.id.tvMetodeTertunda)).setText(FormatUtils.formatRupiah(displayedReport.getPembayaran_tertunda()));
        ((TextView) findViewById(R.id.tvJumlahTransaksi))
                .setText(displayedReport.getJumlah_transaksi() + " transaksi");
        ((TextView) findViewById(R.id.tvPengeluaranSupplier))
                .setText(FormatUtils.formatRupiah(displayedReport.getPengeluaran()));
        ((TextView) findViewById(R.id.tvOperasionalPemasukan))
                .setText(FormatUtils.formatRupiah(displayedReport.getPemasukan()));
        ((TextView) findViewById(R.id.tvOperasionalPengeluaran))
                .setText(FormatUtils.formatRupiah(displayedReport.getPengeluaran()));
        ((TextView) findViewById(R.id.tvOperasionalSaldo))
                .setText(FormatUtils.formatRupiah(displayedReport.getSaldo_bersih()));
        btnSimpan.setVisibility(saved ? View.GONE : View.VISIBLE);
    }

    private void saveCurrentReport() {
        if (displayedReport == null) {
            Toast.makeText(this, "Data laporan belum tersedia.", Toast.LENGTH_SHORT).show();
            return;
        }
        displayedReport.setDisimpan_pada(System.currentTimeMillis());
        Executors.newSingleThreadExecutor().execute(() -> {
            database.laporanHarianDao().insertOrReplace(displayedReport);
            runOnUiThread(() -> Toast.makeText(this,
                    "Laporan harian berhasil disimpan.", Toast.LENGTH_SHORT).show());
        });
    }

    private void exportDisplayedReport() {
        if (displayedReport == null) {
            Toast.makeText(this, "Data laporan belum tersedia.", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = new SimpleDateFormat("yyyyMMdd", Locale.US)
                .format(new Date(displayedReport.getTanggal_laporan()));
        excelLauncher.launch("Laporan_Keuangan_" + date + ".xlsx");
    }

    private void writeExcel(Uri uri, LaporanHarian report) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ExcelReportExporter.write(this, uri, report);
                runOnUiThread(() -> Toast.makeText(this,
                        "Laporan Excel berhasil disimpan.", Toast.LENGTH_SHORT).show());
            } catch (Exception exception) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Gagal menyimpan file Excel.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private long startOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
