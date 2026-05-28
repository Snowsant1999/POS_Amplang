package com.kelompok3.posamplang.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.migration.Migration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kelompok3.posamplang.dao.DetailPesananDao;
import com.kelompok3.posamplang.dao.DetailStokRequestDao;
import com.kelompok3.posamplang.dao.KategoriDao;
import com.kelompok3.posamplang.dao.LaporanHarianDao;
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
import com.kelompok3.posamplang.models.LaporanHarian;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        StokAdjustment.class,
        LaporanHarian.class
    },
    version = 6,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@androidx.annotation.NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE produk ADD COLUMN aktif INTEGER NOT NULL DEFAULT 1");
        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@androidx.annotation.NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE supplier ADD COLUMN email TEXT NOT NULL DEFAULT ''");
        }
    };
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@androidx.annotation.NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE supplier ADD COLUMN image_uri TEXT NOT NULL DEFAULT ''");
        }
    };
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@androidx.annotation.NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE stok_request ADD COLUMN tanggal_selesai INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS `detail_stok_request_new` (" +
                    "`id_detail_request` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`id_request` INTEGER NOT NULL, `id_produk` INTEGER, `jumlah_stok` INTEGER NOT NULL, " +
                    "`produk_baru` INTEGER NOT NULL DEFAULT 0, `nama_produk` TEXT NOT NULL DEFAULT '', " +
                    "`id_kategori_produk` INTEGER, `id_merek` INTEGER, `unit` TEXT NOT NULL DEFAULT '', " +
                    "`harga_jual` REAL NOT NULL DEFAULT 0, `harga_beli` REAL NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(`id_request`) REFERENCES `stok_request`(`id_request`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                    "FOREIGN KEY(`id_produk`) REFERENCES `produk`(`id_produk`) ON UPDATE NO ACTION ON DELETE RESTRICT)");
            database.execSQL("INSERT INTO `detail_stok_request_new` " +
                    "(`id_detail_request`, `id_request`, `id_produk`, `jumlah_stok`) " +
                    "SELECT `id_detail_request`, `id_request`, `id_produk`, `jumlah_stok` FROM `detail_stok_request`");
            database.execSQL("DROP TABLE `detail_stok_request`");
            database.execSQL("ALTER TABLE `detail_stok_request_new` RENAME TO `detail_stok_request`");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_detail_stok_request_id_request` ON `detail_stok_request` (`id_request`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_detail_stok_request_id_produk` ON `detail_stok_request` (`id_produk`)");
        }
    };
    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@androidx.annotation.NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `laporan_harian` (" +
                    "`id_laporan` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`tanggal_laporan` INTEGER NOT NULL, `disimpan_pada` INTEGER NOT NULL, " +
                    "`pemasukan` REAL NOT NULL, `pengeluaran` REAL NOT NULL, " +
                    "`saldo_bersih` REAL NOT NULL, `pembayaran_tunai` REAL NOT NULL, " +
                    "`pembayaran_online` REAL NOT NULL, `pembayaran_tertunda` REAL NOT NULL, " +
                    "`jumlah_transaksi` INTEGER NOT NULL)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_laporan_harian_tanggal_laporan` " +
                    "ON `laporan_harian` (`tanggal_laporan`)");
        }
    };

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
    public abstract LaporanHarianDao laporanHarianDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "pos_amplang.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
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
        // Setup Role & User Admin
        long roleId = db.userRoleDao().insert(new UserRole("Admin", "Administrator Sistem"));
        long userId = db.userDao().insert(new User((int)roleId, "EMP-001", "Admin Utama", "admin", "admin", "081234567890"));

        // Setup Kategori & Merek
        long katAmplangId = db.kategoriDao().insert(new Kategori("Amplang", "Makanan ringan khas kaltim"));
        long katKueId = db.kategoriDao().insert(new Kategori("Kue Kering", "Gabin, kuku macan, dll"));
        long katBahanId = db.kategoriDao().insert(new Kategori("Bahan Produksi", "Bahan baku produksi amplang"));
        long katKemasanId = db.kategoriDao().insert(new Kategori("Kemasan", "Perlengkapan kemasan produk"));
        long merkSalsabilaId = db.merekDao().insert(new Merek("Salsabila"));
        long merkLainId = db.merekDao().insert(new Merek("Khas Kaltim"));

        // Setup 5 Suppliers
        long sup1 = db.supplierDao().insert(new Supplier("PT Ikan Segar", "Jl. Nelayan No 1", "081111111", "ikansegar@email.com", true));
        long sup2 = db.supplierDao().insert(new Supplier("Toko Terigu Makmur", "Jl. Pasar Pagi", "082222222", "terigumakmur@email.com", true));
        long sup3 = db.supplierDao().insert(new Supplier("CV Minyak Kelapa", "Jl. Industri", "083333333", "minyakkelapa@email.com", true));
        long sup4 = db.supplierDao().insert(new Supplier("Bumbu Nusantara", "Jl. Rempah No 4", "084444444", "bumbunusantara@email.com", true));
        long sup5 = db.supplierDao().insert(new Supplier("Plastik Kemasan Jaya", "Jl. Gajah Mada", "085555555", "kemasanjaya@email.com", true));

        // Setup produk dengan supplier yang sesuai untuk pembelian stok awal.
        ProdukDao produkDao = db.produkDao();
        int kA = (int)katAmplangId; int kK = (int)katKueId;
        int kB = (int)katBahanId; int kP = (int)katKemasanId;
        int mS = (int)merkSalsabilaId; int mL = (int)merkLainId;
        int s1 = (int)sup1; int s2 = (int)sup2; int s3 = (int)sup3;
        int s4 = (int)sup4; int s5 = (int)sup5;

        int pTenggiri = (int) produkDao.insert(new Produk(kA, mS, s1, "Amplang Ikan Tenggiri Asli", "Bungkus", 25000, 100));
        int pPipih = (int) produkDao.insert(new Produk(kA, mS, s1, "Amplang Ikan Pipih Super", "Bungkus", 30000, 85));
        int pKuku = (int) produkDao.insert(new Produk(kA, mS, s2, "Amplang Kuku Macan", "Bungkus", 20000, 120));
        produkDao.insert(new Produk(kA, mL, s1, "Amplang Ikan Belida", "Bungkus", 35000, 50));
        produkDao.insert(new Produk(kA, mS, s1, "Amplang Kepiting", "Bungkus", 28000, 60));
        produkDao.insert(new Produk(kA, mS, s1, "Amplang Udang Spesial", "Bungkus", 27000, 70));
        produkDao.insert(new Produk(kA, mL, s1, "Amplang Rumput Laut", "Bungkus", 22000, 90));
        int pGabin = (int) produkDao.insert(new Produk(kK, mS, s2, "Gabin Susu Salsabila", "Pcs", 25000, 150));
        produkDao.insert(new Produk(kK, mS, s2, "Gabin Keju Spesial", "Pcs", 28000, 110));
        produkDao.insert(new Produk(kK, mS, s2, "Gabin Coklat Lumer", "Pcs", 30000, 80));
        produkDao.insert(new Produk(kK, mL, s1, "Kerupuk Ikan Gabus", "Bungkus", 15000, 200));
        produkDao.insert(new Produk(kK, mL, s1, "Kerupuk Udang Manis", "Bungkus", 18000, 180));
        int pPia = (int) produkDao.insert(new Produk(kK, mS, s2, "Pia Kacang Hijau", "Kotak", 35000, 40));
        produkDao.insert(new Produk(kK, mS, s2, "Pia Coklat Keju", "Kotak", 40000, 35));
        int pMiniBalado = (int) produkDao.insert(new Produk(kA, mL, s4, "Amplang Mini Balado", "Bungkus", 12000, 250));
        int pMinyak = (int) produkDao.insert(new Produk(kB, mL, s3, "Minyak Kelapa Produksi", "Liter", 18000, 60));
        int pBumbu = (int) produkDao.insert(new Produk(kB, mL, s4, "Bumbu Balado Amplang", "Pack", 12000, 40));
        int pKemasan = (int) produkDao.insert(new Produk(kP, mL, s5, "Standing Pouch Amplang 100gr", "Pack", 25000, 100));

        // Setup pelanggan untuk history transaksi kasir.
        int pelangganUmum = (int) db.pelangganDao().insert(new Pelanggan("Pelanggan Umum"));
        int pelangganRina = (int) db.pelangganDao().insert(new Pelanggan("Rina Saputri"));
        int pelangganBudi = (int) db.pelangganDao().insert(new Pelanggan("Budi Santoso"));

        long today = getDayStart(System.currentTimeMillis());
        long day4 = today - (4 * 86400000L);
        long day3 = today - (3 * 86400000L);
        long day2 = today - (2 * 86400000L);
        long day1 = today - 86400000L;
        int adminId = (int) userId;

        // History pembelian supplier: selesai menambah stok, status lain tidak mengubah stok.
        int reqIkan = seedRequest(db, s1, adminId, seedNumber("REQ", day4, 1), day4 + hour(8), "Selesai", day4 + hour(15));
        seedCompletedRequestItem(db, reqIkan, pTenggiri, "Amplang Ikan Tenggiri Asli", "Bungkus", 30, 25000, 16000, adminId, day4 + hour(15));
        seedCompletedRequestItem(db, reqIkan, pPipih, "Amplang Ikan Pipih Super", "Bungkus", 20, 30000, 19000, adminId, day4 + hour(15));

        int reqTerigu = seedRequest(db, s2, adminId, seedNumber("REQ", day3, 1), day3 + hour(9), "Selesai", day3 + hour(16));
        seedCompletedRequestItem(db, reqTerigu, pGabin, "Gabin Susu Salsabila", "Pcs", 40, 25000, 14000, adminId, day3 + hour(16));
        seedCompletedRequestItem(db, reqTerigu, pPia, "Pia Kacang Hijau", "Kotak", 10, 35000, 23000, adminId, day3 + hour(16));

        int reqKemasan = seedRequest(db, s5, adminId, seedNumber("REQ", day2, 1), day2 + hour(9), "Gagal", 0);
        seedRequestItem(db, reqKemasan, pKemasan, "Standing Pouch Amplang 100gr", "Pack", 50, 25000, 15000);

        int reqBatal = seedRequest(db, s1, adminId, seedNumber("REQ", day1, 1), day1 + hour(9), "Dibatalkan", 0);
        seedRequestItem(db, reqBatal, pTenggiri, "Amplang Ikan Tenggiri Asli", "Bungkus", 20, 25000, 16000);

        int pLabel = (int) produkDao.insert(new Produk(kP, mL, s5, "Label Stiker Amplang", "Pack", 18000, 25));
        int reqProdukBaru = seedRequest(db, s5, adminId, seedNumber("REQ", day1, 2), day1 + hour(10), "Selesai", day1 + hour(17));
        seedCompletedNewProduct(db, reqProdukBaru, pLabel, "Label Stiker Amplang", kP, mL,
                "Pack", 25, 18000, 10000, adminId, day1 + hour(17));

        int reqBumbu = seedRequest(db, s4, adminId, seedNumber("REQ", today, 1), today + hour(8), "Selesai", today + hour(12));
        seedCompletedRequestItem(db, reqBumbu, pBumbu, "Bumbu Balado Amplang", "Pack", 25, 12000, 7000, adminId, today + hour(12));
        seedCompletedRequestItem(db, reqBumbu, pMiniBalado, "Amplang Mini Balado", "Bungkus", 50, 12000, 7000, adminId, today + hour(12));

        int reqMinyak = seedRequest(db, s3, adminId, seedNumber("REQ", today, 2), today + hour(13), "Diproses", 0);
        seedRequestItem(db, reqMinyak, pMinyak, "Minyak Kelapa Produksi", "Liter", 30, 18000, 12000);

        // History kasir: transaksi lunas mengurangi stok dan masuk laporan pemasukan.
        seedSale(db, pelangganUmum, adminId, seedNumber("ORD", day4, 1), day4 + hour(18), "Tunai",
                new int[]{pTenggiri, pPipih}, new int[]{2, 1}, new double[]{25000, 30000}, "Lunas");
        seedSale(db, pelangganRina, adminId, seedNumber("ORD", day3, 1), day3 + hour(14), "QRIS",
                new int[]{pKuku}, new int[]{3}, new double[]{20000}, "Lunas");
        seedSale(db, pelangganBudi, adminId, seedNumber("ORD", day2, 1), day2 + hour(11), "Tunai",
                new int[]{pTenggiri, pPipih}, new int[]{1, 2}, new double[]{25000, 30000}, "Lunas");
        seedSale(db, pelangganRina, adminId, seedNumber("ORD", day1, 1), day1 + hour(10), "QRIS",
                new int[]{pMiniBalado}, new int[]{5}, new double[]{12000}, "Lunas");
        seedSale(db, pelangganUmum, adminId, seedNumber("ORD", day1, 2), day1 + hour(15), "Tunai",
                new int[]{pGabin}, new int[]{1}, new double[]{25000}, "Menunggu");
        seedSale(db, pelangganUmum, adminId, seedNumber("ORD", today, 1), today + hour(15), "Tunai",
                new int[]{pTenggiri, pPipih}, new int[]{2, 1}, new double[]{25000, 30000}, "Lunas");
        seedSale(db, pelangganBudi, adminId, seedNumber("ORD", today, 2), today + hour(16), "QRIS",
                new int[]{pKuku}, new int[]{2}, new double[]{20000}, "Lunas");

        // Snapshot laporan lama; laporan hari ini tetap dapat disimpan user melalui tombol Simpan.
        seedReport(db, day4, 80000, 860000, 80000, 0, 0, 1);
        seedReport(db, day3, 60000, 790000, 0, 60000, 0, 1);
        seedReport(db, day2, 85000, 0, 85000, 0, 0, 1);
        seedReport(db, day1, 60000, 250000, 0, 60000, 25000, 1);
    }

    private static long getDayStart(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static long hour(int hours) {
        return hours * 3600000L;
    }

    private static String seedNumber(String prefix, long date, int sequence) {
        return prefix + "-" + new SimpleDateFormat("yyyyMMdd", Locale.US)
                .format(new Date(date)) + "-" + String.format(Locale.US, "%03d", sequence);
    }

    private static int seedRequest(AppDatabase db, int supplierId, int userId, String number,
                                   long requestedAt, String status, long completedAt) {
        int requestId = (int) db.stokRequestDao().insert(
                new StokRequest(supplierId, userId, number, requestedAt, status));
        if (completedAt > 0) {
            StokRequest request = db.stokRequestDao().getById(requestId);
            request.setTanggal_selesai(completedAt);
            db.stokRequestDao().update(request);
        }
        return requestId;
    }

    private static void seedRequestItem(AppDatabase db, int requestId, int productId, String name,
                                        String unit, int quantity, double salePrice, double purchasePrice) {
        db.detailStokRequestDao().insert(new DetailStokRequest(
                requestId, productId, quantity, false, name, null, null,
                unit, salePrice, purchasePrice));
    }

    private static void seedCompletedRequestItem(AppDatabase db, int requestId, int productId,
                                                  String name, String unit, int quantity, double salePrice,
                                                  double purchasePrice, int userId, long completedAt) {
        seedRequestItem(db, requestId, productId, name, unit, quantity, salePrice, purchasePrice);
        db.produkDao().tambahStok(productId, quantity);
        db.stokAdjustmentDao().insert(new StokAdjustment(
                productId, userId, completedAt, "Pembelian Supplier", quantity));
    }

    private static void seedCompletedNewProduct(AppDatabase db, int requestId, int productId,
                                                String name, int categoryId, int brandId, String unit,
                                                int quantity, double salePrice, double purchasePrice,
                                                int userId, long completedAt) {
        db.detailStokRequestDao().insert(new DetailStokRequest(
                requestId, productId, quantity, true, name, categoryId, brandId,
                unit, salePrice, purchasePrice));
        db.stokAdjustmentDao().insert(new StokAdjustment(
                productId, userId, completedAt, "Pembelian Supplier", quantity));
    }

    private static void seedSale(AppDatabase db, int customerId, int userId, String orderNumber,
                                 long orderAt, String method, int[] productIds, int[] quantities,
                                 double[] prices, String paymentStatus) {
        String orderStatus = "Lunas".equals(paymentStatus) ? "Selesai" : "Diproses";
        int orderId = (int) db.pesananDao().insert(
                new Pesanan(customerId, orderNumber, orderAt, "Langsung", orderStatus));
        double total = 0;
        for (int i = 0; i < productIds.length; i++) {
            double subTotal = quantities[i] * prices[i];
            total += subTotal;
            db.detailPesananDao().insert(new DetailPesanan(
                    productIds[i], orderId, userId, quantities[i], prices[i], subTotal));
            if ("Lunas".equals(paymentStatus)) {
                db.produkDao().kurangiStok(productIds[i], quantities[i]);
                db.stokAdjustmentDao().insert(new StokAdjustment(
                        productIds[i], userId, orderAt, "Penjualan", quantities[i]));
            }
        }
        db.pembayaranDao().insert(new PembayaranPesanan(
                orderId, method, paymentStatus, orderAt, total, 0));
    }

    private static void seedReport(AppDatabase db, long reportDate, double income, double expense,
                                   double cash, double online, double pending, int transactionCount) {
        db.laporanHarianDao().insertOrReplace(new LaporanHarian(
                reportDate, reportDate + hour(21), income, expense, income - expense,
                cash, online, pending, transactionCount));
    }
}
