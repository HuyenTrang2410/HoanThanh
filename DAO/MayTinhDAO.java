/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Entity.MayTinhEntity;
import Utils.KetNoiDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class MayTinhDAO {
    // Lấy ID PC từ tên máy

    public int getIDPC(String tenMay) {
        String sql = "SELECT id_may FROM May_tinh WHERE ten_may = ?";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenMay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_may");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy danh sách tất cả PC (để hiển thị lên Table)
    public List<MayTinhEntity> getAllPC() {
        List<MayTinhEntity> lstPC = new ArrayList<>();
        String sql = "SELECT * FROM May_tinh";
        try (Connection conn = KetNoiDB.getConnection(); Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                lstPC.add(new MayTinhEntity(
                        rs.getInt("id_may"),
                        rs.getString("ten_may"),
                        rs.getInt("id_khu_vuc")
                ).withTrangThai(rs.getString("trang_thai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstPC;
    }

    // Thêm máy tính mới
    public boolean addPC(MayTinhEntity mt) {
        var sql = """
                        IF NOT EXISTS(SELECT 1 FROM May_tinh WHERE ten_may = ?)
                        INSERT INTO May_tinh(ten_may, id_khu_vuc) VALUES (?, ?)
                        """;
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mt.getTen_may());
            ps.setString(2, mt.getTen_may());
            ps.setObject(3, mt.getId_khu_vuc());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin máy tính
    public boolean updatePCInfo(int id, String tenMay, int idKhuVuc) {
        String sql = "UPDATE May_tinh SET ten_may = ?, id_khu_vuc = ? WHERE id_may = ?";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenMay);
            ps.setInt(2, idKhuVuc);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
        // Cập nhật thông tin máy tính
    public boolean updateStatus(String tenMay, String status) {
        String sql = "UPDATE May_tinh SET trang_thai = ? WHERE ten_may = ?";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, tenMay);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách tên các máy
    public List<String> getListPCNames() {
        List<String> lstPC = new ArrayList<>();
        String sql = "SELECT ten_may FROM May_tinh";
        try (Connection conn = KetNoiDB.getConnection(); Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                lstPC.add(rs.getString("ten_may"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstPC;
    }

    // Lấy giá khu vực theo tên máy
    public int getMoney(String tenMay) {
        String sql = "SELECT gia_khu_vuc FROM Khu_vuc "
                + "JOIN May_tinh ON May_tinh.id_khu_vuc = Khu_vuc.id_khu_vuc "
                + "WHERE ten_may = ?";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenMay);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("gia_khu_vuc");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xóa máy tính theo ID
    public void deletePC(int id) {
        String sql = "DELETE FROM May_tinh WHERE id_may = ?";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MayTinhEntity> parseListPC(ResultSet rs) throws SQLException {
        List<MayTinhEntity> list = new ArrayList<>();
        while (rs.next()) {
            MayTinhEntity pc = new MayTinhEntity();
            pc.setID(rs.getInt("id_may"));
            pc.setTen_may(rs.getString("ten_may"));
            pc.setId_khu_vuc(rs.getInt("id_khu_vuc"));

            // Nếu bảng có thêm cột trang_thai, cau_hinh thì set tiếp
            try {
                pc.setTrangThai(rs.getString("trang_thai"));
            } catch (SQLException ignore) {
            }
            try {
                pc.setCauHinh(rs.getString("cau_hinh"));
            } catch (SQLException ignore) {
            }

            list.add(pc);
        }
        return list;
    }

}
