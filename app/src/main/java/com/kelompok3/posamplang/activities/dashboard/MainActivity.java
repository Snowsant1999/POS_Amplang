package com.kelompok3.posamplang.activities.dashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;

import java.util.ArrayList;

// Halaman Dashboard utama aplikasi
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupSidebar(R.id.btn_nav_dashboard);
        setupCharts();
    }

    private void setupCharts() {
        setupLineChart();
        setupPieChart();
        setupBarChart();
    }

    private void setupLineChart() {
        LineChart lineChart = findViewById(R.id.lineChartPendapatan);
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 150000));
        entries.add(new Entry(2, 220000));
        entries.add(new Entry(3, 180000));
        entries.add(new Entry(4, 250000));
        entries.add(new Entry(5, 300000));

        LineDataSet dataSet = new LineDataSet(entries, "Pendapatan Harian (Rp)");
        dataSet.setColor(Color.parseColor("#9C27B0"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.parseColor("#9C27B0"));
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private void setupPieChart() {
        PieChart pieChart = findViewById(R.id.pieChartMetode);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(65f, "QRIS"));
        entries.add(new PieEntry(35f, "Tunai"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Pembayaran");
        pieChart.getLegend().setEnabled(true);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        BarChart barChart = findViewById(R.id.barChartPerbandingan);
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 300000f)); // Pendapatan
        entries.add(new BarEntry(2f, 150000f)); // Pengeluaran

        BarDataSet dataSet = new BarDataSet(entries, "1: Pendapatan, 2: Pengeluaran");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}