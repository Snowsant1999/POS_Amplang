package com.kelompok3.posamplang.activities.produk;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Kategori;
import com.kelompok3.posamplang.models.Merek;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.utils.FixedViewportScaler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EditProdukActivity extends AppCompatActivity {

    // Konstanta key Intent — harus sama dengan yang dikirim dari ProdukAdapter
    public static final String EXTRA_PRODUK_ID    = "PRODUK_ID";
    public static final String EXTRA_PRODUK_NAMA  = "PRODUK_NAMA";
    public static final String EXTRA_PRODUK_HARGA = "PRODUK_HARGA";
    public static final String EXTRA_PRODUK_STOK  = "PRODUK_STOK";
    public static final String EXTRA_PRODUK_UNIT  = "PRODUK_UNIT";
    public static final String EXTRA_ID_KATEGORI  = "PRODUK_ID_KATEGORI";
    public static final String EXTRA_ID_MEREK     = "PRODUK_ID_MEREK";
    public static final String EXTRA_ID_SUPPLIER  = "PRODUK_ID_SUPPLIER";
    public static final String EXTRA_PRODUK_AKTIF = "PRODUK_AKTIF";

    private EditText etNama, etHargaJual, etStok, etSatuan;
    private Spinner  spinnerKategori;
    private RadioGroup rgStatus;
    private MaterialButton btnSimpan, btnBatal;
    private ImageButton    btnClose;

    private int produkId;
    private int idKategoriAwal, idMerekAwal, idSupplierAwal;
    private List<Kategori> kategoriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_produk2);
        FixedViewportScaler.apply(this);

        initViews();
        loadKategoriLaluIsiForm();
    }

    private void initViews() {
        etNama          = findViewById(R.id.etNamaProdukEdit);
        etHargaJual     = findViewById(R.id.etHargaJualEdit);
        etStok          = findViewById(R.id.etStokEdit);
        etSatuan        = findViewById(R.id.etSatuanEdit);
        spinnerKategori = findViewById(R.id.spinnerKategoriEdit);
        rgStatus        = findViewById(R.id.rgStatusEdit);
        btnSimpan       = findViewById(R.id.btnSimpanEdit);
        btnBatal        = findViewById(R.id.btnBatalEdit);
        btnClose        = findViewById(R.id.btnCloseEdit);

        btnBatal.setOnClickListener(v -> finish());
        btnClose.setOnClickListener(v -> finish());
        btnSimpan.setOnClickListener(v -> { if (validateInput()) updateProduk(); });
    }

    // Muat kategori dari DB, lalu isi form dengan data produk yang diterima dari Intent
    private void loadKategoriLaluIsiForm() {
        // Ambil data dari Intent
        produkId       = getIntent().getIntExtra(EXTRA_PRODUK_ID, -1);
        idKategoriAwal = getIntent().getIntExtra(EXTRA_ID_KATEGORI, 1);
        idMerekAwal    = getIntent().getIntExtra(EXTRA_ID_MEREK, 1);
        idSupplierAwal = getIntent().getIntExtra(EXTRA_ID_SUPPLIER, 1);

        String nama   = getIntent().getStringExtra(EXTRA_PRODUK_NAMA);
        double harga  = getIntent().getDoubleExtra(EXTRA_PRODUK_HARGA, 0);
        int stok      = getIntent().getIntExtra(EXTRA_PRODUK_STOK, 0);
        String satuan = getIntent().getStringExtra(EXTRA_PRODUK_UNIT);
        boolean aktif = getIntent().getBooleanExtra(EXTRA_PRODUK_AKTIF, true);

        // Isi field teks langsung
        etNama.setText(nama);
        etHargaJual.setText(String.valueOf((long) harga));
        etStok.setText(String.valueOf(stok));
        etSatuan.setText(satuan);
        rgStatus.check(aktif ? R.id.rbAktifEdit : R.id.rbNonaktifEdit);

        // Muat kategori dari DB, lalu pilih yang sesuai
        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            kategoriList = db.kategoriDao().getAll();
            List<String> namaKategori = new ArrayList<>();
            int selectedIndex = 0;
            for (int i = 0; i < kategoriList.size(); i++) {
                namaKategori.add(kategoriList.get(i).getNama_kategori());
                if (kategoriList.get(i).getId_kategori() == idKategoriAwal) {
                    selectedIndex = i;
                }
            }
            final int finalSelected = selectedIndex;
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namaKategori);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKategori.setAdapter(adapter);
                spinnerKategori.setSelection(finalSelected);
            });
        });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(etNama.getText().toString().trim())) {
            etNama.setError("Nama produk wajib diisi");
            etNama.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etHargaJual.getText().toString().trim())) {
            etHargaJual.setError("Harga jual wajib diisi");
            etHargaJual.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(etStok.getText().toString().trim())) {
            etStok.setError("Stok wajib diisi");
            etStok.requestFocus();
            return false;
        }
        return true;
    }

    // Simpan perubahan ke Room Database
    private void updateProduk() {
        if (produkId == -1) {
            Toast.makeText(this, "Error: ID Produk tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            // Ambil produk lama dari DB
            Produk produk = db.produkDao().getById(produkId);
            if (produk == null) {
                runOnUiThread(() -> Toast.makeText(this, "Produk tidak ditemukan!", Toast.LENGTH_SHORT).show());
                return;
            }

            // Update data
            produk.setNama_produk(etNama.getText().toString().trim());
            produk.setHarga_produk(Double.parseDouble(etHargaJual.getText().toString().trim()));
            produk.setStok_tersedia(Integer.parseInt(etStok.getText().toString().trim()));
            produk.setUnit(etSatuan.getText().toString().trim());
            produk.setAktif(rgStatus.getCheckedRadioButtonId() != R.id.rbNonaktifEdit);

            // Update kategori dari spinner
            int selectedPos = spinnerKategori.getSelectedItemPosition();
            if (selectedPos >= 0 && selectedPos < kategoriList.size()) {
                produk.setId_kategori_produk(kategoriList.get(selectedPos).getId_kategori());
            }

            db.produkDao().update(produk);

            runOnUiThread(() -> {
                Toast.makeText(this, "Produk berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
