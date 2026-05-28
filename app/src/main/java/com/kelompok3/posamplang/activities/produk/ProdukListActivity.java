package com.kelompok3.posamplang.activities.produk;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.text.TextUtils;

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
import java.util.concurrent.Executors;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Kategori;
import com.kelompok3.posamplang.models.Merek;


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
                            showSuccessNotification(getString(R.string.produk_berhasil_ditambahkan));
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
        btnTambahProduk.setOnClickListener(v -> showTambahProdukDialog());

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

    private void showSuccessNotification(String message) {
        tvSuccessNotification.setText(message);
        tvSuccessNotification.setVisibility(View.VISIBLE);
        tvSuccessNotification.postDelayed(
                () -> tvSuccessNotification.setVisibility(View.GONE),
                3000
        );
    }

    // ─── TAMPILKAN DIALOG TAMBAH PRODUK (SAMA SEPERTI SUPPLIER) ───
    private void showTambahProdukDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_tambah_produk);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        int width = (int)(600 * getResources().getDisplayMetrics().density);
        dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etNama     = dialog.findViewById(R.id.etNamaProduk);
        EditText etMerek    = dialog.findViewById(R.id.etMerek);
        EditText etHargaJual= dialog.findViewById(R.id.etHargaJual);
        EditText etStok     = dialog.findViewById(R.id.etStok);
        EditText etSatuan   = dialog.findViewById(R.id.etSatuan);
        Spinner spinnerKat  = dialog.findViewById(R.id.spinnerKategori);
        RadioGroup rgStatus = dialog.findViewById(R.id.rgStatus);

        AppDatabase db = AppDatabase.getInstance(this);

        List<Kategori> kategoriList = new ArrayList<>();
        loadKategoriForDialog(spinnerKat, kategoriList, -1);

        dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnBatal).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnTambahKategori).setOnClickListener(
                v -> showTambahKategoriDialog(spinnerKat, kategoriList));
        
        dialog.findViewById(R.id.btnSimpan).setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama produk wajib diisi");
                return;
            }
            if (spinnerKat.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Pilih kategori!", Toast.LENGTH_SHORT).show();
                return;
            }

            double harga = Double.parseDouble(etHargaJual.getText().toString().isEmpty() ? "0" : etHargaJual.getText().toString());
            int stok = Integer.parseInt(etStok.getText().toString().isEmpty() ? "0" : etStok.getText().toString());
            String satuan = etSatuan.getText().toString().trim();
            String merek = etMerek.getText().toString().trim();
            boolean aktif = rgStatus.getCheckedRadioButtonId() != R.id.rbNonaktif;

            int idKat = kategoriList.get(spinnerKat.getSelectedItemPosition() - 1).getId_kategori();

            Executors.newSingleThreadExecutor().execute(() -> {
                int idMerek = 1;
                if (!merek.isEmpty()) {
                    List<Merek> mList = db.merekDao().getAll();
                    boolean found = false;
                    for (Merek m : mList) {
                        if (m.getNama_merek().equalsIgnoreCase(merek)) {
                            idMerek = m.getId_merek();
                            found = true;
                            break;
                        }
                    }
                    if (!found) idMerek = (int) db.merekDao().insert(new Merek(merek));
                }

                db.produkDao().insert(new Produk(idKat, idMerek, 1, nama, satuan, harga, stok, aktif));
                runOnUiThread(() -> {
                    Toast.makeText(this, "Produk ditambahkan!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showSuccessNotification(getString(R.string.produk_berhasil_ditambahkan));
                    loadDummyData();
                });
            });
        });
        dialog.show();
    }

    // ─── TAMPILKAN DIALOG EDIT PRODUK (SAMA SEPERTI SUPPLIER) ───
    public void showEditProdukDialog(Produk produk) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_edit_produk2);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        int width = (int)(600 * getResources().getDisplayMetrics().density);
        dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etNama     = dialog.findViewById(R.id.etNamaProdukEdit);
        EditText etMerek    = dialog.findViewById(R.id.etMerekEdit);
        EditText etHargaJual= dialog.findViewById(R.id.etHargaJualEdit);
        EditText etStok     = dialog.findViewById(R.id.etStokEdit);
        EditText etSatuan   = dialog.findViewById(R.id.etSatuanEdit);
        Spinner spinnerKat  = dialog.findViewById(R.id.spinnerKategoriEdit);
        RadioGroup rgStatus = dialog.findViewById(R.id.rgStatusEdit);

        etNama.setText(produk.getNama_produk());
        etHargaJual.setText(String.valueOf((long)produk.getHarga_produk()));
        etStok.setText(String.valueOf(produk.getStok_tersedia()));
        etSatuan.setText(produk.getUnit());
        rgStatus.check(produk.isAktif() ? R.id.rbAktifEdit : R.id.rbNonaktifEdit);

        AppDatabase db = AppDatabase.getInstance(this);
        List<Kategori> kategoriList = new ArrayList<>();

        Executors.newSingleThreadExecutor().execute(() -> {
            // Load Merek
            Merek m = db.merekDao().getById(produk.getId_merek());
            final String merekName = (m != null) ? m.getNama_merek() : "";

            // Load Kategori
            kategoriList.addAll(db.kategoriDao().getAll());
            List<String> namaKategori = new ArrayList<>();
            int sel = 0;
            for (int i=0; i<kategoriList.size(); i++) {
                namaKategori.add(kategoriList.get(i).getNama_kategori());
                if (kategoriList.get(i).getId_kategori() == produk.getId_kategori_produk()) sel = i;
            }
            final int fsel = sel;
            runOnUiThread(() -> {
                etMerek.setText(merekName);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namaKategori);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKat.setAdapter(adapter);
                spinnerKat.setSelection(fsel);
            });
        });

        dialog.findViewById(R.id.btnCloseEdit).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnBatalEdit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.btnSimpanEdit).setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String merek = etMerek.getText().toString().trim();
            if (TextUtils.isEmpty(nama)) return;
            
            produk.setNama_produk(nama);
            produk.setHarga_produk(Double.parseDouble(etHargaJual.getText().toString().isEmpty() ? "0" : etHargaJual.getText().toString()));
            produk.setStok_tersedia(Integer.parseInt(etStok.getText().toString().isEmpty() ? "0" : etStok.getText().toString()));
            produk.setUnit(etSatuan.getText().toString());
            produk.setAktif(rgStatus.getCheckedRadioButtonId() != R.id.rbNonaktifEdit);
            if (kategoriList.size() > 0) {
                produk.setId_kategori_produk(kategoriList.get(spinnerKat.getSelectedItemPosition()).getId_kategori());
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                int idMerek = produk.getId_merek();
                if (!merek.isEmpty()) {
                    List<Merek> mList = db.merekDao().getAll();
                    boolean found = false;
                    for (Merek mk : mList) {
                        if (mk.getNama_merek().equalsIgnoreCase(merek)) {
                            idMerek = mk.getId_merek();
                            found = true;
                            break;
                        }
                    }
                    if (!found) idMerek = (int) db.merekDao().insert(new Merek(merek));
                }
                produk.setId_merek(idMerek);

                db.produkDao().update(produk);
                runOnUiThread(() -> {
                    dialog.dismiss();
                    showSuccessNotification(getString(R.string.produk_telah_diubah));
                    loadDummyData();
                });
            });
        });
        dialog.show();
    }

    private void showTambahKategoriDialog(Spinner spinnerKategori, List<Kategori> kategoriList) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.tambah_kategori);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etNamaKategori = dialog.findViewById(R.id.etNamaKategori);
        dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnBatalKategori).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnSimpanKategori).setOnClickListener(v -> {
            String namaKategori = etNamaKategori.getText().toString().trim();
            if (TextUtils.isEmpty(namaKategori)) {
                etNamaKategori.setError("Nama kategori wajib diisi");
                etNamaKategori.requestFocus();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);
            Executors.newSingleThreadExecutor().execute(() -> {
                long kategoriId = db.kategoriDao().insert(new Kategori(namaKategori, ""));
                runOnUiThread(() -> {
                    dialog.dismiss();
                    loadKategoriForDialog(spinnerKategori, kategoriList, (int) kategoriId);
                });
            });
        });

        dialog.show();
        dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void loadKategoriForDialog(Spinner spinnerKategori, List<Kategori> kategoriList, int selectedId) {
        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Kategori> hasil = db.kategoriDao().getAll();
            List<String> namaKategori = new ArrayList<>();
            namaKategori.add("-- Pilih Kategori --");
            int selectedPosition = 0;
            for (int i = 0; i < hasil.size(); i++) {
                Kategori kategori = hasil.get(i);
                namaKategori.add(kategori.getNama_kategori());
                if (kategori.getId_kategori() == selectedId) {
                    selectedPosition = i + 1;
                }
            }
            int finalSelectedPosition = selectedPosition;
            runOnUiThread(() -> {
                kategoriList.clear();
                kategoriList.addAll(hasil);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, namaKategori);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKategori.setAdapter(adapter);
                spinnerKategori.setSelection(finalSelectedPosition);
            });
        });
    }
}
