/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;
import java.util.Date;
/**
 *
 * @author ASUS
 */
public class DonHangEntity {
    private int id_don_hang;   // Mã đơn hàng (có thể sinh tự động trong DB)
    private int id_tk;         // Mã tài khoản người đặt hàng
    private int tong_tien;     // Tổng tiền của đơn hàng
    private String thoi_gian;  // Thời gian đặt hàng
    private String trang_thai; // Trạng thái đơn hàng (true = đã xử lý, false = chưa xử lý)

    // Constructor đầy đủ
    public DonHangEntity(int id_don_hang, int id_tk, int tong_tien, String thoi_gian, String trang_thai) {
        this.id_don_hang = id_don_hang;
        this.id_tk = id_tk;
        this.tong_tien = tong_tien;
        this.thoi_gian = thoi_gian;
        this.trang_thai = trang_thai;
    }

    // Constructor không có id_don_hang (dùng khi tạo mới, ID tự sinh trong DB)
    public DonHangEntity(int id_tk, int tong_tien, String thoi_gian, String trang_thai) {
        this.id_tk = id_tk;
        this.tong_tien = tong_tien;
        this.thoi_gian = thoi_gian;
        this.trang_thai = trang_thai;
    }

    // Constructor rỗng (dùng trong các framework như Hibernate, Jackson,...)
    public DonHangEntity() {
    }

    // Getters and setters
    public int getId_don_hang() {
        return id_don_hang;
    }

    public void setId_don_hang(int id_don_hang) {
        this.id_don_hang = id_don_hang;
    }

    public int getId_tk() {
        return id_tk;
    }

    public void setId_tk(int id_tk) {
        this.id_tk = id_tk;
    }

    public int getTong_tien() {
        return tong_tien;
    }

    public void setTong_tien(int tong_tien) {
        this.tong_tien = tong_tien;
    }

    public String getThoi_gian() {
        return thoi_gian;
    }

    public void setThoi_gian(String thoi_gian) {
        this.thoi_gian = thoi_gian;
    }

    public String isTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }
}

