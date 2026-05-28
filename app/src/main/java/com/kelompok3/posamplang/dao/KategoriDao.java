package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.Kategori;

import java.util.List;

@Dao
public interface KategoriDao {
    @Insert
    long insert(Kategori kategori);

    @Update
    void update(Kategori kategori);

    @Delete
    void delete(Kategori kategori);

    @Query("SELECT * FROM kategori_produk ORDER BY nama_kategori ASC")
    List<Kategori> getAll();

    @Query("SELECT * FROM kategori_produk WHERE id_kategori = :id LIMIT 1")
    Kategori getById(int id);
}
