package com.kelompok3.posamplang.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    /**
     * Memformat angka menjadi format Rupiah Indonesia.
     * Contoh: 25000 → "Rp25.000"
     *
     * @param amount Jumlah dalam bentuk double.
     * @return String dalam format Rupiah.
     */
    public static String formatRupiah(double amount) {
        return "Rp" + String.format(new Locale("id", "ID"), "%,.0f", amount).replace(".", ",");
    }

    /**
     * Menambahkan TextWatcher ke EditText agar input angka otomatis memiliki format ribuan secara real-time.
     *
     * @param editText EditText tujuan
     * @param listener Callback jika Anda butuh bereaksi terhadap nilai aslinya (misal menghitung kembalian)
     */
    public static void setupRupiahInput(EditText editText, OnValueChangeListener listener) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    // Bersihkan inputan dari titik dll agar menjadi murni angka
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            // Format dengan pemisah ribuan standar
                            String formatted = NumberFormat.getInstance(new Locale("id", "ID")).format(parsed);

                            current = formatted;
                            editText.setText(formatted);
                            editText.setSelection(formatted.length());

                            if (listener != null) {
                                listener.onValueChanged(parsed);
                            }
                        } catch (NumberFormatException e) {
                            if (listener != null) listener.onValueChanged(0);
                        }
                    } else {
                        current = "";
                        editText.setText("");
                        if (listener != null) listener.onValueChanged(0);
                    }

                    editText.addTextChangedListener(this);
                }
            }
        });
    }

    public interface OnValueChangeListener {
        void onValueChanged(double parsedValue);
    }
}
