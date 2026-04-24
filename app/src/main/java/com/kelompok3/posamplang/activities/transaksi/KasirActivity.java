package com.kelompok3.posamplang.activities.transaksi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.auth.LoginActivity;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;
import com.kelompok3.posamplang.activities.produk.ProdukListActivity;
import com.kelompok3.posamplang.adapters.StrukAdapter;
import com.kelompok3.posamplang.models.DetailPesanan;
import com.kelompok3.posamplang.models.Produk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.kelompok3.posamplang.parent.BaseActivity;

/**
 * KasirActivity — Halaman transaksi / kasir POS Amplang.
 *
 * Fitur:
 * - Menampilkan produk yang tersedia
 * - Mengelola keranjang belanja (tambah, kurangi, hapus item)
 * - Memproses pembayaran tunai dan QRIS
 * - Menampilkan struk transaksi
 *
 * TODO: Muat produk dari ProdukDao via Room Database (saat ini masih dummy).
 * TODO: Simpan transaksi ke PesananDao dan DetailPesananDao setelah pembayaran sukses.
 */
public class KasirActivity extends BaseActivity {

    // -------------------------------------------------------------------------
    // Views — Keranjang & Ringkasan
    // -------------------------------------------------------------------------

    private RecyclerView rvStruk;
    private TextView tvTotalItems;
    private TextView tvSubtotal;
    private TextView tvTotalHarga;
    private Button btnBayar;

    // -------------------------------------------------------------------------
    // Data & Adapter
    // -------------------------------------------------------------------------

    private StrukAdapter adapter;
    private List<DetailPesanan> keranjangList = new ArrayList<>();
    private double currentTotal = 0;

    // -------------------------------------------------------------------------
    // Data Produk Sementara (Dummy)
    // TODO: Ganti dengan query dari ProdukDao setelah Room Database diimplementasikan
    //       Format: new Produk(id, id_kategori, id_merek, id_supplier, nama, unit, harga, stok)
    // -------------------------------------------------------------------------

