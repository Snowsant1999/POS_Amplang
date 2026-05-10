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

    private RecyclerView    rvProduk;
    private EditText        etSearch;
    private MaterialButton  btnTambahProduk;
    private TextView        tvSuccessNotification;
    private TextView        tvPaginationInfo;
    private TextView        tvCurrentPage;
    private android.widget.Button btnPagePrev, btnPageNext;
    private android.widget.Spinner spinnerEntries;

    private ProdukAdapter adapter;
    private List<Produk>  produkList;
    private List<Produk>  filteredList;
    private List<Produk>  pagedList;

    private int currentPage = 1;
    private int itemsPerPage = 10;

    private final ActivityResultLauncher<Intent> tambahProdukLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            showSuccessNotification();
                            loadDummyData(); // Muat ulang data setelah ditambah
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
        setupSpinnerEntries();
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
        tvPaginationInfo      = findViewById(R.id.tvPaginationInfo);
        tvCurrentPage         = findViewById(R.id.tvCurrentPage);
        btnPagePrev           = findViewById(R.id.btnPagePrev);
        btnPageNext           = findViewById(R.id.btnPageNext);
        spinnerEntries        = findViewById(R.id.spinnerEntries);
    }

    private void setupSpinnerEntries() {
        String[] options = {"10", "25", "50", "100"};
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntries.setAdapter(spinnerAdapter);

        spinnerEntries.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                itemsPerPage = Integer.parseInt(options[position]);
                currentPage = 1; // Reset ke halaman 1
                updatePagination();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    // Mengisi data produk dari database
    private void loadDummyData() {
        if (produkList == null) produkList = new ArrayList<>();
        if (filteredList == null) filteredList = new ArrayList<>();
        if (pagedList == null) pagedList = new ArrayList<>();
        
        com.kelompok3.posamplang.database.AppDatabase db = com.kelompok3.posamplang.database.AppDatabase.getInstance(this);
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            java.util.List<Produk> produks = db.produkDao().getAll();
            runOnUiThread(() -> {
                produkList.clear();
                produkList.addAll(produks);
                filteredList.clear();
                filteredList.addAll(produks);
                updatePagination();
            });
        });
    }

    // Setup list dan adapter produk
    private void setupRecyclerView() {
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukAdapter(pagedList);
        // Daftarkan listener: reload penuh dari DB setelah edit/delete
        adapter.setOnDataChangedListener(() -> loadDummyData());
        rvProduk.setAdapter(adapter);
    }

    /** Mendaftarkan listener untuk tombol-tombol interaktif. */
    private void setupClickListeners() {
        btnTambahProduk.setOnClickListener(v -> tambahProduk());

        btnPagePrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePagination();
            }
        });

        btnPageNext.setOnClickListener(v -> {
            int maxPage = (int) Math.ceil((double) filteredList.size() / itemsPerPage);
            if (currentPage < maxPage) {
                currentPage++;
                updatePagination();
            }
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

        currentPage = 1; // Reset ke halaman pertama setiap kali mencari
        updatePagination();
    }

    private void updatePagination() {
        pagedList.clear();
        
        int totalFiltered = filteredList.size();
        int maxPage = (int) Math.ceil((double) totalFiltered / itemsPerPage);
        if (maxPage == 0) maxPage = 1; // Minimal 1 halaman walau kosong
        
        if (currentPage > maxPage) currentPage = maxPage;
        
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalFiltered);
        
        for (int i = startIndex; i < endIndex; i++) {
            pagedList.add(filteredList.get(i));
        }
        
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        
        // Update UI info
        if (tvPaginationInfo != null) {
            int total = produkList.size();
            tvPaginationInfo.setText("Menampilkan " + startIndex + "-" + endIndex + " dari " + totalFiltered + " data (Total DB: " + total + ")");
        }
        
        if (tvCurrentPage != null) {
            tvCurrentPage.setText(" " + currentPage + " ");
        }
        
        // Disable tombol jika tidak diperlukan
        if (btnPagePrev != null) {
            btnPagePrev.setEnabled(currentPage > 1);
            btnPagePrev.setAlpha(currentPage > 1 ? 1.0f : 0.5f);
        }
        if (btnPageNext != null) {
            btnPageNext.setEnabled(currentPage < maxPage);
            btnPageNext.setAlpha(currentPage < maxPage ? 1.0f : 0.5f);
        }
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
