package com.kelompok3.posamplang.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kelompok3.posamplang.dao.DetailPesananDao;
import com.kelompok3.posamplang.dao.DetailStokRequestDao;
import com.kelompok3.posamplang.dao.KategoriDao;
import com.kelompok3.posamplang.dao.MerekDao;
import com.kelompok3.posamplang.dao.PelangganDao;
import com.kelompok3.posamplang.dao.PembayaranDao;
import com.kelompok3.posamplang.dao.PesananDao;
import com.kelompok3.posamplang.dao.ProdukDao;
import com.kelompok3.posamplang.dao.StokAdjustmentDao;
import com.kelompok3.posamplang.dao.StokRequestDao;
import com.kelompok3.posamplang.dao.SupplierDao;
import com.kelompok3.posamplang.dao.UserDao;
import com.kelompok3.posamplang.dao.UserRoleDao;
import com.kelompok3.posamplang.models.DetailPesanan;
import com.kelompok3.posamplang.models.DetailStokRequest;
import com.kelompok3.posamplang.models.Kategori;
import com.kelompok3.posamplang.models.Merek;
import com.kelompok3.posamplang.models.Pelanggan;
import com.kelompok3.posamplang.models.PembayaranPesanan;
import com.kelompok3.posamplang.models.Pesanan;
import com.kelompok3.posamplang.models.Produk;
import com.kelompok3.posamplang.models.StokAdjustment;
import com.kelompok3.posamplang.models.StokRequest;
import com.kelompok3.posamplang.models.Supplier;
import com.kelompok3.posamplang.models.User;
import com.kelompok3.posamplang.models.UserRole;

