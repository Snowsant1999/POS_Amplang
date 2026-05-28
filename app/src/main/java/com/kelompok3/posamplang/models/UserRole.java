package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_roles")
public class UserRole {

    @PrimaryKey(autoGenerate = true)
    private int user_roles_id;
    private String nama_role;
    private String deskripsi;

    public UserRole(String nama_role, String deskripsi) {
        this.nama_role = nama_role;
        this.deskripsi = deskripsi;
    }

    public int getUser_roles_id() { return user_roles_id; }
    public void setUser_roles_id(int user_roles_id) { this.user_roles_id = user_roles_id; }
    public String getNama_role() { return nama_role; }
    public void setNama_role(String nama_role) { this.nama_role = nama_role; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}
