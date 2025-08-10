/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Entity.ThongKeTaiKhoan;
import Utils.KetNoiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class ThongKeTaiKhoangDAO {

    public List<ThongKeTaiKhoan> ThongTinThongKeTK() {
        List<ThongKeTaiKhoan> thongKeTaiKhoanLst = new ArrayList<>();
        try (Connection conn = KetNoiDB.getConnection()) {
            String sql = "SELECT id_tk, ho_ten,mat_khau, so_du FROM Tai_khoan ORDER BY so_du DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id_tk = rs.getInt("id_tk");
                String ho_ten = rs.getString("ho_ten");
                int so_du = rs.getInt("so_du");
                ThongKeTaiKhoan thongKe = new ThongKeTaiKhoan(id_tk, ho_ten, so_du);
                thongKeTaiKhoanLst.add(thongKe);
            }
            return thongKeTaiKhoanLst;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            return thongKeTaiKhoanLst;
        }
    }
}
