package POS_Frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import POS.AdminHomePage;
import POS.CashierHomePage;

public class HomePage extends JFrame {

    JButton adminBtn, cashierBtn;

    public HomePage() {
        setTitle("Home Page");
        setSize(1200, 1200);
        setLayout(null);
        setLocation(0,0);
        getContentPane().setBackground(new Color(173,216,230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel heading = new JLabel("CJITS PHARMACY");
        heading.setFont(new Font("Arial", Font.BOLD, 80));
        heading.setBounds(250, 150, 900, 100);
        add(heading);

        adminBtn = new JButton("Admin Login");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 30));
        adminBtn.setBounds(250, 400, 300, 80);
        adminBtn.setBackground(new Color(40,167,69));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.addActionListener(e -> {
            new LoginPage("admin");
            dispose();
        });
        add(adminBtn);

        cashierBtn = new JButton("Cashier Login");
        cashierBtn.setFont(new Font("Arial", Font.BOLD, 30));
        cashierBtn.setBounds(600, 400, 300, 80);
        cashierBtn.setBackground(new Color(255,193,7));
        cashierBtn.setForeground(Color.BLACK);
        cashierBtn.addActionListener(e -> {
            new LoginPage("cashier");
            dispose();
        });
        add(cashierBtn);

        setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage();
    }
}
