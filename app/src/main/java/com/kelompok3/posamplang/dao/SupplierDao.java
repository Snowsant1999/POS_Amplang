package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.Supplier;

import java.util.List;

@Dao
public interface SupplierDao {
    @Insert
    long insert(Supplier supplier);

    @Update
    void update(Supplier supplier);

    @Delete
    void delete(Supplier supplier);

    @Query("SELECT * FROM supplier ORDER BY nama_supplier ASC")
    List<Supplier> getAll();

    @Query("SELECT * FROM supplier WHERE id_supplier = :id LIMIT 1")
    Supplier getById(int id);

    @Query("SELECT * FROM supplier WHERE nama_supplier LIKE '%' || :keyword || '%'")
    List<Supplier> search(String keyword);
}
