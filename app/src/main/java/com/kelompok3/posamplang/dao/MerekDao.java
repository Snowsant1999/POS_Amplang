package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.Merek;

import java.util.List;

@Dao
public interface MerekDao {
    @Insert
    long insert(Merek merek);

    @Update
    void update(Merek merek);

    @Delete
    void delete(Merek merek);

    @Query("SELECT * FROM merek ORDER BY nama_merek ASC")
    List<Merek> getAll();

    @Query("SELECT * FROM merek WHERE id_merek = :id LIMIT 1")
    Merek getById(int id);
}
