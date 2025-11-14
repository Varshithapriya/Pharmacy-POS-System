package POS_Frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import POS.AdminHomePage;
import POS.CashierHomePage;

public class LoginPage extends JFrame implements ActionListener {

    JLabel userLabel, passLabel, titleLabel;
    JTextField userField;
    JPasswordField passField;
    JButton loginButton, backButton;
    String role;

    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public LoginPage(String role) {
        this.role = role;

        setLayout(null);
        getContentPane().setBackground(new Color(173,216,230));

        titleLabel = new JLabel(role.toUpperCase() + " LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setBounds(420, 150, 400, 50);
        add(titleLabel);

        userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 22));
        userLabel.setBounds(350, 280, 150, 30);
        add(userLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 22));
        userField.setBounds(520, 280, 250, 35);
        add(userField);

        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 22));
        passLabel.setBounds(350, 350, 150, 30);
        add(passLabel);

        passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 22));
        passField.setBounds(520, 350, 250, 35);
        add(passField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setBounds(420, 450, 150, 50);
        loginButton.setBackground(Color.YELLOW);
        loginButton.addActionListener(this);
        add(loginButton);

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 22));
        backButton.setBounds(620, 450, 150, 50);
        backButton.setBackground(Color.YELLOW);
        backButton.addActionListener(e -> {
            new HomePage();
            dispose();
        });
        add(backButton);

        setSize(1200,1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String uname = userField.getText().trim();
        String pwd = new String(passField.getPassword()).trim();

        if (uname.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", 
                "priya", 
                "root"
            );

            String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, uname);
            ps.setString(2, pwd);
            ps.setString(3, role);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful as " + role + "!");
                dispose(); // close login page

                // Open correct dashboard
                if (role.equalsIgnoreCase("admin")) {
                    new AdminHomePage(); // your Admin page
                } else if (role.equalsIgnoreCase("cashier")) {
                    new CashierHomePage(); // your Cashier page
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch.");
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new HomePage(); // start from Home
    }
}
