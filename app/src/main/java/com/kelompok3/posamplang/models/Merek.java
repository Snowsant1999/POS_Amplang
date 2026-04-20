package com.kelompok3.posamplang.models;

public class Merek {
    private int id_merek;
    private String nama_merek;

    public Merek(int id_merek, String nama_merek) {
        this.id_merek = id_merek;
        this.nama_merek = nama_merek;
    }

    public int getId_merek() { return id_merek; }
    public void setId_merek(int id_merek) { this.id_merek = id_merek; }
    public String getNama_merek() { return nama_merek; }
    public void setNama_merek(String nama_merek) { this.nama_merek = nama_merek; }
}
