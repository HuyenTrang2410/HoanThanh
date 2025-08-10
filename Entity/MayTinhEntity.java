/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author ASUS
 */
public class MayTinhEntity {

    int ID;
    String ten_may;
    Integer id_khu_vuc;
    String trangThai;

    public MayTinhEntity() {
    }

    public MayTinhEntity(int ID, String ten_may, int id_khu_vuc) {
        this.ID = ID;
        this.ten_may = ten_may;
        this.id_khu_vuc = id_khu_vuc;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public MayTinhEntity(String ten_may, Integer id_khu_vuc) {
        this.ten_may = ten_may;
        this.id_khu_vuc = id_khu_vuc;
    }

    public String getTen_may() {
        return ten_may;
    }

    public void setTen_may(String ten_may) {
        this.ten_may = ten_may;
    }

    public Integer getId_khu_vuc() {
        return id_khu_vuc;
    }

    public void setId_khu_vuc(int id_khu_vuc) {
        this.id_khu_vuc = id_khu_vuc;
    }

    public void setId(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setTenMay(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setTrangThai(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setCauHinh(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getTrangThai() {
        return trangThai;
    }
    
    public MayTinhEntity withTrangThai(String trangThai) {
        this.trangThai = trangThai;
        return this;
    }

}
