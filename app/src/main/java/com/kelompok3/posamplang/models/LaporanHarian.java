package com.kelompok3.posamplang.models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "laporan_harian",
        indices = {@Index(value = "tanggal_laporan", unique = true)}
)
public class LaporanHarian {

    @PrimaryKey(autoGenerate = true)
    private int id_laporan;
    private long tanggal_laporan;
    private long disimpan_pada;
    private double pemasukan;
    private double pengeluaran;
    private double saldo_bersih;
    private double pembayaran_tunai;
    private double pembayaran_online;
    private double pembayaran_tertunda;
    private int jumlah_transaksi;

    public LaporanHarian(long tanggal_laporan, long disimpan_pada, double pemasukan,
                         double pengeluaran, double saldo_bersih, double pembayaran_tunai,
                         double pembayaran_online, double pembayaran_tertunda,
                         int jumlah_transaksi) {
        this.tanggal_laporan = tanggal_laporan;
        this.disimpan_pada = disimpan_pada;
        this.pemasukan = pemasukan;
        this.pengeluaran = pengeluaran;
        this.saldo_bersih = saldo_bersih;
        this.pembayaran_tunai = pembayaran_tunai;
        this.pembayaran_online = pembayaran_online;
        this.pembayaran_tertunda = pembayaran_tertunda;
        this.jumlah_transaksi = jumlah_transaksi;
    }

    public int getId_laporan() { return id_laporan; }
    public void setId_laporan(int id_laporan) { this.id_laporan = id_laporan; }
    public long getTanggal_laporan() { return tanggal_laporan; }
    public void setTanggal_laporan(long tanggal_laporan) { this.tanggal_laporan = tanggal_laporan; }
    public long getDisimpan_pada() { return disimpan_pada; }
    public void setDisimpan_pada(long disimpan_pada) { this.disimpan_pada = disimpan_pada; }
    public double getPemasukan() { return pemasukan; }
    public void setPemasukan(double pemasukan) { this.pemasukan = pemasukan; }
    public double getPengeluaran() { return pengeluaran; }
    public void setPengeluaran(double pengeluaran) { this.pengeluaran = pengeluaran; }
    public double getSaldo_bersih() { return saldo_bersih; }
    public void setSaldo_bersih(double saldo_bersih) { this.saldo_bersih = saldo_bersih; }
    public double getPembayaran_tunai() { return pembayaran_tunai; }
    public void setPembayaran_tunai(double pembayaran_tunai) { this.pembayaran_tunai = pembayaran_tunai; }
    public double getPembayaran_online() { return pembayaran_online; }
    public void setPembayaran_online(double pembayaran_online) { this.pembayaran_online = pembayaran_online; }
    public double getPembayaran_tertunda() { return pembayaran_tertunda; }
    public void setPembayaran_tertunda(double pembayaran_tertunda) { this.pembayaran_tertunda = pembayaran_tertunda; }
    public int getJumlah_transaksi() { return jumlah_transaksi; }
    public void setJumlah_transaksi(int jumlah_transaksi) { this.jumlah_transaksi = jumlah_transaksi; }
}
