package POS;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import POS_Frames.HomePage;



public class MainFrame extends JFrame implements ActionListener {
	
	JButton jb;
	ImageIcon ii1,ii2;
	JLabel bg1;

	public MainFrame() {
		
		jb=new JButton("MY APP");
		jb.setBackground(Color.orange);
		
		setLayout(null);
		
		jb.setFont(new Font("Arial",Font.BOLD,24));
			
		ii1=new ImageIcon("C:\\Users\\priyavarshitha\\OneDrive\\Desktop\\javatraining\\Pharmacy_POS_System\\Assests\\capsule1.jpg");
		bg1=new JLabel(ii1);   
		
		//ii2=new ImageIcon("C:\\Users\\priyavarshitha\\OneDrive\\Desktop\\javatraining\\Pharmacy_POS_System\\Assests\\capsule.jpg");
		//bg2=new JLabel(ii2); 
		
		
		jb.setBounds(430,520,250,100);
		
		bg1.setBounds(350, 150, 450, 300);
		//bg2.setBounds(0, 0, 1200, 1200);
		
		
		add(jb);
		add(bg1);
		//add(bg2);
		
		getContentPane().setBackground(new Color(173, 216, 230));
		
		jb.addActionListener(this);
	
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(jb)) {
			HomePage hp=new HomePage();
			hp.setTitle("HomePage");
			hp.setSize(1200,1200);
			hp.setVisible(true);
		}
	}

	
	


}

