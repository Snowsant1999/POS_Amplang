package com.kelompok3.posamplang.activities.produk;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.ProdukAdapter;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.parent.BaseActivity;

import java.util.ArrayList;
import java.util.List;


// Halaman manajemen daftar produk
public class ProdukListActivity extends BaseActivity {

    private RecyclerView rvProduk;
    private EditText        etSearch;
    private MaterialButton btnTambahProduk;
    private TextView        tvSuccessNotification;

    private ProdukAdapter adapter;
    private List<Produk>  produkList;
    private List<Produk>  filteredList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_produk_list);

        setupSidebar(R.id.btn_nav_stok);

        initViews();
        loadDummyData();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();
    }
    private void initViews() {
        etSearch              = findViewById(R.id.etSearch);
        btnTambahProduk       = findViewById(R.id.btnTambahProduk);
        rvProduk              = findViewById(R.id.rvProduk);
        tvSuccessNotification = findViewById(R.id.tvSuccessNotification);
    }

    // Mengisi data produk sementara
    private void loadDummyData() {
        produkList = new ArrayList<>();
        produkList.add(new Produk(1, 1, 1, 1, "Gabin Susu",       "Pcs", 20000, 30));
        produkList.add(new Produk(2, 1, 1, 1, "Gabin Keju",       "Pcs", 20000, 30));
        produkList.add(new Produk(3, 2, 2, 1, "Amplang Kuku Macan", "Pcs", 35000, 150));

        // Sinkronkan filteredList dengan produkList
        filteredList = new ArrayList<>(produkList);
    }

    // Setup list dan adapter produk
    private void setupRecyclerView() {
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukAdapter(filteredList);
        rvProduk.setAdapter(adapter);
    }

    /** Mendaftarkan listener untuk tombol-tombol interaktif. */
    private void setupClickListeners() {
        btnTambahProduk.setOnClickListener(v -> {
            tambahProduk();
        });
    }


    // Menjalankan pencarian saat pengguna mengetik
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


    private void showSuccessNotification() {
        tvSuccessNotification.setVisibility(View.VISIBLE);
        tvSuccessNotification.postDelayed(
                () -> tvSuccessNotification.setVisibility(View.GONE),
                3000
        );
    }

    // Membuka halaman tambah produk baru
    private void tambahProduk(){
        Intent intent = new Intent(this, TambahProdukActivity.class);
        tambahProdukLauncher.launch(intent);
    }
}
