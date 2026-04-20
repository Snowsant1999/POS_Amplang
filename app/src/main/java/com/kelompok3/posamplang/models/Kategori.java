package com.kelompok3.posamplang.models;

public class Kategori {
    private int id_kategori;
    private String nama_kategori;
    private String deskripsi;

    public Kategori(int id_kategori, String nama_kategori, String deskripsi) {
        this.id_kategori = id_kategori;
        this.nama_kategori = nama_kategori;
        this.deskripsi = deskripsi;
    }

    public int getId_kategori() { return id_kategori; }
    public void setId_kategori(int id_kategori) { this.id_kategori = id_kategori; }
    public String getNama_kategori() { return nama_kategori; }
    public void setNama_kategori(String nama_kategori) { this.nama_kategori = nama_kategori; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}