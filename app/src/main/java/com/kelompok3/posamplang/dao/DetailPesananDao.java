package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.DetailPesanan;

import java.util.List;

@Dao
public interface DetailPesananDao {
    @Insert
    long insert(DetailPesanan detail);

    @Update
    void update(DetailPesanan detail);

    @Delete
    void delete(DetailPesanan detail);

    @Query("SELECT * FROM detail_pesanan WHERE id_pesanan = :idPesanan")
    List<DetailPesanan> getByPesanan(int idPesanan);
}
