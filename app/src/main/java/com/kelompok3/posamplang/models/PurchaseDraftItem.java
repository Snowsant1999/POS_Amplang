package com.kelompok3.posamplang.models;

public class PurchaseDraftItem {
    private final Integer produkId;
    private final boolean produkBaru;
    private final String namaProduk;
    private final Integer kategoriId;
    private Integer merekId;
    private final String namaMerek;
    private final String unit;
    private final double hargaJual;
    private final double hargaBeli;
    private final int jumlah;

    public PurchaseDraftItem(Integer produkId, boolean produkBaru, String namaProduk, Integer kategoriId,
                             Integer merekId, String namaMerek, String unit, double hargaJual,
                             double hargaBeli, int jumlah) {
        this.produkId = produkId;
        this.produkBaru = produkBaru;
        this.namaProduk = namaProduk;
        this.kategoriId = kategoriId;
        this.merekId = merekId;
        this.namaMerek = namaMerek;
        this.unit = unit;
        this.hargaJual = hargaJual;
        this.hargaBeli = hargaBeli;
        this.jumlah = jumlah;
    }

    public Integer getProdukId() { return produkId; }
    public boolean isProdukBaru() { return produkBaru; }
    public String getNamaProduk() { return namaProduk; }
    public Integer getKategoriId() { return kategoriId; }
    public Integer getMerekId() { return merekId; }
    public void setMerekId(Integer merekId) { this.merekId = merekId; }
    public String getNamaMerek() { return namaMerek; }
    public String getUnit() { return unit; }
    public double getHargaJual() { return hargaJual; }
    public double getHargaBeli() { return hargaBeli; }
    public int getJumlah() { return jumlah; }
    public double getTotalHarga() { return hargaBeli * jumlah; }
}
