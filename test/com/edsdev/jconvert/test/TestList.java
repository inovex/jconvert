package com.edsdev.jconvert.test;

import java.awt.Color;
import java.awt.Dimension;
import java.math.BigInteger;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * @author Ed Sarrazin
 */
public class TestList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    BigInteger b1 = new BigInteger("16");
        BigInteger b2 = new BigInteger("32");
        System.out.println(b1.gcd(b2));
        System.out.println(b2.gcd(b1));
        
        BigInteger[] bis = b1.divideAndRemainder(b2);
        System.out.println(bis[0] + " " + bis[1]);
        bis = b2.divideAndRemainder(b1);
        System.out.println(bis[0] + " " + bis[1]);
        
        
	    
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
