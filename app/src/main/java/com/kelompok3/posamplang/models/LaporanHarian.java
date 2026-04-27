package com.kelompok3.posamplang.models;

public class LaporanHarian {
    private String tanggal;
    private long penjualanAwal;
    private long penjualanTunai;
    private long uangTunai;
    private long totalPenjualan;
    private long cash;
    private long pembayaranOnline;
    private long bon;

    public LaporanHarian(String tanggal, long penjualanAwal, long penjualanTunai, long uangTunai, 
                         long totalPenjualan, long cash, long pembayaranOnline, long bon) {
        this.tanggal = tanggal;
        this.penjualanAwal = penjualanAwal;
        this.penjualanTunai = penjualanTunai;
        this.uangTunai = uangTunai;
        this.totalPenjualan = totalPenjualan;
        this.cash = cash;
        this.pembayaranOnline = pembayaranOnline;
        this.bon = bon;
    }

    // Getters and Setters
    public String getTanggal() { return tanggal; }
    public long getPenjualanAwal() { return penjualanAwal; }
    public long getPenjualanTunai() { return penjualanTunai; }
    public long getUangTunai() { return uangTunai; }
    public long getTotalPenjualan() { return totalPenjualan; }
    public long getCash() { return cash; }
    public long getPembayaranOnline() { return pembayaranOnline; }
    public long getBon() { return bon; }
}