package com.kelompok3.posamplang.activities.produk;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Kategori;
import com.kelompok3.posamplang.models.Merek;
import com.kelompok3.posamplang.models.Produk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TambahProdukActivity extends AppCompatActivity {

    private EditText etNamaProduk, etKodeProduk, etMerek;
    private EditText etHargaPokok, etHargaJual, etStok, etSatuan, etDeskripsi;
    private Spinner spinnerKategori;
    private RadioGroup rgStatus;
    private MaterialButton btnBatal, btnSimpan, btnTambahKategori;
    private ImageView btnClose;

    // Data dari DB
    private List<Kategori> kategoriList = new ArrayList<>();
    private List<Merek>    merekList    = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tambah_produk);

        initViews();
        loadKategoriFromDb();

        btnClose.setOnClickListener(v -> finish());
        btnBatal.setOnClickListener(v -> finish());
        btnTambahKategori.setOnClickListener(v -> showTambahKategoriDialog());
        btnSimpan.setOnClickListener(v -> { if (validateInput()) simpanProduk(); });
    }

    private void initViews() {
        etNamaProduk     = findViewById(R.id.etNamaProduk);
        etKodeProduk     = findViewById(R.id.etKodeProduk);
        etMerek          = findViewById(R.id.etMerek);
        etHargaPokok     = findViewById(R.id.etHargaPokok);
        etHargaJual      = findViewById(R.id.etHargaJual);
        etStok           = findViewById(R.id.etStok);
        etSatuan         = findViewById(R.id.etSatuan);
        etDeskripsi      = findViewById(R.id.etDeskripsi);
        spinnerKategori  = findViewById(R.id.spinnerKategori);
        rgStatus         = findViewById(R.id.rgStatus);
        btnBatal         = findViewById(R.id.btnBatal);
        btnSimpan        = findViewById(R.id.btnSimpan);
        btnTambahKategori= findViewById(R.id.btnTambahKategori);
        btnClose         = findViewById(R.id.btnClose);
    }

    // ─── Load Kategori dari Room Database ────────────────────────────────────
    private void loadKategoriFromDb() {
        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            kategoriList = db.kategoriDao().getAll();
            List<String> namaKategori = new ArrayList<>();
            namaKategori.add("-- Pilih Kategori --");
            for (Kategori k : kategoriList) namaKategori.add(k.getNama_kategori());

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, namaKategori);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKategori.setAdapter(adapter);
            });
        });
    }

    // ─── Validasi Input ───────────────────────────────────────────────────────
    private boolean validateInput() {
        String nama    = etNamaProduk.getText().toString().trim();
        String harga   = etHargaJual.getText().toString().trim();
        String stok    = etStok.getText().toString().trim();
        String satuan  = etSatuan.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            etNamaProduk.setError("Nama produk wajib diisi");
            etNamaProduk.requestFocus();
            return false;
        }
        if (spinnerKategori.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(harga)) {
            etHargaJual.setError("Harga jual wajib diisi");
            etHargaJual.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(stok)) {
            etStok.setError("Stok awal wajib diisi");
            etStok.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(satuan)) {
            etSatuan.setError("Satuan wajib diisi");
            etSatuan.requestFocus();
            return false;
        }
        return true;
    }

    // ─── Simpan Produk ke Database ────────────────────────────────────────────
    private void simpanProduk() {
        AppDatabase db = AppDatabase.getInstance(this);

        String namaProduk = etNamaProduk.getText().toString().trim();
        String namaMerek  = etMerek.getText().toString().trim();
        double hargaJual  = Double.parseDouble(etHargaJual.getText().toString().trim());
        int stok          = Integer.parseInt(etStok.getText().toString().trim());
        String satuan     = etSatuan.getText().toString().trim();

        // ID kategori terpilih (posisi -1 karena posisi 0 adalah placeholder)
        int idxKategori = spinnerKategori.getSelectedItemPosition() - 1;
        int idKategori  = kategoriList.get(idxKategori).getId_kategori();

        Executors.newSingleThreadExecutor().execute(() -> {
            // Cek apakah merek sudah ada, jika tidak buat baru
            int idMerek = 1; // default
            if (!namaMerek.isEmpty()) {
                List<Merek> semuaMerek = db.merekDao().getAll();
                boolean merekDitemukan = false;
                for (Merek m : semuaMerek) {
                    if (m.getNama_merek().equalsIgnoreCase(namaMerek)) {
                        idMerek = m.getId_merek();
                        merekDitemukan = true;
                        break;
                    }
                }
                if (!merekDitemukan) {
                    // Buat merek baru
                    idMerek = (int) db.merekDao().insert(new Merek(namaMerek));
                }
            }

            // Ambil supplier default (ID 1) — bisa dikembangkan dengan Spinner Supplier
            int idSupplier = 1;

            Produk produkBaru = new Produk(idKategori, idMerek, idSupplier, namaProduk, satuan, hargaJual, stok);
            db.produkDao().insert(produkBaru);

            runOnUiThread(() -> {
                Toast.makeText(this, "Produk \"" + namaProduk + "\" berhasil disimpan!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    // ─── Dialog Tambah Kategori Baru ─────────────────────────────────────────
    private void showTambahKategoriDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Tambah Kategori Baru");

        final EditText input = new EditText(this);
        input.setHint("Nama kategori");
        builder.setView(input);

        builder.setPositiveButton("Tambah", (dialog, which) -> {
            String namaKategori = input.getText().toString().trim();
            if (TextUtils.isEmpty(namaKategori)) {
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            AppDatabase db = AppDatabase.getInstance(this);
            Executors.newSingleThreadExecutor().execute(() -> {
                db.kategoriDao().insert(new Kategori(namaKategori, ""));
                runOnUiThread(() -> {
                    Toast.makeText(this, "Kategori \"" + namaKategori + "\" ditambahkan!", Toast.LENGTH_SHORT).show();
                    loadKategoriFromDb(); // Refresh spinner
                });
            });
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
