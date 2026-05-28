package com.kelompok3.posamplang.activities.supplier;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.adapters.RequestDetailAdapter;
import com.kelompok3.posamplang.adapters.SupplierRequestAdapter;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.DetailStokRequest;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.models.StokAdjustment;
import com.kelompok3.posamplang.models.StokRequest;
import com.kelompok3.posamplang.models.StokRequestSummary;
import com.kelompok3.posamplang.models.Supplier;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.FixedViewportScaler;
import com.kelompok3.posamplang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SupplierDetailActivity extends BaseActivity implements SupplierRequestAdapter.Listener {

    private static final int ITEMS_PER_PAGE = 10;

    private final List<StokRequestSummary> allRequests = new ArrayList<>();
    private final List<StokRequestSummary> filteredRequests = new ArrayList<>();
    private final List<StokRequestSummary> pagedRequests = new ArrayList<>();

    private TextView tvHeaderTitle;
    private TextView tvNama;
    private TextView tvKontak;
    private TextView tvAlamat;
    private TextView tvEmail;
    private TextView tvStatus;
    private ImageView ivSupplierImage;
    private Spinner spinnerRequestStatus;
    private Spinner spinnerRequestTime;
    private EditText etSearchRequest;
    private TextView tvEmptyRequests;
    private TextView tvRequestPagination;
    private TextView tvCurrentPage;
    private Button btnPagePrev;
    private Button btnPageNext;
    private SupplierRequestAdapter requestAdapter;

    private AppDatabase database;
    private Supplier supplier;
    private int supplierId;
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
                    // Some providers grant sufficient read access without persistence.
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
        setContentView(R.layout.activity_supplier_detail);

        setupSidebar(R.id.btn_nav_supplier);
        database = AppDatabase.getInstance(this);
        supplierId = getIntent().getIntExtra("SUPPLIER_ID", -1);
        initViews();
        setupRequestFilters();

        if (supplierId == -1) {
            Toast.makeText(this, "Data supplier tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadSupplier();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvNama = findViewById(R.id.tv_detail_nama);
        tvKontak = findViewById(R.id.tv_detail_kontak);
        tvAlamat = findViewById(R.id.tv_detail_alamat);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvStatus = findViewById(R.id.tv_status_detail);
        ivSupplierImage = findViewById(R.id.iv_supplier_detail_image);
        spinnerRequestStatus = findViewById(R.id.spinner_riwayat_status);
        spinnerRequestTime = findViewById(R.id.spinner_riwayat_waktu);
        etSearchRequest = findViewById(R.id.et_cari_pembelian);
        tvEmptyRequests = findViewById(R.id.tv_empty_supplier_requests);
        tvRequestPagination = findViewById(R.id.tv_request_pagination_info);
        tvCurrentPage = findViewById(R.id.tv_current_page_riwayat);
        btnPagePrev = findViewById(R.id.btn_page_prev_riwayat);
        btnPageNext = findViewById(R.id.btn_page_next_riwayat);

        RecyclerView rvRequests = findViewById(R.id.rv_supplier_requests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new SupplierRequestAdapter(pagedRequests, this);
        rvRequests.setAdapter(requestAdapter);

        ImageButton btnBack = findViewById(R.id.btn_back);
        MaterialButton btnEdit = findViewById(R.id.btn_edit_detail);
        MaterialButton btnTambahPembelian = findViewById(R.id.btn_tambah_pembelian_detail);
        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            if (supplier != null) {
                showEditSupplierDialog();
            }
        });
        btnTambahPembelian.setOnClickListener(v -> openPurchasePage());

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

    private void setupRequestFilters() {
        String[] statuses = {"Semua Status", "Diproses", "Selesai", "Gagal", "Dibatalkan"};
        String[] times = {"Terbaru", "Terlama"};
        spinnerRequestStatus.setAdapter(createSpinnerAdapter(statuses));
        spinnerRequestTime.setAdapter(createSpinnerAdapter(times));

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRequests();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
        spinnerRequestStatus.setOnItemSelectedListener(listener);
        spinnerRequestTime.setOnItemSelectedListener(listener);
        etSearchRequest.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRequests();
            }
        });
    }

    private ArrayAdapter<String> createSpinnerAdapter(String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void loadSupplier() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Supplier selectedSupplier = database.supplierDao().getById(supplierId);
            runOnUiThread(() -> {
                if (selectedSupplier == null) {
                    Toast.makeText(this, "Data supplier sudah tidak tersedia.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                supplier = selectedSupplier;
                bindSupplier();
                loadRequests();
            });
        });
    }

    private void bindSupplier() {
        tvHeaderTitle.setText("SUPPLIER / " + supplier.getNama_supplier());
        tvNama.setText(supplier.getNama_supplier());
        tvKontak.setText(valueOrDash(supplier.getNo_telepon()));
        tvAlamat.setText(valueOrDash(supplier.getAlamat_supplier()));
        tvEmail.setText(valueOrDash(supplier.getEmail()));
        tvStatus.setText(supplier.isAktif() ? "Aktif" : "Nonaktif");
        tvStatus.setBackgroundResource(
                supplier.isAktif() ? R.drawable.bg_status_aktif : R.drawable.bg_status_nonaktif);
        loadSupplierImage(ivSupplierImage, supplier.getImage_uri());
    }

    private void loadRequests() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<StokRequestSummary> requests = database.stokRequestDao().getSummaryBySupplier(supplierId);
            runOnUiThread(() -> {
                allRequests.clear();
                allRequests.addAll(requests);
                filterRequests();
            });
        });
    }

    private void filterRequests() {
        if (spinnerRequestStatus == null || spinnerRequestTime == null || etSearchRequest == null) {
            return;
        }
        String keyword = etSearchRequest.getText().toString().trim().toLowerCase(Locale.ROOT);
        String selectedStatus = spinnerRequestStatus.getSelectedItem() == null
                ? "Semua Status" : spinnerRequestStatus.getSelectedItem().toString();
        filteredRequests.clear();
        for (StokRequestSummary request : allRequests) {
            boolean statusMatches = "Semua Status".equals(selectedStatus)
                    || selectedStatus.equals(request.getStatus());
            boolean searchMatches = keyword.isEmpty()
                    || request.getNomor_request().toLowerCase(Locale.ROOT).contains(keyword);
            if (statusMatches && searchMatches) {
                filteredRequests.add(request);
            }
        }
        Comparator<StokRequestSummary> comparator =
                Comparator.comparingLong(StokRequestSummary::getTanggal_request);
        if (spinnerRequestTime.getSelectedItemPosition() == 0) {
            comparator = comparator.reversed();
        }
        Collections.sort(filteredRequests, comparator);
        currentPage = 1;
        showCurrentPage();
    }

    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil(filteredRequests.size() / (double) ITEMS_PER_PAGE));
    }

    private void showCurrentPage() {
        int pages = getTotalPages();
        currentPage = Math.max(1, Math.min(currentPage, pages));
        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredRequests.size());
        pagedRequests.clear();
        if (start < filteredRequests.size()) {
            pagedRequests.addAll(filteredRequests.subList(start, end));
        }
        requestAdapter.notifyDataSetChanged();

        RecyclerView rvRequests = findViewById(R.id.rv_supplier_requests);
        boolean empty = pagedRequests.isEmpty();
        rvRequests.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmptyRequests.setVisibility(empty ? View.VISIBLE : View.GONE);
        tvRequestPagination.setText(empty ? "Menampilkan 0 dari 0 data"
                : "Menampilkan " + (start + 1) + "-" + end + " dari " + filteredRequests.size() + " data");
        tvCurrentPage.setText(String.valueOf(currentPage));
        btnPagePrev.setEnabled(currentPage > 1);
        btnPageNext.setEnabled(currentPage < pages);
        btnPagePrev.setAlpha(currentPage > 1 ? 1f : 0.5f);
        btnPageNext.setAlpha(currentPage < pages ? 1f : 0.5f);
    }

    private void openPurchasePage() {
        if (supplier == null || !supplier.isAktif()) {
            Toast.makeText(this, "Supplier nonaktif tidak dapat menerima permintaan.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, SupplierPurchaseActivity.class);
        intent.putExtra("SUPPLIER_ID", supplierId);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onDetailClick(StokRequestSummary request) {
        openRequestDetail(request);
    }

    @Override
    public void onStatusClick(StokRequestSummary request) {
        openRequestDetail(request);
    }

    private void openRequestDetail(StokRequestSummary summary) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<DetailStokRequest> details = database.detailStokRequestDao().getByRequest(summary.getId_request());
            runOnUiThread(() -> showRequestDetailDialog(summary, details));
        });
    }

    private void showRequestDetailDialog(StokRequestSummary summary, List<DetailStokRequest> details) {
        Dialog dialog = createDialog(R.layout.dialog_detail_pembelian_supplier);
        ((TextView) dialog.findViewById(R.id.tv_request_detail_title))
                .setText("Detail Pembelian / " + summary.getNomor_request());
        ((TextView) dialog.findViewById(R.id.tv_request_detail_date))
                .setText(new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("id", "ID"))
                        .format(new Date(summary.getTanggal_request())));
        TextView tvDialogStatus = dialog.findViewById(R.id.tv_request_detail_status);
        tvDialogStatus.setText(summary.getStatus());
        setStatusBackground(tvDialogStatus, summary.getStatus());
        ((TextView) dialog.findViewById(R.id.tv_request_detail_total))
                .setText("Total Pembelian: " + FormatUtils.formatRupiah(summary.getTotal_harga()));

        RecyclerView rvItems = dialog.findViewById(R.id.rv_request_detail_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new RequestDetailAdapter(details));

        LinearLayout actions = dialog.findViewById(R.id.container_request_actions);
        actions.setVisibility("Diproses".equals(summary.getStatus()) ? View.VISIBLE : View.GONE);
        dialog.findViewById(R.id.btn_close_request_detail).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_request_cancel).setOnClickListener(
                v -> updateRequestStatus(dialog, summary.getId_request(), "Dibatalkan"));
        dialog.findViewById(R.id.btn_request_failed).setOnClickListener(
                v -> updateRequestStatus(dialog, summary.getId_request(), "Gagal"));
        dialog.findViewById(R.id.btn_request_complete).setOnClickListener(
                v -> completeRequest(dialog, summary.getId_request()));
        FixedViewportScaler.showResponsiveDialog(this, dialog, 720, 720);
    }

    private void updateRequestStatus(Dialog dialog, int requestId, String status) {
        Executors.newSingleThreadExecutor().execute(() -> {
            StokRequest request = database.stokRequestDao().getById(requestId);
            if (request == null || !"Diproses".equals(request.getStatus())) {
                runOnUiThread(() -> Toast.makeText(this, "Status request tidak dapat diubah.", Toast.LENGTH_SHORT).show());
                return;
            }
            request.setStatus(status);
            database.stokRequestDao().update(request);
            runOnUiThread(() -> {
                dialog.dismiss();
                Toast.makeText(this, "Status diubah menjadi " + status + ".", Toast.LENGTH_SHORT).show();
                loadRequests();
            });
        });
    }

    private void completeRequest(Dialog dialog, int requestId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                database.runInTransaction(() -> {
                    StokRequest request = database.stokRequestDao().getById(requestId);
                    if (request == null || !"Diproses".equals(request.getStatus())) {
                        throw new IllegalStateException("Request sudah diproses.");
                    }
                    List<DetailStokRequest> details = database.detailStokRequestDao().getByRequest(requestId);
                    if (details.isEmpty()) {
                        throw new IllegalStateException("Detail request kosong.");
                    }
                    long completionTime = System.currentTimeMillis();
                    for (DetailStokRequest detail : details) {
                        int productId;
                        if (detail.isProduk_baru()) {
                            if (detail.getId_kategori_produk() == null || detail.getId_merek() == null) {
                                throw new IllegalStateException("Data produk baru tidak lengkap.");
                            }
                            Produk product = new Produk(detail.getId_kategori_produk(), detail.getId_merek(),
                                    supplierId, detail.getNama_produk(), detail.getUnit(),
                                    detail.getHarga_jual(), detail.getJumlah_stok(), true);
                            productId = (int) database.produkDao().insert(product);
                            detail.setId_produk(productId);
                            database.detailStokRequestDao().update(detail);
                        } else {
                            if (detail.getId_produk() == null) {
                                throw new IllegalStateException("Produk request tidak tersedia.");
                            }
                            Produk product = database.produkDao().getById(detail.getId_produk());
                            if (product == null || product.getId_supplier() != supplierId) {
                                throw new IllegalStateException("Produk tidak berasal dari supplier ini.");
                            }
                            productId = product.getId_produk();
                            database.produkDao().tambahStok(productId, detail.getJumlah_stok());
                        }
                        database.stokAdjustmentDao().insert(new StokAdjustment(
                                productId, request.getId_users(), completionTime,
                                "Pembelian Supplier", detail.getJumlah_stok()));
                    }
                    request.setStatus("Selesai");
                    request.setTanggal_selesai(completionTime);
                    database.stokRequestDao().update(request);
                });
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Pembelian selesai, stok telah diperbarui.", Toast.LENGTH_SHORT).show();
                    loadRequests();
                });
            } catch (Exception exception) {
                runOnUiThread(() -> Toast.makeText(
                        this, "Gagal menyelesaikan pembelian: " + exception.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private Dialog createDialog(int layoutResource) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(layoutResource);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void setStatusBackground(TextView view, String status) {
        if ("Selesai".equals(status)) {
            view.setBackgroundResource(R.drawable.bg_status_aktif);
        } else if ("Diproses".equals(status)) {
            view.setBackgroundResource(R.drawable.bg_status_diproses);
        } else {
            view.setBackgroundResource(R.drawable.bg_status_nonaktif);
        }
    }

    private String valueOrDash(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    private void showEditSupplierDialog() {
        Dialog dialog = createDialog(R.layout.dialog_tambah_supplier);
        TextView tvTitle = dialog.findViewById(R.id.tv_title_supplier_dialog);
        EditText etNama = dialog.findViewById(R.id.et_nama_supplier);
        EditText etKontak = dialog.findViewById(R.id.et_kontak_supplier);
        EditText etAlamat = dialog.findViewById(R.id.et_alamat_supplier);
        EditText etEmail = dialog.findViewById(R.id.et_email_supplier);
        ImageView ivImagePreview = dialog.findViewById(R.id.iv_supplier_image_preview);
        RadioButton rbAktif = dialog.findViewById(R.id.rb_aktif);
        RadioButton rbNonaktif = dialog.findViewById(R.id.rb_nonaktif);

        tvTitle.setText("Edit Supplier");
        etNama.setText(supplier.getNama_supplier());
        etKontak.setText(supplier.getNo_telepon());
        etAlamat.setText(supplier.getAlamat_supplier());
        etEmail.setText(supplier.getEmail());
        selectedImageUri = TextUtils.isEmpty(supplier.getImage_uri()) ? null : Uri.parse(supplier.getImage_uri());
        activeImagePreview = ivImagePreview;
        loadSupplierImage(ivImagePreview, supplier.getImage_uri());
        rbAktif.setChecked(supplier.isAktif());
        rbNonaktif.setChecked(!supplier.isAktif());

        dialog.findViewById(R.id.btn_pilih_gambar_supplier).setOnClickListener(v -> openImagePicker());
        dialog.findViewById(R.id.iv_close_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_batal_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_simpan_dialog).setOnClickListener(v -> {
            String name = etNama.getText().toString().trim();
            String phone = etKontak.getText().toString().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Nama dan kontak supplier wajib diisi.", Toast.LENGTH_SHORT).show();
                return;
            }
            supplier.setNama_supplier(name);
            supplier.setNo_telepon(phone);
            supplier.setAlamat_supplier(etAlamat.getText().toString().trim());
            supplier.setEmail(etEmail.getText().toString().trim());
            supplier.setImage_uri(selectedImageUri == null ? "" : selectedImageUri.toString());
            supplier.setAktif(rbAktif.isChecked());
            Executors.newSingleThreadExecutor().execute(() -> {
                database.supplierDao().update(supplier);
                runOnUiThread(() -> {
                    dialog.dismiss();
                    bindSupplier();
                    Toast.makeText(this, "Supplier berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                });
            });
        });
        dialog.setOnDismissListener(ignored -> {
            activeImagePreview = null;
            selectedImageUri = null;
        });
        FixedViewportScaler.showResponsiveDialog(this, dialog, 600, 720);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        supplierImageLauncher.launch(intent);
    }

    private void loadSupplierImage(ImageView imageView, String imageUri) {
        imageView.setImageResource(R.drawable.amplang_background);
        if (!TextUtils.isEmpty(imageUri)) {
            try {
                imageView.setImageURI(Uri.parse(imageUri));
            } catch (Exception ignored) {
                imageView.setImageResource(R.drawable.amplang_background);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (database != null && supplierId != -1 && supplier != null) {
            loadRequests();
        }
    }
}
