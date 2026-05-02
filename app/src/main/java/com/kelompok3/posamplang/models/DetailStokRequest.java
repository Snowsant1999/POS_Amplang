package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "detail_stok_request",
    foreignKeys = {
        @ForeignKey(entity = StokRequest.class, parentColumns = "id_request", childColumns = "id_request", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Produk.class, parentColumns = "id_produk", childColumns = "id_produk", onDelete = ForeignKey.RESTRICT)
    },
    indices = {
        @Index("id_request"),
        @Index("id_produk")
    }
)
public class DetailStokRequest {

    @PrimaryKey(autoGenerate = true)
    private int id_detail_request;
    private int id_request;
    private int id_produk;
    private int jumlah_stok;

    public DetailStokRequest(int id_request, int id_produk, int jumlah_stok) {
        this.id_request = id_request;
        this.id_produk = id_produk;
        this.jumlah_stok = jumlah_stok;
    }

    public int getId_detail_request() { return id_detail_request; }
    public void setId_detail_request(int id_detail_request) { this.id_detail_request = id_detail_request; }
    public int getId_request() { return id_request; }
    public void setId_request(int id_request) { this.id_request = id_request; }
    public int getId_produk() { return id_produk; }
    public void setId_produk(int id_produk) { this.id_produk = id_produk; }
    public int getJumlah_stok() { return jumlah_stok; }
    public void setJumlah_stok(int jumlah_stok) { this.jumlah_stok = jumlah_stok; }
}