@Database(
    entities = {
        UserRole.class,
        User.class,
        Merek.class,
        Kategori.class,
        Supplier.class,
        Pelanggan.class,
        Produk.class,
        Pesanan.class,
        DetailPesanan.class,
        PembayaranPesanan.class,
        StokRequest.class,
        DetailStokRequest.class,
        StokAdjustment.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    // Semua akses ke database (DAO) didaftarkan di sini
    public abstract UserRoleDao userRoleDao();
    public abstract UserDao userDao();
    public abstract MerekDao merekDao();
    public abstract KategoriDao kategoriDao();
    public abstract SupplierDao supplierDao();
    public abstract ProdukDao produkDao();
    public abstract PelangganDao pelangganDao();
    public abstract PesananDao pesananDao();
    public abstract DetailPesananDao detailPesananDao();
    public abstract PembayaranDao pembayaranDao();
    public abstract StokRequestDao stokRequestDao();
    public abstract DetailStokRequestDao detailStokRequestDao();
    public abstract StokAdjustmentDao stokAdjustmentDao();

    // Singleton — pastikan hanya ada 1 instance database di seluruh aplikasi
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "pos_amplang.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Menjalankan seeder di background thread agar UI tidak beku
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                populateDatabase(instance);
            });
        }
    };

    private static void populateDatabase(AppDatabase db) {
        // 1. Setup Role & User Admin
        long roleId = db.userRoleDao().insert(new UserRole("Admin", "Administrator Sistem"));
        long userId = db.userDao().insert(new User((int)roleId, "EMP-001", "Admin Utama", "admin", "admin", "081234567890"));

        // 2. Setup Kategori & Merek
        long katAmplangId = db.kategoriDao().insert(new Kategori("Amplang", "Makanan ringan khas kaltim"));
        long katKueId = db.kategoriDao().insert(new Kategori("Kue Kering", "Gabin, kuku macan, dll"));
        long merkSalsabilaId = db.merekDao().insert(new Merek("Salsabila"));
        long merkLainId = db.merekDao().insert(new Merek("Khas Kaltim"));

        // 3. Setup 5 Suppliers
        long sup1 = db.supplierDao().insert(new Supplier("PT Ikan Segar", "Jl. Nelayan No 1", "081111111", true));
        long sup2 = db.supplierDao().insert(new Supplier("Toko Terigu Makmur", "Jl. Pasar Pagi", "082222222", true));
        long sup3 = db.supplierDao().insert(new Supplier("CV Minyak Kelapa", "Jl. Industri", "083333333", true));
        long sup4 = db.supplierDao().insert(new Supplier("Bumbu Nusantara", "Jl. Rempah No 4", "084444444", true));
        long sup5 = db.supplierDao().insert(new Supplier("Plastik Kemasan Jaya", "Jl. Gajah Mada", "085555555", true));

        // 4. Setup 15 Produk
        ProdukDao produkDao = db.produkDao();
        int kA = (int)katAmplangId; int kK = (int)katKueId;
        int mS = (int)merkSalsabilaId; int mL = (int)merkLainId;
        int s1 = (int)sup1; int s2 = (int)sup2;

        produkDao.insert(new Produk(kA, mS, s1, "Amplang Ikan Tenggiri Asli", "Bungkus", 25000, 100));
        produkDao.insert(new Produk(kA, mS, s1, "Amplang Ikan Pipih Super", "Bungkus", 30000, 85));
        produkDao.insert(new Produk(kA, mS, s1, "Amplang Kuku Macan", "Bungkus", 20000, 120));
        produkDao.insert(new Produk(kA, mL, s2, "Amplang Ikan Belida", "Bungkus", 35000, 50));
        produkDao.insert(new Produk(kA, mS, s1, "Amplang Kepiting", "Bungkus", 28000, 60));
        produkDao.insert(new Produk(kA, mS, s1, "Amplang Udang Spesial", "Bungkus", 27000, 70));
        produkDao.insert(new Produk(kA, mL, s2, "Amplang Rumput Laut", "Bungkus", 22000, 90));
        produkDao.insert(new Produk(kK, mS, s2, "Gabin Susu Salsabila", "Pcs", 25000, 150));
        produkDao.insert(new Produk(kK, mS, s2, "Gabin Keju Spesial", "Pcs", 28000, 110));
        produkDao.insert(new Produk(kK, mS, s2, "Gabin Coklat Lumer", "Pcs", 30000, 80));
        produkDao.insert(new Produk(kK, mL, s2, "Kerupuk Ikan Gabus", "Bungkus", 15000, 200));
        produkDao.insert(new Produk(kK, mL, s2, "Kerupuk Udang Manis", "Bungkus", 18000, 180));
        produkDao.insert(new Produk(kK, mS, s1, "Pia Kacang Hijau", "Kotak", 35000, 40));
        produkDao.insert(new Produk(kK, mS, s1, "Pia Coklat Keju", "Kotak", 40000, 35));
        produkDao.insert(new Produk(kA, mL, s1, "Amplang Mini Balado", "Bungkus", 12000, 250));

        // 5. Setup Pelanggan (Umum)
        long pelId = db.pelangganDao().insert(new Pelanggan("Pelanggan Umum"));

        // 6. Setup 5 Transaksi (Laporan / History)
        long now = System.currentTimeMillis();
        long oneDay = 86400000L;

        for (int i = 1; i <= 5; i++) {
            long wkt = now - (oneDay * (5 - i)); // Mundur beberapa hari untuk variasi tanggal
            long pesananId = db.pesananDao().insert(new Pesanan((int)pelId, "ORD-100" + i, wkt, "Langsung", "Selesai"));

            // 2 detail produk per pesanan
            db.detailPesananDao().insert(new DetailPesanan(1, (int)pesananId, (int)userId, 2, 25000, 50000));
            db.detailPesananDao().insert(new DetailPesanan(2, (int)pesananId, (int)userId, 1, 30000, 30000));

            // Pembayaran
            String metode = (i % 2 == 0) ? "QRIS" : "Tunai";
            db.pembayaranDao().insert(new PembayaranPesanan((int)pesananId, metode, "Lunas", wkt, 80000, 0));
        }
    }
}
