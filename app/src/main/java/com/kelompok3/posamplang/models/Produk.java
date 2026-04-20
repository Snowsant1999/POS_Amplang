package com.kelompok3.posamplang.models;

public class Produk {
    private int id_produk;
    private int id_kategori_produk;
    private int id_merek;
    private int id_supplier;
    private String nama_produk;
    private String unit;
    private double harga_produk;
    private int stok_tersedia;

    // Constructor untuk data dummy Kasir
    public Produk(int id_produk, String nama_produk, double harga_produk, int stok_tersedia) {
        this.id_produk = id_produk;
        this.nama_produk = nama_produk;
        this.harga_produk = harga_produk;
        this.stok_tersedia = stok_tersedia;
    }

    // Getters sesuai ERD
    public int getId_produk() { return id_produk; }
    public String getNama_produk() { return nama_produk; }
    public double getHarga_produk() { return harga_produk; }
    public int getStok_tersedia() { return stok_tersedia; }
}
