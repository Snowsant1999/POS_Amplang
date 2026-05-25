package com.kelompok3.posamplang.activities.supplier;

import android.app.Dialog;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.database.AppDatabase;
import com.kelompok3.posamplang.models.Supplier;
import com.kelompok3.posamplang.parent.BaseActivity;

import java.util.concurrent.Executors;

public class SupplierDetailActivity extends BaseActivity {

    private TextView tvHeaderTitle;
    private TextView tvNama;
    private TextView tvKontak;
    private TextView tvAlamat;
    private TextView tvEmail;
    private TextView tvStatus;
    private ImageView ivSupplierImage;
    private AppDatabase database;
    private Supplier supplier;
    private int supplierId;
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
        setContentView(R.layout.activity_supplier_detail);

        setupSidebar(R.id.btn_nav_supplier);
        database = AppDatabase.getInstance(this);
        supplierId = getIntent().getIntExtra("SUPPLIER_ID", -1);
        initViews();

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
        ImageButton btnBack = findViewById(R.id.btn_back);
        MaterialButton btnEdit = findViewById(R.id.btn_edit_detail);
        MaterialButton btnTambahPembelian = findViewById(R.id.btn_tambah_pembelian_detail);
        Spinner spinnerStatus = findViewById(R.id.spinner_riwayat_status);
        Spinner spinnerWaktu = findViewById(R.id.spinner_riwayat_waktu);

        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            if (supplier != null) {
                showEditSupplierDialog();
            }
        });
        btnTambahPembelian.setOnClickListener(v -> Toast.makeText(
                this, "Fitur tambah pembelian belum tersedia.", Toast.LENGTH_SHORT).show());

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, new String[]{"Semua Status"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> waktuAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, new String[]{"Semua Waktu"});
        waktuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWaktu.setAdapter(waktuAdapter);
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

    private String valueOrDash(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    private void showEditSupplierDialog() {
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

        tvTitle.setText("Edit Supplier");
        etNama.setText(supplier.getNama_supplier());
        etKontak.setText(supplier.getNo_telepon());
        etAlamat.setText(supplier.getAlamat_supplier());
        etEmail.setText(supplier.getEmail());
        selectedImageUri = TextUtils.isEmpty(supplier.getImage_uri())
                ? null : Uri.parse(supplier.getImage_uri());
        activeImagePreview = ivImagePreview;
        loadSupplierImage(ivImagePreview, supplier.getImage_uri());
        rbAktif.setChecked(supplier.isAktif());
        rbNonaktif.setChecked(!supplier.isAktif());

        dialog.findViewById(R.id.btn_pilih_gambar_supplier).setOnClickListener(v -> openImagePicker());
        dialog.findViewById(R.id.iv_close_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_batal_dialog).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_simpan_dialog).setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String kontak = etKontak.getText().toString().trim();
            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama supplier wajib diisi");
                return;
            }
            if (TextUtils.isEmpty(kontak)) {
                etKontak.setError("No kontak wajib diisi");
                return;
            }

            boolean aktif = rbAktif.isChecked();
            supplier.setNama_supplier(nama);
            supplier.setNo_telepon(kontak);
            supplier.setAlamat_supplier(etAlamat.getText().toString().trim());
            supplier.setEmail(etEmail.getText().toString().trim());
            supplier.setImage_uri(selectedImageUri == null ? "" : selectedImageUri.toString());
            supplier.setAktif(aktif);
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

    private void loadSupplierImage(ImageView imageView, String imageUri) {
        imageView.setImageResource(R.drawable.amplang_background);
        if (TextUtils.isEmpty(imageUri)) {
            return;
        }
        try {
            imageView.setImageURI(Uri.parse(imageUri));
        } catch (Exception ignored) {
            imageView.setImageResource(R.drawable.amplang_background);
        }
    }
}
