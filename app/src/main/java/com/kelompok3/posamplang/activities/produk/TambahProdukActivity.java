package com.kelompok3.posamplang.activities.produk;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;

public class TambahProdukActivity extends AppCompatActivity {

    private EditText etNamaProduk, etKodeProduk, etMerek, etHargaPokok, etHargaJual, etStok, etDeskripsi;
    private Spinner spinnerKategori, spinnerSatuan;
    private MaterialButton btnBatal, btnSimpan, btnTambahKategori;
    private ImageView btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_produk);

        // Inisialisasi View
        etNamaProduk = findViewById(R.id.etNamaProduk);
        etKodeProduk = findViewById(R.id.etKodeProduk);
        etMerek = findViewById(R.id.etMerek);
        etHargaPokok = findViewById(R.id.etHargaPokok);
        etHargaJual = findViewById(R.id.etHargaJual);
        etStok = findViewById(R.id.etStok);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        btnBatal = findViewById(R.id.btnBatal);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnTambahKategori = findViewById(R.id.btnTambahKategori);
        btnClose = findViewById(R.id.btnClose);

        // Setup Spinner Dummy Data
        String[] kategori = {"Pilih kategori", "Makanan", "Minuman", "Snack"};
        ArrayAdapter<String> adapterKategori = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategori);
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapterKategori);

        String[] satuan = {"Pilih satuan", "Pcs", "Box", "Kg", "Bungkus"};
        ArrayAdapter<String> adapterSatuan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, satuan);
        adapterSatuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSatuan.setAdapter(adapterSatuan);

        // Event Listeners
        btnClose.setOnClickListener(v -> finish());
        btnBatal.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            if (validateInput()) {
                // Logika simpan data ke database/list di sini
                setResult(RESULT_OK);
                Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnTambahKategori.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur tambah kategori", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInput() {
        if (etNamaProduk.getText().toString().isEmpty()) {
            etNamaProduk.setError("Nama produk wajib diisi");
            return false;
        }
        if (spinnerKategori.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Tambahkan validasi lainnya jika diperlukan
        return true;
    }
}
