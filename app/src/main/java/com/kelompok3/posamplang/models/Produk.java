package com.kelompok3.posamplang.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "produk",
    foreignKeys = {
        @ForeignKey(entity = Kategori.class, parentColumns = "id_kategori", childColumns = "id_kategori_produk", onDelete = ForeignKey.RESTRICT),
        @ForeignKey(entity = Merek.class, parentColumns = "id_merek", childColumns = "id_merek", onDelete = ForeignKey.RESTRICT),
        @ForeignKey(entity = Supplier.class, parentColumns = "id_supplier", childColumns = "id_supplier", onDelete = ForeignKey.RESTRICT)
    },
    indices = {
        @Index("id_kategori_produk"),
        @Index("id_merek"),
        @Index("id_supplier")
    }
)
public class Produk {

    @PrimaryKey(autoGenerate = true)
    private int id_produk;
    private int id_kategori_produk;
    private int id_merek;
    private int id_supplier;
    private String nama_produk;
    private String unit;
    private double harga_produk;
    private int stok_tersedia;
    @ColumnInfo(defaultValue = "1")
    private boolean aktif;

    @Ignore
    public Produk(int id_kategori_produk, int id_merek, int id_supplier, String nama_produk, String unit, double harga_produk, int stok_tersedia) {
        this(id_kategori_produk, id_merek, id_supplier, nama_produk, unit, harga_produk, stok_tersedia, true);
    }

    public Produk(int id_kategori_produk, int id_merek, int id_supplier, String nama_produk, String unit, double harga_produk, int stok_tersedia, boolean aktif) {
        this.id_kategori_produk = id_kategori_produk;
        this.id_merek = id_merek;
        this.id_supplier = id_supplier;
        this.nama_produk = nama_produk;
        this.unit = unit;
        this.harga_produk = harga_produk;
        this.stok_tersedia = stok_tersedia;
        this.aktif = aktif;
    }

    public int getId_produk() { return id_produk; }
    public void setId_produk(int id_produk) { this.id_produk = id_produk; }
    public int getId_kategori_produk() { return id_kategori_produk; }
    public void setId_kategori_produk(int id_kategori_produk) { this.id_kategori_produk = id_kategori_produk; }
    public int getId_merek() { return id_merek; }
    public void setId_merek(int id_merek) { this.id_merek = id_merek; }
    public int getId_supplier() { return id_supplier; }
    public void setId_supplier(int id_supplier) { this.id_supplier = id_supplier; }
    public String getNama_produk() { return nama_produk; }
    public void setNama_produk(String nama_produk) { this.nama_produk = nama_produk; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getHarga_produk() { return harga_produk; }
    public void setHarga_produk(double harga_produk) { this.harga_produk = harga_produk; }
    public int getStok_tersedia() { return stok_tersedia; }
    public void setStok_tersedia(int stok_tersedia) { this.stok_tersedia = stok_tersedia; }
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }
}
