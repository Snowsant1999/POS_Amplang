package com.kelompok3.posamplang.models;

import java.util.Date;

public class StokAdjustment {
    private int id_stok_adjust;
    private int id_produk;
    private int id_users;
    private Date tanggal;
    private String tipe;
    private int jumlah_produk;

    public StokAdjustment(int id_stok_adjust, int id_produk, int id_users, Date tanggal, String tipe, int jumlah_produk) {
        this.id_stok_adjust = id_stok_adjust;
        this.id_produk = id_produk;
        this.id_users = id_users;
        this.tanggal = tanggal;
        this.tipe = tipe;
        this.jumlah_produk = jumlah_produk;
    }

    public int getId_stok_adjust() { return id_stok_adjust; }
    public void setId_stok_adjust(int id_stok_adjust) { this.id_stok_adjust = id_stok_adjust; }
    public int getId_produk() { return id_produk; }
    public void setId_produk(int id_produk) { this.id_produk = id_produk; }
    public int getId_users() { return id_users; }
    public void setId_users(int id_users) { this.id_users = id_users; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public int getJumlah_produk() { return jumlah_produk; }
    public void setJumlah_produk(int jumlah_produk) { this.jumlah_produk = jumlah_produk; }
}
