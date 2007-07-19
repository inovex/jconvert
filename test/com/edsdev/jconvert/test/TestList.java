package com.edsdev.jconvert.test;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class TestList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(600, 400);

		String[] data = { "one", "two", "free", "four" };
		JList dataList = new JList(data);

		JScrollPane scrollPane = new JScrollPane(dataList);
		scrollPane.setPreferredSize(new Dimension(300, 150));
		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		panel.add(scrollPane);

		JTabbedPane tabPane = new JTabbedPane();
		JPanel newPanel = new JPanel();
		newPanel.add(panel);
		tabPane.addTab("test", newPanel);
		JPanel otherPanel = new JPanel();
		otherPanel.add(panel);
		tabPane.addTab("Test 2", otherPanel);

		frame.getContentPane().add(tabPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
