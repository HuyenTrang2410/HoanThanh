/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author ASUS
 */

import DTO.DonHangDTO;

import java.util.List;
import java.util.Map;

public class DonHang {
    private int id_don_hang;
    private long tong_tien;
    private String thoi_gian;
    private String trang_thai;
    private int id_tk;

    private List<ChiTietDonHang> don_hang;

    public DonHang(Map.Entry<Integer, List<DonHangDTO>> dto) {
        this.id_don_hang = dto.getKey();
        this.tong_tien = dto.getValue().get(0).getTongTien();
        this.thoi_gian = dto.getValue().get(0).getThoiGian();
        this.trang_thai = dto.getValue().get(0).getTrangThai();
        this.id_tk = dto.getValue().get(0).getIdTk();
        this.don_hang = dto.getValue().stream()
                .map(dh -> new ChiTietDonHang(dh))
                .toList();
    }
    public int getId_don_hang() {
        return id_don_hang;
    }

    public void setId_don_hang(int id_don_hang) {
        this.id_don_hang = id_don_hang;
    }

    public long getTong_tien() {
        return tong_tien;
    }

    public void setTong_tien(long tong_tien) {
        this.tong_tien = tong_tien;
    }

    public String getThoi_gian() {
        return thoi_gian;
    }

    public void setThoi_gian(String thoi_gian) {
        this.thoi_gian = thoi_gian;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public int getId_tk() {
        return id_tk;
    }

    public void setId_tk(int id_tk) {
        this.id_tk = id_tk;
    }

    public List<ChiTietDonHang> getDon_hang() {
        return don_hang;
    }

    public void setDon_hang(List<ChiTietDonHang> don_hang) {
        this.don_hang = don_hang;
    }
}
