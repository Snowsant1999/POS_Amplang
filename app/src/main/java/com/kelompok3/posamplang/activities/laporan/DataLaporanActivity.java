package com.kelompok3.posamplang.activities.laporan;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;

public class DataLaporanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_laporan);

        MaterialButton btnPrint = findViewById(R.id.btnPrint);
        MaterialButton btnEdit = findViewById(R.id.btnEdit);
        ImageButton btnDelete = findViewById(R.id.btnDelete);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DataLaporanActivity.this, "Mencetak Laporan...", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DataLaporanActivity.this, "Membuka Edit Laporan...", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DataLaporanActivity.this, "Laporan Dihapus", Toast.LENGTH_SHORT).show();
            }
        });
    }
}