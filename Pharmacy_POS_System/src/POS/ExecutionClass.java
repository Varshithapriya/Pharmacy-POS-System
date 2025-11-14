package POS;

import javax.swing.JFrame;

public class ExecutionClass {

	public static void main(String[] args) {
		
		MainFrame mf=new MainFrame();
		mf.setTitle("Main Frame");
		mf.setSize(1200,1200);
		mf.setVisible(true);
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
