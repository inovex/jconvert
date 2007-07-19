package com.edsdev.jconvert.presentation.component.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class MyListCellRenderer implements ListCellRenderer {

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		JPanel panel = new JPanel();
		panel.setBorder(noFocusBorder);

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		TestObject to = (TestObject) value;
		JLabel lbl1 = new JLabel(to.getName());
		lbl1.setPreferredSize(new Dimension(2000, 10));
		JLabel lbl2 = new JLabel(to.getGeneration() + "");
		if (to.getGeneration() > 2) {
			lbl2.setForeground(Color.RED);
		}

		lbl1.setOpaque(true);
		lbl2.setOpaque(true);
		panel.setOpaque(true);

		panel.add(lbl1);
		panel.add(lbl2);

		if (isSelected) {
			lbl1.setBackground(list.getSelectionBackground());
			lbl2.setBackground(list.getSelectionBackground());
			panel.setBackground(list.getSelectionBackground());
		} else {
			lbl1.setBackground(list.getBackground());
			lbl2.setBackground(list.getBackground());
			panel.setBackground(list.getBackground());
		}

		lbl1.setEnabled(list.isEnabled());
		lbl2.setEnabled(list.isEnabled());
		lbl1.setFont(list.getFont());
		lbl2.setFont(list.getFont());

		if (cellHasFocus) {
			panel.setBorder(UIManager
					.getBorder("List.focusCellHighlightBorder"));
		}

		return panel;
	}

}
