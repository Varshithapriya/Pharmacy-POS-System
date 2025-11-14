package POS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddUserPage extends JFrame implements ActionListener {

    JLabel lblUsername, lblPassword, lblRole;
    JTextField txtUsername;
    JPasswordField txtPassword;
    JComboBox<String> cmbRole;
    JButton btnAdd, btnBack;

    public AddUserPage() {
        setTitle("Add User");
        setSize(800, 600);
        setLocation(150, 150);
        getContentPane().setBackground(new Color(173, 216, 230));
        setLayout(null);

        lblUsername = new JLabel("Username:");
        lblUsername.setBounds(200, 150, 150, 30);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(350, 150, 250, 30);
        add(txtUsername);

        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(200, 220, 150, 30);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(350, 220, 250, 30);
        add(txtPassword);

        lblRole = new JLabel("Role:");
        lblRole.setBounds(200, 290, 150, 30);
        add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"admin", "cashier"});
        cmbRole.setBounds(350, 290, 250, 30);
        add(cmbRole);

        btnAdd = new JButton("Add User");
        btnAdd.setBounds(250, 380, 150, 50);
        btnAdd.setBackground(Color.GREEN);
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnBack = new JButton("Back");
        btnBack.setBounds(450, 380, 150, 50);
        btnBack.setBackground(Color.RED);
        btnBack.addActionListener(e -> dispose());
        add(btnBack);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
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

            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding user:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new AddUserPage();
    }
}

