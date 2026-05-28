package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.StokAdjustment;

import java.util.List;

@Dao
public interface StokAdjustmentDao {
    @Insert
    long insert(StokAdjustment stokAdjustment);

    @Update
    void update(StokAdjustment stokAdjustment);

    @Delete
    void delete(StokAdjustment stokAdjustment);

    @Query("SELECT * FROM stok_adjustment ORDER BY tanggal DESC")
    List<StokAdjustment> getAll();

    @Query("SELECT * FROM stok_adjustment WHERE id_produk = :idProduk ORDER BY tanggal DESC")
    List<StokAdjustment> getByProduk(int idProduk);
}
