package com.kelompok3.posamplang.activities.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;
import com.kelompok3.posamplang.adapters.StrukAdapter;
import com.kelompok3.posamplang.models.DetailPesanan;
import com.kelompok3.posamplang.models.Produk;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KasirActivity extends AppCompatActivity {

    private RecyclerView rvStruk;
    private StrukAdapter adapter;
    private List<DetailPesanan> keranjangList = new ArrayList<>();
    
    private TextView tvTotalItems, tvSubtotal, tvTotalHarga;
    private Button btnBayar;
    private NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kasir);

        // Handle Window Insets
        android.view.View mainView = findViewById(R.id.main);
        if (mainView != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
        
        formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }

    private void initViews() {
        rvStruk = findViewById(R.id.rv_struk);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotalHarga = findViewById(R.id.tv_total_harga);
        btnBayar = findViewById(R.id.btn_bayar);
    }

    private void setupRecyclerView() {
        adapter = new StrukAdapter(keranjangList);
        rvStruk.setLayoutManager(new LinearLayoutManager(this));
        rvStruk.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Navigasi Sidebar
        findViewById(R.id.btn_nav_dashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        findViewById(R.id.btn_nav_logout).setOnClickListener(v -> {
            finish();
        });

        // Dummy Data Produk
        Produk p1 = new Produk(101, "Gabin Susu", 20000, 50);
        Produk p2 = new Produk(102, "Gabin Keju", 25000, 30);
        Produk p3 = new Produk(103, "Amplang Kuku Macan", 35000, 100);
        Produk p4 = new Produk(104, "Amplang Ikan Pipih", 40000, 20);

        // Produk Click
        findViewById(R.id.card_gabin_susu).setOnClickListener(v -> tambahKeStruk(p1));
        findViewById(R.id.card_gabin_keju).setOnClickListener(v -> tambahKeStruk(p2));
        findViewById(R.id.card_kuku_macan).setOnClickListener(v -> tambahKeStruk(p3));
        findViewById(R.id.card_ikan_pipih).setOnClickListener(v -> tambahKeStruk(p4));

        btnBayar.setOnClickListener(v -> {
            if (keranjangList.isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Proses Pembayaran Berhasil!", Toast.LENGTH_LONG).show();
                keranjangList.clear();
                adapter.notifyDataSetChanged();
                updateSummary();
            }
        });
    }

    private void tambahKeStruk(Produk produk) {
        boolean ada = false;
        for (DetailPesanan detail : keranjangList) {
            if (detail.getId_produk() == produk.getId_produk()) {
                detail.tambahJumlah(1);
                ada = true;
                break;
            }
        }

        if (!ada) {
            keranjangList.add(new DetailPesanan(produk, 1));
        }

        adapter.notifyDataSetChanged();
        updateSummary();
    }

    private void updateSummary() {
        int totalQty = 0;
        double subtotal = 0;

        for (DetailPesanan detail : keranjangList) {
            totalQty += detail.getJumlah_produk();
            subtotal += detail.getTotal_harga();
        }

        tvTotalItems.setText(getString(R.string.total_items, totalQty));
        tvSubtotal.setText(formatPlainNumber(subtotal));
        tvTotalHarga.setText(formatPlainNumber(subtotal));
    }

    private String formatPlainNumber(double amount) {
        return String.format(new Locale("id", "ID"), "%,.0f", amount).replace(".", ",");
    }
}