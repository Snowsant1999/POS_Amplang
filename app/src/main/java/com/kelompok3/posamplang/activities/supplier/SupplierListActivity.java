package com.kelompok3.posamplang.activities.supplier;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.SupplierAdapter;
import com.kelompok3.posamplang.models.Supplier;
import com.kelompok3.posamplang.parent.BaseActivity;
import java.util.ArrayList;
import java.util.List;

public class SupplierListActivity extends BaseActivity {

    private RecyclerView rvSupplier;
    private SupplierAdapter adapter;
    private List<Supplier> supplierList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier);
        
        setupSidebar(R.id.btn_nav_supplier);
        initViews();
        loadDummyData();
    }

    private void initViews() {
        rvSupplier = findViewById(R.id.rv_supplier);
        rvSupplier.setLayoutManager(new LinearLayoutManager(this));
        
        supplierList = new ArrayList<>();
        adapter = new SupplierAdapter(supplierList, this);
        rvSupplier.setAdapter(adapter);

        findViewById(R.id.btn_tambah_supplier).setOnClickListener(v -> showTambahSupplierDialog());
    }

    private void showTambahSupplierDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tambah_supplier);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        int width = (int)(600 * getResources().getDisplayMetrics().density);
        dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.findViewById(R.id.btn_batal_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.iv_close_dialog).setOnClickListener(v -> dialog.dismiss());
        
        dialog.findViewById(R.id.btn_simpan_dialog).setOnClickListener(v -> {
            dialog.dismiss();
            showSuccessDialog();
        });

        dialog.show();
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_berhasil);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMessage = dialog.findViewById(R.id.tv_dialog_message);
        tvMessage.setText("Data Supplier baru berhasil disimpan.");

        TextView tvTitleMessage = dialog.findViewById(R.id.tv_title_dialog_berhasil);
        tvTitleMessage.setText("Supplier Ditambahkan!");

        dialog.findViewById(R.id.btn_cetak_struk).setVisibility(android.view.View.GONE);
        
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void loadDummyData() {
        com.kelompok3.posamplang.database.AppDatabase db = com.kelompok3.posamplang.database.AppDatabase.getInstance(this);
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            java.util.List<Supplier> suppliers = db.supplierDao().getAll();
            runOnUiThread(() -> {
                supplierList.clear();
                supplierList.addAll(suppliers);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }
}
