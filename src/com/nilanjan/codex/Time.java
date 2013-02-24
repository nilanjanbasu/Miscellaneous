package com.nilanjan.codex;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

interface TimeListener {
	public void timeActionPerformed(final TimeEvent ev);
}

class TimeEvent{
	boolean isEnd = false;
	int minutes;
	int sec;
	public TimeEvent(int min, int sec1, boolean x) {
		minutes = min;
		isEnd = x;
		sec = sec1;
	}
}


public class Time {
	
	long timeInMillisecs;
	public Time(long totalTime){
		this.timeInMillisecs = totalTime;
	}
	
	public void decrement(long t){
		timeInMillisecs -= t;
	}
	
	public long getHourRemaining(){
		return timeInMillisecs / (3600 * 1000);
	}
	public long getMinuteRemaining(){
		
		long rem = timeInMillisecs % (3600 * 1000);
		return timeInMillisecs / (long)(60 * 1000);
	}
	
	public long getSecondsRemaining(){
		long rem = timeInMillisecs % (60 * 1000);
		return rem / (1000);
	}
}

class MyTimer{
		
	private Time time;
	private long total;
	private long interval;
	private Timer timer;
	
	private long count = 0;
	
	List<TimeListener> listeners = new LinkedList<TimeListener>();
	
	public MyTimer(long total1, long interval1){ //interval in milli secs
		timer = new Timer();
		total = total1;
		interval = interval1;
		time = new Time(total*interval1);
	}
	
	public void addListener(TimeListener l){
		listeners.add(l);
	}
	
	public void start(){
		this.timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				time.decrement(interval);
				if(count < total){
					for(TimeListener t: listeners){
						t.timeActionPerformed(new TimeEvent((int)time.getMinuteRemaining(),(int)time.getSecondsRemaining(), false));
					}
				} else {
					for(TimeListener t:listeners){
						t.timeActionPerformed(new TimeEvent((int)time.getMinuteRemaining(),(int)time.getSecondsRemaining(), true));
					}
					timer.cancel();
				}
				count++;
				
			}
		}, interval , interval  );
	}	
}