package POS;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewUsersPage extends JFrame {
    JTable userTable;
    DefaultTableModel model;

    public ViewUsersPage() {
        setTitle("View Users");
        setSize(1000, 600);
        setLocation(100, 100);
        getContentPane().setBackground(new Color(173, 216, 230));
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Role");

        userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        loadUsers();

        setVisible(true);
    }

    private void loadUsers() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "priya",
                    "root"
            );

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username, role FROM users ORDER BY id");

            model.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                };
                model.addRow(row);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching users:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewUsersPage();
    }
}

