package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;

public class CashierHomePage extends JFrame implements ActionListener {

    JLabel lblProduct, lblQuantity, lblPrice;
    JTextField txtProduct, txtQuantity, txtPrice;
    JButton btnAddItem, btnGeneratePDF, btnLogout;
    JTable billTable;
    DefaultTableModel tableModel;

    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public CashierHomePage() {
        setTitle("Cashier Dashboard");
        setSize(1200, 1200);
        setLayout(null);
        setLocation(0, 0);
        getContentPane().setBackground(new Color(173, 216, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel heading = new JLabel("Cashier Billing");
        heading.setFont(new Font("Arial", Font.BOLD, 50));
        heading.setBounds(350, 50, 500, 60);
        add(heading);

        lblProduct = new JLabel("Product Name:");
        lblProduct.setBounds(50, 150, 150, 30);
        add(lblProduct);
        txtProduct = new JTextField();
        txtProduct.setBounds(200, 150, 200, 30);
        add(txtProduct);

        lblPrice = new JLabel("Price:");
        lblPrice.setBounds(450, 150, 100, 30);
        add(lblPrice);
        txtPrice = new JTextField();
        txtPrice.setBounds(550, 150, 100, 30);
        txtPrice.setEditable(false);
        add(txtPrice);

        lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(700, 150, 100, 30);
        add(lblQuantity);
        txtQuantity = new JTextField();
        txtQuantity.setBounds(800, 150, 100, 30);
        add(txtQuantity);

        btnAddItem = new JButton("Add Item");
        btnAddItem.setBounds(950, 150, 150, 30);
        btnAddItem.setBackground(Color.GREEN);
        btnAddItem.addActionListener(this);
        add(btnAddItem);

        btnGeneratePDF = new JButton("Generate PDF");
        btnGeneratePDF.setBounds(950, 200, 150, 30);
        btnGeneratePDF.setBackground(Color.ORANGE);
        btnGeneratePDF.addActionListener(this);
        add(btnGeneratePDF);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(500, 900, 200, 50);
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            new POS_Frames.HomePage();
            dispose();
        });
        add(btnLogout);

        tableModel = new DefaultTableModel(new String[]{"Product", "Quantity", "Price", "Total"}, 0);
        billTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(billTable);
        scroll.setBounds(50, 250, 1050, 600);
        add(scroll);

        // Auto-fill price when product name loses focus
        txtProduct.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    String productName = txtProduct.getText().trim();
                    if (!productName.isEmpty()) {
                        Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                        PreparedStatement ps = con.prepareStatement("SELECT price FROM products WHERE LOWER(product_name)=LOWER(?)");
                        ps.setString(1, productName);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            txtPrice.setText(String.valueOf(rs.getDouble("price")));
                        } else {
                            txtPrice.setText("");
                        }
                        con.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddItem) {
            try {
                String product = txtProduct.getText().trim();
                int quantity = Integer.parseInt(txtQuantity.getText().trim());
                double price = Double.parseDouble(txtPrice.getText().trim());
                double total = quantity * price;

                tableModel.addRow(new Object[]{product, quantity, price, total});

                // Save to DB
                Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO bills (product_name, quantity, price, total) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, product);
                ps.setInt(2, quantity);
                ps.setDouble(3, price);
                ps.setDouble(4, total);
                ps.executeUpdate();
                con.close();

                txtProduct.setText("");
                txtPrice.setText("");
                txtQuantity.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == btnGeneratePDF) {
            generatePDF();
        }
    }

    private void generatePDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items to generate PDF.");
            return;
        }
        try {
            String folderPath = "C:\\Users\\priyavarshitha\\Desktop\\javatraining\\Bills";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String pdfFile = folderPath + "\\CJITS_PHARMACY_BILL_" + timestamp + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Title
            Paragraph title = new Paragraph("CJITS PHARMACY BILLING REPORT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new Paragraph(" "));

            // Table
            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.addCell("Product");
            pdfTable.addCell("Quantity");
            pdfTable.addCell("Price");
            pdfTable.addCell("Total");

            double grandTotal = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pdfTable.addCell(tableModel.getValueAt(i,0).toString());
                pdfTable.addCell(tableModel.getValueAt(i,1).toString());
                pdfTable.addCell(tableModel.getValueAt(i,2).toString());
                pdfTable.addCell(tableModel.getValueAt(i,3).toString());

                grandTotal += Double.parseDouble(tableModel.getValueAt(i,3).toString());
            }
            PdfPCell cell = new PdfPCell(new Phrase("Grand Total"));
            cell.setColspan(3);
            pdfTable.addCell(cell);
            pdfTable.addCell(String.valueOf(grandTotal));

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF generated successfully:\n" + pdfFile);
            tableModel.setRowCount(0); // clear table

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new CashierHomePage();
    }
}
