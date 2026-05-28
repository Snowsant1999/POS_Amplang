package com.kelompok3.posamplang.activities.pengaturan;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.google.android.material.button.MaterialButton;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.parent.BaseActivity;
import com.kelompok3.posamplang.utils.StoreSettings;

public class PengaturanActivity extends BaseActivity {

    private EditText etNamaToko;
    private EditText etAlamatToko;
    private EditText etEmailToko;
    private EditText etNoTelp;
    private MaterialButton btnEdit;
    private MaterialButton btnSimpan;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pengaturan);
        
        setupSidebar(R.id.btn_nav_pengaturan);
        bindViews();
        loadSettings();
        setEditMode(false);

        btnEdit.setOnClickListener(v -> {
            if (editMode) {
                loadSettings();
                setEditMode(false);
            } else {
                setEditMode(true);
                etNamaToko.requestFocus();
                showKeyboard(etNamaToko);
            }
        });
        btnSimpan.setOnClickListener(v -> saveSettings());
    }

    private void bindViews() {
        etNamaToko = findViewById(R.id.valNamaToko);
        etAlamatToko = findViewById(R.id.valAlamatToko);
        etEmailToko = findViewById(R.id.valEmailToko);
        etNoTelp = findViewById(R.id.valNoTelp);
        btnEdit = findViewById(R.id.btnEdit);
        btnSimpan = findViewById(R.id.btnSimpan);
    }

    private void loadSettings() {
        StoreSettings.StoreSettingsData data = StoreSettings.get(this);
        etNamaToko.setText(data.name);
        etAlamatToko.setText(data.address);
        etEmailToko.setText(data.email);
        etNoTelp.setText(data.phone);
        clearErrors();
    }

    private void setEditMode(boolean enabled) {
        editMode = enabled;
        setFieldEnabled(etNamaToko, enabled);
        setFieldEnabled(etAlamatToko, enabled);
        setFieldEnabled(etEmailToko, enabled);
        setFieldEnabled(etNoTelp, enabled);
        btnEdit.setText(enabled ? "Batal" : "Edit");
        btnSimpan.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void setFieldEnabled(EditText field, boolean enabled) {
        field.setEnabled(enabled);
        field.setFocusable(enabled);
        field.setFocusableInTouchMode(enabled);
        field.setCursorVisible(enabled);
        field.setAlpha(1f);
    }

    private void saveSettings() {
        String name = etNamaToko.getText().toString().trim();
        String address = etAlamatToko.getText().toString().trim();
        String email = etEmailToko.getText().toString().trim();
        String phone = etNoTelp.getText().toString().trim();

        if (!validate(name, address, email, phone)) {
            return;
        }

        StoreSettings.save(this, new StoreSettings.StoreSettingsData(name, address, email, phone));
        setEditMode(false);
        applySidebarStoreName();
        hideKeyboard();
        showSuccessDialog();
    }

    private boolean validate(String name, String address, String email, String phone) {
        clearErrors();
        boolean valid = true;
        if (TextUtils.isEmpty(name)) {
            etNamaToko.setError("Nama toko wajib diisi.");
            valid = false;
        }
        if (TextUtils.isEmpty(address)) {
            etAlamatToko.setError("Alamat toko wajib diisi.");
            valid = false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailToko.setError("Email toko tidak valid.");
            valid = false;
        }
        if (TextUtils.isEmpty(phone) || !phone.matches("[0-9+\\-\\s]{6,20}")) {
            etNoTelp.setError("Nomor telepon tidak valid.");
            valid = false;
        }
        return valid;
    }

    private void clearErrors() {
        etNamaToko.setError(null);
        etAlamatToko.setError(null);
        etEmailToko.setError(null);
        etNoTelp.setError(null);
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_berhasil);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ((TextView) dialog.findViewById(R.id.tv_title_dialog_berhasil))
                .setText("Pengaturan Disimpan!");
        ((TextView) dialog.findViewById(R.id.tv_dialog_message))
                .setText("Informasi toko berhasil diperbarui.");
        dialog.findViewById(R.id.btn_cetak_struk).setVisibility(View.GONE);
        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setLayout(getResponsiveDialogWidth(480), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int getResponsiveDialogWidth(int preferredWidthDp) {
        float density = getResources().getDisplayMetrics().density;
        int preferredWidth = (int) (preferredWidthDp * density);
        int availableWidth = getResources().getDisplayMetrics().widthPixels - (int) (48 * density);
        return Math.min(preferredWidth, availableWidth);
    }
}
