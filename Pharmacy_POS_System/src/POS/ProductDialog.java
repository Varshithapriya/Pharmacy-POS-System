package POS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ProductDialog extends JDialog implements ActionListener {
    JTextField txtName, txtPrice, txtExpiry;
    JButton btnSave;
    Object[] productData; // null for add, filled for update
    ManageProductsFrame parent;

    // DB credentials
    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String DB_USER = "priya";
    private final String DB_PASS = "root";

    public ProductDialog(ManageProductsFrame parent, String title, Object[] productData) {
        super(parent, title, true);
        this.parent = parent;
        this.productData = productData;
        setSize(400, 400);
        setLayout(null);
        setLocationRelativeTo(parent);

        JLabel lblName = new JLabel("Product Name:");
        lblName.setBounds(30, 30, 150, 30);
        add(lblName);
        txtName = new JTextField();
        txtName.setBounds(150, 30, 200, 30);
        add(txtName);

        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setBounds(30, 80, 150, 30);
        add(lblPrice);
        txtPrice = new JTextField();
        txtPrice.setBounds(150, 80, 200, 30);
        add(txtPrice);

        JLabel lblExpiry = new JLabel("Expiry (yyyy-mm-dd):");
        lblExpiry.setBounds(30, 130, 200, 30);
        add(lblExpiry);
        txtExpiry = new JTextField();
        txtExpiry.setBounds(200, 130, 150, 30);
        add(txtExpiry);

        btnSave = new JButton("Save");
        btnSave.setBounds(120, 200, 150, 40);
        btnSave.addActionListener(this);
        add(btnSave);

        if (productData != null) {
            txtName.setText(productData[1].toString());
            txtPrice.setText(productData[2].toString());
            txtExpiry.setText(productData[3].toString());
        }

        setVisible(true);
    }

    public ProductDialog(ManageProductsFrame parent2, String title, Object productData2) {
		// TODO Auto-generated constructor stub
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        String name = txtName.getText().trim();
        double price = Double.parseDouble(txtPrice.getText().trim());
        java.sql.Date expiry = java.sql.Date.valueOf(txtExpiry.getText().trim());

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            if (productData == null) { // Add
                PreparedStatement ps = con.prepareStatement("INSERT INTO products (product_name, price, expiry_date) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setDate(3, expiry);
                ps.executeUpdate();
            } else { // Update
                int id = (int) productData[0];
                PreparedStatement ps = con.prepareStatement("UPDATE products SET product_name=?, price=?, expiry_date=? WHERE product_id=?");
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setDate(3, expiry);
                ps.setInt(4, id);
                ps.executeUpdate();
            }

            con.close();
            parent.loadProducts();
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving product:\n" + ex.getMessage());
        }
    }
}

