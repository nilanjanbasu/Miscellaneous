package com.nilanjan.codex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Team {
	
	private int count;
	private float score;
	private File file;
	private String teamname;
	private List<Question> qlist;
	private boolean existedBefore = false;
	private boolean isValid = true;
	private Iterator<Question> it;
	private File log;
	private BufferedWriter buff_log;
	
	
	public Team(String name, File work_dir, List<Question> q_list){
		count =0 ;
		score = 0;
		teamname = name;
		qlist = q_list;
		it = q_list.iterator();
		
		file = new File(work_dir, name);
		if(file.exists()){
			System.out.println("Team already exists!");
			existedBefore = true;
			initLogger();
		} else {
			try{
				file.mkdir();
				initLogger();
			} catch(Exception e){
				System.out.println("Could not create the directory! ");
				isValid = false;
				e.printStackTrace();
			}			
		}		
	}
	
	private void initLogger(){
		this.log = new File(file, "log.txt");
		try {
			if(! this.log.exists()){
				this.log.createNewFile();	
			} else{
				System.out.println("File: "+ this.log.getAbsolutePath() +" already exists." );
			}
			this.buff_log = new BufferedWriter(new FileWriter(this.log, true));
			this.buff_log.write("Starting log for team: "+teamname+"\n");
			
		} catch (IOException e) {				
			e.printStackTrace();
			this.log = null;
			this.buff_log = null;
			System.out.println("Failed to create a log file for team: "+ teamname);
		}
	}
	
	public void addScore(float sc){
		score += sc;
	}
	
	public boolean hasNextQuestion(){
		return it.hasNext();
	}
	public final Question getNext(){
		count++;
		return it.next();
	}
	public void logTotalScore(){
		File score_file = new File(file, "score.txt");
		
		try {
			boolean st = score_file.createNewFile();
			if(!st)
				System.out.println("File: "+score_file.getAbsolutePath()+" already exists. Appending...");
		
			BufferedWriter out = new BufferedWriter(new FileWriter(score_file, true));
			out.write(this.teamname +": " + String.valueOf(score));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public float getScore(){
		return score;
	}
	public int getCount(){
		return this.count;
	}
	public void log(String text){
		if(buff_log != null){
			try {
				this.buff_log.write(this.teamname+":during question no-"+String.valueOf(this.count)+": [LOG]"+text+"\n");
				this.buff_log.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("buff_log is null");
		}
	}
	public void finishUpLogger(){
		try {
			this.buff_log.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
