package com.nilanjan.codex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Data {
	
	public File working_dir;
	public File rules_file;
	public File question_file;
	public String password;
	
	public boolean isValid = false;
}

class Question {
	
	public String question;
	public String imagePath;
	public String answer;
	public String hint;
	
	public Question(String q, String ans, String hint1, String img) {
		question = q;
		imagePath = img;
		answer = ans.toLowerCase().replaceAll("\\s+"," ").trim();
		hint = hint1;		
	}
	public void print(){
		System.out.println("Question: "+question+" Answer: "+answer+" hint: "+hint+"; img: "+imagePath);
	}
}

class QuestionWrap{
	public Question q;
	float total_score = 10;	
	boolean correctly_answered = false;
	
	public QuestionWrap(Question q1) {
		q = q1;
	}
	public boolean check(String ans) {
		if(ans.equals(q.answer)){
			correctly_answered = true;
			return true;
		} else {
			total_score -= .5;
			return false;
		}
	}
	
	public boolean hasHint(){
		return "".equals(q.hint);
	}
	
	public String getHint(){
		total_score -= 3;
		return q.hint;
	}
	
	public float getScore(){
		if(correctly_answered)
			return total_score;
		else
			return 0;
	}
	public String getQuestion(){
		return q.question;
	}
	public String getImage(){
		return q.imagePath;
	}
	public boolean hasImage(){
		return q.imagePath!="";
	}
	
}


class LoginData{	
	public String teamName;
}

class RoundData{
	public static final int LOGOUT = 1;
	public static final int COMPLETE = 2;
	public static final int END_SESSION = 3;
	
	public int status;
	public Team team;	
}


public class Main{
	
	private Data data;
	private List<Question> questions;

	
	public Main(){
		questions = new LinkedList<Question>();
		data = getAdminOptions();
	}
/* For test	
	private Main(boolean x){ //for test
		questions = new LinkedList<Question>();
//		data = getAdminOptions();
		populateQuestions(new File("src/question.xml"));
		System.out.println("Length: " + String.valueOf(questions.size()));
		for(Question q : questions){
			q.print();
		}
	}
*/
	public boolean start() {
		if(!data.isValid){
			System.out.println("Invalid data: You pressed Cancel. Exiting...");
			return false;
		}
		boolean isok = true;
		if( !data.question_file.canRead()){
			System.out.println("Can not read question file: "+ data.question_file.getAbsolutePath());
			isok = false;
		}
		if( !data.rules_file.canRead()){
			System.out.println("Can not read question file: "+ data.rules_file.getAbsolutePath());
			isok = false;
		}
		if( !data.working_dir.canWrite()){
			System.out.println("Can not write to working directory: "+ data.working_dir.getAbsolutePath());
			isok = false;
		}
		
		if(!isok)
			return false;
		
		populateQuestions(data.question_file);
		
		RoundData rdata;
		do{		
			String teamName = makeLoginWindow();
			if(teamName.equals(""))
				break;
			System.out.println("Teamname: " + teamName);
			rdata = startRound(teamName);	
			if(rdata.status == RoundData.COMPLETE){
				rdata.team.logTotalScore();
				rdata.team.log("At main thread: Score saved. Now finishing up!");
				rdata.team.finishUpLogger();
			}
		}while( rdata.status != RoundData.END_SESSION);
		
		return true;
	}
		
	private RoundData startRound(String teamName){
		Object lock = new Object();
		RoundData rdata = new RoundData();
		System.out.println("Now showing login window...");
		
		ContestFrame cf = new ContestFrame(rdata, lock, data, teamName, questions);
		cf.setVisible(true);
		
		synchronized(lock){			
			while(cf.isVisible()){
				try{
					lock.wait();
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		return rdata;
	}
	
	private String makeLoginWindow(){
		Object lock = new Object();
		LoginData l = new LoginData();
		System.out.println("Now showing login window...");
		
		LoginWindow login = new LoginWindow(l, lock, data.password);
		login.setFrameVisible(true);
		
		synchronized(lock){			
			while(login.isVisible()){
				try{
					lock.wait();
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		return l.teamName;
		
	}
	
	private void populateQuestions(File qFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(qFile);
			
			Element root = doc.getDocumentElement(); // Should return set
			NodeList list = root.getElementsByTagName("question");
			for(int i=0; i<list.getLength() ; ++i){
				Node node = list.item(i);
//				System.out.println(node.getNodeName());
			
				String q = getValueOfChildElementWithTagName(node,"statement");
				String ans = getValueOfChildElementWithTagName(node,"answer");
				String hint = getValueOfChildElementWithTagName(node, "hint");
				String img = getAttributeOfChildElementWithTagName(node, "statement", "img");
				
				questions.add(new Question(q, ans, hint, img));
//				System.out.println("o");
			}
						
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e){
			e.printStackTrace();
			System.exit(1);
		}		
	}
	
	private String getValueOfChildElementWithTagName(Node node, String tag){
		NodeList l = ((Element)node).getElementsByTagName(tag);
		if(l.getLength() > 0){
			String ans = ((Element)l.item(0)).getFirstChild().getTextContent();
			return ans;
		} else {
			return "";
		}
			
	}
	
	private String getAttributeOfChildElementWithTagName(Node node, String tag, String attr){
		NodeList l = ((Element)node).getElementsByTagName(tag);
		if(l.getLength() > 0){
			String ans = ((Element)l.item(0)).getAttribute(attr);
				return ans;
		} else {
			return "";
		}			
	}

	private Data getAdminOptions() {
		
		Object lock = new Object();
		System.out.println("Starting Codex Admin...");
		Data data = new Data();
		Admin admin = new Admin(data, lock);
		admin.setVisible(true);
		
		synchronized(lock){			
			while(admin.isVisible()){
				try{
					lock.wait();
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		return data;
		
	}
	
	public static void main(String[] args) {
		
//		Main app = new Main(true);
		
		Main app = new Main();
		app.start();
		
		
		
//		System.out.println(data.question_file.getAbsolutePath()+ " " + data.working_dir.getAbsolutePath()
//				+ " " + data.question_file.getAbsolutePath() + " " + data.password );
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					LoginWindow window = new LoginWindow();
//					window.setFrameVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}
}