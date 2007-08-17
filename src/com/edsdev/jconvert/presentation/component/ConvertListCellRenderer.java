package com.edsdev.jconvert.presentation.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.edsdev.jconvert.presentation.ConversionUnitData;

public class ConvertListCellRenderer extends JLabel implements ListCellRenderer {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        
        ConversionUnitData cud = (ConversionUnitData) value;

        String text = "";
        if (cud.getGenerationAge() >= 2) {
            text = "*** ";
        }
        text += cud.getUnit();
        if (cud.getUnitAbbrev() != null && !cud.getUnitAbbrev().trim().equals("")) {
            text += " - " + cud.getUnitAbbrev();
        }
        this.setText(text);
        this.setOpaque(true);
        
        if (isSelected) {
            if (cud.getGenerationAge() >= 2) {
                this.setBackground(Color.YELLOW);
                this.setForeground(list.getForeground());
            } else {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
            }
        } else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }

        this.setEnabled(list.isEnabled());
        this.setFont(list.getFont());

        if (cellHasFocus) {
            this.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
        }

        return this;
    }

}
