package com.kelompok3.posamplang.activities.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;
import com.kelompok3.posamplang.adapters.StrukAdapter;
import com.kelompok3.posamplang.models.DetailPesanan;
import com.kelompok3.posamplang.models.Produk;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KasirActivity extends AppCompatActivity {

    private RecyclerView rvStruk;
    private StrukAdapter adapter;
    private List<DetailPesanan> keranjangList = new ArrayList<>();
    
    private TextView tvTotalItems, tvSubtotal, tvTotalHarga;
    private Button btnBayar;
    private NumberFormat formatter;
    private double currentTotal = 0;

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
        adapter = new StrukAdapter(keranjangList, new StrukAdapter.OnItemQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int position, int newQty) {
                keranjangList.get(position).setJumlah_produk(newQty);
                adapter.notifyItemChanged(position);
                updateSummary();
            }

            @Override
            public void onItemRemoved(int position) {
                keranjangList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, keranjangList.size());
                updateSummary();
            }
        });
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

        findViewById(R.id.btn_nav_stok).setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.stok.StokActivity"));
        findViewById(R.id.btn_nav_supplier).setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.supplier.SupplierActivity"));
        findViewById(R.id.btn_nav_laporan).setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.laporan.LaporanActivity"));
        findViewById(R.id.btn_nav_pengaturan).setOnClickListener(v -> navigateTo("com.kelompok3.posamplang.activities.pengaturan.PengaturanActivity"));

        findViewById(R.id.btn_nav_logout).setOnClickListener(v -> finish());

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
                showDialogPilihMetode();
            }
        });
    }

    private void navigateTo(String className) {
        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(this, targetClass);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "Halaman belum tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogPilihMetode() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pilih_metode);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> dialog.dismiss());
        
        dialog.findViewById(R.id.btn_cash).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogBayarTunai();
        });

        dialog.findViewById(R.id.btn_qris).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogQRIS();
        });

        dialog.show();
    }

    private void showDialogBayarTunai() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_bayar_tunai);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTotal = dialog.findViewById(R.id.tv_dialog_total);
        EditText etBayar = dialog.findViewById(R.id.et_bayar_tunai);
        TextView tvKembalian = dialog.findViewById(R.id.tv_dialog_kembalian);

        tvTotal.setText(formatCurrency(currentTotal));

        etBayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        double bayar = Double.parseDouble(s.toString());
                        double kembalian = bayar - currentTotal;
                        tvKembalian.setText(formatCurrency(Math.max(0, kembalian)));
                    } catch (NumberFormatException e) {
                        tvKembalian.setText(formatCurrency(0));
                    }
                } else {
                    tvKembalian.setText(formatCurrency(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.findViewById(R.id.btn_proses).setOnClickListener(v -> {
            String bayarStr = etBayar.getText().toString();
            if (bayarStr.isEmpty()) {
                Toast.makeText(this, "Masukkan jumlah uang!", Toast.LENGTH_SHORT).show();
                return;
            }
            double bayar = Double.parseDouble(bayarStr);
            if (bayar < currentTotal) {
                Toast.makeText(this, "Uang tidak cukup!", Toast.LENGTH_SHORT).show();
                showDialogGagal();
                dialog.dismiss();
            } else {
                double kembalian = bayar - currentTotal;
                dialog.dismiss();
                showDialogBerhasil(bayar, kembalian);
            }
        });

        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogPilihMetode();
        });

        dialog.show();
    }

    private void showDialogQRIS() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qris);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.btn_selesai).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogBerhasil(currentTotal, 0);
        });

        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogPilihMetode();
        });

        dialog.show();
    }

    private void showDialogBerhasil(double bayar, double kembalian) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_berhasil);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvMsg = dialog.findViewById(R.id.tv_dialog_message);
        String message = getString(R.string.pembayaran_berhasil_msg, formatCurrency(currentTotal));
        tvMsg.setText(message);

        dialog.findViewById(R.id.btn_cetak_struk).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogStrukFinal(bayar, kembalian);
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> {
            resetKasir();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDialogGagal() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gagal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.btn_coba_lagi).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogPilihMetode();
        });

        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDialogStrukFinal(double bayar, double kembalian) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_struk_final);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvDateTime = dialog.findViewById(R.id.tv_struk_datetime);
        TextView tvTotal = dialog.findViewById(R.id.tv_struk_total);
        TextView tvPayment = dialog.findViewById(R.id.tv_struk_payment);
        TextView tvChange = dialog.findViewById(R.id.tv_struk_change);
        RecyclerView rvItems = dialog.findViewById(R.id.rv_struk_items);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvDateTime.setText("Date time : " + sdf.format(new Date()));
        
        tvTotal.setText(formatCurrency(currentTotal));
        tvPayment.setText(formatCurrency(bayar));
        tvChange.setText(formatCurrency(kembalian));

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new StrukAdapter(new ArrayList<>(keranjangList)));

        dialog.findViewById(R.id.btn_cetak).setOnClickListener(v -> {
            Toast.makeText(this, "Mencetak struk...", Toast.LENGTH_SHORT).show();
            resetKasir();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.btn_batal).setOnClickListener(v -> {
            resetKasir();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void resetKasir() {
        keranjangList.clear();
        adapter.notifyDataSetChanged();
        updateSummary();
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

        currentTotal = subtotal;
        tvTotalItems.setText(getString(R.string.total_items, totalQty));
        tvSubtotal.setText(formatPlainNumber(subtotal));
        tvTotalHarga.setText(formatPlainNumber(subtotal));
    }

    private String formatPlainNumber(double amount) {
        return String.format(new Locale("id", "ID"), "%,.0f", amount).replace(".", ",");
    }

    private String formatCurrency(double amount) {
        return "Rp" + formatPlainNumber(amount);
    }
}