package com.kelompok3.posamplang.activities.supplier;

import android.app.Dialog;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.SupplierAdapter;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Supplier;
import com.kelompok3.posamplang.parent.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SupplierListActivity extends BaseActivity
        implements SupplierAdapter.OnSupplierActionListener {

    private static final int ITEMS_PER_PAGE = 10;

    private final List<Supplier> allSuppliers = new ArrayList<>();
    private final List<Supplier> filteredSuppliers = new ArrayList<>();
    private final List<Supplier> pagedSuppliers = new ArrayList<>();

    private SupplierAdapter adapter;
    private AppDatabase database;
    private EditText etSearch;
    private Spinner spinnerStatus;
    private Spinner spinnerSort;
    private TextView tvPaginationInfo;
    private TextView tvCurrentPage;
    private TextView tvNotification;
    private Button btnPagePrev;
    private Button btnPageNext;
    private int currentPage = 1;
    private Uri selectedImageUri;
    private ImageView activeImagePreview;

    private final ActivityResultLauncher<Intent> supplierImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                    return;
                }
                Uri imageUri = result.getData().getData();
                if (imageUri == null) {
                    return;
                }
                try {
                    getContentResolver().takePersistableUriPermission(
                            imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // Some document providers grant read access without persistable permission.
                }
                selectedImageUri = imageUri;
                if (activeImagePreview != null) {
                    activeImagePreview.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier);

        setupSidebar(R.id.btn_nav_supplier);
        database = AppDatabase.getInstance(this);
        initViews();
        setupFilters();
        setupActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            loadSuppliers();
        }
    }

    private void initViews() {
        RecyclerView rvSupplier = findViewById(R.id.rv_supplier);
        etSearch = findViewById(R.id.et_search_supplier);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerSort = findViewById(R.id.spinner_sort);
        tvPaginationInfo = findViewById(R.id.tv_pagination_info);
        tvCurrentPage = findViewById(R.id.tv_current_page_supplier);
        tvNotification = findViewById(R.id.tv_supplier_notification);
        btnPagePrev = findViewById(R.id.btn_page_prev_supplier);
        btnPageNext = findViewById(R.id.btn_page_next_supplier);

        rvSupplier.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SupplierAdapter(pagedSuppliers, this);
        rvSupplier.setAdapter(adapter);
    }

    private void setupFilters() {
        String[] statuses = {"Semua Status", "Aktif", "Nonaktif"};
        String[] sorts = {"Urutkan: Nama A-Z", "Nama Z-A", "Status Aktif"};

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sorts);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Keep the currently displayed result.
            }
        };
        spinnerStatus.setOnItemSelectedListener(listener);
        spinnerSort.setOnItemSelectedListener(listener);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setupActions() {
        findViewById(R.id.btn_tambah_supplier).setOnClickListener(v -> showSupplierDialog(null));
        btnPagePrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                showCurrentPage();
            }
        });
        btnPageNext.setOnClickListener(v -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                showCurrentPage();
            }
        });
    }

    private void loadSuppliers() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Supplier> suppliers = database.supplierDao().getAll();
            runOnUiThread(() -> {
                allSuppliers.clear();
                allSuppliers.addAll(suppliers);
                applyFilters();
            });
        });
    }

    private void applyFilters() {
        if (spinnerStatus == null || spinnerSort == null || etSearch == null) {
            return;
        }

        String keyword = etSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        int selectedStatus = spinnerStatus.getSelectedItemPosition();
        filteredSuppliers.clear();

        for (Supplier supplier : allSuppliers) {
            boolean matchesKeyword = keyword.isEmpty()
                    || contains(supplier.getNama_supplier(), keyword)
                    || contains(supplier.getNo_telepon(), keyword)
                    || contains(supplier.getAlamat_supplier(), keyword)
                    || contains(supplier.getEmail(), keyword);
            boolean matchesStatus = selectedStatus == 0
                    || (selectedStatus == 1 && supplier.isAktif())
                    || (selectedStatus == 2 && !supplier.isAktif());

            if (matchesKeyword && matchesStatus) {
                filteredSuppliers.add(supplier);
            }
        }

        Comparator<Supplier> byName = Comparator.comparing(
                supplier -> safe(supplier.getNama_supplier()).toLowerCase(Locale.ROOT));
        if (spinnerSort.getSelectedItemPosition() == 1) {
            Collections.sort(filteredSuppliers, byName.reversed());
        } else if (spinnerSort.getSelectedItemPosition() == 2) {
            Collections.sort(filteredSuppliers,
                    Comparator.comparing(Supplier::isAktif).reversed().thenComparing(byName));
        } else {
            Collections.sort(filteredSuppliers, byName);
        }

        currentPage = 1;
        showCurrentPage();
    }

    private boolean contains(String value, String keyword) {
        return safe(value).toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil(filteredSuppliers.size() / (double) ITEMS_PER_PAGE));
    }

    private void showCurrentPage() {
        int totalPages = getTotalPages();
        currentPage = Math.max(1, Math.min(currentPage, totalPages));
        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredSuppliers.size());

        pagedSuppliers.clear();
        if (start < filteredSuppliers.size()) {
            pagedSuppliers.addAll(filteredSuppliers.subList(start, end));
        }
        adapter.notifyDataSetChanged();

        if (filteredSuppliers.isEmpty()) {
            tvPaginationInfo.setText("Tidak ada data supplier");
        } else {
            tvPaginationInfo.setText("Menampilkan " + (start + 1) + "-" + end
                    + " dari " + filteredSuppliers.size() + " data");
        }
        tvCurrentPage.setText(String.valueOf(currentPage));
        btnPagePrev.setEnabled(currentPage > 1);
        btnPageNext.setEnabled(currentPage < totalPages);
        btnPagePrev.setAlpha(currentPage > 1 ? 1f : 0.5f);
        btnPageNext.setAlpha(currentPage < totalPages ? 1f : 0.5f);
    }

    private void showSupplierDialog(Supplier supplier) {
        boolean isEdit = supplier != null;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tambah_supplier);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dialog.findViewById(R.id.tv_title_supplier_dialog);
        EditText etNama = dialog.findViewById(R.id.et_nama_supplier);
        EditText etKontak = dialog.findViewById(R.id.et_kontak_supplier);
        EditText etAlamat = dialog.findViewById(R.id.et_alamat_supplier);
        EditText etEmail = dialog.findViewById(R.id.et_email_supplier);
        ImageView ivImagePreview = dialog.findViewById(R.id.iv_supplier_image_preview);
        RadioButton rbAktif = dialog.findViewById(R.id.rb_aktif);
        RadioButton rbNonaktif = dialog.findViewById(R.id.rb_nonaktif);

        tvTitle.setText(isEdit ? "Edit Supplier" : "Tambah Supplier");
        selectedImageUri = isEdit && !TextUtils.isEmpty(supplier.getImage_uri())
                ? Uri.parse(supplier.getImage_uri()) : null;
        activeImagePreview = ivImagePreview;
        loadSupplierImage(ivImagePreview, selectedImageUri);
        if (isEdit) {
            etNama.setText(supplier.getNama_supplier());
            etKontak.setText(supplier.getNo_telepon());
            etAlamat.setText(supplier.getAlamat_supplier());
            etEmail.setText(supplier.getEmail());
            rbAktif.setChecked(supplier.isAktif());
            rbNonaktif.setChecked(!supplier.isAktif());
        }

        dialog.findViewById(R.id.btn_pilih_gambar_supplier).setOnClickListener(v -> openImagePicker());
        dialog.findViewById(R.id.iv_close_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_batal_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_simpan_dialog).setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String kontak = etKontak.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String imageUri = selectedImageUri == null ? "" : selectedImageUri.toString();
            boolean aktif = rbAktif.isChecked();

            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama supplier wajib diisi");
                return;
            }
            if (TextUtils.isEmpty(kontak)) {
                etKontak.setError("No kontak wajib diisi");
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                if (isEdit) {
                    supplier.setNama_supplier(nama);
                    supplier.setNo_telepon(kontak);
                    supplier.setAlamat_supplier(alamat);
                    supplier.setEmail(email);
                    supplier.setImage_uri(imageUri);
                    supplier.setAktif(aktif);
                    database.supplierDao().update(supplier);
                } else {
                    database.supplierDao().insert(
                            new Supplier(nama, alamat, kontak, email, imageUri, aktif));
                }
                runOnUiThread(() -> {
                    dialog.dismiss();
                    loadSuppliers();
                    if (isEdit) {
                        showNotification("Supplier Telah Diubah");
                    } else {
                        showSuccessDialog();
                    }
                });
            });
        });
        dialog.setOnDismissListener(ignored -> {
            activeImagePreview = null;
            selectedImageUri = null;
        });

        dialog.show();
        int width = (int) (600 * getResources().getDisplayMetrics().density);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        supplierImageLauncher.launch(intent);
    }

    private void loadSupplierImage(ImageView imageView, Uri imageUri) {
        imageView.setImageResource(R.drawable.amplang_background);
        if (imageUri == null) {
            return;
        }
        try {
            imageView.setImageURI(imageUri);
        } catch (Exception ignored) {
            imageView.setImageResource(R.drawable.amplang_background);
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_berhasil);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ((TextView) dialog.findViewById(R.id.tv_title_dialog_berhasil))
                .setText("Supplier Ditambahkan!");
        ((TextView) dialog.findViewById(R.id.tv_dialog_message))
                .setText("Data supplier baru berhasil disimpan.");
        dialog.findViewById(R.id.btn_cetak_struk).setVisibility(View.GONE);
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setLayout(getResponsiveDialogWidth(480), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int getResponsiveDialogWidth(int preferredWidthDp) {
        float density = getResources().getDisplayMetrics().density;
        int preferredWidth = (int) (preferredWidthDp * density);
        int horizontalMargin = (int) (24 * density);
        int availableWidth = getResources().getDisplayMetrics().widthPixels - (horizontalMargin * 2);
        return Math.min(preferredWidth, availableWidth);
    }

    private void showNotification(String message) {
        tvNotification.setText(message);
        tvNotification.setVisibility(View.VISIBLE);
        tvNotification.removeCallbacks(hideNotification);
        tvNotification.postDelayed(hideNotification, 3000);
    }

    private final Runnable hideNotification = () -> tvNotification.setVisibility(View.GONE);

    @Override
    public void onDetailClick(Supplier supplier) {
        Intent intent = new Intent(this, SupplierDetailActivity.class);
        intent.putExtra("SUPPLIER_ID", supplier.getId_supplier());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Supplier supplier) {
        showSupplierDialog(supplier);
    }

    @Override
    public void onDeleteClick(Supplier supplier) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hapus Supplier")
                .setMessage("Hapus data supplier " + supplier.getNama_supplier() + "?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", (dialog, which) ->
                        Executors.newSingleThreadExecutor().execute(() -> {
                            try {
                                database.supplierDao().delete(supplier);
                                runOnUiThread(() -> {
                                    loadSuppliers();
                                    showNotification("Supplier Telah Dihapus");
                                });
                            } catch (Exception exception) {
                                runOnUiThread(() -> Toast.makeText(
                                        this,
                                        "Supplier masih digunakan oleh produk dan tidak dapat dihapus.",
                                        Toast.LENGTH_SHORT).show());
                            }
                        }))
                .show();
    }
}
