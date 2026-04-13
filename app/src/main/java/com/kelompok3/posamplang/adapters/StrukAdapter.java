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

    public StrukAdapter(List<DetailPesanan> detailPesananList) {
        this.detailPesananList = detailPesananList;
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
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return detailPesananList.size();
    }

    static class StrukViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName, tvItemQty, tvItemHarga, tvItemTotal;
        private NumberFormat formatter;

        public StrukViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemQty = itemView.findViewById(R.id.tv_item_qty);
            tvItemHarga = itemView.findViewById(R.id.tv_item_harga);
            tvItemTotal = itemView.findViewById(R.id.tv_item_total);
            formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        }

        public void bind(DetailPesanan item) {
            tvItemName.setText(item.getNama_produk_display());
            tvItemQty.setText(String.valueOf(item.getJumlah_produk()));
            tvItemHarga.setText(formatRupiah(item.getHarga_produk()));
            tvItemTotal.setText(formatRupiah(item.getTotal_harga()));
        }

        private String formatRupiah(double amount) {
            return formatter.format(amount).replace("Rp", "Rp ").replace(",00", "");
        }
    }
}
