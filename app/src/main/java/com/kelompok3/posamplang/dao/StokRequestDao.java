package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.StokRequest;
import com.kelompok3.posamplang.models.StokRequestSummary;

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

    @Query("SELECT r.id_request, r.id_supplier, r.id_users, r.nomor_request, r.tanggal_request, r.tanggal_selesai, r.status, " +
            "COALESCE(SUM(d.harga_beli * d.jumlah_stok), 0) AS total_harga " +
            "FROM stok_request r LEFT JOIN detail_stok_request d ON r.id_request = d.id_request " +
            "WHERE r.id_supplier = :supplierId GROUP BY r.id_request ORDER BY r.tanggal_request DESC")
    List<StokRequestSummary> getSummaryBySupplier(int supplierId);
}
