package com.kelompok3.posamplang.models;

import java.util.Date;

public class PembayaranPesanan {
    private int id_pembayaran;
    private int id_pesanan;
    private String metode_pembayaran;
    private String status_pembayaran;
    private Date tanggal_pembayaran;
    private double total_pembayaran;
    private double total_kembalian;

    public PembayaranPesanan(int id_pembayaran, int id_pesanan, String metode_pembayaran, String status_pembayaran, Date tanggal_pembayaran, double total_pembayaran, double total_kembalian) {
        this.id_pembayaran = id_pembayaran;
        this.id_pesanan = id_pesanan;
        this.metode_pembayaran = metode_pembayaran;
        this.status_pembayaran = status_pembayaran;
        this.tanggal_pembayaran = tanggal_pembayaran;
        this.total_pembayaran = total_pembayaran;
        this.total_kembalian = total_kembalian;
    }

    public int getId_pembayaran() { return id_pembayaran; }
    public void setId_pembayaran(int id_pembayaran) { this.id_pembayaran = id_pembayaran; }
    public int getId_pesanan() { return id_pesanan; }
    public void setId_pesanan(int id_pesanan) { this.id_pesanan = id_pesanan; }
    public String getMetode_pembayaran() { return metode_pembayaran; }
    public void setMetode_pembayaran(String metode_pembayaran) { this.metode_pembayaran = metode_pembayaran; }
    public String getStatus_pembayaran() { return status_pembayaran; }
    public void setStatus_pembayaran(String status_pembayaran) { this.status_pembayaran = status_pembayaran; }
    public Date getTanggal_pembayaran() { return tanggal_pembayaran; }
    public void setTanggal_pembayaran(Date tanggal_pembayaran) { this.tanggal_pembayaran = tanggal_pembayaran; }
    public double getTotal_pembayaran() { return total_pembayaran; }
    public void setTotal_pembayaran(double total_pembayaran) { this.total_pembayaran = total_pembayaran; }
    public double getTotal_kembalian() { return total_kembalian; }
    public void setTotal_kembalian(double total_kembalian) { this.total_kembalian = total_kembalian; }
}
