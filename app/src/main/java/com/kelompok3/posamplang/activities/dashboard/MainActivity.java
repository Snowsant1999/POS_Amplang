package com.kelompok3.posamplang.activities.dashboard;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kelompok3.posamplang.R;

public class MainActivity extends AppCompatActivity {

    LinearLayout menuKasir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        menuKasir = findViewById(R.id.menu_kasir_layout);

        menuKasir.setOnClickListener(v -> {
           Intent intent = new Intent(MainActivity.this, com.kelompok3.posamplang.activities.transaksi.KasirActivity.class);
           startActivity(intent);
           overridePendingTransition(0, 0);
        });

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}