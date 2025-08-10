/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

/**
 *
 * @author ASUS
 */

import java.sql.ResultSet;
import java.sql.SQLException;

public class DonHangDTO {
    private int idDonHang;
    private String thoiGian;
    private int idTk;
    private String trangThai;
    private long tongTien;
    private int idCtdh;
    private String tenSp;
    private int giaSp;
    private int soLuong;
    private long thanhTien;

    public DonHangDTO(ResultSet rs) throws SQLException {
        this.idDonHang = rs.getInt("id_don_hang");
        this.thoiGian = rs.getString("thoi_gian");
        this.idTk = rs.getInt("id_tk");
        this.trangThai = rs.getString("trang_thai");
        this.tongTien = rs.getLong("tong_tien");
        this.idCtdh = rs.getInt("id_ctdh");
        this.tenSp = rs.getString("ten_sp");
        this.giaSp = rs.getInt("gia_sp");
        this.soLuong = rs.getInt("so_luong");
        this.thanhTien = rs.getLong("thanh_tien");
    }

    public int getIdDonHang() {
        return idDonHang;
    }

    public void setIdDonHang(int idDonHang) {
        this.idDonHang = idDonHang;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public int getIdTk() {
        return idTk;
    }

    public void setIdTk(int idTk) {
        this.idTk = idTk;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    public int getIdCtdh() {
        return idCtdh;
    }

    public void setIdCtdh(int idCtdh) {
        this.idCtdh = idCtdh;
    }

    public String getTenSp() {
        return tenSp;
    }

    public void setTenSp(String tenSp) {
        this.tenSp = tenSp;
    }

    public int getGiaSp() {
        return giaSp;
    }

    public void setGiaSp(int giaSp) {
        this.giaSp = giaSp;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public long getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(long thanhTien) {
        this.thanhTien = thanhTien;
    }
}

