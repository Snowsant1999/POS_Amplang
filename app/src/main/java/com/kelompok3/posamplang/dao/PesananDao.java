package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.Pesanan;

import java.util.List;

@Dao
public interface PesananDao {
    @Insert
    long insert(Pesanan pesanan);

    @Update
    void update(Pesanan pesanan);

    @Delete
    void delete(Pesanan pesanan);

    @Query("SELECT * FROM pesanan ORDER BY tanggal_pesanan DESC")
    List<Pesanan> getAll();

    @Query("SELECT * FROM pesanan WHERE id_pesanan = :id LIMIT 1")
    Pesanan getById(int id);

    @Query("SELECT COUNT(*) FROM pesanan WHERE tanggal_pesanan >= :startOfDay AND tanggal_pesanan <= :endOfDay")
    int countHariIni(long startOfDay, long endOfDay);
}
