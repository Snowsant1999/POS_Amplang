package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.Pelanggan;

import java.util.List;

@Dao
public interface PelangganDao {
    @Insert
    long insert(Pelanggan pelanggan);

    @Update
    void update(Pelanggan pelanggan);

    @Delete
    void delete(Pelanggan pelanggan);

    @Query("SELECT * FROM pelanggan ORDER BY nama_pelanggan ASC")
    List<Pelanggan> getAll();

    @Query("SELECT * FROM pelanggan WHERE id_pelanggan = :id LIMIT 1")
    Pelanggan getById(int id);
}
