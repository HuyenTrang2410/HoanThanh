/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Ui;

import DAO.DatDoAnDAO;
import java.sql.*;
import DAO.KhuVucDAO;
import DAO.MayTinhDAO;
import DAO.TaiKhoanDAO;
import DAO.ThongKeTaiKhoangDAO;
import DTO.DonHangDTO;
import Entity.ChiTietDonHang;
import Entity.DonHang;
import Entity.KhuVucEntity;
import Entity.MayTinhEntity;
import Entity.TaiKhoanEntity;
import Entity.ThongKeTaiKhoan;
import Utils.Auth;
import Utils.HostServer;
import Utils.HostUtil;
import Utils.KetNoiDB;
import Utils.PriceUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author ASUS
 */
public class MainJFrame extends javax.swing.JFrame {

    private List<DonHang> donHangs;
    private DonHang selectedDonHang;

    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        initComponents();

        setupTable();
        loadAccountsToTable();
        addSearchEvent();
        loadTableKhuVuc();
        capNhatComboBoxKhuVuc();
        loadTablePC();
        loadDonHang("Đang làm");
        loadThongKeTaiKhoan();
        loadDoanhThuMon();
        this.createAndConnect2Server();
    }

    void loadDonHang(String status) {
        DefaultTableModel model;
        if ("Đang làm".equals(status)) {
            System.out.println("Loading pending orders...");
            model = (DefaultTableModel) tabDonHang.getModel();
        } else {
            System.out.println("Loading completed orders...");
            model = (DefaultTableModel) tabDonHangDone.getModel();

        }
        model.setRowCount(0); // xóa dữ liệu cũ

        DatDoAnDAO dao = new DatDoAnDAO();
        donHangs = dao.getDonHang(status).stream()
                .collect(Collectors.groupingBy(DonHangDTO::getIdDonHang))
                .entrySet().stream()
                .map(DonHang::new).toList();
        for (DonHang dh : donHangs) {
            model.addRow(new Object[]{
                dh.getId_don_hang(),
                dh.getThoi_gian(),
                dh.getId_tk(),
                PriceUtils.formatCurrency(dh.getTong_tien()),
                dh.getTrang_thai(),});
        }
    }

    void loadCtDonHang(DonHang dh, boolean isDone) {
        DefaultTableModel model = (DefaultTableModel) tabChiTiet.getModel();
        if (isDone) {
            model = (DefaultTableModel) tabChiTietDone.getModel();
        }
        model.setRowCount(0); // xóa dữ liệu cũ

        for (ChiTietDonHang ct : dh.getDon_hang()) {
            model.addRow(new Object[]{
                ct.getId_ctdh(),
                ct.getTen_san_pham(),
                ct.getSo_luong(),
                PriceUtils.formatCurrency(ct.getGia()),
                PriceUtils.formatCurrency(ct.getThanh_tien())
            });
        }
    }

    private void loadTable() {
        DefaultTableModel model = (DefaultTableModel) tblBang.getModel();
        model.setRowCount(0); // xóa dữ liệu cũ

        TaiKhoanDAO dao = new TaiKhoanDAO();
        for (TaiKhoanEntity tk : dao.getAllAccounts()) {
            model.addRow(new Object[]{
                tk.getHo_ten(),
                tk.getEmail(),
                tk.getSdt(),
                tk.getSo_du(),
                tk.getTen_dang_nhap(),
                tk.getMat_khau()
            });
        }
    }

    private void setupTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Họ tên", "Email", "SĐT", "Số dư", "Tên đăng nhập", "Mật khẩu"}, 0
        );
        tblBang.setModel(model);

        // Ẩn cột mật khẩu
        tblBang.getColumnModel().getColumn(5).setMinWidth(0);
        tblBang.getColumnModel().getColumn(5).setMaxWidth(0);
        tblBang.getColumnModel().getColumn(5).setWidth(0);
    }

    private void loadAccountsToTable() {
        TaiKhoanDAO dao = new TaiKhoanDAO();
        List<TaiKhoanEntity> list = dao.getAllAccounts();

        DefaultTableModel model = (DefaultTableModel) tblBang.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (TaiKhoanEntity tk : list) {
            model.addRow(new Object[]{
                tk.getHo_ten(),
                tk.getEmail(),
                tk.getSdt(),
                tk.getSo_du(),
                tk.getTen_dang_nhap(),
                tk.getMat_khau()
            });
        }
    }

    private void addSearchEvent() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tblBang.getModel());
        tblBang.setRowSorter(sorter);

        txtTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String keyword = txtTimKiem.getText().trim();
                if (keyword.isEmpty()) {
                    sorter.setRowFilter(null); // Hiện tất cả
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword, 4));
                }
            }
        });
    }

    public void loadThongKeTaiKhoan() {
        DefaultTableModel model = (DefaultTableModel) TabThong_ke_tai_khoan.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        ThongKeTaiKhoangDAO dao = new ThongKeTaiKhoangDAO();
        List<ThongKeTaiKhoan> list = dao.ThongTinThongKeTK();
        for (ThongKeTaiKhoan tk : list) {
            model.addRow(new Object[]{
                tk.getId_tk(),
                tk.getHo_ten(),
                tk.getSo_du()
            });
        }
    }

    private void loadDoanhThuMon() {
        DefaultTableModel model = (DefaultTableModel) tabDoanhThuMon.getModel();
        model.setRowCount(0);

        String sql = """
        SELECT sp.id_san_pham,
               sp.ten_sp,
               SUM(ct.so_luong) AS so_luong_ban,
               SUM(ct.so_luong * ct.gia) AS doanh_thu
        FROM Chi_tiet_don_hang ct
        JOIN San_pham sp ON ct.id_san_pham = sp.id_san_pham
        GROUP BY sp.id_san_pham, sp.ten_sp
        ORDER BY sp.id_san_pham
    """;

        try (Connection conn = KetNoiDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_san_pham");
                String tenMon = rs.getString("ten_sp");
                int soLuong = rs.getInt("so_luong_ban");
                long doanhThu = rs.getLong("doanh_thu"); // dùng long để tránh mất mát nếu số lớn

                // Thêm row theo thứ tự: id, tên, số lượng, doanh thu
                model.addRow(new Object[]{id, tenMon, soLuong, doanhThu});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu doanh thu món!");
        }
    }

    // Xử lý khi nhấn nút Thêm
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblXinChao = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        cboQl = new javax.swing.JComboBox<>();
        Tên = new javax.swing.JLabel();
        txtTenDangNhap = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtSoDu = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblBang = new javax.swing.JTable();
        btnDangXuat = new javax.swing.JButton();
        txtMatKhau = new javax.swing.JPasswordField();
        txtTimKiem = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtNapTien = new javax.swing.JTextField();
        btnNapTien = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblPC = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtPCname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboKhuvuc = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        btnThem1 = new javax.swing.JButton();
        btnSua1 = new javax.swing.JButton();
        btnXoa1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblKhuvuc = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtGiaPC = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTenKhu = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        btnThem2 = new javax.swing.JButton();
        btnSua2 = new javax.swing.JButton();
        btnXoa2 = new javax.swing.JButton();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        lblDatDo = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tabDonHang = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        tabChiTiet = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        txtDonHang = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        btnDone = new javax.swing.JToggleButton();
        jPanel8 = new javax.swing.JPanel();
        lblDatDo1 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tabDonHangDone = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        tabChiTietDone = new javax.swing.JTable();
        jPanel19 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtDonHangDone = new javax.swing.JTextField();
        btnDatDo = new javax.swing.JButton();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabDoanhThuMon = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        lblNapTheChart = new javax.swing.JLabel();
        lblPieChart = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboYear = new javax.swing.JComboBox<>();
        btnLoc = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TabThong_ke_tai_khoan = new javax.swing.JTable();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtA = new javax.swing.JTextArea();
        txtNoiDung = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblXinChao.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        lblXinChao.setForeground(new java.awt.Color(255, 153, 153));
        lblXinChao.setText("<< Xin chào Admin ! >>");

        jTabbedPane1.setForeground(new java.awt.Color(255, 153, 153));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153)));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 153, 153));
        jLabel14.setText("Quản lý tài khoản");

        cboQl.setBackground(new java.awt.Color(255, 204, 204));
        cboQl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Khách", " " }));

        Tên.setText("Tên ĐN");

        txtTenDangNhap.setBackground(new java.awt.Color(255, 204, 204));

        jLabel15.setText("Mật khẩu");

        jLabel16.setText("Số dư");

        txtSoDu.setBackground(new java.awt.Color(255, 204, 204));

        jLabel19.setText("SDT");

        txtSDT.setBackground(new java.awt.Color(255, 204, 204));

        jLabel5.setText("Họ tên");

        txtHoTen.setBackground(new java.awt.Color(255, 204, 204));

        jLabel3.setText("Email");

        txtEmail.setBackground(new java.awt.Color(255, 204, 204));

        btnThem.setBackground(new java.awt.Color(255, 204, 204));
        btnThem.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnThem.setForeground(new java.awt.Color(255, 153, 153));
        btnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Add.png"))); // NOI18N
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setBackground(new java.awt.Color(255, 204, 204));
        btnSua.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnSua.setForeground(new java.awt.Color(255, 153, 153));
        btnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Gear.png"))); // NOI18N
        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setBackground(new java.awt.Color(255, 204, 204));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 153, 153));
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Delete.png"))); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        tblBang.setBackground(new java.awt.Color(255, 204, 204));
        tblBang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Tài khoản", "Vai trò", "Tên đăng nhập", "Số dư", "SDT", "Họ Tên", "Email"
            }
        ));
        tblBang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBangMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBangMousePressed(evt);
            }
        });
        jScrollPane7.setViewportView(tblBang);

        btnDangXuat.setBackground(new java.awt.Color(255, 204, 204));
        btnDangXuat.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnDangXuat.setForeground(new java.awt.Color(255, 153, 153));
        btnDangXuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Exit.png"))); // NOI18N
        btnDangXuat.setText("Đăng xuất");
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });

        txtMatKhau.setBackground(new java.awt.Color(255, 204, 204));

        txtTimKiem.setBackground(new java.awt.Color(255, 204, 204));
        txtTimKiem.setForeground(new java.awt.Color(0, 0, 0));
        txtTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimKiemActionPerformed(evt);
            }
        });

        jLabel6.setText("Tìm kiếm");

        txtNapTien.setBackground(new java.awt.Color(255, 204, 204));

        btnNapTien.setBackground(new java.awt.Color(255, 204, 204));
        btnNapTien.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnNapTien.setForeground(new java.awt.Color(255, 153, 153));
        btnNapTien.setText("Nạp tiền");
        btnNapTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNapTienActionPerformed(evt);
            }
        });

        jLabel8.setText("Vai trò");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDangXuat, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(278, 278, 278)
                                .addComponent(jLabel14))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(131, 131, 131)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(17, 17, 17)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(51, 51, 51)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Tên, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(126, 126, 126))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(54, 54, 54)
                                            .addComponent(txtTimKiem))))
                                .addComponent(txtNapTien, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNapTien)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(116, 116, 116)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(18, 18, 18))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(126, 126, 126)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(cboQl, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(122, 122, 122)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtHoTen, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .addComponent(txtSoDu)
                        .addComponent(txtTenDangNhap))
                    .addGap(127, 127, 127))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane7)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNapTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNapTien))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(Tên))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(94, 94, 94))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa)
                    .addComponent(btnDangXuat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 444, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(168, 168, 168)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboQl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTenDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(26, 26, 26)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(txtSoDu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel19))
                    .addGap(18, 18, 18)
                    .addComponent(jLabel3)
                    .addGap(151, 151, 151)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane2.addTab("Quản lý tài khoản", jPanel1);

        jTabbedPane1.addTab("Quản lý tài khoản", jTabbedPane2);

        jTabbedPane4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane4StateChanged(evt);
            }
        });

        jScrollPane4.setAutoscrolls(true);

        tblPC.setBackground(new java.awt.Color(255, 204, 204));
        tblPC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID ", "Tên máy", "Khu vực", "Trạng thái"
            }
        ));
        tblPC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPCMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblPC);

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 153, 153), 2, true));
        jPanel12.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 153, 153));
        jLabel1.setText("Tên máy");

        txtPCname.setBackground(new java.awt.Color(255, 204, 204));
        txtPCname.setForeground(new java.awt.Color(255, 153, 153));
        txtPCname.setSelectionColor(new java.awt.Color(255, 153, 102));
        txtPCname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPCnameActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 153, 153));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 153, 153));
        jLabel2.setText("Khu vực");

        cboKhuvuc.setBackground(new java.awt.Color(255, 204, 204));
        cboKhuvuc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cboKhuvuc.setForeground(new java.awt.Color(255, 153, 153));
        cboKhuvuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKhuvucActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPCname, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(cboKhuvuc, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPCname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cboKhuvuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 153, 153));
        jLabel11.setText("Quản lý dàn máy ");

        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 153, 153), 1, true));
        jPanel13.setForeground(new java.awt.Color(255, 255, 255));
        jPanel13.setLayout(new java.awt.GridLayout(1, 0));

        btnThem1.setBackground(new java.awt.Color(255, 204, 204));
        btnThem1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnThem1.setForeground(new java.awt.Color(255, 153, 153));
        btnThem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Add.png"))); // NOI18N
        btnThem1.setText("Thêm");
        btnThem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThem1ActionPerformed(evt);
            }
        });
        jPanel13.add(btnThem1);

        btnSua1.setBackground(new java.awt.Color(255, 204, 204));
        btnSua1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnSua1.setForeground(new java.awt.Color(255, 153, 153));
        btnSua1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Gear.png"))); // NOI18N
        btnSua1.setText("Sửa");
        btnSua1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSua1ActionPerformed(evt);
            }
        });
        jPanel13.add(btnSua1);

        btnXoa1.setBackground(new java.awt.Color(255, 204, 204));
        btnXoa1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnXoa1.setForeground(new java.awt.Color(255, 153, 153));
        btnXoa1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Delete.png"))); // NOI18N
        btnXoa1.setText("Xóa");
        btnXoa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoa1ActionPerformed(evt);
            }
        });
        jPanel13.add(btnXoa1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(278, 278, 278)
                .addComponent(jLabel11)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(26, 26, 26)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addGap(72, 72, 72))
        );

        jTabbedPane4.addTab("Quản lý máy", jPanel3);

        tblKhuvuc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID khu vực", "Giá khu vực", "Tên khu vực"
            }
        ));
        tblKhuvuc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKhuvucMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblKhuvuc);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 153, 153));
        jLabel13.setText("Quản lý khu vực");

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153), 2));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Giá khu vực");

        txtGiaPC.setBackground(new java.awt.Color(255, 255, 255));
        txtGiaPC.setForeground(new java.awt.Color(255, 153, 153));
        txtGiaPC.setSelectionColor(new java.awt.Color(255, 153, 102));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Tên khu vực");

        txtTenKhu.setBackground(new java.awt.Color(255, 255, 255));
        txtTenKhu.setForeground(new java.awt.Color(255, 153, 153));
        txtTenKhu.setSelectionColor(new java.awt.Color(255, 153, 102));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtGiaPC, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtTenKhu, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtGiaPC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtTenKhu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153)));
        jPanel18.setLayout(new java.awt.GridLayout(1, 0));

        btnThem2.setBackground(new java.awt.Color(255, 204, 204));
        btnThem2.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnThem2.setForeground(new java.awt.Color(255, 153, 153));
        btnThem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Add.png"))); // NOI18N
        btnThem2.setText("Thêm");
        btnThem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThem2ActionPerformed(evt);
            }
        });
        jPanel18.add(btnThem2);

        btnSua2.setBackground(new java.awt.Color(255, 204, 204));
        btnSua2.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnSua2.setForeground(new java.awt.Color(255, 153, 153));
        btnSua2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Sad.png"))); // NOI18N
        btnSua2.setText("Sửa");
        btnSua2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSua2ActionPerformed(evt);
            }
        });
        jPanel18.add(btnSua2);

        btnXoa2.setBackground(new java.awt.Color(255, 204, 204));
        btnXoa2.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnXoa2.setForeground(new java.awt.Color(255, 153, 153));
        btnXoa2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/Delete.png"))); // NOI18N
        btnXoa2.setText("Xóa");
        btnXoa2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoa2ActionPerformed(evt);
            }
        });
        jPanel18.add(btnXoa2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(278, 278, 278)
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(26, 26, 26)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                .addGap(114, 114, 114))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 832, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 854, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane4.addTab("Quản lý khu vực", jPanel5);

        jTabbedPane1.addTab("Quản lý khu máy", jTabbedPane4);

        jTabbedPane6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane6StateChanged(evt);
            }
        });

        lblDatDo.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblDatDo.setForeground(new java.awt.Color(255, 153, 153));
        lblDatDo.setText("Đơn hàng đang làm");

        tabDonHang.setBackground(new java.awt.Color(255, 204, 204));
        tabDonHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID đơn hàng", "Thời gian", "ID user", "Tổng tiền", "Trạng thái"
            }
        ));
        tabDonHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabDonHangMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tabDonHang);

        tabChiTiet.setBackground(new java.awt.Color(255, 204, 204));
        tabChiTiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID đơn hàng", "Tên sản phẩm", "Số lượng", "Giá", "Thành tiền"
            }
        ));
        jScrollPane10.setViewportView(tabChiTiet);

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153), 2));

        txtDonHang.setBackground(new java.awt.Color(255, 204, 204));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 153, 153));
        jLabel17.setText("Đơn hàng bạn chọn là:");

        btnDone.setBackground(new java.awt.Color(255, 204, 204));
        btnDone.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDone.setForeground(new java.awt.Color(255, 153, 153));
        btnDone.setText("ĐÃ LÀM XONG");
        btnDone.setPreferredSize(new java.awt.Dimension(336, 36));
        btnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDonHang, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addComponent(btnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtDonHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDatDo)
                .addGap(237, 237, 237))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDatDo)
                .addGap(20, 20, 20)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(377, Short.MAX_VALUE))
        );

        jTabbedPane6.addTab("Đơn hàng đang làm", jPanel9);

        lblDatDo1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblDatDo1.setForeground(new java.awt.Color(255, 153, 153));
        lblDatDo1.setText("Đơn hàng đã làm");

        tabDonHangDone.setBackground(new java.awt.Color(255, 204, 204));
        tabDonHangDone.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID đơn hàng", "Thời gian", "ID user", "Tổng tiền", "Trạng thái"
            }
        ));
        tabDonHangDone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabDonHangDoneMouseClicked(evt);
            }
        });
        jScrollPane11.setViewportView(tabDonHangDone);

        tabChiTietDone.setBackground(new java.awt.Color(255, 204, 204));
        tabChiTietDone.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID đơn hàng", "Tên sản phẩm", "Số lượng", "giá", "Thành tiền"
            }
        ));
        jScrollPane12.setViewportView(tabChiTietDone);

        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153), 2));
        jPanel19.setPreferredSize(new java.awt.Dimension(759, 52));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 153, 153));
        jLabel18.setText("Đơn hàng bạn chọn là:");

        txtDonHangDone.setBackground(new java.awt.Color(255, 204, 204));

        btnDatDo.setBackground(new java.awt.Color(255, 204, 204));
        btnDatDo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDatDo.setForeground(new java.awt.Color(255, 153, 153));
        btnDatDo.setText("Quản lý đồ đặt");
        btnDatDo.setPreferredSize(new java.awt.Dimension(336, 36));
        btnDatDo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatDoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDonHangDone, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDatDo, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtDonHangDone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDatDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblDatDo1)
                .addGap(269, 269, 269))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDatDo1)
                .addGap(20, 20, 20)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 373, Short.MAX_VALUE))
        );

        jTabbedPane6.addTab("Đơn hàng đã làm", jPanel8);

        jTabbedPane1.addTab("Quản lý đơn hàng", jTabbedPane6);

        tabDoanhThuMon.setBackground(new java.awt.Color(255, 204, 204));
        tabDoanhThuMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID sản phẩm", "Tên sản phẩm", "Số lượng đã bán", "Tổng doanh thu"
            }
        ));
        jScrollPane1.setViewportView(tabDoanhThuMon);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 865, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 791, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 63, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Doanh thu món", jPanel4);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel10.setText("Lọc theo năm");

        cboYear.setBackground(new java.awt.Color(255, 204, 204));
        cboYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboYearActionPerformed(evt);
            }
        });

        btnLoc.setBackground(new java.awt.Color(255, 204, 204));
        btnLoc.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnLoc.setForeground(new java.awt.Color(255, 153, 153));
        btnLoc.setText("Lọc");
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(lblPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(lblNapTheChart, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboYear, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(btnLoc)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(btnLoc))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNapTheChart, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(385, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Biểu đồ", jPanel11);

        TabThong_ke_tai_khoan.setBackground(new java.awt.Color(255, 204, 204));
        TabThong_ke_tai_khoan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID tài khoản", "Họ tên", "Số dư"
            }
        ));
        jScrollPane2.setViewportView(TabThong_ke_tai_khoan);

        jTabbedPane3.addTab("Thống kê tài khoản", jScrollPane2);

        jTabbedPane1.addTab("Thống kê", jTabbedPane3);

        txtA.setEditable(false);
        txtA.setBackground(new java.awt.Color(255, 204, 204));
        txtA.setColumns(20);
        txtA.setForeground(new java.awt.Color(0, 51, 102));
        txtA.setRows(5);
        txtA.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153)));
        jScrollPane6.setViewportView(txtA);

        txtNoiDung.setBackground(new java.awt.Color(255, 204, 204));
        txtNoiDung.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNoiDung.setForeground(new java.awt.Color(0, 51, 102));
        txtNoiDung.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoiDung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoiDungActionPerformed(evt);
            }
        });

        btnSend.setBackground(new java.awt.Color(255, 204, 204));
        btnSend.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnSend.setForeground(new java.awt.Color(255, 153, 153));
        btnSend.setText("Send");
        btnSend.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 153)));
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 153, 153));
        jLabel12.setText("CHAT CÙNG DTLNT!");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 153, 153));
        jLabel7.setText("Nhập tin nhắn:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane6))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNoiDung, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(205, 205, 205))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel12)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNoiDung, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addContainerGap(459, Short.MAX_VALUE))
        );

        jTabbedPane7.addTab("Chat", jPanel10);

        jTabbedPane1.addTab("Chat", jTabbedPane7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(lblXinChao)
                .addGap(285, 285, 285))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(lblXinChao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    static Socket s;
    static InputStream is;
    static BufferedReader br;
    static OutputStream os;
    static PrintWriter ps;

    public void createAndConnect2Server() {
        //Create and connect to Server - Create 2 threads run in a time
        new Thread(() -> {
            HostServer.createServer();
        }).start();
        try {
            s = new Socket("192.168.1.14", 12345);
            is = s.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            os = s.getOutputStream();
            this.ps = new PrintWriter(os, true);

            new Thread(() -> {
                try {
                    String message = "";
                    while ((message = br.readLine()) != null) {
                        System.out.println("Main: " + message);
                        txtA.append("\n" + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void tblPCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPCMouseClicked
        int selectedRow = tblPC.getSelectedRow();
        if (selectedRow != -1) {
            String tenMay = tblPC.getValueAt(selectedRow, 1).toString(); // Cột 1: Tên máy
            String tenKhuVuc = Optional.ofNullable(tblPC.getValueAt(selectedRow, 2)).map(String::valueOf).orElse(""); // Cột 2: Khu vực

            txtPCname.setText(tenMay);
            cboKhuvuc.setSelectedItem(tenKhuVuc);
        }
    }//GEN-LAST:event_tblPCMouseClicked

    private void txtPCnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPCnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPCnameActionPerformed

    private void cboKhuvucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKhuvucActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKhuvucActionPerformed
    private KhuVucDAO khuVucDAO = new KhuVucDAO();
    MayTinhDAO pcDAO = new MayTinhDAO();

    private void loadTablePC() {
        DefaultTableModel model = (DefaultTableModel) tblPC.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        List<MayTinhEntity> list = pcDAO.getAllPC();
        for (MayTinhEntity pc : list) {
            String tenKhuVuc = khuVucDAO.getTenKhuVuc(pc.getId_khu_vuc());
            model.addRow(new Object[]{
                pc.getID(),
                pc.getTen_may(),
                tenKhuVuc,
                pc.getTrangThai()
            });
        }
    }
    private void btnThem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThem1ActionPerformed
        try {
            String tenMay = txtPCname.getText().trim();
            String tenKhuVuc = cboKhuvuc.getSelectedItem().toString();

            if (tenMay.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên máy!");
                return;
            }

            // Kiểm tra trùng tên máy từ danh sách hiện có
            List<MayTinhEntity> listPC = pcDAO.getAllPC();
            for (MayTinhEntity pc : listPC) {
                if (pc.getTen_may().equalsIgnoreCase(tenMay)) {
                    JOptionPane.showMessageDialog(this, "Tên máy đã tồn tại, vui lòng nhập tên khác!");
                    return;
                }
            }

            // Lấy id_khu_vuc từ tên khu vực
            int idKhuVuc = khuVucDAO.getIDKhuVuc(tenKhuVuc);

            MayTinhEntity pc = new MayTinhEntity();
            pc.setTen_may(tenMay);
            pc.setId_khu_vuc(idKhuVuc);

            pcDAO.addPC(pc);
            loadTablePC();
            JOptionPane.showMessageDialog(this, "Thêm máy thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm máy: " + e.getMessage());
        }
    }//GEN-LAST:event_btnThem1ActionPerformed

    private void btnSua1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSua1ActionPerformed
        try {
            int selectedRow = tblPC.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn máy cần sửa!");
                return;
            }

            int id = (int) tblPC.getValueAt(selectedRow, 0); // Cột 0 là id_may
            String tenMay = txtPCname.getText().trim();
            String tenKhuVuc = (String) cboKhuvuc.getSelectedItem();

            if (tenMay.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên máy!");
                return;
            }

            // Kiểm tra trùng tên (ngoại trừ máy hiện tại)
            List<MayTinhEntity> listPC = pcDAO.getAllPC();
            for (MayTinhEntity pc : listPC) {
                if (pc.getTen_may().equalsIgnoreCase(tenMay) && pc.getID() != id) {
                    JOptionPane.showMessageDialog(this, "Tên máy đã tồn tại, vui lòng nhập tên khác!");
                    return;
                }
            }

            // Lấy id_khu_vuc từ tên khu vực
            int idKhuVuc = khuVucDAO.getIDKhuVuc(tenKhuVuc);

            if (pcDAO.updatePCInfo(id, tenMay, idKhuVuc)) {
                loadTablePC();
                JOptionPane.showMessageDialog(this, "Sửa máy thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy máy để sửa!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa máy: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSua1ActionPerformed

    private void btnXoa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoa1ActionPerformed
        try {
            int selectedRow = tblPC.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn máy cần xóa!");
                return;
            }

            int id = (int) tblPC.getValueAt(selectedRow, 0); // Cột 0 là ID máy

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa máy này?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                pcDAO.deletePC(id);
                loadTablePC();
                JOptionPane.showMessageDialog(this, "Xóa máy thành công!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa máy: " + e.getMessage());
        }
    }//GEN-LAST:event_btnXoa1ActionPerformed
    private int selectedKhuVucID = -1;
    private void tblKhuvucMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKhuvucMouseClicked
        int row = tblKhuvuc.getSelectedRow();
        if (row >= 0) {
            // Lấy dữ liệu từ hàng được chọn
            String id = tblKhuvuc.getValueAt(row, 0).toString();
            String gia = tblKhuvuc.getValueAt(row, 1).toString();
            String ten = tblKhuvuc.getValueAt(row, 2).toString();

            // Đưa dữ liệu vào các ô nhập
            txtTenKhu.setText(ten);
            txtGiaPC.setText(gia);

            // Lưu lại ID để sửa hoặc xóa
            try {
                selectedKhuVucID = Integer.parseInt(id);
            } catch (NumberFormatException ex) {
                selectedKhuVucID = -1;
                JOptionPane.showMessageDialog(this, "ID khu vực không hợp lệ!");
            }
        }
    }//GEN-LAST:event_tblKhuvucMouseClicked

    private void btnThem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThem2ActionPerformed
        String tenKhuVuc = txtTenKhu.getText().trim();
        if (tenKhuVuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khu vực");
            return;
        }

        int giaKhuVuc;
        try {
            giaKhuVuc = Integer.parseInt(txtGiaPC.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá khu vực phải là số");
            return;
        }

        // Kiểm tra trùng tên trước khi thêm
        KhuVucDAO dao = new KhuVucDAO();
        List<String> danhSachKhuVuc = dao.getListKhuVuc();
        for (String ten : danhSachKhuVuc) {
            if (ten.equalsIgnoreCase(tenKhuVuc)) {
                JOptionPane.showMessageDialog(this, "Tên khu vực đã tồn tại!");
                return;
            }
        }

        // Nếu không trùng thì thêm mới
        KhuVucEntity kv = new KhuVucEntity(tenKhuVuc, giaKhuVuc);
        dao.addKhuvucPC(kv);

        JOptionPane.showMessageDialog(this, "Thêm khu vực thành công");
        loadTableKhuVuc();
        capNhatComboBoxKhuVuc();

    }//GEN-LAST:event_btnThem2ActionPerformed
    public void capNhatComboBoxKhuVuc() {
        cboKhuvuc.removeAllItems();
        KhuVucDAO dao = new KhuVucDAO();
        List<String> danhSachKhuVuc = dao.getListKhuVuc();
        for (String tenKhu : danhSachKhuVuc) {
            cboKhuvuc.addItem(tenKhu);
        }
    }

    public void loadTableKhuVuc() {
        DefaultTableModel model = (DefaultTableModel) tblKhuvuc.getModel();
        model.setRowCount(0); // Xoá toàn bộ dòng cũ

        String sql = "SELECT id_khu_vuc, ten_khu_vuc, gia_khu_vuc FROM Khu_vuc";

        try (Connection conn = KetNoiDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_khu_vuc");
                String ten = rs.getString("ten_khu_vuc");
                float gia = rs.getFloat("gia_khu_vuc");
                model.addRow(new Object[]{id, gia, ten}); // Thứ tự phải khớp cột trong JTable
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải khu vực: " + e.getMessage());
        }
    }


    private void btnSua2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSua2ActionPerformed
        int row = tblKhuvuc.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa");
            return;
        }

        int id = Integer.parseInt(tblKhuvuc.getValueAt(row, 0).toString());
        String tenKhuVuc = txtTenKhu.getText().trim();
        if (tenKhuVuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khu vực");
            return;
        }

        int giaKhuVuc;
        try {
            giaKhuVuc = Integer.parseInt(txtGiaPC.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá khu vực phải là số");
            return;
        }

        KhuVucDAO dao = new KhuVucDAO();

        // Lấy tên khu vực hiện tại trong DB của ID đang sửa
        String tenCu = dao.getTenKhuVuc(id);

        // Nếu tên mới khác tên cũ, kiểm tra trùng
        if (!tenKhuVuc.equalsIgnoreCase(tenCu)) {
            List<String> danhSachKhuVuc = dao.getListKhuVuc();
            for (String ten : danhSachKhuVuc) {
                if (ten.equalsIgnoreCase(tenKhuVuc)) {
                    JOptionPane.showMessageDialog(this, "Tên khu vực đã tồn tại!");
                    return;
                }
            }
        }

        // Nếu hợp lệ thì update
        KhuVucEntity kv = new KhuVucEntity(tenKhuVuc, giaKhuVuc);
        dao.updateKhuVucPC(id, kv);

        JOptionPane.showMessageDialog(this, "Cập nhật thành công");
        loadTableKhuVuc();
        capNhatComboBoxKhuVuc();
    }//GEN-LAST:event_btnSua2ActionPerformed

    private void btnXoa2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoa2ActionPerformed
        int row = tblKhuvuc.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa");
            return;
        }

        int id = Integer.parseInt(tblKhuvuc.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new KhuVucDAO().deleteKhuVucPC(id);
            JOptionPane.showMessageDialog(this, "Xóa thành công");
            loadTableKhuVuc();
            capNhatComboBoxKhuVuc();
        }
    }//GEN-LAST:event_btnXoa2ActionPerformed

    private void tabDonHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabDonHangMouseClicked
        var row = tabDonHang.getSelectedRow();
        if (row >= 0) {
            selectedDonHang = donHangs.get(row);
            txtDonHang.setText(String.valueOf(selectedDonHang.getId_don_hang()));
            loadCtDonHang(selectedDonHang, false);
        }
    }//GEN-LAST:event_tabDonHangMouseClicked

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
        if (selectedDonHang == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần làm xong!");
            return;
        }
        try {
            // Cập nhật trạng thái đơn hàng
            DatDoAnDAO dao = new DatDoAnDAO();
            dao.capNhatTrangThaiDonHang(selectedDonHang.getId_don_hang());
            selectedDonHang = null; // Reset selectedDonHang
            txtDonHang.setText(""); // Xóa nội dung ô nhập đơn hàng
            tabChiTiet.setModel(new DefaultTableModel()); // Xóa bảng chi tiết đơn hàng
            loadDonHang("Đang làm");

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(this, "✅ Đơn hàng đã được đánh dấu là đã làm xong!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }//GEN-LAST:event_btnDoneActionPerformed

    private void tabDonHangDoneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabDonHangDoneMouseClicked
        var row = tabDonHangDone.getSelectedRow();
        if (row >= 0) {
            selectedDonHang = donHangs.get(row);
            txtDonHangDone.setText(String.valueOf(selectedDonHang.getId_don_hang()));
            loadCtDonHang(selectedDonHang, true);
        }
    }//GEN-LAST:event_tabDonHangDoneMouseClicked

    private void btnDatDoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatDoActionPerformed
        QuanLyDoAnJFrame doAnFrame = new QuanLyDoAnJFrame();
        doAnFrame.setVisible(true);
        doAnFrame.setLocationRelativeTo(null); // Hiển thị giữa màn hình (nếu cần)
    }//GEN-LAST:event_btnDatDoActionPerformed

    private void cboYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboYearActionPerformed
        // TODO add your handling code here:
        //        this.drawChartBySort();
    }//GEN-LAST:event_cboYearActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        // TODO add your handling code here:
        String sql = "SELECT GETDATE() AS ThoiGianHienTai";
        try (Connection conn = KetNoiDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Timestamp thoiGian = rs.getTimestamp("ThoiGianHienTai");
                System.out.println("Thời gian hiện tại từ SQL: " + thoiGian);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
        String str2;
        str2 = txtNoiDung.getText();
        String txt = "Admin: " + str2 + "\n";
        txtNoiDung.setText("");
        ps.println(txt);
    }//GEN-LAST:event_btnSendActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        try {
            // Kiểm tra dữ liệu bắt buộc
            if (txtTenDangNhap.getText().trim().isEmpty() || txtMatKhau.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu!");
                return;
            }

            int soDu;
            try {
                soDu = Integer.parseInt(txtSoDu.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số dư phải là số nguyên!");
                return;
            }

            String vaiTro = cboQl.getSelectedItem().toString().trim();

            TaiKhoanEntity tk = new TaiKhoanEntity(
                    vaiTro,
                    txtTenDangNhap.getText().trim(),
                    txtMatKhau.getText().trim(),
                    soDu,
                    txtSDT.getText().trim(),
                    txtEmail.getText().trim(),
                    txtHoTen.getText().trim(),
                    "Offline"
            );

            TaiKhoanDAO dao = new TaiKhoanDAO();
            dao.addAccount(tk);
            JOptionPane.showMessageDialog(this, "✅ Thêm tài khoản thành công!");
            loadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + e.getMessage());
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        try {
            if (txtSDT.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa!");
                return;
            }

            int soDu;
            try {
                soDu = Integer.parseInt(txtSoDu.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số dư phải là số nguyên!");
                return;
            }

            String vaiTro = cboQl.getSelectedItem().toString().trim();

            TaiKhoanEntity tk = new TaiKhoanEntity(
                    vaiTro,
                    txtTenDangNhap.getText().trim(),
                    txtMatKhau.getText().trim(),
                    soDu,
                    txtSDT.getText().trim(),
                    txtEmail.getText().trim(),
                    txtHoTen.getText().trim(),
                    null // ❌ Không trạng thái
            );

            TaiKhoanDAO dao = new TaiKhoanDAO();
            dao.updateAccount(tk);
            JOptionPane.showMessageDialog(this, "✅ Cập nhật tài khoản thành công!");
            loadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        try {
            String username = txtTenDangNhap.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập hoặc chọn SĐT để xóa!");
                return;
            }

            TaiKhoanDAO dao = new TaiKhoanDAO();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa tài khoản này?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String role = dao.getRole(username);
                if (role.equals("Admin")) {
                    JOptionPane.showMessageDialog(rootPane, "Can't delete this account due to the role is Admin");
                } else {
                    int id = dao.getIdByUsername(username);
                    dao.deleteAccount(id);
                    JOptionPane.showMessageDialog(this, "🗑 Xóa tài khoản thành công!");
                    loadTable();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void tblBangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBangMouseClicked
        int selectedRow = tblBang.getSelectedRow();
        TaiKhoanDAO tkDAO = new TaiKhoanDAO();
        if (selectedRow >= 0) {
            txtHoTen.setText(tblBang.getValueAt(selectedRow, 0).toString());
            txtEmail.setText(tblBang.getValueAt(selectedRow, 1).toString());
            txtSDT.setText(tblBang.getValueAt(selectedRow, 2).toString());
            txtSoDu.setText(tblBang.getValueAt(selectedRow, 3).toString());
            txtTenDangNhap.setText(tblBang.getValueAt(selectedRow, 4).toString());
            cboQl.setSelectedItem(tkDAO.getRole(tblBang.getValueAt(selectedRow, 4).toString()));
            // Hiển thị mật khẩu dưới dạng sao
            txtMatKhau.setEchoChar('*');
            txtMatKhau.setText(tblBang.getValueAt(selectedRow, 5).toString());
        }

//        if (evt.getClickCount() == 1) {
//            //JOptionPane.showMessageDialog(null, "double click");// for testing
//            if (selectedRow >= 0) {
//                // Lấy tên đăng nhập từ cột 4 (Tên đăng nhập)
//                String tenDangNhap = tblBang.getValueAt(selectedRow, 4).toString();
//
//                // Mở JFrame nạp tiền mới và truyền tên đăng nhập
//                NapTienNewJFrame napTienFrame = new NapTienNewJFrame();
//                napTienFrame.setVisible(true);
//                napTienFrame.setLocationRelativeTo(this); // Căn giữa so với frame hiện tại
//
//                // Sau khi nạp tiền xong, load lại bảng để cập nhật số dư
//                loadAccountsToTable();
//            }
//        }
    }//GEN-LAST:event_tblBangMouseClicked

    private void txtNoiDungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoiDungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoiDungActionPerformed

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Reset thông tin người dùng (nếu cần)
            Auth.currentUserTenDangNhap = null;
            Auth.currentUserVaiTro = null;
            Auth.currentUserSoDu = 0;

            // Đóng form hiện tại
            this.dispose();

            // Quay về màn hình đăng nhập
            new DangNhapJFrame().setVisible(true);
        }
    }//GEN-LAST:event_btnDangXuatActionPerformed

    private void txtTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimKiemActionPerformed

    private void tblBangMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBangMousePressed

    }//GEN-LAST:event_tblBangMousePressed

    private void btnNapTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNapTienActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblBang.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản trong bảng.");
            return;
        }

        try {
            // Lấy số tiền đã nhập từ txtNapTien
            int soTienNap = Integer.parseInt(txtNapTien.getText().trim());

            if (soTienNap <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0.");
                return;
            }

            // Lấy tên đăng nhập từ cột thứ 4 của bảng
            String tenDangNhap = tblBang.getValueAt(selectedRow, 4).toString();

            // Lấy thông tin tài khoản từ DAO
            TaiKhoanDAO dao = new TaiKhoanDAO();
            TaiKhoanEntity taiKhoan = dao.timTheoTenDangNhap(tenDangNhap);

            if (taiKhoan == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản.");
                return;
            }

            // Cộng số dư mới
            int soDuMoi = taiKhoan.getSo_du() + soTienNap;

            // Cập nhật vào DB
            boolean ok = dao.capNhatSoDu(tenDangNhap, soDuMoi);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Nạp tiền thành công. Số dư mới: " + soDuMoi);
                loadAccountsToTable(); // Tải lại bảng nếu bạn có hàm này
            } else {
                JOptionPane.showMessageDialog(this, "Nạp tiền thất bại.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ.");
        }
    }//GEN-LAST:event_btnNapTienActionPerformed

    private void jTabbedPane4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane4StateChanged
        // TODO add your handling code here:
        jTabbedPane4.addChangeListener(e -> {
            int selectedIndex = jTabbedPane4.getSelectedIndex();
            if (selectedIndex == 0) {
                loadTablePC();
            } else if (selectedIndex == 1) {
                loadTableKhuVuc();
            }
        });
    }//GEN-LAST:event_jTabbedPane4StateChanged

    private void jTabbedPane6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane6StateChanged
        // TODO add your handling code here:
        var idx = jTabbedPane6.getSelectedIndex();
        if (idx == 0) {
            loadDonHang("Đang làm");
        } else {
            loadDonHang("Đã làm xong");
        }
    }//GEN-LAST:event_jTabbedPane6StateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        MayTinhDAO mtDao = new MayTinhDAO();
        String hostName = HostUtil.getHostname();
        mtDao.updateStatus(hostName, "Đang tắt");
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TabThong_ke_tai_khoan;
    private javax.swing.JLabel Tên;
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnDatDo;
    private javax.swing.JToggleButton btnDone;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnNapTien;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnSua1;
    private javax.swing.JButton btnSua2;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnThem1;
    private javax.swing.JButton btnThem2;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoa1;
    private javax.swing.JButton btnXoa2;
    private javax.swing.JComboBox<String> cboKhuvuc;
    private javax.swing.JComboBox<String> cboQl;
    private javax.swing.JComboBox<String> cboYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JTabbedPane jTabbedPane7;
    private javax.swing.JLabel lblDatDo;
    private javax.swing.JLabel lblDatDo1;
    private javax.swing.JLabel lblNapTheChart;
    private javax.swing.JLabel lblPieChart;
    private javax.swing.JLabel lblXinChao;
    private javax.swing.JTable tabChiTiet;
    private javax.swing.JTable tabChiTietDone;
    private javax.swing.JTable tabDoanhThuMon;
    private javax.swing.JTable tabDonHang;
    private javax.swing.JTable tabDonHangDone;
    private javax.swing.JTable tblBang;
    private javax.swing.JTable tblKhuvuc;
    private javax.swing.JTable tblPC;
    private javax.swing.JTextArea txtA;
    private javax.swing.JTextField txtDonHang;
    private javax.swing.JTextField txtDonHangDone;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtGiaPC;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JPasswordField txtMatKhau;
    private javax.swing.JTextField txtNapTien;
    private javax.swing.JTextField txtNoiDung;
    private javax.swing.JTextField txtPCname;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSoDu;
    private javax.swing.JTextField txtTenDangNhap;
    private javax.swing.JTextField txtTenKhu;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables

    private void preInit() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void ListDonHang() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void drawChart() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void ListDonHangDone() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
