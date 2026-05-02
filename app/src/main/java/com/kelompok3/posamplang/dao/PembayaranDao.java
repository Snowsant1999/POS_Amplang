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

    // Total pendapatan untuk dashboard
    @Query("SELECT SUM(total_pembayaran) FROM pembayaran_pesanan WHERE tanggal_pembayaran >= :startOfDay AND tanggal_pembayaran <= :endOfDay")
    double getTotalPendapatanHariIni(long startOfDay, long endOfDay);

    // Data untuk grafik (pendapatan per hari dalam 7 hari terakhir)
    @Query("SELECT SUM(total_pembayaran) FROM pembayaran_pesanan WHERE tanggal_pembayaran >= :start AND tanggal_pembayaran <= :end")
    double getTotalPendapatanPeriode(long start, long end);
}
