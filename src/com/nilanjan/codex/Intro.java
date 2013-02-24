package com.nilanjan.codex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class Intro extends JPanel {
	
	private List<String> l;
	private JButton bt_next;
	private JButton bt_back;
	
	/**
	 * Create the panel.
	 */
	public Intro(List<String> rules, File ruleHTML) {
		l = rules;
		setMaximumSize(new Dimension(600, 400));
		setBackground(Color.white);
		JEditorPane editor = new JEditorPane();
		editor.setPreferredSize(new Dimension(400, 300));
		editor.setEditable(false);
		try {
			setLayout(new MigLayout());
			editor.setPage(ruleHTML.toURI().toURL());
			JScrollPane editorScrollPane = new JScrollPane(editor);
			editorScrollPane.setPreferredSize(new Dimension(400, 300));
			editorScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//			editorScrollPane.setSize(400, 100);
			add(editorScrollPane, "dock center, wrap");
			
			bt_next = new JButton("Start");
			bt_back = new JButton("Back");
			
			add(bt_next,"alignx center, split 2");
			add(bt_back,"alignx left, wrap");
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void addBackButtonActionListener(ActionListener x){
		this.bt_back.addActionListener(x);
	}
	
	public void addStartButtonActionListener(ActionListener a){
		this.bt_next.addActionListener(a);
	}

}
