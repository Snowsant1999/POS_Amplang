package com.kelompok3.posamplang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kelompok3.posamplang.R;
import com.kelompok3.posamplang.models.DetailPesanan;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StrukAdapter extends RecyclerView.Adapter<StrukAdapter.StrukViewHolder> {

    private List<DetailPesanan> detailPesananList;
    private OnItemQuantityChangeListener listener;

    public interface OnItemQuantityChangeListener {
        void onQuantityChanged(int position, int newQty);
        void onItemRemoved(int position);
    }

    public StrukAdapter(List<DetailPesanan> detailPesananList) {
        this.detailPesananList = detailPesananList;
        this.listener = null;
    }

    public StrukAdapter(List<DetailPesanan> detailPesananList, OnItemQuantityChangeListener listener) {
        this.detailPesananList = detailPesananList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StrukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_struk, parent, false);
        return new StrukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StrukViewHolder holder, int position) {
        DetailPesanan item = detailPesananList.get(position);
        holder.bind(item, position, listener);
    }

    @Override
    public int getItemCount() {
        return detailPesananList.size();
    }

    static class StrukViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName, tvItemQty, tvItemHarga, tvItemTotal;
        private View btnPlus, btnMinus;
        private NumberFormat formatter;

        public StrukViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemQty = itemView.findViewById(R.id.tv_item_qty);
            tvItemHarga = itemView.findViewById(R.id.tv_item_harga);
            tvItemTotal = itemView.findViewById(R.id.tv_item_total);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        }

        public void bind(DetailPesanan item, int position, OnItemQuantityChangeListener listener) {
            tvItemName.setText(item.getNama_produk_display());
            tvItemQty.setText(String.valueOf(item.getJumlah_produk()));
            tvItemHarga.setText(formatRupiah(item.getHarga_produk()));
            tvItemTotal.setText(formatRupiah(item.getTotal_harga()));

            if (listener == null) {
                btnPlus.setVisibility(View.GONE);
                btnMinus.setVisibility(View.GONE);
            } else {
                btnPlus.setVisibility(View.VISIBLE);
                btnMinus.setVisibility(View.VISIBLE);
                
                btnPlus.setOnClickListener(v -> {
                    int newQty = item.getJumlah_produk() + 1;
                    listener.onQuantityChanged(position, newQty);
                });

                btnMinus.setOnClickListener(v -> {
                    int newQty = item.getJumlah_produk() - 1;
                    if (newQty > 0) {
                        listener.onQuantityChanged(position, newQty);
                    } else {
                        listener.onItemRemoved(position);
                    }
                });
            }
        }

        private String formatRupiah(double amount) {
            return formatter.format(amount).replace("Rp", "Rp ").replace(",00", "");
        }
    }
}
