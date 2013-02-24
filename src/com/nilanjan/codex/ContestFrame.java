package com.nilanjan.codex;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class ContestFrame extends JFrame {

	private JPanel contentPane;
	private Object lock;
	private RoundData rounddt; 
	private Data otherData;
	private String teamName;
	private List<Question> questions_list;
	private ContestMain contestPane;
	
	private Team cur_team; 
	
	public static final String INFOPANEL = "INFO";
	public static final String CONTESTPANEL = "CONTEST"; 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
//		JOptionPane.showMessageDialog(null,	"choose", "choose one", JOptionPane.INFORMATION_MESSAGE);
		
		Question q = new Question("Hello?", "Ok", "a hint", null);
		List<Question> l = new LinkedList<Question>();
		l.add(q);			
		ContestFrame frame = new ContestFrame(new RoundData(), new Object(), new Data(), "Cipherx0001",l);
		frame.makeRulesPageVisible();
		frame.setVisible(true);
//				
	}

	/**
	 * Create the frame.
	 */
	public ContestFrame(RoundData rd, Object lock1, Data otherData1, String teamName1, List<Question> questions1) {
		
		rounddt = rd;
		lock = lock1;
		otherData = otherData1;
		teamName = teamName1;
		questions_list = questions1;
		
		setExtendedState(ContestFrame.MAXIMIZED_BOTH); //maximise window
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel cards = new JPanel(new CardLayout());
		setContentPane(cards);
		contentPane = cards;
		
		cards.add(makeRulesPage(), ContestFrame.INFOPANEL);
		cards.add(makeQuestionsPage(), ContestFrame.CONTESTPANEL);
		
		
	}
	
	public JPanel makeQuestionsPage(){
		JPanel pane = new JPanel();

		pane.setLayout(new BorderLayout(0, 0));
		
		ContestMain cm = new ContestMain();
		contestPane = cm;
		pane.add(cm, BorderLayout.CENTER);
		
		Component verticalStrut = Box.createVerticalStrut(50);
		JLabel teamid = new JLabel("Team: "+teamName);
		teamid.setFont(new Font("Dialog", Font.BOLD, 25));
				
		JPanel details = new JPanel();
		pane.add(details, BorderLayout.NORTH);
		
		details.add(teamid);
		details.add(verticalStrut);
		
		Component verticalStrut_1 = Box.createVerticalStrut(100);
		pane.add(verticalStrut_1, BorderLayout.SOUTH);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		pane.add(horizontalStrut, BorderLayout.WEST);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		pane.add(horizontalStrut_1, BorderLayout.EAST);
		
		return pane;
		
	}
	
	public JPanel makeRulesPage(){
		
		JPanel infoPane = new JPanel();
//		setContentPane(contentPane);
		infoPane.setLayout(new BorderLayout(0, 0));
		
		Intro intro = new Intro(null,otherData.rules_file);
		infoPane.add(intro, BorderLayout.CENTER);
		
		Component verticalStrut = Box.createVerticalStrut(50);
		JLabel teamid = new JLabel("Team: "+teamName);
		teamid.setFont(new Font("Dialog", Font.BOLD, 25));
				
		JPanel details = new JPanel();
		infoPane.add(details, BorderLayout.NORTH);
		
		details.add(teamid);
		details.add(verticalStrut);
		
		Component verticalStrut_1 = Box.createVerticalStrut(100);
		infoPane.add(verticalStrut_1, BorderLayout.SOUTH);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		infoPane.add(horizontalStrut, BorderLayout.WEST);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		infoPane.add(horizontalStrut_1, BorderLayout.EAST);
		
		intro.addBackButtonActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				dispose();
				rounddt.status = RoundData.LOGOUT;
				notifyLock();		
			}
		});
		
		intro.addStartButtonActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				initializeTeam();				
				CardLayout cl = (CardLayout)contentPane.getLayout();
				cl.show(contentPane, ContestFrame.CONTESTPANEL);
			}
		});
		
		return infoPane;
		
	}
	
	private void disposeAndNotify(RoundData rd){
		rounddt.status = rd.status;
		rounddt.team = rd.team;
		dispose();
		notifyLock();
	}
	
	private void initializeTeam(){
		cur_team = new Team(teamName, otherData.working_dir, questions_list);
		contestPane.cur_question = new QuestionWrap(cur_team.getNext());
		contestPane.updateAll();
	}
	
	public void makeRulesPageVisible(){
		
	}
	private void notifyLock(){
		synchronized (lock) {
			lock.notify();
		}		
	}
	
	@SuppressWarnings("serial")
	public class ContestMain extends JPanel {

		JButton checkbt;
		JButton hintbt;
		JTextField answer;
//		JLabel hintlabel;
		JEditorPane hint_editor;
		JEditorPane questions_editor;
		JButton submitbtn;
		JButton skipbtn;
		ImagePanel img;
		
		QuestionWrap cur_question= null;
		
		/**
		 * Create the panel.
		 */
		public ContestMain() {
//			setMaximumSize(new Dimension(600, 400));
//			setLayout(new MigLayout("fill", "[fill]5[fill]", "[fill]10[fill]"));
//			setBackground(Color.red);
			
			setLayout(new BorderLayout());
			
			JPanel texts = new JPanel(new MigLayout("fill", "[grow][][]", "[fill, grow]10[]10[]"));
			img          = new ImagePanel();
			
			texts.setPreferredSize(new Dimension(300, 200));
			img.setBackground(Color.white);
			img.setPreferredSize(new Dimension(400, 180));
			
//			add(texts, "growy");
//			add(img, "wrap");
			
			add(texts, BorderLayout.CENTER);
			add(img, BorderLayout.EAST);
			
			JPanel btnPane = new JPanel(new MigLayout("fill","",""));
			submitbtn = new JButton("Submit");
			skipbtn = new JButton("Skip");
			
			btnPane.add(skipbtn, "aligny top");
			btnPane.add(submitbtn, "aligny top, alignx right, wrap");		
			
//			add(btnPane, "span 2");
			add(btnPane, BorderLayout.SOUTH);
			
			questions_editor = new JEditorPane();
			questions_editor.setEditable(false);		
			questions_editor.setFont(new Font("Dialog", Font.BOLD, 14));
			
			JScrollPane editorScrollPane = new JScrollPane(questions_editor);
			editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			answer = new JTextField(100);
			answer.setMinimumSize(new Dimension(6, 19));
			checkbt = new JButton("Check");
			hintbt  = new JButton("Hint");
			
			hint_editor = new JEditorPane();
			hint_editor.setEnabled(false);
			hint_editor.setEditable(false);
			hint_editor.setFont(new Font("Dialog", Font.BOLD, 18));
			
			texts.add(editorScrollPane, "span 3, wrap, w 100%");
			texts.add(hint_editor, "span 3, wrap, w 100%");
			texts.add(answer, "aligny top");
			texts.add(checkbt, "aligny top");
			texts.add(hintbt, "aligny top, wrap");
			
			
			
			submitbtn.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					cur_team.log("Pressed Submit Button");
					submitAction();					
				}
			});
			
			skipbtn.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					cur_team.log("Pressed Skip Button");
					if(cur_team.hasNextQuestion()){
						cur_question = new QuestionWrap(cur_team.getNext());
						updateAll();
					} 
				}
			});
			
			hintbt.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					final String s = cur_question.getHint();
					cur_team.log("Pressed Hint Button");
					EventQueue.invokeLater(new Runnable() {						
						public void run() {
							hint_editor.setText(s);
							hint_editor.setEnabled(true);
							hintbt.setEnabled(false);
						}
					});
				}
			});
			
			checkbt.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) { //VERY IMP FUNCTION TAKE UTMOST CARE
					String ans = answer.getText().toLowerCase().replaceAll("\\s+"," ").trim();
					
					cur_team.log("Entered: "+answer.getText()+" || Processed to be: "+ ans);
					
					if(cur_question.check(ans)){
						cur_team.log("Answer: ("+ ans+ ") accepted");
						
						cur_team.addScore(cur_question.total_score);
						if(cur_team.hasNextQuestion()){
							cur_question = new QuestionWrap(cur_team.getNext());
							updateAll();
						} else{
							submitAction();
						}
					} else {
						answer.setBackground(Color.orange);
						cur_team.log("Answer: ("+ ans+ ") rejected");
					}
				}
			});
		}
		
		public void updateAll(){
			EventQueue.invokeLater(new Runnable() {
				
				public void run() {
					questions_editor.setText(cur_question.getQuestion());
					hint_editor.setText("");
					if(!cur_question.hasHint())
						hintbt.setEnabled(true);
					else
						hintbt.setEnabled(false);
					answer.setText("");
					answer.setBackground(Color.white);
					if(!cur_team.hasNextQuestion()){
						skipbtn.setEnabled(false);
					}
					
					if(cur_question.hasImage()){
						File f = getImagePath(otherData.question_file, cur_question.getImage());
						img.setImage(f);
					} else {
						img.clearImage();
					}
					//TODO Image
				}
			});
			
			
		}
		private void submitAction(){
			cur_team.log("Submitting results to main thread... at ContestMain.submitAction()");
			
			RoundData rd = new RoundData();
			rd.status = RoundData.COMPLETE;
			rd.team = cur_team;
			setAlwaysOnTop(false);
			JOptionPane.showMessageDialog(null,	"Total score" + String.valueOf(cur_team.getScore()), "choose one", JOptionPane.INFORMATION_MESSAGE);		
			disposeAndNotify(rd);
		}
	}
	
	private File getImagePath(File wd, String imgrelpath){
		File parent = wd.getParentFile();
		File img = new File(parent, imgrelpath);
		return img;
	}
	
	@SuppressWarnings("serial")
	class ImagePanel extends JPanel{
		
		BufferedImage buff;
		
		public void setImage(File img){
			if(img.exists() && img.canRead()){
				try {
					buff = ImageIO.read(img.toURI().toURL());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				repaint();
			}
				
		}
		
		public void clearImage(){
			buff = null;
			repaint();
		}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			int h = getHeight();
			int w = getWidth();	
			Graphics2D g2 = (Graphics2D) g; 
			if(buff == null){						
				buff = (BufferedImage)createImage(w, h);
				Graphics2D gc = buff.createGraphics();
				gc.setColor(Color.WHITE);
				gc.fillRect(0, 0, w, h);				
			}else {
				BufferedImage im= new BufferedImage(w, h, buff.getType());
				Graphics2D gx = im.createGraphics();
				gx.drawImage(buff, 0, 0, w, h, null);
				gx.dispose();
				buff = im;
			}
			g2.drawImage(buff, null, 0 , 0);
		}
	}

}
