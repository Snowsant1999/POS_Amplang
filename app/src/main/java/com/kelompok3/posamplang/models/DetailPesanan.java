package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "detail_pesanan",
    foreignKeys = {
        @ForeignKey(entity = Produk.class, parentColumns = "id_produk", childColumns = "id_produk", onDelete = ForeignKey.RESTRICT),
        @ForeignKey(entity = Pesanan.class, parentColumns = "id_pesanan", childColumns = "id_pesanan", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = User.class, parentColumns = "id_users", childColumns = "id_users", onDelete = ForeignKey.RESTRICT)
    },
    indices = {
        @Index("id_produk"),
        @Index("id_pesanan"),
        @Index("id_users")
    }
)
public class DetailPesanan {

    @PrimaryKey(autoGenerate = true)
    private int id_detail_pesanan;
    private int id_produk;
    private int id_pesanan;
    private int id_users;
    private int jumlah_produk;
    private double harga_produk;
    private double total_harga;

    // Field tambahan untuk display di UI (tidak disimpan ke DB)
    @Ignore
    private String nama_produk_display;

    public DetailPesanan(int id_produk, int id_pesanan, int id_users, int jumlah_produk, double harga_produk, double total_harga) {
        this.id_produk = id_produk;
        this.id_pesanan = id_pesanan;
        this.id_users = id_users;
        this.jumlah_produk = jumlah_produk;
        this.harga_produk = harga_produk;
        this.total_harga = total_harga;
    }

    public int getId_detail_pesanan() { return id_detail_pesanan; }
    public void setId_detail_pesanan(int id_detail_pesanan) { this.id_detail_pesanan = id_detail_pesanan; }
    public int getId_produk() { return id_produk; }
    public void setId_produk(int id_produk) { this.id_produk = id_produk; }
    public int getId_pesanan() { return id_pesanan; }
    public void setId_pesanan(int id_pesanan) { this.id_pesanan = id_pesanan; }
    public int getId_users() { return id_users; }
    public void setId_users(int id_users) { this.id_users = id_users; }
    public int getJumlah_produk() { return jumlah_produk; }
    public void setJumlah_produk(int jumlah_produk) {
        this.jumlah_produk = jumlah_produk;
        this.total_harga = this.harga_produk * this.jumlah_produk;
    }
    public double getHarga_produk() { return harga_produk; }
    public void setHarga_produk(double harga_produk) { this.harga_produk = harga_produk; }
    public double getTotal_harga() { return total_harga; }
    public void setTotal_harga(double total_harga) { this.total_harga = total_harga; }
    public String getNama_produk_display() { return nama_produk_display; }
    public void setNama_produk_display(String nama_produk_display) { this.nama_produk_display = nama_produk_display; }

    public void tambahJumlah(int qty) {
        this.jumlah_produk += qty;
        this.total_harga = this.harga_produk * this.jumlah_produk;
    }
}
