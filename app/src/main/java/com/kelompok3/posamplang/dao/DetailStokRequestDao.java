package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.DetailStokRequest;
import com.kelompok3.posamplang.models.PengeluaranProdukSummary;

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

    @Query("SELECT COALESCE(SUM(d.harga_beli * d.jumlah_stok), 0) FROM detail_stok_request d " +
            "INNER JOIN stok_request r ON r.id_request = d.id_request WHERE r.status = 'Selesai'")
    double getTotalPembelianSelesai();

    @Query("SELECT COALESCE(SUM(d.harga_beli * d.jumlah_stok), 0) FROM detail_stok_request d " +
            "INNER JOIN stok_request r ON r.id_request = d.id_request " +
            "WHERE r.status = 'Selesai' AND r.tanggal_selesai >= :start AND r.tanggal_selesai <= :end")
    double getTotalPembelianSelesaiPeriode(long start, long end);

    @Query("SELECT d.nama_produk, SUM(d.harga_beli * d.jumlah_stok) AS total_pengeluaran " +
            "FROM detail_stok_request d INNER JOIN stok_request r ON r.id_request = d.id_request " +
            "WHERE r.status = 'Selesai' GROUP BY d.nama_produk ORDER BY total_pengeluaran DESC")
    List<PengeluaranProdukSummary> getPengeluaranPerProdukSelesai();
}
