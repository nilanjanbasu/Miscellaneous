package com.nilanjan.codex;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import java.awt.Font;

@SuppressWarnings("serial")
public class Admin extends JFrame {

	private JPanel contentPane;
	JTextField questionFilePath = new JTextField(20);
	JTextField ruleFilePath      = new JTextField(20);
	JTextField workingDirectory = new JTextField(20);
	JPasswordField password         = new JPasswordField(10);
	JLabel     statusText       = new JLabel(" ");
	private Data data;	
	private Object lock;
	
	private static final int QUESTION = 1;
	private static final int RULEFILE = 2;
	private static final int WORK_DIR = 3;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Admin frame = new Admin(new Data(),new Object());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Admin(Data data1, Object lock1) {
		setResizable(false);
		
		this.data = data1;
		this.lock = lock1;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[]", "[]"));
		
		
		JButton questionBtn = getButton("Select",JFileChooser.FILES_ONLY, questionFilePath, Admin.QUESTION);
		JButton regSelect   = getButton("select", JFileChooser.FILES_ONLY, ruleFilePath, Admin.RULEFILE);
		JButton workDirectoryBtn = getButton("Select",JFileChooser.DIRECTORIES_ONLY, workingDirectory, Admin.WORK_DIR);
		final JPasswordField password_again = new JPasswordField(10);
		
		
		contentPane.add(new JLabel("Question File"));
		questionFilePath.setForeground(Color.BLACK);
		questionFilePath.setEditable(false);
		contentPane.add(questionFilePath);
		contentPane.add(questionBtn,"wrap");
		
		contentPane.add(new JLabel("Rules File"));
		ruleFilePath.setForeground(Color.BLACK);
		ruleFilePath.setEditable(false);
		contentPane.add(ruleFilePath);
		contentPane.add(regSelect,"wrap");
		
		contentPane.add(new JLabel("Working Directory"));
		workingDirectory.setForeground(Color.BLACK);
		workingDirectory.setEditable(false);
		contentPane.add(workingDirectory);
		contentPane.add(workDirectoryBtn,"wrap");
		contentPane.add(new JLabel("Password"));
		contentPane.add(password, "split 2");
		contentPane.add(password_again, "wrap");
		statusText.setForeground(Color.RED);
		statusText.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 15));
		contentPane.add(statusText, "span 3,wrap");
		
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		contentPane.add(horizontalStrut, "gapy unrel");
		
		JButton btnOkay = new JButton("Okay");
		contentPane.add(btnOkay, "align right ,gapy 20px");
		
		
		btnOkay.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(data.question_file == null) {
					statusText.setText("You need to select a Question File");
				} else if (data.working_dir == null) {
					statusText.setText("You need to select a working directory");
				} else if (data.rules_file == null){
					statusText.setText("You need to select a rules html file");
				} else{
					String p1 = new String(password.getPassword());
					String p2 = new String(password_again.getPassword());
					
					if(p1.equals("")){
						System.out.println(p1 + " "+ p2);
						statusText.setText("Enter a password");
					} else if(!p1.equals(p2)){
						statusText.setText("Passwords do not match");
					} else {
						data.password = p1;
						data.isValid = true;
						dispose();
						notifyLock();
						return;
					}					
				}
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				data.isValid = false;
				dispose();
				notifyLock();
			}
		});		
	}
	
	private JButton getButton(String title, final int mode, final JTextField txtfld, final int localmode) {
		JButton bt = new JButton(title);
		if(mode == JFileChooser.FILES_ONLY || mode== JFileChooser.DIRECTORIES_ONLY){
			bt.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(new File("."));
					fc.setFileSelectionMode(mode);
					int reval = fc.showOpenDialog(null);
					if(reval == JFileChooser.APPROVE_OPTION){
						File file = fc.getSelectedFile();
						txtfld.setText(file.getAbsolutePath());
						if(localmode == Admin.QUESTION){
							data.question_file = file;
						} else if( localmode == Admin.WORK_DIR){
							data.working_dir = file;
						} else if(localmode == Admin.RULEFILE) {
							data.rules_file = file; 
						}
					} else {
						return;
					}
				}
			});
			return bt;
		} else {
			return null;
		}
	}
	private void notifyLock(){
		synchronized (lock) {
			lock.notify();
		}
	}

}
