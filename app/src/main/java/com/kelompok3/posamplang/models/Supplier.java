package com.kelompok3.posamplang.models;

public class Supplier {
    private int id_supplier;
    private String nama_supplier;
    private String alamat_supplier;
    private String no_telepon;

    public Supplier(int id_supplier, String nama_supplier, String alamat_supplier, String no_telepon) {
        this.id_supplier = id_supplier;
        this.nama_supplier = nama_supplier;
        this.alamat_supplier = alamat_supplier;
        this.no_telepon = no_telepon;
    }

    public int getId_supplier() { return id_supplier; }
    public void setId_supplier(int id_supplier) { this.id_supplier = id_supplier; }
    public String getNama_supplier() { return nama_supplier; }
    public void setNama_supplier(String nama_supplier) { this.nama_supplier = nama_supplier; }
    public String getAlamat_supplier() { return alamat_supplier; }
    public void setAlamat_supplier(String alamat_supplier) { this.alamat_supplier = alamat_supplier; }
    public String getNo_telepon() { return no_telepon; }
    public void setNo_telepon(String no_telepon) { this.no_telepon = no_telepon; }
}
