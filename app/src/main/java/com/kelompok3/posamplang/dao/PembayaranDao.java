package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.PembayaranPesanan;

import java.util.List;

@Dao
public interface PembayaranDao {
    @Insert
    long insert(PembayaranPesanan pembayaran);

    @Update
    void update(PembayaranPesanan pembayaran);

    @Delete
    void delete(PembayaranPesanan pembayaran);

    @Query("SELECT * FROM pembayaran_pesanan ORDER BY tanggal_pembayaran DESC")
    List<PembayaranPesanan> getAll();

    // Total seluruh pendapatan (semua waktu)
    @Query("SELECT COALESCE(SUM(total_pembayaran), 0) FROM pembayaran_pesanan WHERE status_pembayaran = 'Lunas'")
    double getTotalPendapatanAll();

    // Total pendapatan hari ini
    @Query("SELECT COALESCE(SUM(total_pembayaran), 0) FROM pembayaran_pesanan WHERE tanggal_pembayaran >= :startOfDay AND tanggal_pembayaran <= :endOfDay")
    double getTotalPendapatanHariIni(long startOfDay, long endOfDay);

    // Total pendapatan dalam rentang waktu (untuk grafik 7 hari)
    @Query("SELECT COALESCE(SUM(total_pembayaran), 0) FROM pembayaran_pesanan WHERE tanggal_pembayaran >= :start AND tanggal_pembayaran <= :end AND status_pembayaran = 'Lunas'")
    double getTotalPendapatanPeriode(long start, long end);

    // Count per metode pembayaran untuk Pie Chart
    @Query("SELECT COUNT(*) FROM pembayaran_pesanan WHERE metode_pembayaran = :metode")
    int countByMetode(String metode);

    // Total jumlah transaksi
    @Query("SELECT COUNT(*) FROM pembayaran_pesanan WHERE status_pembayaran = 'Lunas'")
    int getTotalTransaksi();

    @Query("SELECT COALESCE(SUM(total_pembayaran), 0) FROM pembayaran_pesanan " +
            "WHERE status_pembayaran = 'Lunas' AND tanggal_pembayaran >= :start AND tanggal_pembayaran <= :end")
    double getTotalLunasPeriode(long start, long end);

    @Query("SELECT COALESCE(SUM(total_pembayaran), 0) FROM pembayaran_pesanan " +
            "WHERE status_pembayaran = 'Lunas' AND metode_pembayaran = :metode " +
            "AND tanggal_pembayaran >= :start AND tanggal_pembayaran <= :end")
    double getTotalLunasPeriodeByMetode(String metode, long start, long end);

    @Query("SELECT COALESCE(SUM(total_pembayaran), 0) FROM pembayaran_pesanan " +
            "WHERE status_pembayaran != 'Lunas' AND tanggal_pembayaran >= :start AND tanggal_pembayaran <= :end")
    double getTotalTertundaPeriode(long start, long end);

    @Query("SELECT COUNT(*) FROM pembayaran_pesanan WHERE status_pembayaran = 'Lunas' " +
            "AND tanggal_pembayaran >= :start AND tanggal_pembayaran <= :end")
    int countLunasPeriode(long start, long end);
}
