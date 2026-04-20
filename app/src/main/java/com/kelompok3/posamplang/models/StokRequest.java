package com.kelompok3.posamplang.models;

import java.util.Date;

public class StokRequest {
    private int id_request;
    private int id_supplier;
    private int id_users;
    private String nomor_request;
    private Date tanggal_request;
    private String status;

    public StokRequest(int id_request, int id_supplier, int id_users, String nomor_request, Date tanggal_request, String status) {
        this.id_request = id_request;
        this.id_supplier = id_supplier;
        this.id_users = id_users;
        this.nomor_request = nomor_request;
        this.tanggal_request = tanggal_request;
        this.status = status;
    }

    public int getId_request() { return id_request; }
    public void setId_request(int id_request) { this.id_request = id_request; }
    public int getId_supplier() { return id_supplier; }
    public void setId_supplier(int id_supplier) { this.id_supplier = id_supplier; }
    public int getId_users() { return id_users; }
    public void setId_users(int id_users) { this.id_users = id_users; }
    public String getNomor_request() { return nomor_request; }
    public void setNomor_request(String nomor_request) { this.nomor_request = nomor_request; }
    public Date getTanggal_request() { return tanggal_request; }
    public void setTanggal_request(Date tanggal_request) { this.tanggal_request = tanggal_request; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
