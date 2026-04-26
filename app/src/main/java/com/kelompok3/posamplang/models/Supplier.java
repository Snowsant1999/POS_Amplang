package com.kelompok3.posamplang.models;

public class Supplier {
    private int id_supplier;
    private String nama_supplier;
    private String alamat_supplier;
    private String no_telepon;
    private String email;
    private boolean status; // true = Aktif, false = Nonaktif

    public Supplier(int id_supplier, String nama_supplier, String alamat_supplier, String no_telepon, String email, boolean status) {
        this.id_supplier = id_supplier;
        this.nama_supplier = nama_supplier;
        this.alamat_supplier = alamat_supplier;
        this.no_telepon = no_telepon;
        this.email = email;
        this.status = status;
    }

    public int getId_supplier() { return id_supplier; }
    public String getNama_supplier() { return nama_supplier; }
    public String getAlamat_supplier() { return alamat_supplier; }
    public String getNo_telepon() { return no_telepon; }
    public String getEmail() { return email; }
    public boolean isStatus() { return status; }
}
