/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import DTO.DonHangDTO;

/**
 *
 * @author ASUS
 */
public class ChiTietDonHang {
 int id_don_hang, id_san_pham, so_luong, gia, id_ctdh;
    String ghi_chu;
    String ten_san_pham;
    long thanh_tien;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(DonHangDTO dto) {
        this.id_ctdh = dto.getIdCtdh();
        this.id_don_hang = dto.getIdDonHang();
        this.id_san_pham = dto.getIdCtdh();
        this.so_luong = dto.getSoLuong();
        this.gia = dto.getGiaSp();
        this.ghi_chu = dto.getTenSp();
        this.ten_san_pham = dto.getTenSp();
        this.thanh_tien = dto.getThanhTien();
    }

    public ChiTietDonHang(int id_don_hang, int id_san_pham, int so_luong, int gia, String ghi_chu) {
        this.id_don_hang = id_don_hang;
        this.id_san_pham = id_san_pham;
        this.so_luong = so_luong;
        this.gia = gia;
        this.ghi_chu = ghi_chu;
    }

    public int getId_don_hang() {
        return id_don_hang;
    }

    public void setId_don_hang(int id_don_hang) {
        this.id_don_hang = id_don_hang;
    }

    public int getId_san_pham() {
        return id_san_pham;
    }

    public void setId_san_pham(int id_san_pham) {
        this.id_san_pham = id_san_pham;
    }

    public int getSo_luong() {
        return so_luong;
    }

    public void setSo_luong(int so_luong) {
        this.so_luong = so_luong;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getGhi_chu() {
        return ghi_chu;
    }

    public void setGhi_chu(String ghi_chu) {
        this.ghi_chu = ghi_chu;
    }

    public String getTen_san_pham() {
        return ten_san_pham;
    }

    public void setTen_san_pham(String ten_san_pham) {
        this.ten_san_pham = ten_san_pham;
    }

    public long getThanh_tien() {
        return thanh_tien;
    }

    public void setThanh_tien(long thanh_tien) {
        this.thanh_tien = thanh_tien;
    }

    public int getId_ctdh() {
        return id_ctdh;
    }

    public void setId_ctdh(int id_ctdh) {
        this.id_ctdh = id_ctdh;
    }

}
