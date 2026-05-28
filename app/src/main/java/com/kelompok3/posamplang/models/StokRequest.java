package com.kelompok3.posamplang.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "stok_request",
    foreignKeys = {
        @ForeignKey(entity = Supplier.class, parentColumns = "id_supplier", childColumns = "id_supplier", onDelete = ForeignKey.RESTRICT),
        @ForeignKey(entity = User.class, parentColumns = "id_users", childColumns = "id_users", onDelete = ForeignKey.RESTRICT)
    },
    indices = {
        @Index("id_supplier"),
        @Index("id_users")
    }
)
public class StokRequest {

    @PrimaryKey(autoGenerate = true)
    private int id_request;
    private int id_supplier;
    private int id_users;
    private String nomor_request;
    private long tanggal_request; // timestamp
    private String status;
    @ColumnInfo(defaultValue = "0")
    private long tanggal_selesai;

    public StokRequest(int id_supplier, int id_users, String nomor_request, long tanggal_request, String status) {
        this.id_supplier = id_supplier;
        this.id_users = id_users;
        this.nomor_request = nomor_request;
        this.tanggal_request = tanggal_request;
        this.status = status;
        this.tanggal_selesai = 0;
    }

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getTanggal_selesai() { return tanggal_selesai; }
    public void setTanggal_selesai(long tanggal_selesai) { this.tanggal_selesai = tanggal_selesai; }
}
