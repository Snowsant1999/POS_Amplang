package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kelompok3.posamplang.models.LaporanHarian;

import java.util.List;

@Dao
public interface LaporanHarianDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrReplace(LaporanHarian laporan);

    @Delete
    void delete(LaporanHarian laporan);

    @Query("SELECT * FROM laporan_harian ORDER BY tanggal_laporan DESC")
    List<LaporanHarian> getAll();

    @Query("SELECT * FROM laporan_harian WHERE id_laporan = :id LIMIT 1")
    LaporanHarian getById(int id);
}
