package com.kelompok3.posamplang.activities.produk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.Produk;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ProdukListActivity extends AppCompatActivity {

    private RecyclerView rvProduk;
    private ProdukAdapter adapter;
    private List<Produk> produkList;
    private List<Produk> filteredList;
    private EditText etSearch;
    private MaterialButton btnTambahProduk;
    private TextView tvSuccessNotification;
    private static final int REQUEST_CODE_TAMBAH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manajemen_stok);

        etSearch = findViewById(R.id.etSearch);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);
        rvProduk = findViewById(R.id.rvProduk);
        tvSuccessNotification = findViewById(R.id.tvSuccessNotification);
        
        rvProduk.setLayoutManager(new LinearLayoutManager(this));

        produkList = new ArrayList<>();
        // Data Dummy sesuai gambar
        produkList.add(new Produk("Gabin Susu", "xxxxxx", "Gabin", "Makanan", "Pcs", 15000, 20000, 30, 5, "Aktif"));
        produkList.add(new Produk("Gabin Keju", "xxxxxx", "Gabin", "Makanan", "Pcs", 15000, 20000, 30, 3, "Aktif"));
        produkList.add(new Produk("Amplang Kuku Macan", "xxxxxx", "Amplang", "Makanan", "Pcs", 20000, 30000, 150, 10, "Aktif"));

        filteredList = new ArrayList<>(produkList);
        adapter = new ProdukAdapter(filteredList);
        rvProduk.setAdapter(adapter);

        // Fungsi Tambah Produk
        btnTambahProduk.setOnClickListener(v -> {
            Intent intent = new Intent(ProdukListActivity.this, TambahProdukActivity.class);
            startActivityForResult(intent, REQUEST_CODE_TAMBAH);
        });

        // Fungsi Pencarian
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAMBAH && resultCode == RESULT_OK) {
            showSuccessNotification();
        }
    }

    private void showSuccessNotification() {
        tvSuccessNotification.setVisibility(View.VISIBLE);
        tvSuccessNotification.postDelayed(() -> tvSuccessNotification.setVisibility(View.GONE), 3000);
    }

    private void filter(String text) {
        filteredList.clear();
        for (Produk item : produkList) {
            if (item.getNama().toLowerCase().contains(text.toLowerCase()) || 
                item.getKode().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
