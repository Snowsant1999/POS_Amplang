package com.kelompok3.posamplang.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
    private Integer id_produk;
    private int jumlah_stok;

    @ColumnInfo(defaultValue = "0")
    private boolean produk_baru;

    @NonNull
    @ColumnInfo(defaultValue = "''")
    private String nama_produk;

    private Integer id_kategori_produk;
    private Integer id_merek;

    @NonNull
    @ColumnInfo(defaultValue = "''")
    private String unit;

    @ColumnInfo(defaultValue = "0")
    private double harga_jual;

    @ColumnInfo(defaultValue = "0")
    private double harga_beli;

    public DetailStokRequest(int id_request, Integer id_produk, int jumlah_stok, boolean produk_baru,
                             @NonNull String nama_produk, Integer id_kategori_produk, Integer id_merek,
                             @NonNull String unit, double harga_jual, double harga_beli) {
        this.id_request = id_request;
        this.id_produk = id_produk;
        this.jumlah_stok = jumlah_stok;
        this.produk_baru = produk_baru;
        this.nama_produk = nama_produk;
        this.id_kategori_produk = id_kategori_produk;
        this.id_merek = id_merek;
        this.unit = unit;
        this.harga_jual = harga_jual;
        this.harga_beli = harga_beli;
    }

    @Ignore
    public DetailStokRequest(int id_request, int id_produk, int jumlah_stok) {
        this(id_request, id_produk, jumlah_stok, false, "", null, null, "", 0, 0);
    }

    public int getId_detail_request() { return id_detail_request; }
    public void setId_detail_request(int id_detail_request) { this.id_detail_request = id_detail_request; }
    public int getId_request() { return id_request; }
    public void setId_request(int id_request) { this.id_request = id_request; }
    public Integer getId_produk() { return id_produk; }
    public void setId_produk(Integer id_produk) { this.id_produk = id_produk; }
    public int getJumlah_stok() { return jumlah_stok; }
    public void setJumlah_stok(int jumlah_stok) { this.jumlah_stok = jumlah_stok; }
    public boolean isProduk_baru() { return produk_baru; }
    public void setProduk_baru(boolean produk_baru) { this.produk_baru = produk_baru; }
    public String getNama_produk() { return nama_produk; }
    public void setNama_produk(@NonNull String nama_produk) { this.nama_produk = nama_produk; }
    public Integer getId_kategori_produk() { return id_kategori_produk; }
    public void setId_kategori_produk(Integer id_kategori_produk) { this.id_kategori_produk = id_kategori_produk; }
    public Integer getId_merek() { return id_merek; }
    public void setId_merek(Integer id_merek) { this.id_merek = id_merek; }
    public String getUnit() { return unit; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
    public double getHarga_jual() { return harga_jual; }
    public void setHarga_jual(double harga_jual) { this.harga_jual = harga_jual; }
    public double getHarga_beli() { return harga_beli; }
    public void setHarga_beli(double harga_beli) { this.harga_beli = harga_beli; }
    public double getTotal_harga() { return harga_beli * jumlah_stok; }
}
