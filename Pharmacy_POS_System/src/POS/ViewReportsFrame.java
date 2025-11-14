package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

public class ViewReportsFrame extends JFrame implements ActionListener {

    JTable billTable;
    DefaultTableModel tableModel;
    JButton btnExportPDF, btnRefresh;

    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public ViewReportsFrame() {
        setTitle("Sales Reports");
        setSize(1200, 1200);
        setLayout(null);
        getContentPane().setBackground(new Color(173, 216, 230));
        setLocation(0, 0);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel("Sales Reports");
       // heading.setFont(new Font("Arial", Font.BOLD, 50));
        heading.setBounds(350, 30, 500, 60);
        add(heading);

        tableModel = new DefaultTableModel(new String[]{"Bill ID", "Product", "Quantity", "Price", "Total", "Bill Date"}, 0);
        billTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(billTable);
        scroll.setBounds(50, 150, 1100, 700);
        add(scroll);

        btnExportPDF = new JButton("Export as PDF");
        btnExportPDF.setBounds(300, 900, 200, 50);
        btnExportPDF.setBackground(new Color(60, 179, 113));
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.addActionListener(this);
        add(btnExportPDF);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(600, 900, 200, 50);
        btnRefresh.setBackground(new Color(255, 193, 7));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.addActionListener(this);
        add(btnRefresh);

        loadBills();

        setVisible(true);
    }

    private void loadBills() {
        tableModel.setRowCount(0);
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
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
            JOptionPane.showMessageDialog(this, "Error loading bills:\n" + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnExportPDF) {
            exportPDF();
        } else if (e.getSource() == btnRefresh) {
            loadBills();
        }
    }

    private void exportPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No bills to export.");
            return;
        }

        try {
            // Create folder on Desktop if not exists
            String folderPath = System.getProperty("user.home") + "\\Desktop\\javatraining\\Reports";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String pdfFile = folderPath + "\\CJITS_PHARMACY_REPORT_" + timestamp + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Heading
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("CJITS PHARMACY BILLING REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated On: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new Paragraph(" "));

            // Table in PDF
            PdfPTable pdfTable = new PdfPTable(6); // 6 columns
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);

            // Add headers
            String[] headers = {"Bill ID", "Product", "Quantity", "Price", "Total", "Bill Date"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(cell);
            }

            double grandTotal = 0;

            // Add rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pdfTable.addCell(tableModel.getValueAt(i, 0).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 1).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 2).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 3).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 4).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 5).toString());

                grandTotal += Double.parseDouble(tableModel.getValueAt(i, 4).toString());
            }

            // Grand total row
            PdfPCell totalCell = new PdfPCell(new Phrase("Grand Total"));
            totalCell.setColspan(4);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfTable.addCell(totalCell);
            pdfTable.addCell(String.valueOf(grandTotal));
            pdfTable.addCell(""); // empty cell for Bill Date column

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF report generated successfully at:\n" + pdfFile);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewReportsFrame();
    }
}

