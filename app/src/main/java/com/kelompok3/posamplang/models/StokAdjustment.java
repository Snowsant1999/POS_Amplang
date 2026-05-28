package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "stok_adjustment",
    foreignKeys = {
        @ForeignKey(entity = Produk.class, parentColumns = "id_produk", childColumns = "id_produk", onDelete = ForeignKey.RESTRICT),
        @ForeignKey(entity = User.class, parentColumns = "id_users", childColumns = "id_users", onDelete = ForeignKey.RESTRICT)
    },
    indices = {
        @Index("id_produk"),
        @Index("id_users")
    }
)
public class StokAdjustment {

    @PrimaryKey(autoGenerate = true)
    private int id_stok_adjus;
    private int id_produk;
    private int id_users;
    private long tanggal; // timestamp
    private String tipe;  // contoh: "masuk", "keluar", "koreksi"
    private int jumlah_produk;

    public StokAdjustment(int id_produk, int id_users, long tanggal, String tipe, int jumlah_produk) {
        this.id_produk = id_produk;
        this.id_users = id_users;
        this.tanggal = tanggal;
        this.tipe = tipe;
        this.jumlah_produk = jumlah_produk;
    }

    public int getId_stok_adjus() { return id_stok_adjus; }
    public void setId_stok_adjus(int id_stok_adjus) { this.id_stok_adjus = id_stok_adjus; }
    public int getId_produk() { return id_produk; }
    public void setId_produk(int id_produk) { this.id_produk = id_produk; }
    public int getId_users() { return id_users; }
    public void setId_users(int id_users) { this.id_users = id_users; }
    public long getTanggal() { return tanggal; }
    public void setTanggal(long tanggal) { this.tanggal = tanggal; }
    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public int getJumlah_produk() { return jumlah_produk; }
    public void setJumlah_produk(int jumlah_produk) { this.jumlah_produk = jumlah_produk; }
}
