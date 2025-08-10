/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author ASUS
 */
public class KhuVucEntity {
    String ten_khu_vuc;
    int gia_khu_vuc;

    public KhuVucEntity() {
    }

    public KhuVucEntity(String ten_khu_vuc, int gia_khu_vuc) {
        this.ten_khu_vuc = ten_khu_vuc;
        this.gia_khu_vuc = gia_khu_vuc;
    }

    public String getTen_khu_vuc() {
        return ten_khu_vuc;
    }

    public void setTen_khu_vuc(String ten_khu_vuc) {
        this.ten_khu_vuc = ten_khu_vuc;
    }

    public int getGia_khu_vuc() {
        return gia_khu_vuc;
    }

    public void setGia_khu_vuc(int gia_khu_vuc) {
        this.gia_khu_vuc = gia_khu_vuc;
    }
    
    
}
