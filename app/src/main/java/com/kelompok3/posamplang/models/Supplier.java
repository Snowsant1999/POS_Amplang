package com.kelompok3.posamplang.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "supplier")
public class Supplier {

    @PrimaryKey(autoGenerate = true)
    private int id_supplier;
    private String nama_supplier;
    private String alamat_supplier;
    private String no_telepon;

    @NonNull
    @ColumnInfo(defaultValue = "''")
    private String email;

    @NonNull
    @ColumnInfo(defaultValue = "''")
    private String image_uri;

    private boolean aktif;

    public Supplier(String nama_supplier, String alamat_supplier, String no_telepon,
                    @NonNull String email, @NonNull String image_uri, boolean aktif) {
        this.nama_supplier = nama_supplier;
        this.alamat_supplier = alamat_supplier;
        this.no_telepon = no_telepon;
        this.email = email;
        this.image_uri = image_uri;
        this.aktif = aktif;
    }

    @Ignore
    public Supplier(String nama_supplier, String alamat_supplier, String no_telepon,
                    @NonNull String email, boolean aktif) {
        this(nama_supplier, alamat_supplier, no_telepon, email, "", aktif);
    }

    @Ignore
    public Supplier(String nama_supplier, String alamat_supplier, String no_telepon, boolean aktif) {
        this(nama_supplier, alamat_supplier, no_telepon, "", "", aktif);
    }

    public int getId_supplier() { return id_supplier; }
    public void setId_supplier(int id_supplier) { this.id_supplier = id_supplier; }
    public String getNama_supplier() { return nama_supplier; }
    public void setNama_supplier(String nama_supplier) { this.nama_supplier = nama_supplier; }
    public String getAlamat_supplier() { return alamat_supplier; }
    public void setAlamat_supplier(String alamat_supplier) { this.alamat_supplier = alamat_supplier; }
    public String getNo_telepon() { return no_telepon; }
    public void setNo_telepon(String no_telepon) { this.no_telepon = no_telepon; }
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }
    public String getImage_uri() { return image_uri; }
    public void setImage_uri(@NonNull String image_uri) { this.image_uri = image_uri; }
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }
}
