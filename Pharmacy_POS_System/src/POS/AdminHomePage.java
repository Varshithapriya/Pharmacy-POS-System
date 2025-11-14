package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*; // for java.awt.Font, Color, etc.
import java.awt.event.*;
import java.sql.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import POS_Frames.HomePage;

public class AdminHomePage extends JFrame implements ActionListener {

    // Buttons
    JButton btnManageProducts, btnExpiryAlerts, btnSalesReport, btnLogout;

    // DB credentials
    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public AdminHomePage() {
        setTitle("Admin Dashboard");
        setSize(1200, 1200);
        setLayout(null);
        setLocation(0, 0);
        getContentPane().setBackground(new Color(173, 216, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel headingLabel = new JLabel("Welcome, Admin!");
        headingLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 60)); // Explicit AWT font
        headingLabel.setBounds(370, 150, 700, 100);
        add(headingLabel);

        btnManageProducts = new JButton("Manage Products");
        btnManageProducts.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        btnManageProducts.setBounds(400, 280, 400, 60);
        btnManageProducts.setBackground(new Color(60, 179, 113));
        btnManageProducts.setForeground(Color.WHITE);
        btnManageProducts.addActionListener(this);
        add(btnManageProducts);

        btnExpiryAlerts = new JButton("Expiry Alerts");
        btnExpiryAlerts.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        btnExpiryAlerts.setBounds(400, 380, 400, 60);
        btnExpiryAlerts.setBackground(new Color(255, 193, 7));
        btnExpiryAlerts.setForeground(Color.BLACK);
        btnExpiryAlerts.addActionListener(this);
        add(btnExpiryAlerts);

        btnSalesReport = new JButton("Sales Report");
        btnSalesReport.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        btnSalesReport.setBounds(400, 480, 400, 60);
        btnSalesReport.setBackground(new Color(30, 144, 255));
        btnSalesReport.setForeground(Color.WHITE);
        btnSalesReport.addActionListener(this);
        add(btnSalesReport);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        btnLogout.setBounds(500, 580, 200, 60);
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(this);
        add(btnLogout);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnManageProducts) {
            new ManageProductsFrame();
        } else if (e.getSource() == btnExpiryAlerts) {
            showExpiryAlerts();
        } else if (e.getSource() == btnSalesReport) {
            generateSalesReport();
        } else if (e.getSource() == btnLogout) {
            JOptionPane.showMessageDialog(this, "Logging out...");
            new HomePage();
            dispose();
        }
    }

    private void showExpiryAlerts() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "SELECT product_name, expiry_date FROM products WHERE expiry_date <= SYSDATE + 30";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"Product", "Expiry Date"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("product_name"), rs.getDate("expiry_date")});
            }

            JTable table = new JTable(model);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scroll, "Products Expiring Soon", JOptionPane.INFORMATION_MESSAGE);
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching expiry alerts:\n" + ex.getMessage());
        }
    }

    private void generateSalesReport() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "SELECT * FROM bills ORDER BY bill_date";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Document document = new Document();
            String folderPath = System.getProperty("user.home") + "\\Desktop\\javatraining\\SalesReports";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String pdfFile = folderPath + "\\CJITS_SALES_REPORT_" + timestamp + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.open();

            // Use iText Font explicitly
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("CJITS PHARMACY SALES REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100);
            String[] headers = {"Bill ID", "Product", "Quantity", "Price", "Total", "Date"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(cell);
            }

            while (rs.next()) {
                pdfTable.addCell(String.valueOf(rs.getInt("bill_id")));
                pdfTable.addCell(rs.getString("product_name"));
                pdfTable.addCell(String.valueOf(rs.getInt("quantity")));
                pdfTable.addCell(String.valueOf(rs.getDouble("price")));
                pdfTable.addCell(String.valueOf(rs.getDouble("total")));
                pdfTable.addCell(rs.getDate("bill_date").toString());
            }

            document.add(pdfTable);
            document.close();
            con.close();

            JOptionPane.showMessageDialog(this, "Sales report generated successfully:\n" + pdfFile);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating sales report:\n" + ex.getMessage());
        }
    }

    // Inner frame for managing products
    class ManageProductsFrame extends JFrame implements ActionListener {
        JTextField txtProduct, txtPrice;
        JFormattedTextField txtExpiry;
        JButton btnAdd, btnUpdate;
        JTable productTable;
        DefaultTableModel model;

        public ManageProductsFrame() {
            setTitle("Manage Products");
            setSize(1000, 700);
            setLayout(null);
            getContentPane().setBackground(new Color(173, 216, 230));

            JLabel lblProd = new JLabel("Product Name:");
            lblProd.setBounds(50, 30, 150, 30);
            add(lblProd);

            txtProduct = new JTextField();
            txtProduct.setBounds(200, 30, 200, 30);
            add(txtProduct);

            JLabel lblPrice = new JLabel("Price:");
            lblPrice.setBounds(50, 80, 150, 30);
            add(lblPrice);

            txtPrice = new JTextField();
            txtPrice.setBounds(200, 80, 200, 30);
            add(txtPrice);

            JLabel lblExpiry = new JLabel("Expiry (yyyy-MM-dd):");
            lblExpiry.setBounds(50, 130, 200, 30);
            add(lblExpiry);

            txtExpiry = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
            txtExpiry.setBounds(250, 130, 150, 30);
            add(txtExpiry);

            btnAdd = new JButton("Add Product");
            btnAdd.setBounds(500, 30, 150, 40);
            btnAdd.addActionListener(this);
            add(btnAdd);

            btnUpdate = new JButton("Update Price");
            btnUpdate.setBounds(500, 80, 150, 40);
            btnUpdate.addActionListener(this);
            add(btnUpdate);

            model = new DefaultTableModel(new String[]{"Product", "Price", "Expiry"}, 0);
            productTable = new JTable(model);
            JScrollPane scroll = new JScrollPane(productTable);
            scroll.setBounds(50, 200, 800, 400);
            add(scroll);

            loadProducts();

            setVisible(true);
        }

        private void loadProducts() {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

                ResultSet rs = con.createStatement().executeQuery("SELECT product_name, price, expiry_date FROM products");
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("product_name"), rs.getDouble("price"), rs.getDate("expiry_date")});
                }

                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String product = txtProduct.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String expiryStr = txtExpiry.getText().trim();

            if (product.isEmpty() || priceStr.isEmpty() || expiryStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter all fields!");
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                java.sql.Date expiry = java.sql.Date.valueOf(expiryStr);

                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

                if (e.getSource() == btnAdd) {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO products (product_name, price, expiry_date) VALUES (?, ?, ?)");
                    ps.setString(1, product);
                    ps.setDouble(2, price);
                    ps.setDate(3, expiry);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                } else if (e.getSource() == btnUpdate) {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE products SET price=?, expiry_date=? WHERE product_name=?");
                    ps.setDouble(1, price);
                    ps.setDate(2, expiry);
                    ps.setString(3, product);
                    int rows = ps.executeUpdate();
                    if (rows > 0) JOptionPane.showMessageDialog(this, "Product updated successfully!");
                    else JOptionPane.showMessageDialog(this, "Product not found!");
                }

                con.close();
                loadProducts();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new AdminHomePage();
    }
}
