/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author ASUS
 */
public class KetNoiDB {
    // Phương thức kết nối CSDL
  // Phương thức tạo kết nối tới CSDL CHATFPT
    public static Connection getConnection() throws SQLException {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=CHATFPT3;"  // đúng tên database
                + "user=sa;"
                + "password=1234;"
                + "encrypt=true;"
                + "trustServerCertificate=true";

        return DriverManager.getConnection(connectionUrl);
    }

    // Hàm main để test kết nối và truy vấn bảng Tai_Khoan
    public static void main(String[] args) {
        String sql = "SELECT * FROM Tai_Khoan";

        try (
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                String id = rs.getString("id_tk");
                String hoTen = rs.getString("ho_ten");
                String vaiTro = rs.getString("vai_tro");

                System.out.println("ID: " + id + " | Họ tên: " + hoTen + " | Vai trò: " + vaiTro);
            }
        } catch (SQLException e) {
            System.err.println("Kết nối hoặc truy vấn thất bại:");
            e.printStackTrace();
        }
    }
}
