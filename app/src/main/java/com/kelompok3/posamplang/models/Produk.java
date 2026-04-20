package com.kelompok3.posamplang.models;

public class Produk {
    private String nama;
    private String kode;
    private String merek;
    private String kategori;
    private String satuan;
    private double hargaBeli;
    private double hargaJual;
    private int stok;
    private int minStok;
    private String status;

    public Produk(String nama, String kode, String merek, String kategori, String satuan, double hargaBeli, double hargaJual, int stok, int minStok, String status) {
        this.nama = nama;
        this.kode = kode;
        this.merek = merek;
        this.kategori = kategori;
        this.satuan = satuan;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.minStok = minStok;
        this.status = status;
    }

    // Getters
    public String getNama() { return nama; }
    public String getKode() { return kode; }
    public String getMerek() { return merek; }
    public String getKategori() { return kategori; }
    public String getSatuan() { return satuan; }
    public double getHargaBeli() { return hargaBeli; }
    public double getHargaJual() { return hargaJual; }
    public int getStok() { return stok; }
    public int getMinStok() { return minStok; }
    public String getStatus() { return status; }
}
