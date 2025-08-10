/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author ASUS
 */
public class ThongKeDoanhThu {
       private int id_san_pham;
    private String ten_sp;
    private int so_luong_ban;
    private int tong_doanh_thu;

    public ThongKeDoanhThu() {
    }

    public ThongKeDoanhThu(int id_san_pham, String ten_sp, int so_luong_ban, int tong_doanh_thu) {
        this.id_san_pham = id_san_pham;
        this.ten_sp = ten_sp;
        this.so_luong_ban = so_luong_ban;
        this.tong_doanh_thu = tong_doanh_thu;
    }

    public int getId_san_pham() {
        return id_san_pham;
    }

    public void setId_san_pham(int id_san_pham) {
        this.id_san_pham = id_san_pham;
    }

    public String getTen_sp() {
        return ten_sp;
    }

    public void setTen_sp(String ten_sp) {
        this.ten_sp = ten_sp;
    }

    public int getSo_luong_ban() {
        return so_luong_ban;
    }

    public void setSo_luong_ban(int so_luong_ban) {
        this.so_luong_ban = so_luong_ban;
    }

    public int getTong_doanh_thu() {
        return tong_doanh_thu;
    }

    public void setTong_doanh_thu(int tong_doanh_thu) {
        this.tong_doanh_thu = tong_doanh_thu;
    }
    
    
}
