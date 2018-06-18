package de.hsrm.cs.emu.interton.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = -7949128791038641438L;
	private static MainFrame _instance = null;

	private MainFrame(String title) {
		super(title);
		setResizable(false);
	}
	
	public static MainFrame getInstance() {
		if(MainFrame._instance==null) {
			MainFrame._instance = new MainFrame("Emulator");
			MainFrame._instance.setDefaultCloseOperation(EXIT_ON_CLOSE);
			MainFrame._instance.setLayout(new BorderLayout());
			MainFrame._instance.setVisible(true);
		}
		return MainFrame._instance;
	}
	
	public void setPanel(JPanel panel) {
		MainFrame._instance.getContentPane().add(panel);
		MainFrame._instance.pack();
	}
}
