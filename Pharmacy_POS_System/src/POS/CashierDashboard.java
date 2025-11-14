package POS;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import POS_Frames.HomePage;

public class CashierDashboard extends JFrame implements ActionListener {
    JButton btnBilling, btnHistory, btnLogout;
    JLabel jl1;

    public CashierDashboard() {
        setLayout(null);

        jl1 = new JLabel("CASHIER DASHBOARD");
        jl1.setFont(new Font("Arial", Font.BOLD, 40));
        jl1.setBounds(350, 150, 600, 60);
        add(jl1);

        btnBilling = new JButton("New Billing");
        btnHistory = new JButton("View Sales History");
        btnLogout = new JButton("Logout");

        btnBilling.setFont(new Font("Arial", Font.BOLD, 25));
        btnHistory.setFont(new Font("Arial", Font.BOLD, 25));
        btnLogout.setFont(new Font("Arial", Font.BOLD, 25));

        btnBilling.setBackground(Color.YELLOW);
        btnHistory.setBackground(Color.YELLOW);
        btnLogout.setBackground(Color.YELLOW);

        btnBilling.setBounds(420, 300, 300, 60);
        btnHistory.setBounds(420, 400, 300, 60);
        btnLogout.setBounds(420, 500, 300, 60);

        add(btnBilling);
        add(btnHistory);
        add(btnLogout);

        btnLogout.addActionListener(this);

        getContentPane().setBackground(new Color(173, 216, 230));
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Cashier Dashboard");
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            new HomePage();
            dispose();
        }
    }
}


