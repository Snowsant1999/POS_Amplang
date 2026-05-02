package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.DetailStokRequest;

import java.util.List;

@Dao
public interface DetailStokRequestDao {
    @Insert
    long insert(DetailStokRequest detail);

    @Update
    void update(DetailStokRequest detail);

    @Delete
    void delete(DetailStokRequest detail);

    @Query("SELECT * FROM detail_stok_request WHERE id_request = :idRequest")
    List<DetailStokRequest> getByRequest(int idRequest);
}
