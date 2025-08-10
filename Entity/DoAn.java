/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author ASUS
 */
public class DoAn {
 int id_san_pham;

    String ten_sp;
    int id_danh_muc;
    int gia_sp;

    public DoAn() {
    }

    public DoAn(DoAn dto) {
        this(dto.getTen_sp(), dto.getId_danh_muc(), dto.getGia_sp());
        this.id_san_pham = dto.getId_san_pham();
    }

    public DoAn(String ten_sp, int id_danh_muc, int gia_sp) {
        this.ten_sp = ten_sp;
        this.id_danh_muc = id_danh_muc;
        this.gia_sp = gia_sp;
    }

    public void setTen_sp(String ten_sp) {
        this.ten_sp = ten_sp;
    }

    public void setId_danh_muc(int id_danh_muc) {
        this.id_danh_muc = id_danh_muc;
    }

    public void setGia_sp(int gia_sp) {
        this.gia_sp = gia_sp;
    }

    public String getTen_sp() {
        return ten_sp;
    }

    public int getId_danh_muc() {
        return id_danh_muc;
    }

    public int getGia_sp() {
        return gia_sp;
    }

    public int getId_san_pham() {
        return id_san_pham;
    }

    public DoAn withId_san_pham(int id_san_pham) {
        this.id_san_pham = id_san_pham;
        return this;
    }

    @Override
    public String toString() {
        return "DoAn{" +
                "id_san_pham=" + id_san_pham +
                ", ten_sp='" + ten_sp + '\'' +
                ", id_danh_muc=" + id_danh_muc +
                ", gia_sp=" + gia_sp +
                '}';
    }

}
