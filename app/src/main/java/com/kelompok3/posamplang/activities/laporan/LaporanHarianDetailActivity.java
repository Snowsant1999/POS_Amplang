package com.kelompok3.posamplang.activities.laporan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;

public class LaporanHarianDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.laporan);

        setupSidebar(R.id.btn_nav_laporan);
    }
}
