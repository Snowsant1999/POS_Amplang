package com.kelompok3.posamplang.activities.supplier;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;

public class SupplierListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        setupSidebar(R.id.btn_nav_supplier);
        
        Toast.makeText(this, "Modul Supplier sedang dalam pengembangan", Toast.LENGTH_SHORT).show();
    }
}
