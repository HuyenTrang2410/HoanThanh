/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author ASUS
 */
public class Auth {

    public static String currentUserTenDangNhap; // Lưu tên đăng nhập
    public static String currentUserVaiTro;      // Lưu vai trò
    public static double currentUserSoDu;           // Lưu số dư

    // Hàm kiểm tra thông tin đăng nhập
    public boolean login(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM Tai_Khoan WHERE ten_dang_nhap = ? AND mat_khau = ?";
        try {
            Connection con = KetNoiDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Nếu đăng nhập thành công -> Lưu thông tin người dùng hiện tại
                currentUserTenDangNhap = rs.getString("ten_dang_nhap");
                currentUserVaiTro = rs.getString("vai_tro");
                currentUserSoDu = rs.getDouble("so_du");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy vai trò người dùng theo tên đăng nhập
    public String getVaiTro(String tenDangNhap) {
        String sql = "SELECT vai_tro FROM Tai_Khoan WHERE ten_dang_nhap = ?";
        try {
            Connection con = KetNoiDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("vai_tro");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class user {

        public user() {
        }
    }
}
