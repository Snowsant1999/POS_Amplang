package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "pesanan",
    foreignKeys = @ForeignKey(
        entity = Pelanggan.class,
        parentColumns = "id_pelanggan",
        childColumns = "id_pelanggan",
        onDelete = ForeignKey.RESTRICT
    ),
    indices = {@Index("id_pelanggan")}
)
public class Pesanan {

    @PrimaryKey(autoGenerate = true)
    private int id_pesanan;
    private int id_pelanggan;
    private String nomor_pesanan;
    private long tanggal_pesanan; // disimpan sebagai timestamp (ms)
    private String tipe_pembelian;
    private String status_pesanan;

    public Pesanan(int id_pelanggan, String nomor_pesanan, long tanggal_pesanan, String tipe_pembelian, String status_pesanan) {
        this.id_pelanggan = id_pelanggan;
        this.nomor_pesanan = nomor_pesanan;
        this.tanggal_pesanan = tanggal_pesanan;
        this.tipe_pembelian = tipe_pembelian;
        this.status_pesanan = status_pesanan;
    }

    public int getId_pesanan() { return id_pesanan; }
    public void setId_pesanan(int id_pesanan) { this.id_pesanan = id_pesanan; }
    public int getId_pelanggan() { return id_pelanggan; }
    public void setId_pelanggan(int id_pelanggan) { this.id_pelanggan = id_pelanggan; }
    public String getNomor_pesanan() { return nomor_pesanan; }
    public void setNomor_pesanan(String nomor_pesanan) { this.nomor_pesanan = nomor_pesanan; }
    public long getTanggal_pesanan() { return tanggal_pesanan; }
    public void setTanggal_pesanan(long tanggal_pesanan) { this.tanggal_pesanan = tanggal_pesanan; }
    public String getTipe_pembelian() { return tipe_pembelian; }
    public void setTipe_pembelian(String tipe_pembelian) { this.tipe_pembelian = tipe_pembelian; }
    public String getStatus_pesanan() { return status_pesanan; }
    public void setStatus_pesanan(String status_pesanan) { this.status_pesanan = status_pesanan; }
}
