package com.kelompok3.posamplang.activities.produk;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kelompok3.posamplang.R;
import com.google.android.material.button.MaterialButton;

public class EditProdukActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_produk);

        MaterialButton btnSimpan = findViewById(R.id.btnSimpan);
        MaterialButton btnEdit = findViewById(R.id.btnEditAction);
        MaterialButton btnDelete = findViewById(R.id.btnDeleteAction);

        btnSimpan.setOnClickListener(v -> {
            Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Mode Edit diaktifkan", Toast.LENGTH_SHORT).show();
            // Logika untuk mengubah TextView menjadi EditText bisa ditambahkan di sini
        });

        btnDelete.setOnClickListener(v -> {
            Toast.makeText(this, "Produk dihapus", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