    private Produk produkGabinSusu;
    private Produk produkGabinKeju;
    private Produk produkKukuMacan;
    private Produk produkIkanPipih;

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kasir);

        setupWindowInsets();
        setupSidebar(R.id.btn_nav_kasir);
        initViews();
        initDummyProducts();
        setupRecyclerView();
        setupProductClickListeners();
        setupPaymentButton();
    }

    // -------------------------------------------------------------------------
    // Inisialisasi
    // -------------------------------------------------------------------------

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

    private void initViews() {
        rvStruk       = findViewById(R.id.rv_struk);
        tvTotalItems  = findViewById(R.id.tv_total_items);
        tvSubtotal    = findViewById(R.id.tv_subtotal);
        tvTotalHarga  = findViewById(R.id.tv_total_harga);
        btnBayar      = findViewById(R.id.btn_bayar);
    }

    /**
     * Menginisialisasi data produk sementara.
     * TODO: Hapus metode ini dan ganti dengan loadProdukFromDatabase() saat DB siap.
     */
    private void initDummyProducts() {
        produkGabinSusu  = new Produk(101, 1, 1, 1, "Gabin Susu",       "Pcs", 20000, 50);
        produkGabinKeju  = new Produk(102, 1, 1, 1, "Gabin Keju",       "Pcs", 25000, 30);
        produkKukuMacan  = new Produk(103, 2, 2, 1, "Amplang Kuku Macan", "Pcs", 35000, 100);
        produkIkanPipih  = new Produk(104, 2, 2, 1, "Amplang Ikan Pipih", "Pcs", 40000, 20);
    }

    /** Menyiapkan RecyclerView keranjang belanja beserta callback-nya. */
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

    /** Mendaftarkan listener klik untuk setiap card produk. */
    private void setupProductClickListeners() {
        findViewById(R.id.card_gabin_susu).setOnClickListener(v -> tambahKeKeranjang(produkGabinSusu));
        findViewById(R.id.card_gabin_keju).setOnClickListener(v -> tambahKeKeranjang(produkGabinKeju));
        findViewById(R.id.card_kuku_macan).setOnClickListener(v -> tambahKeKeranjang(produkKukuMacan));
        findViewById(R.id.card_ikan_pipih).setOnClickListener(v -> tambahKeKeranjang(produkIkanPipih));
    }

    /** Mendaftarkan listener untuk tombol bayar. */
    private void setupPaymentButton() {
        btnBayar.setOnClickListener(v -> {
            if (keranjangList.isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
            } else {
                showDialogPilihMetode();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Logic Keranjang
    // -------------------------------------------------------------------------

    /**
     * Menambahkan produk ke keranjang. Jika produk sudah ada, jumlahnya ditambah 1.
     *
     * @param produk Produk yang akan ditambahkan ke keranjang.
     */
    private void tambahKeKeranjang(Produk produk) {
        for (DetailPesanan detail : keranjangList) {
            if (detail.getId_produk() == produk.getId_produk()) {
                detail.tambahJumlah(1);
                adapter.notifyDataSetChanged();
                updateSummary();
                return;
            }
        }

        // Produk belum ada di keranjang, tambahkan sebagai item baru
        keranjangList.add(new DetailPesanan(produk, 1));
        adapter.notifyItemInserted(keranjangList.size() - 1);
        updateSummary();
    }

    /** Memperbarui tampilan ringkasan total item dan harga. */
    private void updateSummary() {
        int    totalQty  = 0;
        double subtotal  = 0;

        for (DetailPesanan detail : keranjangList) {
            totalQty += detail.getJumlah_produk();
            subtotal += detail.getTotal_harga();
        }

        currentTotal = subtotal;
        tvTotalItems.setText(getString(R.string.total_items, totalQty));
        tvSubtotal.setText(formatRupiah(subtotal));
        tvTotalHarga.setText(formatRupiah(subtotal));
    }

    /** Mengosongkan keranjang dan mereset tampilan ringkasan. */
    private void resetKasir() {
        keranjangList.clear();
        adapter.notifyDataSetChanged();
        updateSummary();
    }

    // -------------------------------------------------------------------------
    // Dialog Pembayaran
    // -------------------------------------------------------------------------

    /** Menampilkan dialog pemilihan metode pembayaran (Tunai / QRIS). */
    private void showDialogPilihMetode() {
        Dialog dialog = createDialog(R.layout.dialog_pilih_metode);

        dialog.findViewById(R.id.btn_close).setOnClickListener(v   -> dialog.dismiss());
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

    /** Menampilkan dialog pembayaran tunai dengan kalkulasi kembalian real-time. */
    private void showDialogBayarTunai() {
        Dialog dialog = createDialog(R.layout.dialog_bayar_tunai);

        TextView tvTotal      = dialog.findViewById(R.id.tv_dialog_total);
        EditText etBayar      = dialog.findViewById(R.id.et_bayar_tunai);
        TextView tvKembalian  = dialog.findViewById(R.id.tv_dialog_kembalian);

        tvTotal.setText(formatRupiah(currentTotal));

        // Hitung kembalian secara real-time saat pengguna mengetik jumlah uang
        etBayar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        double bayar     = Double.parseDouble(s.toString());
                        double kembalian = Math.max(0, bayar - currentTotal);
                        tvKembalian.setText(formatRupiah(kembalian));
                    } catch (NumberFormatException e) {
                        tvKembalian.setText(formatRupiah(0));
                    }
                } else {
                    tvKembalian.setText(formatRupiah(0));
                }
            }
        });

        dialog.findViewById(R.id.btn_proses).setOnClickListener(v -> {
            String bayarStr = etBayar.getText().toString().trim();

            if (bayarStr.isEmpty()) {
                Toast.makeText(this, "Masukkan jumlah uang!", Toast.LENGTH_SHORT).show();
                return;
            }

            double bayar = Double.parseDouble(bayarStr);
            if (bayar < currentTotal) {
                Toast.makeText(this, "Uang tidak cukup!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showDialogGagal();
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

    /** Menampilkan dialog pembayaran QRIS. */
    private void showDialogQRIS() {
        Dialog dialog = createDialog(R.layout.dialog_qris);

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

    /** Menampilkan dialog konfirmasi pembayaran berhasil. */
    private void showDialogBerhasil(double bayar, double kembalian) {
        Dialog dialog = createDialog(R.layout.dialog_berhasil);

        TextView tvMsg = dialog.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(getString(R.string.pembayaran_berhasil_msg, formatRupiah(currentTotal)));

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

    /** Menampilkan dialog pemberitahuan pembayaran gagal. */
    private void showDialogGagal() {
        Dialog dialog = createDialog(R.layout.dialog_gagal);

        dialog.findViewById(R.id.btn_coba_lagi).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogPilihMetode();
        });
        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Menampilkan dialog struk transaksi lengkap.
     *
     * @param bayar     Jumlah uang yang dibayarkan pelanggan.
     * @param kembalian Jumlah uang kembalian untuk pelanggan.
     */
    private void showDialogStrukFinal(double bayar, double kembalian) {
        Dialog dialog = createDialog(R.layout.dialog_struk_final);

        TextView     tvDateTime = dialog.findViewById(R.id.tv_struk_datetime);
        TextView     tvTotal    = dialog.findViewById(R.id.tv_struk_total);
        TextView     tvPayment  = dialog.findViewById(R.id.tv_struk_payment);
        TextView     tvChange   = dialog.findViewById(R.id.tv_struk_change);
        RecyclerView rvItems    = dialog.findViewById(R.id.rv_struk_items);

        // Tampilkan waktu transaksi
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvDateTime.setText("Date time : " + sdf.format(new Date()));

        tvTotal.setText(formatRupiah(currentTotal));
        tvPayment.setText(formatRupiah(bayar));
        tvChange.setText(formatRupiah(kembalian));

        // Tampilkan item dalam struk (mode read-only, tanpa tombol +/-)
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new StrukAdapter(new ArrayList<>(keranjangList)));

        dialog.findViewById(R.id.btn_cetak).setOnClickListener(v -> {
            // TODO: Implementasikan koneksi ke printer Bluetooth/WiFi
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

    /**
     * Menampilkan pesan bahwa fitur belum tersedia.
     *
     * @param moduleName Nama modul yang dituju.
     */
    private void showComingSoon(String moduleName) {
        Toast.makeText(this, "Modul " + moduleName + " belum tersedia", Toast.LENGTH_SHORT).show();
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Membuat Dialog dengan layout tertentu dan latar transparan.
     *
     * @param layoutResId ID resource layout untuk dialog.
     * @return Dialog yang sudah dikonfigurasi.
     */
    private Dialog createDialog(int layoutResId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutResId);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    /**
     * Memformat angka menjadi format Rupiah Indonesia.
     * Contoh: 25000 → "Rp25.000"
     *
     * @param amount Jumlah dalam bentuk double.
     * @return String dalam format Rupiah.
     */
    private String formatRupiah(double amount) {
        String formatted = String.format(new Locale("id", "ID"), "%,.0f", amount)
                .replace(".", ",");
        return "Rp" + formatted;
    }
}
