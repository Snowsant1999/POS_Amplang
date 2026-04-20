package com.kelompok3.posamplang.models;

public class Pelanggan {
    private int id_pelanggan;
    private String nama_pelanggan;

    public Pelanggan(int id_pelanggan, String nama_pelanggan) {
        this.id_pelanggan = id_pelanggan;
        this.nama_pelanggan = nama_pelanggan;
    }

    public int getId_pelanggan() { return id_pelanggan; }
    public void setId_pelanggan(int id_pelanggan) { this.id_pelanggan = id_pelanggan; }
    public String getNama_pelanggan() { return nama_pelanggan; }
    public void setNama_pelanggan(String nama_pelanggan) { this.nama_pelanggan = nama_pelanggan; }
}
