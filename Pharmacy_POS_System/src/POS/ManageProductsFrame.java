package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ManageProductsFrame extends JFrame implements ActionListener {

    JLabel lblProduct, lblPrice, lblExpiry;
    JTextField txtProduct, txtPrice, txtExpiry;
    JButton btnAdd, btnUpdate, btnRefresh;
    JTable productTable;
    DefaultTableModel tableModel;

    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public ManageProductsFrame() {
        setTitle("Manage Products");
        setSize(1200, 1200);
        setLayout(null);
        getContentPane().setBackground(new Color(173, 216, 230));
        setLocation(0, 0);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel("Product Management");
        heading.setFont(new Font("Arial", Font.BOLD, 50));
        heading.setBounds(300, 30, 600, 60);
        add(heading);

        lblProduct = new JLabel("Product Name:");
        lblProduct.setBounds(50, 120, 150, 30);
        add(lblProduct);
        txtProduct = new JTextField();
        txtProduct.setBounds(200, 120, 200, 30);
        add(txtProduct);

        lblPrice = new JLabel("Price:");
        lblPrice.setBounds(50, 170, 150, 30);
        add(lblPrice);
        txtPrice = new JTextField();
        txtPrice.setBounds(200, 170, 200, 30);
        add(txtPrice);

        lblExpiry = new JLabel("Expiry Date (YYYY-MM-DD):");
        lblExpiry.setBounds(50, 220, 250, 30);
        add(lblExpiry);
        txtExpiry = new JTextField();
        txtExpiry.setBounds(250, 220, 150, 30);
        add(txtExpiry);

        btnAdd = new JButton("Add Product");
        btnAdd.setBounds(50, 270, 150, 40);
        btnAdd.setBackground(new Color(60, 179, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnUpdate = new JButton("Update Product");
        btnUpdate.setBounds(220, 270, 150, 40);
        btnUpdate.setBackground(new Color(255, 193, 7));
        btnUpdate.setForeground(Color.BLACK);
        btnUpdate.addActionListener(this);
        add(btnUpdate);

        btnRefresh = new JButton("Refresh Table");
        btnRefresh.setBounds(400, 270, 150, 40);
        btnRefresh.setBackground(new Color(30, 144, 255));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(this);
        add(btnRefresh);

        tableModel = new DefaultTableModel(new String[]{"Product ID", "Product Name", "Price", "Expiry Date"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBounds(50, 350, 900, 500);
        add(scroll);

        loadProducts();

        setVisible(true);
    }

    void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM products ORDER BY product_id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getDate("expiry_date")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products:\n" + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            if (e.getSource() == btnAdd) {
                String name = txtProduct.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                java.sql.Date expiry = java.sql.Date.valueOf(txtExpiry.getText().trim());

                String sql = "INSERT INTO products (product_name, price, expiry_date) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setDate(3, expiry);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product added successfully!");
            } else if (e.getSource() == btnUpdate) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a product to update.");
                    return;
                }
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = txtProduct.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                java.sql.Date expiry = java.sql.Date.valueOf(txtExpiry.getText().trim());

                String sql = "UPDATE products SET product_name=?, price=?, expiry_date=? WHERE product_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setDate(3, expiry);
                ps.setInt(4, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product updated successfully!");
            } else if (e.getSource() == btnRefresh) {
                loadProducts();
                return;
            }
            loadProducts();
            txtProduct.setText("");
            txtPrice.setText("");
            txtExpiry.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ManageProductsFrame();
    }
}
