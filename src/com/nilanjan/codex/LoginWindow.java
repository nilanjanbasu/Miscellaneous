package com.nilanjan.codex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.SystemColor;

public class LoginWindow {

	private JFrame frame;
	private JPanel panel;
	private JLabel lblNewLabel;
	private JTextField textField;
	private JButton loginbtn;
	private JButton btnquit;
	
	private LoginData log_dat;
	private Object lock;
	private String password;
	/**
	 * Create the application.
	 */
	public LoginWindow(LoginData login_dt, Object lock1, String password1) {
		log_dat = login_dt;
		lock = lock1;
		this.password = password1;
		initialize();
	}
	
	
	public void setFrameVisible(boolean st){
		frame.setVisible(st);
	}

	public boolean isVisible(){
		return frame.isVisible();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); //maximise window
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[]{1.0};
		panel.setLayout(gbl_panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.window);
		panel_1.setPreferredSize(new Dimension(300, 200));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		GridLayout gl_panel_1 = new GridLayout(2,2);
		gl_panel_1.setVgap(2);
		gl_panel_1.setHgap(2);
		panel_1.setLayout(gl_panel_1);
		
		lblNewLabel = new JLabel("Username");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		lblNewLabel.setMinimumSize(new Dimension(72, 10));
		lblNewLabel.setMaximumSize(new Dimension(72, 10));
		lblNewLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setMinimumSize(new Dimension(4, 15));
		textField.setFont(new Font("Dialog", Font.PLAIN, 18));
		textField.setPreferredSize(new Dimension(4, 10));
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton loginbtn = new JButton("Login");
		loginbtn.setPreferredSize(new Dimension(70, 15));
		loginbtn.setFont(new Font("Dialog", Font.BOLD, 18));
		panel_1.add(loginbtn);
		
		btnquit = new JButton("Quit");
		btnquit.setPreferredSize(new Dimension(63, 15));
		btnquit.setFont(new Font("Dialog", Font.BOLD, 18));
		panel_1.add(btnquit);
		
		loginbtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String team_name = textField.getText();
				if(team_name.length() >0)
				{
					log_dat.teamName = textField.getText();
					frame.dispose();	
					notifyLock();
				}
			}
		});
		
		btnquit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				frame.setAlwaysOnTop(false);
				String entered_password = JOptionPane.showInputDialog("Enter the password-");
				if(password.equals(entered_password)){
					log_dat.teamName = "";
					frame.dispose();
					notifyLock();
				}else{
					frame.setAlwaysOnTop(true);
				}
			}
		});
		
		
	}
	
	private void notifyLock(){
		synchronized (lock) {
			lock.notify();
		}
	}

}
