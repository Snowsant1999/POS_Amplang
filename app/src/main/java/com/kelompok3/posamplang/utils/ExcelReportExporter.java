package com.kelompok3.posamplang.utils;

import android.content.Context;
import android.net.Uri;

import com.kelompok3.posamplang.models.LaporanHarian;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ExcelReportExporter {

    private ExcelReportExporter() { }

    public static void write(Context context, Uri uri, LaporanHarian laporan) throws IOException {
        try (OutputStream stream = context.getContentResolver().openOutputStream(uri);
             ZipOutputStream zip = new ZipOutputStream(stream)) {
            add(zip, "[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                            "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
                            "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                            "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>" +
                            "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" +
                            "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>" +
                            "</Types>");
            add(zip, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                            "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>" +
                            "</Relationships>");
            add(zip, "xl/workbook.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" " +
                            "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">" +
                            "<sheets><sheet name=\"Laporan Harian\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            add(zip, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                            "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>" +
                            "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>" +
                            "</Relationships>");
            add(zip, "xl/styles.xml", styles());
            add(zip, "xl/worksheets/sheet1.xml",
                    worksheet(laporan, StoreSettings.get(context)));
        }
    }

    private static String worksheet(LaporanHarian laporan, StoreSettings.StoreSettingsData store) {
        String tanggal = new SimpleDateFormat("dd/MM/yyyy", new Locale("id", "ID"))
                .format(laporan.getTanggal_laporan());
        String disimpan = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("id", "ID"))
                .format(laporan.getDisimpan_pada());
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
                .append("<cols><col min=\"1\" max=\"1\" width=\"30\" customWidth=\"1\"/>")
                .append("<col min=\"2\" max=\"2\" width=\"22\" customWidth=\"1\"/></cols><sheetData>")
                .append(row(1, text("A1", "LAPORAN KEUANGAN HARIAN", 2)))
                .append(row(2, text("A2", "Nama Toko", 3), text("B2", store.name, 0)))
                .append(row(3, text("A3", "Alamat", 3), text("B3", store.address, 0)))
                .append(row(4, text("A4", "Email", 3), text("B4", store.email, 0)))
                .append(row(5, text("A5", "Telepon", 3), text("B5", store.phone, 0)))
                .append(row(7, text("A7", "Tanggal Laporan", 3), text("B7", tanggal, 0)))
                .append(row(8, text("A8", "Disimpan Pada", 3), text("B8", disimpan, 0)))
                .append(row(10, text("A10", "RINGKASAN KEUANGAN", 2)))
                .append(row(11, text("A11", "Pemasukan", 3), number("B11", laporan.getPemasukan())))
                .append(row(12, text("A12", "Pengeluaran", 3), number("B12", laporan.getPengeluaran())))
                .append(row(13, text("A13", "Saldo Bersih", 3), number("B13", laporan.getSaldo_bersih())))
                .append(row(15, text("A15", "METODE PEMBAYARAN", 2)))
                .append(row(16, text("A16", "Tunai", 3), number("B16", laporan.getPembayaran_tunai())))
                .append(row(17, text("A17", "Online / QRIS", 3), number("B17", laporan.getPembayaran_online())))
                .append(row(18, text("A18", "Tertunda", 3), number("B18", laporan.getPembayaran_tertunda())))
                .append(row(20, text("A20", "Jumlah Transaksi Selesai", 3),
                        plainNumber("B20", laporan.getJumlah_transaksi())))
                .append("</sheetData><mergeCells count=\"3\"><mergeCell ref=\"A1:B1\"/>")
                .append("<mergeCell ref=\"A10:B10\"/><mergeCell ref=\"A15:B15\"/></mergeCells></worksheet>");
        return xml.toString();
    }

    private static String styles() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" +
                "<numFmts count=\"1\"><numFmt numFmtId=\"164\" formatCode=\"&quot;Rp&quot; #,##0\"/></numFmts>" +
                "<fonts count=\"3\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font>" +
                "<font><b/><sz val=\"16\"/><color rgb=\"FFFFFFFF\"/><name val=\"Calibri\"/></font>" +
                "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>" +
                "<fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill>" +
                "<fill><patternFill patternType=\"gray125\"/></fill>" +
                "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFB21F1F\"/><bgColor indexed=\"64\"/></patternFill></fill></fills>" +
                "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>" +
                "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>" +
                "<cellXfs count=\"4\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>" +
                "<xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyNumberFormat=\"1\"/>" +
                "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\"/>" +
                "<xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/></cellXfs>" +
                "</styleSheet>";
    }

    private static String row(int row, String... cells) {
        StringBuilder value = new StringBuilder("<row r=\"").append(row).append("\">");
        for (String cell : cells) {
            value.append(cell);
        }
        return value.append("</row>").toString();
    }

    private static String text(String cell, String value, int style) {
        return "<c r=\"" + cell + "\" t=\"inlineStr\" s=\"" + style + "\"><is><t>" +
                escape(value) + "</t></is></c>";
    }

    private static String number(String cell, double value) {
        return "<c r=\"" + cell + "\" s=\"1\"><v>" + value + "</v></c>";
    }

    private static String plainNumber(String cell, int value) {
        return "<c r=\"" + cell + "\"><v>" + value + "</v></c>";
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private static void add(ZipOutputStream zip, String path, String contents) throws IOException {
        zip.putNextEntry(new ZipEntry(path));
        zip.write(contents.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}
