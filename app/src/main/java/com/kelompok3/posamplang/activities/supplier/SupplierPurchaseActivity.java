package com.kelompok3.posamplang.activities.supplier;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.PurchaseDraftAdapter;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.DetailStokRequest;
import com.kelompok3.posamplang.models.Kategori;
import com.kelompok3.posamplang.models.Merek;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.models.PurchaseDraftItem;
import com.kelompok3.posamplang.models.StokRequest;
import com.kelompok3.posamplang.models.Supplier;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SupplierPurchaseActivity extends BaseActivity {

    private static final int CURRENT_USER_ID = 1;

    private final List<Produk> products = new ArrayList<>();
    private final List<Kategori> categories = new ArrayList<>();
    private final List<PurchaseDraftItem> draftItems = new ArrayList<>();

    private AppDatabase database;
    private int supplierId;
    private Supplier supplier;
    private Spinner spinnerProduct;
    private Spinner spinnerCategory;
    private RadioButton rbExisting;
    private LinearLayout existingContainer;
    private LinearLayout newContainer;
    private EditText etName;
    private EditText etBrand;
    private EditText etUnit;
    private EditText etSellPrice;
    private EditText etQuantity;
    private EditText etBuyPrice;
    private TextView tvTotal;
    private PurchaseDraftAdapter draftAdapter;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_purchase);
        setupSidebar(R.id.btn_nav_supplier);

        database = AppDatabase.getInstance(this);
        supplierId = getIntent().getIntExtra("SUPPLIER_ID", -1);
        if (supplierId == -1) {
            Toast.makeText(this, "Data supplier tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initViews();
        loadData();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back_purchase);
        RadioGroup typeGroup = findViewById(R.id.rg_purchase_item_type);
        rbExisting = findViewById(R.id.rb_purchase_existing);
        existingContainer = findViewById(R.id.container_existing_product);
        newContainer = findViewById(R.id.container_new_product);
        spinnerProduct = findViewById(R.id.spinner_existing_product);
        spinnerCategory = findViewById(R.id.spinner_request_category);
        etName = findViewById(R.id.et_request_product_name);
        etBrand = findViewById(R.id.et_request_brand);
        etUnit = findViewById(R.id.et_request_unit);
        etSellPrice = findViewById(R.id.et_request_sell_price);
        etQuantity = findViewById(R.id.et_request_quantity);
        etBuyPrice = findViewById(R.id.et_request_buy_price);
        tvTotal = findViewById(R.id.tv_purchase_total);
        btnSubmit = findViewById(R.id.btn_submit_purchase);

        RecyclerView rvDraft = findViewById(R.id.rv_purchase_draft);
        rvDraft.setLayoutManager(new LinearLayoutManager(this));
        draftAdapter = new PurchaseDraftAdapter(draftItems, position -> {
            if (position >= 0 && position < draftItems.size()) {
                draftItems.remove(position);
                draftAdapter.notifyItemRemoved(position);
                updateDraftTotal();
            }
        });
        rvDraft.setAdapter(draftAdapter);

        btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.btn_cancel_purchase).setOnClickListener(v -> finish());
        typeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean existing = checkedId == R.id.rb_purchase_existing;
            existingContainer.setVisibility(existing ? View.VISIBLE : View.GONE);
            newContainer.setVisibility(existing ? View.GONE : View.VISIBLE);
        });
        findViewById(R.id.btn_add_request_item).setOnClickListener(v -> addItem());
        btnSubmit.setOnClickListener(v -> saveRequest());
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Supplier selectedSupplier = database.supplierDao().getById(supplierId);
            List<Produk> supplierProducts = database.produkDao().getBySupplier(supplierId);
            List<Kategori> allCategories = database.kategoriDao().getAll();
            runOnUiThread(() -> {
                if (selectedSupplier == null || !selectedSupplier.isAktif()) {
                    Toast.makeText(this, "Supplier tidak tersedia atau sudah nonaktif.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                supplier = selectedSupplier;
                products.clear();
                products.addAll(supplierProducts);
                categories.clear();
                categories.addAll(allCategories);
                bindSupplier();
                setupSelectors();
            });
        });
    }

    private void bindSupplier() {
        ((TextView) findViewById(R.id.tv_purchase_header_title))
                .setText("SUPPLIER / " + supplier.getNama_supplier() + " / TAMBAH PEMBELIAN");
        ((TextView) findViewById(R.id.tv_purchase_supplier_name)).setText(supplier.getNama_supplier());
        ((TextView) findViewById(R.id.tv_purchase_supplier_phone)).setText(valueOrDash(supplier.getNo_telepon()));
        ((TextView) findViewById(R.id.tv_purchase_supplier_address)).setText(valueOrDash(supplier.getAlamat_supplier()));
        ((TextView) findViewById(R.id.tv_purchase_supplier_email)).setText(valueOrDash(supplier.getEmail()));
        TextView tvStatus = findViewById(R.id.tv_purchase_supplier_status);
        tvStatus.setText("Aktif");
        tvStatus.setBackgroundResource(R.drawable.bg_status_aktif);
        ImageView image = findViewById(R.id.iv_purchase_supplier_image);
        image.setImageResource(R.drawable.amplang_background);
        if (!TextUtils.isEmpty(supplier.getImage_uri())) {
            try {
                image.setImageURI(android.net.Uri.parse(supplier.getImage_uri()));
            } catch (Exception ignored) {
                image.setImageResource(R.drawable.amplang_background);
            }
        }
    }

    private void setupSelectors() {
        List<String> productNames = new ArrayList<>();
        productNames.add("-- Pilih Produk --");
        for (Produk product : products) {
            productNames.add(product.getNama_produk());
        }
        spinnerProduct.setAdapter(createSpinnerAdapter(productNames));

        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("-- Pilih Kategori --");
        for (Kategori category : categories) {
            categoryNames.add(category.getNama_kategori());
        }
        spinnerCategory.setAdapter(createSpinnerAdapter(categoryNames));
    }

    private ArrayAdapter<String> createSpinnerAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void addItem() {
        if (supplier == null) {
            Toast.makeText(this, "Data supplier masih dimuat.", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = parsePositiveInt(etQuantity);
        double buyPrice = parsePositiveDouble(etBuyPrice);
        if (quantity <= 0 || buyPrice <= 0) {
            Toast.makeText(this, "Jumlah dan harga beli harus lebih dari 0.", Toast.LENGTH_SHORT).show();
            return;
        }
        PurchaseDraftItem item;
        if (rbExisting.isChecked()) {
            int selected = spinnerProduct.getSelectedItemPosition() - 1;
            if (selected < 0 || selected >= products.size()) {
                Toast.makeText(this, "Pilih produk yang akan dipesan.", Toast.LENGTH_SHORT).show();
                return;
            }
            Produk product = products.get(selected);
            item = new PurchaseDraftItem(product.getId_produk(), false, product.getNama_produk(),
                    product.getId_kategori_produk(), product.getId_merek(), "", product.getUnit(),
                    product.getHarga_produk(), buyPrice, quantity);
        } else {
            String name = etName.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();
            int categoryPosition = spinnerCategory.getSelectedItemPosition() - 1;
            double sellPrice = parsePositiveDouble(etSellPrice);
            if (name.isEmpty() || brand.isEmpty() || unit.isEmpty()
                    || categoryPosition < 0 || sellPrice <= 0) {
                Toast.makeText(this, "Lengkapi data produk baru dan harga jual.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sellPrice <= buyPrice) {
                Toast.makeText(this, "Harga jual produk baru harus lebih besar dari harga beli.", Toast.LENGTH_SHORT).show();
                return;
            }
            item = new PurchaseDraftItem(null, true, name,
                    categories.get(categoryPosition).getId_kategori(), null, brand, unit,
                    sellPrice, buyPrice, quantity);
        }
        draftItems.add(item);
        draftAdapter.notifyItemInserted(draftItems.size() - 1);
        updateDraftTotal();
        etQuantity.setText("");
        etBuyPrice.setText("");
    }

    private void updateDraftTotal() {
        double total = 0;
        for (PurchaseDraftItem item : draftItems) {
            total += item.getTotalHarga();
        }
        tvTotal.setText("Total Pembelian: " + FormatUtils.formatRupiah(total));
    }

    private void saveRequest() {
        if (draftItems.isEmpty()) {
            Toast.makeText(this, "Tambahkan minimal satu produk.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSubmit.setEnabled(false);
        List<PurchaseDraftItem> itemsToSave = new ArrayList<>(draftItems);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                long now = System.currentTimeMillis();
                String number = "REQ-" + new SimpleDateFormat(
                        "yyyyMMdd-HHmmssSSS", Locale.getDefault()).format(new Date(now));
                database.runInTransaction(() -> {
                    Supplier currentSupplier = database.supplierDao().getById(supplierId);
                    if (currentSupplier == null || !currentSupplier.isAktif()) {
                        throw new IllegalStateException("Supplier sudah nonaktif.");
                    }
                    long requestId = database.stokRequestDao().insert(
                            new StokRequest(supplierId, CURRENT_USER_ID, number, now, "Diproses"));
                    for (PurchaseDraftItem item : itemsToSave) {
                        Integer brandId = item.getMerekId();
                        if (item.isProdukBaru()) {
                            brandId = findOrCreateBrand(item.getNamaMerek());
                        }
                        database.detailStokRequestDao().insert(new DetailStokRequest(
                                (int) requestId, item.getProdukId(), item.getJumlah(), item.isProdukBaru(),
                                item.getNamaProduk(), item.getKategoriId(), brandId, item.getUnit(),
                                item.getHargaJual(), item.getHargaBeli()));
                    }
                });
                runOnUiThread(() -> {
                    Toast.makeText(this, "Permintaan pembelian berhasil dibuat.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception exception) {
                runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Gagal membuat permintaan pembelian: "
                            + exception.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private int findOrCreateBrand(String name) {
        for (Merek brand : database.merekDao().getAll()) {
            if (brand.getNama_merek().equalsIgnoreCase(name)) {
                return brand.getId_merek();
            }
        }
        return (int) database.merekDao().insert(new Merek(name));
    }

    private int parsePositiveInt(EditText input) {
        try {
            return Integer.parseInt(input.getText().toString().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private double parsePositiveDouble(EditText input) {
        try {
            return Double.parseDouble(input.getText().toString().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String valueOrDash(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
