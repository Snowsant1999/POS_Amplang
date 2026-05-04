package com.kelompok3.posamplang.activities.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.StokRendahAdapter;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

    // ─── Views ───────────────────────────────────────────────────────────────
    private TextView tvTanggal, tvTotalTransaksi, tvTotalPendapatan, tvTotalPengeluaran;
    private TextView tvJudulGrafik;
    private Button btnTabPendapatan, btnTabPengeluaran, btnTabPerbandingan;

    private LineChart lineChartPendapatan;
    private BarChart barChartPengeluaran;
    private BarChart barChartPerbandingan;
    private PieChart pieChartMetode;
    private RecyclerView rvStokRendah;

    // ─── State ───────────────────────────────────────────────────────────────
    private static final int BATAS_STOK_RENDAH = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupSidebar(R.id.btn_nav_dashboard);
        initViews();
        setupTabButtons();
        loadAllData();
    }

    // ─── Init Views ───────────────────────────────────────────────────────────
    private void initViews() {
        tvTanggal          = findViewById(R.id.tv_tanggal_header);
        tvTotalTransaksi   = findViewById(R.id.tv_total_transaksi);
        tvTotalPendapatan  = findViewById(R.id.tv_total_pendapatan);
        tvTotalPengeluaran = findViewById(R.id.tv_total_pengeluaran);
        tvJudulGrafik      = findViewById(R.id.tv_judul_grafik);

        btnTabPendapatan   = findViewById(R.id.btn_tab_pendapatan);
        btnTabPengeluaran  = findViewById(R.id.btn_tab_pengeluaran);
        btnTabPerbandingan = findViewById(R.id.btn_tab_perbandingan);

        lineChartPendapatan  = findViewById(R.id.lineChartPendapatan);
        barChartPengeluaran  = findViewById(R.id.barChartPengeluaran);
        barChartPerbandingan = findViewById(R.id.barChartPerbandingan);
        pieChartMetode       = findViewById(R.id.pieChartMetode);
        rvStokRendah         = findViewById(R.id.rv_stok_rendah);

        // Tampilkan tanggal hari ini
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        tvTanggal.setText(sdf.format(new Date()));
    }

    // ─── Tab Buttons ─────────────────────────────────────────────────────────
    private void setupTabButtons() {
        btnTabPendapatan.setOnClickListener(v   -> switchTab(0));
        btnTabPengeluaran.setOnClickListener(v  -> switchTab(1));
        btnTabPerbandingan.setOnClickListener(v -> switchTab(2));
    }

    private void switchTab(int tab) {
        // Reset semua tab ke style nonaktif
        int inactive = android.R.color.transparent;
        btnTabPendapatan.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
        btnTabPengeluaran.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
        btnTabPerbandingan.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
        btnTabPendapatan.setTextColor(Color.parseColor("#888888"));
        btnTabPengeluaran.setTextColor(Color.parseColor("#888888"));
        btnTabPerbandingan.setTextColor(Color.parseColor("#888888"));

        // Sembunyikan semua grafik
        lineChartPendapatan.setVisibility(View.GONE);
        barChartPengeluaran.setVisibility(View.GONE);
        barChartPerbandingan.setVisibility(View.GONE);

        switch (tab) {
            case 0: // Pendapatan
                btnTabPendapatan.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#B22222")));
                btnTabPendapatan.setTextColor(Color.WHITE);
                tvJudulGrafik.setText("Pendapatan Harian (7 Hari Terakhir)");
                lineChartPendapatan.setVisibility(View.VISIBLE);
                break;

            case 1: // Pengeluaran
                btnTabPengeluaran.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#B22222")));
                btnTabPengeluaran.setTextColor(Color.WHITE);
                tvJudulGrafik.setText("Pengeluaran Stok per Produk");
                barChartPengeluaran.setVisibility(View.VISIBLE);
                break;

            case 2: // Perbandingan
                btnTabPerbandingan.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#B22222")));
                btnTabPerbandingan.setTextColor(Color.WHITE);
                tvJudulGrafik.setText("Pendapatan vs Pengeluaran (7 Hari)");
                barChartPerbandingan.setVisibility(View.VISIBLE);
                break;
        }
    }

    // ─── Load Semua Data dari Database ──────────────────────────────────────
    private void loadAllData() {
        AppDatabase db = AppDatabase.getInstance(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            // ── Kartu Statistik ─────────────────────────────────────────────
            int totalTransaksi       = db.pembayaranDao().getTotalTransaksi();
            double totalPendapatan   = db.pembayaranDao().getTotalPendapatanAll();
            double totalPengeluaran  = hitungTotalPengeluaran(db); // dari stok_adjustment

            // ── Pendapatan 7 Hari (untuk Line Chart) ────────────────────────
            float[] pendapatan7Hari = new float[7];
            String[] labelHari = new String[7];
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdfLabel = new SimpleDateFormat("dd/MM", Locale.getDefault());
            for (int i = 6; i >= 0; i--) {
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -i);
                cal.set(Calendar.HOUR_OF_DAY, 0);  cal.set(Calendar.MINUTE, 0);  cal.set(Calendar.SECOND, 0);
                long start = cal.getTimeInMillis();
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
                long end   = cal.getTimeInMillis();
                pendapatan7Hari[6 - i] = (float) db.pembayaranDao().getTotalPendapatanPeriode(start, end);
                labelHari[6 - i]       = sdfLabel.format(new Date(start));
            }

            // ── Pengeluaran per Produk (Bar Chart Pengeluaran) ───────────────
            List<Produk> semuaProduk = db.produkDao().getAll();

            // ── Pembayaran per Metode (Pie Chart) ───────────────────────────
            int countTunai = db.pembayaranDao().countByMetode("Tunai");
            int countQris  = db.pembayaranDao().countByMetode("QRIS");

            // ── Stok Rendah ─────────────────────────────────────────────────
            List<Produk> stokRendah = db.produkDao().getStokRendah(BATAS_STOK_RENDAH);

            // Pendapatan & pengeluaran 7 hari untuk Bar Chart Perbandingan
            float[] pengeluaran7Hari = new float[7];
            Calendar cal2 = Calendar.getInstance();
            SimpleDateFormat sdfLabel2 = new SimpleDateFormat("dd/MM", Locale.getDefault());
            for (int i = 6; i >= 0; i--) {
                cal2 = Calendar.getInstance();
                cal2.add(Calendar.DAY_OF_YEAR, -i);
                cal2.set(Calendar.HOUR_OF_DAY, 0);  cal2.set(Calendar.MINUTE, 0);  cal2.set(Calendar.SECOND, 0);
                long start = cal2.getTimeInMillis();
                cal2.set(Calendar.HOUR_OF_DAY, 23); cal2.set(Calendar.MINUTE, 59); cal2.set(Calendar.SECOND, 59);
                long end   = cal2.getTimeInMillis();
                pengeluaran7Hari[6 - i] = (float) hitungPengeluaranPeriode(db, start, end);
            }

            // ── Update UI di Main Thread ─────────────────────────────────────
            final float[] p7 = pendapatan7Hari;
            final float[] e7 = pengeluaran7Hari;
            final String[] labels = labelHari;
            final int trx = totalTransaksi;
            final double pendAll = totalPendapatan;
            final double penAll  = totalPengeluaran;
            final int tunai = countTunai;
            final int qris  = countQris;
            final List<Produk> produksAll = semuaProduk;
            final List<Produk> stokList   = stokRendah;

            runOnUiThread(() -> {
                // Kartu statistik
                tvTotalTransaksi.setText(String.valueOf(trx));
                tvTotalPendapatan.setText(FormatUtils.formatRupiah(pendAll));
                tvTotalPengeluaran.setText(FormatUtils.formatRupiah(penAll));

                // Grafik
                setupLineChart(p7, labels);
                setupBarChartPengeluaran(produksAll);
                setupBarChartPerbandingan(p7, e7, labels);
                setupPieChart(tunai, qris);

                // List stok rendah
                setupStokRendah(stokList);

                // Aktifkan tab default
                switchTab(0);
            });
        });
    }

    // ─── Hitung Pengeluaran dari StokAdjustment ────────────────────────────
    private double hitungTotalPengeluaran(AppDatabase db) {
        // Pengeluaran dihitung dari harga produk * jumlah yang dikurangi karena penjualan
        // Kita gunakan pendekatan: jumlah unit terjual * harga (dari stok_adjustment tipe Penjualan)
        // Karena tidak ada tabel pengeluaran terpisah, ini mewakili HPP (Harga Pokok Penjualan)
        List<com.kelompok3.posamplang.models.StokAdjustment> adjs =
                db.stokAdjustmentDao().getAll();
        double total = 0;
        List<Produk> produkList = db.produkDao().getAll();
        for (com.kelompok3.posamplang.models.StokAdjustment adj : adjs) {
            if ("Penjualan".equals(adj.getTipe())) {
                for (Produk p : produkList) {
                    if (p.getId_produk() == adj.getId_produk()) {
                        total += p.getHarga_produk() * adj.getJumlah_produk();
                        break;
                    }
                }
            }
        }
        return total;
    }

    private double hitungPengeluaranPeriode(AppDatabase db, long start, long end) {
        List<com.kelompok3.posamplang.models.StokAdjustment> adjs =
                db.stokAdjustmentDao().getAll();
        List<Produk> produkList = db.produkDao().getAll();
        double total = 0;
        for (com.kelompok3.posamplang.models.StokAdjustment adj : adjs) {
            if ("Penjualan".equals(adj.getTipe()) && adj.getTanggal() >= start && adj.getTanggal() <= end) {
                for (Produk p : produkList) {
                    if (p.getId_produk() == adj.getId_produk()) {
                        total += p.getHarga_produk() * adj.getJumlah_produk();
                        break;
                    }
                }
            }
        }
        return total;
    }

    // ─── Setup Grafik ─────────────────────────────────────────────────────────

    private void setupLineChart(float[] data, String[] labels) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            entries.add(new Entry(i, data[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Pendapatan (Rp)");
        dataSet.setColor(Color.parseColor("#B22222"));
        dataSet.setCircleColor(Color.parseColor("#B22222"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#B22222"));
        dataSet.setFillAlpha(30);

        lineChartPendapatan.setData(new LineData(dataSet));
        lineChartPendapatan.getDescription().setEnabled(false);
        lineChartPendapatan.getLegend().setEnabled(false);
        lineChartPendapatan.getAxisRight().setEnabled(false);
        lineChartPendapatan.getAxisLeft().setTextColor(Color.parseColor("#888888"));
        lineChartPendapatan.getAxisLeft().setGridColor(Color.parseColor("#F0F0F0"));

        XAxis xAxis = lineChartPendapatan.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#888888"));

        lineChartPendapatan.setExtraBottomOffset(8f);
        lineChartPendapatan.animateX(800);
        lineChartPendapatan.invalidate();
    }

    private void setupBarChartPengeluaran(List<Produk> produkList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> produkLabels = new ArrayList<>();
        int max = Math.min(produkList.size(), 8); // Tampilkan maks 8 produk
        for (int i = 0; i < max; i++) {
            Produk p = produkList.get(i);
            entries.add(new BarEntry(i, p.getStok_tersedia()));
            String nama = p.getNama_produk();
            produkLabels.add(nama.length() > 10 ? nama.substring(0, 10) + "…" : nama);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Sisa Stok (unit)");
        dataSet.setColor(Color.parseColor("#B22222"));
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(true);

        barChartPengeluaran.setData(new BarData(dataSet));
        barChartPengeluaran.getDescription().setEnabled(false);
        barChartPengeluaran.getLegend().setEnabled(false);
        barChartPengeluaran.getAxisRight().setEnabled(false);
        barChartPengeluaran.getAxisLeft().setGridColor(Color.parseColor("#F0F0F0"));

        XAxis xAxis = barChartPengeluaran.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(produkLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(9f);

        barChartPengeluaran.setExtraBottomOffset(8f);
        barChartPengeluaran.animateY(800);
        barChartPengeluaran.invalidate();
    }

    private void setupBarChartPerbandingan(float[] pendapatan, float[] pengeluaran, String[] labels) {
        ArrayList<BarEntry> entPend = new ArrayList<>();
        ArrayList<BarEntry> entPenge = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entPend.add(new BarEntry(i, pendapatan[i]));
            entPenge.add(new BarEntry(i, pengeluaran[i]));
        }

        BarDataSet dsPend = new BarDataSet(entPend, "Pendapatan");
        dsPend.setColor(Color.parseColor("#1565C0"));
        dsPend.setDrawValues(false);

        BarDataSet dsPenge = new BarDataSet(entPenge, "Pengeluaran");
        dsPenge.setColor(Color.parseColor("#C62828"));
        dsPenge.setDrawValues(false);

        BarData barData = new BarData(dsPend, dsPenge);
        float groupSpace = 0.3f, barSpace = 0.05f, barWidth = 0.3f;
        barData.setBarWidth(barWidth);

        barChartPerbandingan.setData(barData);
        barChartPerbandingan.groupBars(0, groupSpace, barSpace);
        barChartPerbandingan.getDescription().setEnabled(false);
        barChartPerbandingan.getAxisRight().setEnabled(false);
        barChartPerbandingan.getAxisLeft().setGridColor(Color.parseColor("#F0F0F0"));

        XAxis xAxis = barChartPerbandingan.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(7f);

        barChartPerbandingan.setVisibleXRangeMaximum(7);
        barChartPerbandingan.setExtraBottomOffset(8f);
        barChartPerbandingan.animateY(800);
        barChartPerbandingan.invalidate();
    }

    private void setupPieChart(int tunai, int qris) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (tunai > 0) entries.add(new PieEntry(tunai, "Tunai"));
        if (qris  > 0) entries.add(new PieEntry(qris,  "QRIS"));
        if (entries.isEmpty()) entries.add(new PieEntry(1f, "Belum ada"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                Color.parseColor("#F4C430"),
                Color.parseColor("#1565C0"),
                Color.parseColor("#CCCCCC")
        });
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(3f);

        pieChartMetode.setData(new PieData(dataSet));
        pieChartMetode.getDescription().setEnabled(false);
        pieChartMetode.setCenterText("Metode");
        pieChartMetode.setCenterTextSize(12f);
        pieChartMetode.setHoleRadius(52f);
        pieChartMetode.setTransparentCircleRadius(57f);
        pieChartMetode.getLegend().setEnabled(true);
        pieChartMetode.animateY(900);
        pieChartMetode.invalidate();
    }

    private void setupStokRendah(List<Produk> list) {
        rvStokRendah.setLayoutManager(new LinearLayoutManager(this));
        rvStokRendah.setAdapter(new StokRendahAdapter(list));
        rvStokRendah.setNestedScrollingEnabled(false);
    }
}