package POS;

import javax.swing.*;

import POS_Frames.HomePage;

import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame implements ActionListener {
    JLabel headingLabel;
    JButton logoutBtn;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setLayout(null);
        setSize(1200, 1200);
        setLocation(0, 0);
        getContentPane().setBackground(new Color(173, 216, 230)); // light blue
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        headingLabel = new JLabel("Welcome, Admin!");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 60));
        headingLabel.setBounds(300, 250, 700, 100);
        add(headingLabel);

        logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 30));
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBounds(500, 450, 200, 80);
        logoutBtn.addActionListener(this);
        add(logoutBtn);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutBtn) {
            JOptionPane.showMessageDialog(this, "Logging out...");
            new HomePage();
            dispose();
        }
    }
}
