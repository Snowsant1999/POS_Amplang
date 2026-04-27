package com.kelompok3.posamplang.activities.pengaturan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;

public class PengaturanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pengaturan);
        
        setupSidebar(R.id.btn_nav_pengaturan);
    }
}
