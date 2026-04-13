package com.kelompok3.posamplang.models;

public class DetailPesanan {
    private int id_detail_pesanan;
    private int id_produk;
    private int id_pesanan;
    private int id_users;
    private int jumlah_produk;
    private double harga_produk;
    private double total_harga;
    
    // Field tambahan untuk keperluan display di UI (tidak disimpan di tabel detail_pesanan secara permanen)
    private String nama_produk_display;

    public DetailPesanan(Produk produk, int jumlah) {
        this.id_produk = produk.getId_produk();
        this.nama_produk_display = produk.getNama_produk();
        this.jumlah_produk = jumlah;
        this.harga_produk = produk.getHarga_produk();
        this.total_harga = this.harga_produk * jumlah;
    }

    public void tambahJumlah(int qty) {
        this.jumlah_produk += qty;
        this.total_harga = this.harga_produk * this.jumlah_produk;
    }

    // Getters
    public String getNama_produk_display() { return nama_produk_display; }
    public int getJumlah_produk() { return jumlah_produk; }
    public double getHarga_produk() { return harga_produk; }
    public double getTotal_harga() { return total_harga; }
    public int getId_produk() { return id_produk; }
}
