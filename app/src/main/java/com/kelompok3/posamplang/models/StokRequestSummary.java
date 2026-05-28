package com.kelompok3.posamplang.models;

public class StokRequestSummary {
    private int id_request;
    private int id_supplier;
    private int id_users;
    private String nomor_request;
    private long tanggal_request;
    private long tanggal_selesai;
    private String status;
    private double total_harga;

    public int getId_request() { return id_request; }
    public void setId_request(int id_request) { this.id_request = id_request; }
    public int getId_supplier() { return id_supplier; }
    public void setId_supplier(int id_supplier) { this.id_supplier = id_supplier; }
    public int getId_users() { return id_users; }
    public void setId_users(int id_users) { this.id_users = id_users; }
    public String getNomor_request() { return nomor_request; }
    public void setNomor_request(String nomor_request) { this.nomor_request = nomor_request; }
    public long getTanggal_request() { return tanggal_request; }
    public void setTanggal_request(long tanggal_request) { this.tanggal_request = tanggal_request; }
    public long getTanggal_selesai() { return tanggal_selesai; }
    public void setTanggal_selesai(long tanggal_selesai) { this.tanggal_selesai = tanggal_selesai; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotal_harga() { return total_harga; }
    public void setTotal_harga(double total_harga) { this.total_harga = total_harga; }
}
