package com.kelompok3.posamplang.models;

public class User {
    private int id_users;
    private int user_roles_id;
    private String nik_karyawan;
    private String nama_karyawan;
    private String email;
    private String password;
    private String no_telepon;

    public User(int id_users, int user_roles_id, String nik_karyawan, String nama_karyawan, String email, String password, String no_telepon) {
        this.id_users = id_users;
        this.user_roles_id = user_roles_id;
        this.nik_karyawan = nik_karyawan;
        this.nama_karyawan = nama_karyawan;
        this.email = email;
        this.password = password;
        this.no_telepon = no_telepon;
    }

    public int getId_users() { return id_users; }
    public void setId_users(int id_users) { this.id_users = id_users; }
    public int getUser_roles_id() { return user_roles_id; }
    public void setUser_roles_id(int user_roles_id) { this.user_roles_id = user_roles_id; }
    public String getNik_karyawan() { return nik_karyawan; }
    public void setNik_karyawan(String nik_karyawan) { this.nik_karyawan = nik_karyawan; }
    public String getNama_karyawan() { return nama_karyawan; }
    public void setNama_karyawan(String nama_karyawan) { this.nama_karyawan = nama_karyawan; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNo_telepon() { return no_telepon; }
    public void setNo_telepon(String no_telepon) { this.no_telepon = no_telepon; }
}
