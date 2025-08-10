/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

/**
 *
 * @author ASUS
 */
public class ThongKeTaiKhoan {

    int id_tk;
    String ho_ten;
    int so_du;

    public ThongKeTaiKhoan() {
    }

    public ThongKeTaiKhoan(int id_tk, String ho_ten, int so_du) {
        this.id_tk = id_tk;
        this.ho_ten = ho_ten;
        this.so_du = so_du;
    }

    public int getId_tk() {
        return id_tk;
    }

    public void setId_tk(int id_tk) {
        this.id_tk = id_tk;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public void setHo_ten(String ho_ten) {
        this.ho_ten = ho_ten;
    }

    public int getSo_du() {
        return so_du;
    }

    public void setSo_du(int so_du) {
        this.so_du = so_du;
    }

}
