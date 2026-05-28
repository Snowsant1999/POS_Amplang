package com.kelompok3.posamplang.activities.transaksi;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.MenuKasirAdapter;
import com.kelompok3.posamplang.adapters.StrukAdapter;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.DetailPesanan;
import com.kelompok3.posamplang.models.PembayaranPesanan;
import com.kelompok3.posamplang.models.Pesanan;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.models.StokAdjustment;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.FormatUtils;
import com.kelompok3.posamplang.utils.StoreSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// Halaman untuk mengelola transaksi pembayaran
public class KasirActivity extends BaseActivity {

    // ─── Komponen UI ───────────────────────────────────────────────────────────
    private RecyclerView rvStruk;
    private RecyclerView rvMenuProduk;
    private TextView tvTotalItems;
    private TextView tvSubtotal;
    private TextView tvTotalHarga;
    private Button btnBayar;

    // ─── Data & Adapter ────────────────────────────────────────────────────────
    private StrukAdapter adapter;
    private MenuKasirAdapter menuAdapter;
    private List<DetailPesanan> keranjangList = new ArrayList<>();
    private List<Produk> menuProdukList = new ArrayList<>();
    private double currentTotal = 0;

    // ID user yang sedang login (default 1 = admin; nanti bisa diambil dari SharedPreferences)
    private int currentUserId = 1;

    // Nomor urut pesanan dalam satu sesi (untuk generate nomor otomatis)
    private static final AtomicInteger nomorUrut = new AtomicInteger(1);

    // ─── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kasir);

