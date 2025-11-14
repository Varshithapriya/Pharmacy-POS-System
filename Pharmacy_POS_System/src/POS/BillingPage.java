package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

public class BillingPage extends JFrame implements ActionListener {

    JLabel lblProduct, lblQuantity, lblPrice, lblGrandTotal;
    JTextField txtProduct, txtQuantity, txtPrice;
    JButton btnAddItem, btnRemoveItem, btnGeneratePDF;
    JTable billTable;
    DefaultTableModel tableModel;

    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public BillingPage() {
        setTitle("CJITS Pharmacy Billing System");
        setSize(1100, 850);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(173, 216, 230)); // light blue

        lblProduct = new JLabel("Product Name:");
        lblProduct.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblProduct.setBounds(50, 50, 150, 30);
        add(lblProduct);

        txtProduct = new JTextField();
        txtProduct.setBounds(200, 50, 200, 30);
        add(txtProduct);

        lblQuantity = new JLabel("Quantity:");
        lblQuantity.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblQuantity.setBounds(50, 100, 150, 30);
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(200, 100, 200, 30);
        add(txtQuantity);

        lblPrice = new JLabel("Price:");
        lblPrice.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lblPrice.setBounds(50, 150, 150, 30);
        add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(200, 150, 200, 30);
        txtPrice.setEditable(false);
        add(txtPrice);

        btnAddItem = new JButton("Add Item");
        btnAddItem.setBounds(450, 50, 150, 50);
        btnAddItem.setBackground(new Color(0, 200, 0));
        btnAddItem.setForeground(Color.WHITE);
        btnAddItem.addActionListener(this);
        add(btnAddItem);

        btnRemoveItem = new JButton("Remove Item");
        btnRemoveItem.setBounds(630, 50, 150, 50);
        btnRemoveItem.setBackground(new Color(255, 69, 0));
        btnRemoveItem.setForeground(Color.WHITE);
        btnRemoveItem.addActionListener(this);
        add(btnRemoveItem);

        btnGeneratePDF = new JButton("Generate PDF");
        btnGeneratePDF.setBounds(810, 50, 200, 50);
        btnGeneratePDF.setBackground(Color.ORANGE);
        btnGeneratePDF.addActionListener(this);
        add(btnGeneratePDF);

        tableModel = new DefaultTableModel(new String[]{"Product", "Quantity", "Price", "Total"}, 0);
        billTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(billTable);
        scroll.setBounds(50, 250, 950, 450);
        add(scroll);

        lblGrandTotal = new JLabel("Grand Total: ₹ 0.00");
        lblGrandTotal.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        lblGrandTotal.setForeground(Color.BLACK);
        lblGrandTotal.setBounds(700, 720, 300, 40); // move up a bit if needed
        add(lblGrandTotal);


