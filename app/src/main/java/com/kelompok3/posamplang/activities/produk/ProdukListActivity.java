package com.kelompok3.posamplang.activities.produk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.activities.dashboard.MainActivity;
import com.kelompok3.posamplang.activities.transaksi.KasirActivity;
import com.kelompok3.posamplang.adapters.ProdukAdapter;
import com.kelompok3.posamplang.models.Produk;

import java.util.ArrayList;
import java.util.List;

/**
 * ProdukListActivity — Halaman manajemen daftar produk.
 *
 * Menampilkan semua produk beserta fitur pencarian dan tombol tambah produk.
 *
 * TODO: Ganti data dummy dengan query dari ProdukDao via Room Database.
 */
public class ProdukListActivity extends AppCompatActivity {

    // -------------------------------------------------------------------------
    // Views
    // -------------------------------------------------------------------------

    private RecyclerView    rvProduk;
    private EditText        etSearch;
    private MaterialButton  btnTambahProduk;
    private TextView        tvSuccessNotification;

    // -------------------------------------------------------------------------
    // Data & Adapter
    // -------------------------------------------------------------------------

    private ProdukAdapter adapter;
    private List<Produk>  produkList;
    private List<Produk>  filteredList;

    // -------------------------------------------------------------------------
    // Activity Result Launcher (pengganti startActivityForResult yang sudah deprecated)
    // -------------------------------------------------------------------------

    private final ActivityResultLauncher<Intent> tambahProdukLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            showSuccessNotification();
                            // TODO: Muat ulang data produk dari database setelah produk berhasil ditambah
                        }
                    }
            );

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manajemen_stok);

        initViews();
        loadDummyData();
        setupNavigationListener();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();
    }

    // -------------------------------------------------------------------------
    // Inisialisasi
    // -------------------------------------------------------------------------

    /** Menghubungkan variabel ke elemen UI dari layout. */
    private void initViews() {
        etSearch              = findViewById(R.id.etSearch);
        btnTambahProduk       = findViewById(R.id.btnTambahProduk);
        rvProduk              = findViewById(R.id.rvProduk);
        tvSuccessNotification = findViewById(R.id.tvSuccessNotification);
    }

    /**
     * Memuat data produk sementara (dummy).
     * TODO: Ganti dengan pemanggilan ProdukDao.getAllProduk() dari Room Database.
     */
    private void loadDummyData() {
        produkList = new ArrayList<>();
        produkList.add(new Produk(1, 1, 1, 1, "Gabin Susu",       "Pcs", 20000, 30));
        produkList.add(new Produk(2, 1, 1, 1, "Gabin Keju",       "Pcs", 20000, 30));
        produkList.add(new Produk(3, 2, 2, 1, "Amplang Kuku Macan", "Pcs", 35000, 150));

        // Sinkronkan filteredList dengan produkList
        filteredList = new ArrayList<>(produkList);
    }

    /** Mendaftarkan semua listener navigasi sidebar. */
    private void setupNavigationListener() {
        // Stok — sudah berada di halaman ini
        findViewById(R.id.btn_nav_stok).setOnClickListener(v      -> { /* Aktif saat ini */ });

        // Modul yang sudah tersedia
        findViewById(R.id.btn_nav_dashboard).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.btn_nav_kasir).setOnClickListener(v     -> navigateTo(KasirActivity.class));

        // Modul yang belum diimplementasikan
        // TODO: Ganti dengan intent eksplisit setelah modul-modul ini selesai dibuat
        findViewById(R.id.btn_nav_supplier).setOnClickListener(v   -> showComingSoon("Supplier"));
        findViewById(R.id.btn_nav_laporan).setOnClickListener(v    -> showComingSoon("Laporan"));
        findViewById(R.id.btn_nav_pengaturan).setOnClickListener(v -> showComingSoon("Pengaturan"));

        // Logout — kembali ke LoginActivity dan hapus semua activity dari back stack
        findViewById(R.id.btn_nav_logout).setOnClickListener(v     -> handleLogout());
    }

    /** Menyiapkan RecyclerView dengan adapter dan layout manager. */
    private void setupRecyclerView() {
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukAdapter(filteredList);
        rvProduk.setAdapter(adapter);
    }

    /** Mendaftarkan listener untuk tombol-tombol interaktif. */
    private void setupClickListeners() {
        btnTambahProduk.setOnClickListener(v -> {
            Intent intent = new Intent(ProdukListActivity.this, TambahProdukActivity.class);
            tambahProdukLauncher.launch(intent);
        });
    }

    /** Mendaftarkan TextWatcher untuk kolom pencarian produk. */
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProduk(s.toString());
            }
        });
    }

    // -------------------------------------------------------------------------
    // Logic Utama
    // -------------------------------------------------------------------------

    /**
     * Memfilter daftar produk berdasarkan nama atau ID produk.
     *
     * @param keyword Kata kunci pencarian dari input pengguna.
     */
    private void filterProduk(String keyword) {
        filteredList.clear();
        String lowerKeyword = keyword.toLowerCase().trim();

        for (Produk produk : produkList) {
            boolean namaMatch = produk.getNama_produk().toLowerCase().contains(lowerKeyword);
            boolean idMatch   = String.valueOf(produk.getId_produk()).contains(lowerKeyword);

            if (namaMatch || idMatch) {
                filteredList.add(produk);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Menampilkan notifikasi sukses selama 3 detik kemudian menyembunyikannya otomatis.
     */
    private void showSuccessNotification() {
        tvSuccessNotification.setVisibility(View.VISIBLE);
        tvSuccessNotification.postDelayed(
                () -> tvSuccessNotification.setVisibility(View.GONE),
                3000
        );
    }

    // -------------------------------------------------------------------------
    // Navigasi & Aksi
    // -------------------------------------------------------------------------

    /**
     * Navigasi ke activity tertentu menggunakan explicit intent.
     *
     * @param targetClass Class activity tujuan.
     */
    private void navigateTo(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * Menampilkan pesan bahwa fitur belum tersedia.
     *
     * @param moduleName Nama modul yang dituju.
     */
    private void showComingSoon(String moduleName) {
        Toast.makeText(this, "Modul " + moduleName + " belum tersedia", Toast.LENGTH_SHORT).show();
    }

    /** Keluar dari sesi dan kembali ke halaman Login. */
    private void handleLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