        setupSidebar(R.id.btn_nav_kasir);
        initViews();
        setupMenuRecyclerView();
        setupStrukRecyclerView();
        setupBayarButton();
        loadProductsFromDb();
    }

    // ─── Inisialisasi View ─────────────────────────────────────────────────────
    private void initViews() {
        rvStruk      = findViewById(R.id.rv_struk);
        rvMenuProduk = findViewById(R.id.rv_menu_produk);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tvSubtotal   = findViewById(R.id.tv_subtotal);
        tvTotalHarga = findViewById(R.id.tv_total_harga);
        btnBayar     = findViewById(R.id.btn_bayar);
    }

    // ─── Load Produk dari Database ─────────────────────────────────────────────
    private void loadProductsFromDb() {
        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Produk> produks = db.produkDao().getAktif();
            runOnUiThread(() -> {
                menuProdukList.clear();
                menuProdukList.addAll(produks);
                if (menuAdapter != null) menuAdapter.notifyDataSetChanged();
            });
        });
    }

    // ─── Setup RecyclerView Menu Produk (Grid 4 Kolom) ────────────────────────
    private void setupMenuRecyclerView() {
        rvMenuProduk.setLayoutManager(new GridLayoutManager(this, 4));
        menuAdapter = new MenuKasirAdapter(menuProdukList, this::showDialogKonfirmasiStok);
        rvMenuProduk.setAdapter(menuAdapter);
    }

    // ─── Setup RecyclerView Keranjang / Struk ─────────────────────────────────
    private void setupStrukRecyclerView() {
        adapter = new StrukAdapter(keranjangList, new StrukAdapter.OnItemQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int position, int newQty) {
                DetailPesanan item = keranjangList.get(position);
                // Cek stok di memori (list produk menu)
                Produk produk = findProdukById(item.getId_produk());
                if (produk != null && newQty > produk.getStok_tersedia()) {
                    Toast.makeText(KasirActivity.this,
                            "Stok tersedia hanya " + produk.getStok_tersedia(), Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                    return;
                }
                item.setJumlah_produk(newQty);
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

    // ─── Tombol Bayar ─────────────────────────────────────────────────────────
    private void setupBayarButton() {
        btnBayar.setOnClickListener(v -> {
            if (keranjangList.isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
            } else {
                showDialogPilihMetode();
            }
        });
    }

    // ─── Dialog Konfirmasi Stok (saat produk diklik) ───────────────────────────
    private void showDialogKonfirmasiStok(Produk produk) {
        if (produk.getStok_tersedia() == 0) {
            Toast.makeText(this, "Stok produk ini habis!", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = createDialog(R.layout.dialog_konfirmasi_stok);
        TextView tvNama  = dialog.findViewById(R.id.tv_produk_nama);
        TextView tvHarga = dialog.findViewById(R.id.tv_produk_harga);
        TextView tvStok  = dialog.findViewById(R.id.tv_stok_tersisa);
        EditText etQty   = dialog.findViewById(R.id.et_qty);

        // Hitung sisa stok yang belum masuk keranjang
        int sudahDiKeranjang = getQtyDiKeranjang(produk.getId_produk());
        int sisaStok = produk.getStok_tersedia() - sudahDiKeranjang;

        tvNama.setText(produk.getNama_produk());
        tvHarga.setText(formatRupiah(produk.getHarga_produk()));
        tvStok.setText(String.valueOf(sisaStok));
        etQty.setText("1");

        // Tombol Plus
        dialog.findViewById(R.id.btn_plus).setOnClickListener(v -> {
            try {
                int cur = Integer.parseInt(etQty.getText().toString());
                if (cur < sisaStok) {
                    etQty.setText(String.valueOf(cur + 1));
                } else {
                    Toast.makeText(this, "Maksimal stok tersedia: " + sisaStok, Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) { etQty.setText("1"); }
        });

        // Tombol Minus
        dialog.findViewById(R.id.btn_minus).setOnClickListener(v -> {
            try {
                int cur = Integer.parseInt(etQty.getText().toString());
                if (cur > 1) etQty.setText(String.valueOf(cur - 1));
            } catch (NumberFormatException e) { etQty.setText("1"); }
        });

        // Konfirmasi
        dialog.findViewById(R.id.btn_konfirmasi).setOnClickListener(v -> {
            try {
                int qty = Integer.parseInt(etQty.getText().toString());
                if (qty <= 0) {
                    Toast.makeText(this, "Jumlah tidak valid!", Toast.LENGTH_SHORT).show();
                } else if (qty > sisaStok) {
                    Toast.makeText(this, "Melebihi stok tersedia!", Toast.LENGTH_SHORT).show();
                } else {
                    tambahKeKeranjang(produk, qty);
                    dialog.dismiss();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Masukkan angka yang valid!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.btn_batal).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ─── Tambah Produk ke Keranjang ────────────────────────────────────────────
    private void tambahKeKeranjang(Produk produk, int qty) {
        // Jika produk sudah ada di keranjang → tambah jumlahnya
        for (DetailPesanan detail : keranjangList) {
            if (detail.getId_produk() == produk.getId_produk()) {
                detail.tambahJumlah(qty);
                adapter.notifyDataSetChanged();
                updateSummary();
                Toast.makeText(this, "Jumlah " + produk.getNama_produk() + " diperbarui", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Produk baru → buat entri baru di keranjang
        DetailPesanan newDetail = new DetailPesanan(
                produk.getId_produk(),
                0,                           // id_pesanan (belum ada, diisi saat checkout)
                currentUserId,
                qty,
                produk.getHarga_produk(),
                produk.getHarga_produk() * qty
        );
        newDetail.setNama_produk_display(produk.getNama_produk());
        keranjangList.add(newDetail);
        adapter.notifyItemInserted(keranjangList.size() - 1);
        updateSummary();
        Toast.makeText(this, produk.getNama_produk() + " ditambahkan", Toast.LENGTH_SHORT).show();
    }

    // ─── Dialog Pilih Metode Pembayaran ────────────────────────────────────────
    private void showDialogPilihMetode() {
        Dialog dialog = createDialog(R.layout.dialog_pilih_metode);
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_cash).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogBayarTunai();
        });
        dialog.findViewById(R.id.btn_qris).setOnClickListener(v -> {
            dialog.dismiss();
            prosesTransaksi("QRIS", currentTotal, 0);
        });
        dialog.show();
    }

    // ─── Dialog Bayar Tunai ────────────────────────────────────────────────────
    private void showDialogBayarTunai() {
        Dialog dialog = createDialog(R.layout.dialog_bayar_tunai);
        TextView tvTotal     = dialog.findViewById(R.id.tv_dialog_total);
        EditText etBayar     = dialog.findViewById(R.id.et_bayar_tunai);
        TextView tvKembalian = dialog.findViewById(R.id.tv_dialog_kembalian);

        tvTotal.setText(formatRupiah(currentTotal));
        tvKembalian.setText(formatRupiah(0));

        FormatUtils.setupRupiahInput(etBayar, parsed -> {
            tvKembalian.setText(formatRupiah(Math.max(0, parsed - currentTotal)));
        });

        dialog.findViewById(R.id.btn_proses).setOnClickListener(v -> {
            String str = etBayar.getText().toString().trim().replaceAll("[^\\d]", "");
            if (str.isEmpty()) {
                Toast.makeText(this, "Masukkan jumlah uang!", Toast.LENGTH_SHORT).show();
                return;
            }
            double bayar = Double.parseDouble(str);
            if (bayar < currentTotal) {
                dialog.dismiss();
                showDialogGagal();
            } else {
                double kembalian = bayar - currentTotal;
                dialog.dismiss();
                prosesTransaksi("Tunai", bayar, kembalian);
            }
        });

        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogPilihMetode();
        });

        dialog.show();
    }

    // ─── PROSES UTAMA TRANSAKSI (SIMPAN KE DATABASE) ──────────────────────────
    /**
     * Menyimpan seluruh data transaksi ke database sesuai ERD:
     * 1. Insert ke tabel `pesanan`
     * 2. Insert tiap item ke tabel `detail_pesanan`
     * 3. Kurangi `stok_tersedia` di tabel `produk`
     * 4. Catat perubahan stok di tabel `stok_adjustment`
     * 5. Insert ke tabel `pembayaran_pesanan`
     */
    private void prosesTransaksi(String metode, double bayar, double kembalian) {
        AppDatabase db = AppDatabase.getInstance(this);
        long now = System.currentTimeMillis();
        String noPesanan = generateNomorPesanan();

        // Salin daftar keranjang agar aman dipakai di background thread
        List<DetailPesanan> snapshot = new ArrayList<>(keranjangList);

        Executors.newSingleThreadExecutor().execute(() -> {
            // Buat record Pesanan
            Pesanan pesanan = new Pesanan(
                    1,             // id_pelanggan = 1 (Pelanggan Umum)
                    noPesanan,
                    now,
                    "Langsung",
                    "Selesai"
            );
            long idPesanan = db.pesananDao().insert(pesanan);

            // Insert tiap detail & kurangi stok
            for (DetailPesanan item : snapshot) {
                // Tetapkan id_pesanan yang baru saja digenerate
                item.setId_pesanan((int) idPesanan);
                item.setId_users(currentUserId);
                db.detailPesananDao().insert(item);

                // Kurangi stok produk
                db.produkDao().kurangiStok(item.getId_produk(), item.getJumlah_produk());

                // Catat ke stok_adjustment (tipe = "Penjualan")
                StokAdjustment adj = new StokAdjustment(
                        item.getId_produk(),
                        currentUserId,
                        now,
                        "Penjualan",
                        item.getJumlah_produk()
                );
                db.stokAdjustmentDao().insert(adj);
            }

            // Catat pembayaran
            PembayaranPesanan pembayaran = new PembayaranPesanan(
                    (int) idPesanan,
                    metode,
                    "Lunas",
                    now,
                    currentTotal,
                    kembalian
            );
            db.pembayaranDao().insert(pembayaran);

            // Refresh daftar produk (stok sudah berubah)
            List<Produk> produks = db.produkDao().getAktif();

            // Kembali ke UI thread
            runOnUiThread(() -> {
                // Perbarui daftar produk di menu agar stok yang tampil ikut berkurang
                menuProdukList.clear();
                menuProdukList.addAll(produks);
                menuAdapter.notifyDataSetChanged();

                // Tampilkan dialog berhasil
                showDialogBerhasil(bayar, kembalian);
            });
        });
    }

    // ─── Dialog Berhasil ───────────────────────────────────────────────────────
    private void showDialogBerhasil(double bayar, double kembalian) {
        Dialog dialog = createDialog(R.layout.dialog_berhasil);
        TextView tvMsg = dialog.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(getString(R.string.pembayaran_berhasil_msg, formatRupiah(currentTotal)));

        dialog.findViewById(R.id.btn_cetak_struk).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogStrukFinal(bayar, kembalian);
        });
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> {
            dialog.dismiss();
            resetKasir();
        });
        dialog.show();
        float density = getResources().getDisplayMetrics().density;
        int preferredWidth = (int) (480 * density);
        int availableWidth = getResources().getDisplayMetrics().widthPixels - (int) (48 * density);
        dialog.getWindow().setLayout(Math.min(preferredWidth, availableWidth), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    // ─── Dialog Gagal ──────────────────────────────────────────────────────────
    private void showDialogGagal() {
        Dialog dialog = createDialog(R.layout.dialog_gagal);
        dialog.findViewById(R.id.btn_coba_lagi).setOnClickListener(v -> {
            dialog.dismiss();
            showDialogPilihMetode();
        });
        dialog.findViewById(R.id.btn_kembali).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ─── Dialog Struk Final ────────────────────────────────────────────────────
    private void showDialogStrukFinal(double bayar, double kembalian) {
        Dialog dialog = createDialog(R.layout.dialog_struk_final);
        TextView tvDateTime = dialog.findViewById(R.id.tv_struk_datetime);
        TextView tvStorePrimary = dialog.findViewById(R.id.tv_struk_store_primary);
        TextView tvStoreSecondary = dialog.findViewById(R.id.tv_struk_store_secondary);
        TextView tvTotal    = dialog.findViewById(R.id.tv_struk_total);
        TextView tvPayment  = dialog.findViewById(R.id.tv_struk_payment);
        TextView tvChange   = dialog.findViewById(R.id.tv_struk_change);
        RecyclerView rvItems = dialog.findViewById(R.id.rv_struk_items);

        String[] storeName = StoreSettings.get(this).name.trim().split("\\s+", 2);
        tvStorePrimary.setText(storeName[0].toUpperCase(Locale.ROOT));
        tvStoreSecondary.setText(storeName.length > 1 ? storeName[1].toUpperCase(Locale.ROOT) : "");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvDateTime.setText("Date time : " + sdf.format(new Date()));
        tvTotal.setText(formatRupiah(currentTotal));
        tvPayment.setText(formatRupiah(bayar));
        tvChange.setText(formatRupiah(kembalian));

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

    // ─── Helper ───────────────────────────────────────────────────────────────

    /** Hitung total & tampilkan di ringkasan bawah. */
    private void updateSummary() {
        int totalQty = 0;
        double subtotal = 0;
        for (DetailPesanan d : keranjangList) {
            totalQty += d.getJumlah_produk();
            subtotal += d.getTotal_harga();
        }
        currentTotal = subtotal;
        tvTotalItems.setText(getString(R.string.total_items, totalQty));
        tvSubtotal.setText(formatRupiah(subtotal));
        tvTotalHarga.setText(formatRupiah(subtotal));
    }

    /** Reset keranjang setelah transaksi selesai. */
    private void resetKasir() {
        keranjangList.clear();
        adapter.notifyDataSetChanged();
        updateSummary();
    }

    /** Cari objek Produk di menu berdasarkan id. */
    private Produk findProdukById(int idProduk) {
        for (Produk p : menuProdukList) {
            if (p.getId_produk() == idProduk) return p;
        }
        return null;
    }

    /** Hitung berapa unit produk yang sudah ada di keranjang. */
    private int getQtyDiKeranjang(int idProduk) {
        for (DetailPesanan d : keranjangList) {
            if (d.getId_produk() == idProduk) return d.getJumlah_produk();
        }
        return 0;
    }

    /** Generate nomor pesanan otomatis: TRX-YYYYMMDD-XXXX */
    private String generateNomorPesanan() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String tanggal = sdf.format(new Date());
        return "TRX-" + tanggal + "-" + String.format(Locale.getDefault(), "%04d", nomorUrut.getAndIncrement());
    }

    /** Membuat Dialog dengan latar transparan. */
    private Dialog createDialog(int layoutResId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutResId);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    /** Format angka ke Rupiah menggunakan FormatUtils */
    private String formatRupiah(double amount) {
        return FormatUtils.formatRupiah(amount);
    }
}
