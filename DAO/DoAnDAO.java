/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import Utils.KetNoiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import Entity.DoAn;
import java.util.Collections;
/**
 *
 * @author ASUS
 */
public class DoAnDAO {
   public List<DoAn> danhSachSanPham(int danhMuc) {
        var list = new ArrayList<DoAn>();
        String sql = "SELECT * FROM San_pham WHERE id_danh_muc = ?";
        try (Connection con = KetNoiDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setInt(1, danhMuc);
            // id_danh_muc = 1: Đồ uống, id_danh_muc = 2: Đồ ăn
            var rs = ps.executeQuery();
            while (rs.next()) {
                DoAn doAn = new DoAn(rs.getString("ten_sp"), rs.getInt("id_danh_muc"), rs.getInt("gia_sp"))
                        .withId_san_pham(rs.getInt("id_san_pham"));
                list.add(doAn);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public int createSanPham(DoAn doAn) {
        if (doAn == null || doAn.getTen_sp() == null || doAn.getId_danh_muc() <= 0 || doAn.getGia_sp() <= 0) {
            return 0; // Invalid input
        }
        // Kiểm tra xem sản phẩm đã tồn tại chưa
        String checkSql = "SELECT COUNT(*) FROM San_pham WHERE ten_sp = ? AND id_danh_muc = ?";
        String sql = "INSERT INTO San_pham VALUES (?, ?, ?)";
        try (Connection con = KetNoiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            // Kiểm tra sản phẩm đã tồn tại
            checkPs.setString(1, doAn.getTen_sp());
            checkPs.setInt(2, doAn.getId_danh_muc());
            var rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return -1; // Sản phẩm đã tồn tại
            }
            ps.setString(1, doAn.getTen_sp());
            ps.setInt(2, doAn.getId_danh_muc());
            ps.setInt(3, doAn.getGia_sp());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int xoaSanPham(int id) {
        String sqlDeleteOrders = "DELETE FROM Chi_tiet_don_hang WHERE id_san_pham = ?";
        String sql = "DELETE FROM San_pham WHERE id_san_pham = ?";
        try (Connection con = KetNoiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             PreparedStatement psDeleteOrders = con.prepareStatement(sqlDeleteOrders)) {
            // Xóa các chi tiết đơn hàng liên quan đến sản phẩm
            psDeleteOrders.setInt(1, id);
            psDeleteOrders.executeUpdate();
            // Xóa sản phẩm
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int SuaSanPham(DoAn doAn) {
        if (doAn == null || doAn.getId_san_pham() <= 0 || doAn.getTen_sp() == null || doAn.getId_danh_muc() <= 0 || doAn.getGia_sp() <= 0) {
            return 0; // Invalid input
        }
        // Kiểm tra xem tên sản phẩm có trùng không
        String checkSql = "SELECT COUNT(*) FROM San_pham WHERE id_san_pham != ? AND ten_sp = ?";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, doAn.getId_san_pham());
            checkPs.setString(2, doAn.getTen_sp());
            var rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return -1; // Tên sản phẩm đã tồn tại
            }
            String sql = "UPDATE San_pham SET ten_sp = ?, gia_sp = ?, id_danh_muc = ? WHERE id_san_pham = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, doAn.getTen_sp());
            ps.setInt(2, doAn.getGia_sp());
            ps.setInt(3, doAn.getId_danh_muc());
            ps.setInt(4, doAn.getId_san_pham());
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
