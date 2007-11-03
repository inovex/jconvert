package com.edsdev.jconvert.presentation.component;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 * This is a renderer specifically for displaying and updating hidden and visible tabs within the application
 * 
 * @author Ed Sarrazin Created on Nov 2, 2007 4:03:38 PM
 */
public class VisibleTabsListCellRenderer extends JPanel implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        //right now I plan to put an x and an o in front of the string to designate checked or not
        //this is cheap and does not require me to set up another class to create a model. Just to save
        //space, not time.

        this.removeAll();
        boolean checked = value.toString().startsWith("x");

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JCheckBox box = new JCheckBox();
        box.setPreferredSize(new Dimension(20, 20));
        box.setMaximumSize(new Dimension(20, 20));
        box.setSelected(checked);
        this.add(box);

        //this piece of code absolutely sucks - I am embarassed. I did not see a way to do a List Editor.
        //basically this just changes the model if an item is selected - toggles
        if (isSelected) {
            if (checked) {
                value = "o" + value.toString().substring(1);
            } else {
                value = "x" + value.toString().substring(1);
            }
            Vector newList = new Vector();
            for (int i = 0; i < list.getModel().getSize(); i++) {
                if (i == index) {
                    newList.add(value);
                } else {
                    newList.add(list.getModel().getElementAt(i));
                }
            }
            //don't force the list to repaint while we are still painting this component
            SwingUtilities.invokeLater(new RebuildData(list, newList));
        }

        JLabel label = new JLabel(value.toString().substring(1));
        this.add(label);

        label.setBackground(list.getBackground());
        box.setBackground(list.getBackground());
        this.setBackground(list.getBackground());
        this.setForeground(list.getForeground());
        box.setForeground(list.getForeground());
        label.setForeground(list.getForeground());

        this.setFont(list.getFont());
        this.setOpaque(true);

        return this;
    }

    private class RebuildData implements Runnable {
        JList list;
        Vector newList;

        public RebuildData(JList pList, Vector pNewList) {
            list = pList;
            newList = pNewList;
        }

        public void run() {
            list.setListData(newList);
        }
    }
}
