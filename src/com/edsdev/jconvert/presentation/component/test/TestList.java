package com.edsdev.jconvert.presentation.component.test;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class TestList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		JPanel panel = new JPanel();
		panel.setSize(640, 480);
		frame.getContentPane().add(panel);

		panel.setLayout(null);
		JList list = new JList();
		list.setBounds(5, 5, 500, 300);

		list.setModel(new MyListModel(getData()));
		list.setCellRenderer(new MyListCellRenderer());
		
		panel.add(list);

		frame.setVisible(true);
	}

	private static List getData() {
		List rv = new ArrayList();
		TestObject obj = new TestObject();
		obj.setName("Name1");
		obj.setGeneration(0);
		rv.add(obj);

		obj = new TestObject();
		obj.setName("Name2");
		obj.setGeneration(1);
		rv.add(obj);

		obj = new TestObject();
		obj.setName("Name3");
		obj.setGeneration(2);
		rv.add(obj);

		obj = new TestObject();
		obj.setName("Name4");
		obj.setGeneration(3);
		rv.add(obj);

		obj = new TestObject();
		obj.setName("Name5");
		obj.setGeneration(4);
		rv.add(obj);

		return rv;
	}

}