        txtProduct.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String productName = txtProduct.getText().trim();
                if (!productName.isEmpty()) {
                    fetchPrice(productName);
                }
            }
        });

        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void fetchPrice(String productName) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT price FROM products WHERE product_name=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtPrice.setText(String.valueOf(rs.getDouble("price")));
            } else {
                txtPrice.setText("");
                JOptionPane.showMessageDialog(this, "Product not found!");
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddItem) {
            addItemToBill();
        } else if (e.getSource() == btnRemoveItem) {
            removeSelectedItem();
        } else if (e.getSource() == btnGeneratePDF) {
            generatePDFBill();
        }
    }
    private void addItemToBill() {
        try {
            if (txtProduct.getText().isEmpty() || txtQuantity.getText().isEmpty() || txtPrice.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            String product = txtProduct.getText().trim();
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());
            double total = quantity * price;

            tableModel.addRow(new Object[]{product, quantity, price, total});

            // ✅ Update total
            updateGrandTotal();

            txtProduct.setText("");
            txtQuantity.setText("");
            txtPrice.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

 
    private void removeSelectedItem() {
        int row = billTable.getSelectedRow();
        if (row != -1) {
            tableModel.removeRow(row);
            updateGrandTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Select an item to remove.");
        }
    }

    private void updateGrandTotal() {
        double grandTotal = 0.0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object totalObj = tableModel.getValueAt(i, 3);
            if (totalObj != null) {
                try {
                    grandTotal += Double.parseDouble(totalObj.toString());
                } catch (NumberFormatException e) {
                    // Skip invalid numbers
                }
            }
        }

        lblGrandTotal.setText(String.format("Grand Total: ₹ %.2f", grandTotal));
    }


    private void generatePDFBill() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items to generate PDF.");
            return;
        }

        Connection con = null;
        try {
            // Create folder on Desktop
            String folderPath = System.getProperty("user.home") + "\\Desktop\\CJITS_Bill_Invoices";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String pdfFile = folderPath + "\\CJITS_BILL_" + timestamp + ".pdf";

            // ✅ Connect to database early to get bill_id
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            int billId = 0;
            String getIdSQL = "SELECT bill_seq.NEXTVAL FROM dual";
            PreparedStatement psGetId = con.prepareStatement(getIdSQL);
            ResultSet rs = psGetId.executeQuery();
            if (rs.next()) {
                billId = rs.getInt(1);
            }

            // Generate PDF
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Add border
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = new Rectangle(30, 30, 565, 802);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            rect.setBorderColor(BaseColor.GRAY);
            canvas.rectangle(rect);
            canvas.stroke();

            // Add logo
            try {
                Image logo = Image.getInstance("https://scontent.fwgc2-1.fna.fbcdn.net/v/t39.30808-1/300495276_420582160179152_2870931290092225439_n.png?stp=dst-png_s200x200&_nc_cat=105&ccb=1-7&_nc_sid=2d3e12&_nc_ohc=yk3Vwi84c1EQ7kNvwFU9dZJ&_nc_oc=AdnltK-TEH4Mp_V9BoTKcE2xSHzZ3NhhccgJSkBFWyBCCZFUG7e0FQa7dAWO_8GHCQ0&_nc_zt=24&_nc_ht=scontent.fwgc2-1.fna&_nc_gid=x1zV1TC9ZeW9cdBPQsoGaA&oh=00_AfgsxkxW0-2Pmy7CP1XR_COWTiPptPewUpUmakB8v52bhA&oe=69137259"); // <<<< Replace this with your logo path
                logo.scaleToFit(120, 70);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception ex) {
                // If logo not found, continue without it
            }
            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("CJITS Pharmacy Bill Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Bill info
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Paragraph billInfo = new Paragraph("Bill No: " + billId + "     |     Date: " + dateNow, infoFont);
            billInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(billInfo);
            document.add(new Paragraph(" "));

            // Pharmacy info
            Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.DARK_GRAY);
            document.add(new Paragraph("CJITS Pharmacy, Warangal, Telangana", subFont));
            document.add(new Paragraph("Contact: +91 9876543210 | Email: pharmacy@cjits.edu", subFont));
            document.add(new Paragraph(" "));

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Product");
            table.addCell("Quantity");
            table.addCell("Price");
            table.addCell("Total");

            double grandTotal = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                table.addCell(tableModel.getValueAt(i, 0).toString());
                table.addCell(tableModel.getValueAt(i, 1).toString());
                table.addCell(tableModel.getValueAt(i, 2).toString());
                table.addCell(tableModel.getValueAt(i, 3).toString());
                grandTotal += Double.parseDouble(tableModel.getValueAt(i, 3).toString());
            }

            PdfPCell totalCell = new PdfPCell(new Phrase("Grand Total"));
            totalCell.setColspan(3);
            table.addCell(totalCell);
            table.addCell(String.valueOf(grandTotal));
            document.add(table);

            Paragraph footer = new Paragraph("Thank you for visiting CJITS Pharmacy!",
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.close();

            // ✅ Save bill details into Oracle Database
            String insertSQL = "INSERT INTO bills (bill_id, bill_date, grand_total, file_path) VALUES (?, SYSDATE, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertSQL);
            ps.setInt(1, billId);
            ps.setDouble(2, grandTotal);
            ps.setString(3, pdfFile);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bill PDF generated successfully!\nFile: " + pdfFile);
            Desktop.getDesktop().open(new File(pdfFile));

            tableModel.setRowCount(0);
            lblGrandTotal.setText("Grand Total: 0.0");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF:\n" + ex.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ignored) {}
        }
    }


    public static void main(String[] args) {
        new BillingPage();
    }
}
