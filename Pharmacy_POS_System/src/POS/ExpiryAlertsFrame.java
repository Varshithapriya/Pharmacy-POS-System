package POS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ExpiryAlertsFrame extends JFrame {

    JTable expiryTable;
    DefaultTableModel tableModel;

    // DB credentials
    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public ExpiryAlertsFrame() {
        setTitle("Expiry Alerts");
        setSize(1200, 1200);
        setLayout(null);
        setLocation(0, 0);
        getContentPane().setBackground(new Color(173, 216, 230));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel("Products Near Expiry (Next 30 Days)");
        heading.setFont(new Font("Arial", Font.BOLD, 40));
        heading.setBounds(200, 50, 900, 50);
        add(heading);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Product Name", "Expiry Date"}, 0);
        expiryTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(expiryTable);
        scroll.setBounds(150, 150, 900, 700);
        add(scroll);

        fetchExpiryData();

        setVisible(true);
    }

    private void fetchExpiryData() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Select products expiring in the next 30 days
            String sql = "SELECT product_name, expiry_date FROM products WHERE expiry_date <= SYSDATE + 30 ORDER BY expiry_date";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                String name = rs.getString("product_name");
                String date = sdf.format(rs.getDate("expiry_date"));
                tableModel.addRow(new Object[]{name, date});
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching expiry data:\n" + ex.getMessage());
        }
    }

    // --- Main for testing ---
    public static void main(String[] args) {
        new ExpiryAlertsFrame();
    }
}
