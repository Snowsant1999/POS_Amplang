package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pelanggan")
public class Pelanggan {

    @PrimaryKey(autoGenerate = true)
    private int id_pelanggan;
    private String nama_pelanggan;

    public Pelanggan(String nama_pelanggan) {
        this.nama_pelanggan = nama_pelanggan;
    }

    public int getId_pelanggan() { return id_pelanggan; }
    public void setId_pelanggan(int id_pelanggan) { this.id_pelanggan = id_pelanggan; }
    public String getNama_pelanggan() { return nama_pelanggan; }
    public void setNama_pelanggan(String nama_pelanggan) { this.nama_pelanggan = nama_pelanggan; }
}
