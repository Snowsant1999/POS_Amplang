package com.kelompok3.posamplang.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kelompok3.posamplang.models.Produk;

import java.util.List;

@Dao
public interface ProdukDao {
    @Insert
    long insert(Produk produk);

    @Update
    void update(Produk produk);

    @Delete
    void delete(Produk produk);

    @Query("SELECT * FROM produk ORDER BY nama_produk ASC")
    List<Produk> getAll();

    @Query("SELECT * FROM produk WHERE aktif = 1 ORDER BY nama_produk ASC")
    List<Produk> getAktif();

    @Query("SELECT * FROM produk WHERE id_produk = :id LIMIT 1")
    Produk getById(int id);

    @Query("SELECT * FROM produk WHERE nama_produk LIKE '%' || :keyword || '%'")
    List<Produk> search(String keyword);

    @Query("SELECT * FROM produk WHERE stok_tersedia <= :batas ORDER BY stok_tersedia ASC")
    List<Produk> getStokRendah(int batas);

    @Query("UPDATE produk SET stok_tersedia = stok_tersedia - :jumlah WHERE id_produk = :id")
    void kurangiStok(int id, int jumlah);

    @Query("UPDATE produk SET stok_tersedia = stok_tersedia + :jumlah WHERE id_produk = :id")
    void tambahStok(int id, int jumlah);
}
