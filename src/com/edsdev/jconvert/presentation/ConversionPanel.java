package com.edsdev.jconvert.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.edsdev.jconvert.presentation.component.NumericalTextField;

public class ConversionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private ConversionTypeData ctd;

    private JList list;

    private JList list2;

    private JLabel labelFromUnit;

    private JLabel labelToUnit;

    private NumericalTextField txtFrom;

    private JTextField txtTo;

    public void setConversionTypeData(ConversionTypeData newData) {
        ctd = newData;
        list.setListData(ctd.getAllFromUnits().toArray());
        list2.setListData(new Object[0]);

        setDefaultSelections();
    }

    public ConversionPanel(ConversionTypeData pCtd) {
        ctd = pCtd;
        init();
    }

    private String getSelectedValue(JList theList) {
        Object value = theList.getSelectedValue();
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    private void convert() {
        String fromUnit = getSelectedValue(list);
        String toUnit = getSelectedValue(list2);
        if (fromUnit != null && toUnit != null) {
            if (txtFrom.getText() != null && !txtFrom.getText().trim().equals("")) {
                double startValue = new Double(txtFrom.getText()).doubleValue();
                Double value = new Double(ctd.convert(startValue, fromUnit, toUnit));
                DecimalFormat fmt = new DecimalFormat(
                    "#.######################################################################################################");
                txtTo.setText(fmt.format(value));
            }
        }
    }

    private void init() {
        Dimension scrollPaneSize = new Dimension(280, 220);

        list = new JList(ctd.getAllFromUnits().toArray());
        JScrollPane scrollPanel = new JScrollPane(list);
        scrollPanel.setPreferredSize(scrollPaneSize);

        list2 = new JList();
        JScrollPane scrollPanel2 = new JScrollPane(list2);
        scrollPanel2.setPreferredSize(scrollPaneSize);

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // For now assume one tab
                String selectedValue = getSelectedValue((JList) e.getSource());
                List results = ctd.getToUnits(selectedValue);

                String list2Val = getSelectedValue(list2);

                list2.setListData(results.toArray());
                labelFromUnit.setText(selectedValue);

                if (list2Val != null) {
                    int i = 0;
                    for (int row = 0; row < list2.getModel().getSize(); row++) {
                        if (list2.getModel().getElementAt(row).equals(list2Val)) {
                            i = row;
                            break;
                        }
                    }
                    list2.setSelectedIndex(i);
                }
                convert();
            }
        });
        list2.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // For now assume one tab
                String selectedValue = getSelectedValue((JList) e.getSource());
                labelToUnit.setText(selectedValue);
                convert();
            }
        });
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
        
        listPanel.add(scrollPanel);
        listPanel.add(Box.createHorizontalStrut(5));
        listPanel.add(scrollPanel2);
        
        this.add(listPanel);

        JPanel conversionPanel = new JPanel();
        Dimension panelSize = new Dimension(580, 55);
        conversionPanel.setSize(panelSize);
        conversionPanel.setPreferredSize(panelSize);

        JLabel labelFrom = new JLabel("Conversion From");
        labelFrom.setBounds(5, 5, 100, 22);
        JLabel labelTo = new JLabel("Conversion To");
        labelTo.setBounds(5, 30, 100, 22);
        labelFromUnit = new JLabel("xxx");
        labelFromUnit.setBounds(330, 5, 90, 22);
        labelToUnit = new JLabel("yyy");
        labelToUnit.setBounds(330, 30, 90, 22);

        txtFrom = new NumericalTextField();
        txtFrom.setText("1");
        txtFrom.setBounds(110, 5, 200, 22);

        txtFrom.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                convert();
            }
        });

        txtTo = new JTextField();
        txtTo.setBounds(110, 30, 200, 22);
        txtTo.setEditable(false);

        conversionPanel.setLayout(null);
        conversionPanel.add(labelFrom);
        conversionPanel.add(labelTo);
        conversionPanel.add(labelFromUnit);
        conversionPanel.add(labelToUnit);
        conversionPanel.add(txtFrom);
        conversionPanel.add(txtTo);

        setDefaultSelections();

        this.add(conversionPanel);
    }

    private void setDefaultSelections() {
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
        if (list2.getModel().getSize() > 0) {
            list2.setSelectedIndex(0);
        }
    }
}
