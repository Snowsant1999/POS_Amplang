package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.StokRequest;

import java.util.List;

@Dao
public interface StokRequestDao {
    @Insert
    long insert(StokRequest stokRequest);

    @Update
    void update(StokRequest stokRequest);

    @Delete
    void delete(StokRequest stokRequest);

    @Query("SELECT * FROM stok_request ORDER BY tanggal_request DESC")
    List<StokRequest> getAll();

    @Query("SELECT * FROM stok_request WHERE id_request = :id LIMIT 1")
    StokRequest getById(int id);

    @Query("SELECT * FROM stok_request WHERE status = :status")
    List<StokRequest> getByStatus(String status);
}
