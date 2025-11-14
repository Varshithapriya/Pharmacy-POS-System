package POS;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UpdateUserPage extends JFrame implements ActionListener {

    JLabel lblUserId, lblUsername, lblPassword, lblRole;
    JTextField txtUserId, txtUsername;
    JPasswordField txtPassword;
    JComboBox<String> cmbRole;
    JButton btnUpdate, btnBack;

    public UpdateUserPage() {
        setTitle("Update User");
        setSize(800, 600);
        setLocation(150, 150);
        getContentPane().setBackground(new Color(173, 216, 230));
        setLayout(null);

        lblUserId = new JLabel("User ID:");
        lblUserId.setBounds(200, 100, 150, 30);
        add(lblUserId);

        txtUserId = new JTextField();
        txtUserId.setBounds(350, 100, 250, 30);
        add(txtUserId);

        lblUsername = new JLabel("New Username:");
        lblUsername.setBounds(200, 180, 150, 30);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(350, 180, 250, 30);
        add(txtUsername);

        lblPassword = new JLabel("New Password:");
        lblPassword.setBounds(200, 260, 150, 30);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(350, 260, 250, 30);
        add(txtPassword);

        lblRole = new JLabel("Role:");
        lblRole.setBounds(200, 340, 150, 30);
        add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"admin", "cashier"});
        cmbRole.setBounds(350, 340, 250, 30);
        add(cmbRole);

        btnUpdate = new JButton("Update User");
        btnUpdate.setBounds(250, 430, 150, 50);
        btnUpdate.setBackground(Color.YELLOW);
        btnUpdate.addActionListener(this);
        add(btnUpdate);

        btnBack = new JButton("Back");
        btnBack.setBounds(450, 430, 150, 50);
        btnBack.setBackground(Color.RED);
        btnBack.addActionListener(e -> dispose());
        add(btnBack);

        setVisible(true);
    }


	@Override
    public void actionPerformed(ActionEvent e) {
        String userId = txtUserId.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cmbRole.getSelectedItem().toString();

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "priya",
                    "root"
            );

            String sql = "UPDATE users SET username=?, password=?, role=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setInt(4, Integer.parseInt(userId));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(btnBack, this, "User updated successfully!", rows);
            } else {
                JOptionPane.showMessageDialog(btnBack, this, "User ID not found.", rows);
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(btnBack, this, "Error updating user:\n" + ex.getMessage(), 0);
        }
    }

    public static void main(String[] args) {
        new UpdateUserPage();
    }
}

