package com.kelompok3.posamplang.activities.laporan;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.utils.FixedViewportScaler;

public class EditLaporanActivity extends AppCompatActivity {

    private EditText etCabang, etTanggalLaporan, etPenjualanAwal, etPenjualanTunai,
            etUangTunai, etTotalPenjualan, etCash, etPembayaranOnline, etBon, etVoid, etDpp, etPpn;
    private MaterialButton btnSimpan;
    private ImageButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_laporan);
        FixedViewportScaler.apply(this);

        // Inisialisasi View
        etCabang = findViewById(R.id.etCabang);
        etTanggalLaporan = findViewById(R.id.etTanggalLaporan);
        etPenjualanAwal = findViewById(R.id.etPenjualanAwal);
        etPenjualanTunai = findViewById(R.id.etPenjualanTunai);
        etUangTunai = findViewById(R.id.etUangTunai);
        etTotalPenjualan = findViewById(R.id.etTotalPenjualan);
        etCash = findViewById(R.id.etCash);
        etPembayaranOnline = findViewById(R.id.etPembayaranOnline);
        etBon = findViewById(R.id.etBon);
        etVoid = findViewById(R.id.etVoid);
        etDpp = findViewById(R.id.etDpp);
        etPpn = findViewById(R.id.etPpn);
        
        btnSimpan = findViewById(R.id.btnSimpan);
        btnDelete = findViewById(R.id.btnDelete);

        // Fungsi Simpan
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cabang = etCabang.getText().toString();
                // Logika simpan data bisa ditambahkan di sini
                Toast.makeText(EditLaporanActivity.this, "Data Laporan " + cabang + " Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                finish(); // Kembali ke layar sebelumnya setelah simpan
            }
        });

        // Fungsi Hapus
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logika hapus data
                Toast.makeText(EditLaporanActivity.this, "Laporan Telah Dihapus", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
