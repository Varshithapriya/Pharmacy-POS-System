package POS;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteUserPage extends JFrame implements ActionListener {

    JLabel lblUserId;
    JTextField txtUserId;
    JButton btnDelete, btnBack;

    public DeleteUserPage() {
        setTitle("Delete User");
        setSize(600, 400);
        setLocation(200, 200);
        getContentPane().setBackground(new Color(173, 216, 230));
        setLayout(null);

        lblUserId = new JLabel("User ID:");
        lblUserId.setBounds(150, 100, 100, 30);
        add(lblUserId);

        txtUserId = new JTextField();
        txtUserId.setBounds(250, 100, 200, 30);
        add(txtUserId);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(150, 200, 150, 50);
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(this);
        add(btnDelete);

        btnBack = new JButton("Back");
        btnBack.setBounds(300, 200, 150, 50);
        btnBack.setBackground(Color.GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> dispose());
        add(btnBack);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userId = txtUserId.getText().trim();

        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete User ID " + userId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "priya",
                    "root"
            );

            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(userId));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "User ID not found.");
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new DeleteUserPage();
    }
}

