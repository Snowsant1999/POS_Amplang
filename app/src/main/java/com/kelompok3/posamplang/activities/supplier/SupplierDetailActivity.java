package com.kelompok3.posamplang.activities.supplier;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;

public class SupplierDetailActivity extends BaseActivity {

    private TextView tvHeaderTitle, tvNama, tvKontak, tvAlamat, tvEmail;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_detail);

        setupSidebar(R.id.btn_nav_supplier);
        initViews();
        handleIntent();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvNama = findViewById(R.id.tv_detail_nama);
        tvKontak = findViewById(R.id.tv_detail_kontak);
        tvAlamat = findViewById(R.id.tv_detail_alamat);
        tvEmail = findViewById(R.id.tv_detail_email);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
    }

    private void handleIntent() {
        String nama = getIntent().getStringExtra("SUPPLIER_NAMA");
        if (nama != null) {
            tvHeaderTitle.setText("SUPPLIER / " + nama);
            tvNama.setText(nama);
        }
        
        // Data lainnya bisa diambil dari database berdasarkan ID yang dikirim
    }
}
