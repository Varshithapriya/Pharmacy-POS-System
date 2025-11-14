package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

public class SalesReportFrame extends JFrame implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JButton btnExportPDF;

    // ✅ FIXED: Added DB credentials directly
    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public SalesReportFrame() {
        setTitle("Sales Report");
        setSize(1200, 1200);
        setLayout(null);
        setLocation(0, 0);
        getContentPane().setBackground(new Color(173, 216, 230));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel("Sales Report");
        heading.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
        heading.setBounds(400, 20, 500, 50);
        add(heading);

        model = new DefaultTableModel(new String[]{"Bill ID", "Product", "Quantity", "Price", "Total", "Date"}, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 100, 1000, 800);
        add(scroll);

        btnExportPDF = new JButton("Export PDF");
        btnExportPDF.setBounds(450, 920, 200, 50);
        btnExportPDF.setBackground(Color.ORANGE);
        btnExportPDF.addActionListener(this);
        add(btnExportPDF);

        loadSalesData();

        setVisible(true);
    }

    private void loadSalesData() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT bill_id, product_name, quantity, price, total, bill_date FROM bills ORDER BY bill_id");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total"),
                        rs.getDate("bill_date")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnExportPDF) {
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data to export.");
                return;
            }
            try {
                String folderPath = "C:\\Users\\priyavarshitha\\Documents\\Bills";
                File folder = new File(folderPath);
                if (!folder.exists()) folder.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "SalesReport_" + timeStamp + ".pdf";
                String pdfFilePath = folderPath + "\\" + fileName;

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
                document.open();

                // Title
                Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph title = new Paragraph("CJITS PHARMACY SALES REPORT", headingFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                document.add(new Paragraph(" "));

                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
                Statement stmt = con.createStatement();

                // ======================
                //  WEEKLY SALES SECTION
                // ======================
                Paragraph weeklyTitle = new Paragraph("WEEKLY SALES REPORT", headingFont);
                weeklyTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(weeklyTitle);
                document.add(new Paragraph(" "));

                String weeklyQuery = "SELECT PRODUCT_NAME, SUM(QUANTITY) AS TOTAL_QUANTITY, SUM(TOTAL) AS TOTAL_AMOUNT "
                        + "FROM bills WHERE BILL_DATE >= SYSDATE - 7 GROUP BY PRODUCT_NAME";
                ResultSet rsWeekly = stmt.executeQuery(weeklyQuery);

                PdfPTable weeklyTable = new PdfPTable(3);
                weeklyTable.setWidthPercentage(100);
                weeklyTable.addCell("Product Name");
                weeklyTable.addCell("Total Quantity");
                weeklyTable.addCell("Total Amount");

                while (rsWeekly.next()) {
                    weeklyTable.addCell(rsWeekly.getString("PRODUCT_NAME"));
                    weeklyTable.addCell(rsWeekly.getString("TOTAL_QUANTITY"));
                    weeklyTable.addCell(rsWeekly.getString("TOTAL_AMOUNT"));
                }

                document.add(weeklyTable);
                document.add(new Paragraph(" "));

                // ======================
                //  MONTHLY SALES SECTION
                // ======================
                Paragraph monthlyTitle = new Paragraph("MONTHLY SALES REPORT", headingFont);
                monthlyTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(monthlyTitle);
                document.add(new Paragraph(" "));

                String monthlyQuery = "SELECT PRODUCT_NAME, SUM(QUANTITY) AS TOTAL_QUANTITY, SUM(TOTAL) AS TOTAL_AMOUNT "
                        + "FROM bills WHERE EXTRACT(MONTH FROM BILL_DATE) = EXTRACT(MONTH FROM SYSDATE) "
                        + "AND EXTRACT(YEAR FROM BILL_DATE) = EXTRACT(YEAR FROM SYSDATE) "
                        + "GROUP BY PRODUCT_NAME";
                ResultSet rsMonthly = stmt.executeQuery(monthlyQuery);

                PdfPTable monthlyTable = new PdfPTable(3);
                monthlyTable.setWidthPercentage(100);
                monthlyTable.addCell("Product Name");
                monthlyTable.addCell("Total Quantity");
                monthlyTable.addCell("Total Amount");

                while (rsMonthly.next()) {
                    monthlyTable.addCell(rsMonthly.getString("PRODUCT_NAME"));
                    monthlyTable.addCell(rsMonthly.getString("TOTAL_QUANTITY"));
                    monthlyTable.addCell(rsMonthly.getString("TOTAL_AMOUNT"));
                }

                document.add(monthlyTable);

                // Close everything
                document.close();
                rsWeekly.close();
                rsMonthly.close();
                stmt.close();
                con.close();

                JOptionPane.showMessageDialog(this, "PDF exported successfully:\n" + pdfFilePath);

                // ✅ Open the generated PDF automatically
                File pdfFile = new File(pdfFilePath);
                if (pdfFile.exists()) {
                    Desktop.getDesktop().open(pdfFile);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage());
            }


        }
    }

    public static void main(String[] args) {
        new SalesReportFrame();
    }
}
