package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ViewTransactionsPage extends JFrame implements ActionListener {

    JTable transactionsTable;
    DefaultTableModel tableModel;
    JButton btnExportPDF;
    
    // DB credentials
    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public ViewTransactionsPage() {
        setTitle("View Transactions");
        setSize(1200, 1200);
        setLayout(null);
        setLocation(0, 0);
        getContentPane().setBackground(new Color(173, 216, 230));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"Bill ID", "Product", "Quantity", "Price", "Total", "Date"}, 0);
        transactionsTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(transactionsTable);
        scroll.setBounds(50, 50, 1000, 700);
        add(scroll);

        btnExportPDF = new JButton("Export All to PDF");
        btnExportPDF.setBounds(450, 800, 250, 50);
        btnExportPDF.setBackground(Color.ORANGE);
        btnExportPDF.addActionListener(this);
        add(btnExportPDF);

        loadTransactionsFromDB();

        setVisible(true);
    }

    private void loadTransactionsFromDB() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0); // clear previous
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total"),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("bill_date"))
                });
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching transactions:\n" + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnExportPDF) {
            exportToPDF();
        }
    }

    private void exportToPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No transactions to export.");
            return;
        }

        try {
            String folderPath = "C:\\Users\\priyavarshitha\\Desktop\\javatraining\\Transactions";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String pdfFile = folderPath + "\\CJITS_PHARMACY_REPORT_" + timestamp + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            Paragraph title = new Paragraph("CJITS PHARMACY BILLING REPORT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph date = new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(6); // 6 columns
            pdfTable.setWidthPercentage(100);
            pdfTable.addCell("Bill ID");
            pdfTable.addCell("Product");
            pdfTable.addCell("Quantity");
            pdfTable.addCell("Price");
            pdfTable.addCell("Total");
            pdfTable.addCell("Date");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pdfTable.addCell(tableModel.getValueAt(i, 0).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 1).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 2).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 3).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 4).toString());
                pdfTable.addCell(tableModel.getValueAt(i, 5).toString());
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "Transactions PDF generated successfully:\n" + pdfFile);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewTransactionsPage();
    }
}
