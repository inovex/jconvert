package com.edsdev.jconvert.presentation;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.edsdev.jconvert.presentation.component.ConvertListCellRenderer;
import com.edsdev.jconvert.presentation.component.ConvertListModel;
import com.edsdev.jconvert.presentation.component.NumericalTextField;
import com.edsdev.jconvert.util.Messages;

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
        list.setModel(new ConvertListModel(ctd.getAllFromUnits()));
        list2.setModel(new ConvertListModel(new ArrayList()));

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
        if (value instanceof ConversionUnitData) {
            return ((ConversionUnitData) value).getUnit();
        }
        return value.toString();
    }

    private String getSelectedTranslatedValue(JList theList) {
        Object value = theList.getSelectedValue();
        if (value == null) {
            return null;
        }
        if (value instanceof ConversionUnitData) {
            return ((ConversionUnitData) value).getTranslatedUnit();
        }
        return value.toString();
    }

    private void setSelectedStringValue(JList theList, String value) {
        for (int i = 0; i < theList.getModel().getSize(); i++) {
            ConversionUnitData data = (ConversionUnitData) theList.getModel().getElementAt(i);
            if (data.getUnit().equals(value)) {
                theList.setSelectedIndex(i);
                break;
            }
        }
    }

    private int getGenerationAge(JList theList) {
        Object value = theList.getSelectedValue();

        if (value instanceof ConversionUnitData) {
            return ((ConversionUnitData) value).getGenerationAge();
        }
        return 0;
    }

    private void convert() {
        convert(false);
    }

    public String getFromValue() {
        return txtFrom.getText();
    }

    public void setFromValue(String value) {
        txtFrom.setText(value);
    }

    public String getFromUnit() {
        return getSelectedValue(list) + "";
    }

    public String getToUnit() {
        return getSelectedValue(list2) + "";
    }

    public void setFromUnit(String value) {
        setSelectedStringValue(list, value);
    }

    public void setToUnit(String value) {
        setSelectedStringValue(list2, value);
    }

    private void convert(boolean reverse) {
        String fromUnit = getSelectedValue(list);
        String toUnit = getSelectedValue(list2);
        if (fromUnit != null && toUnit != null) {
            DecimalFormat fmt = new DecimalFormat();
            fmt.setMaximumFractionDigits(30);
            if (reverse) {
                if (txtTo.getText() != null && !txtTo.getText().trim().equals("")) {
                    double startValue = new Double(txtTo.getText()).doubleValue();
                    Double value = new Double(ctd.convert(startValue, toUnit, fromUnit));
                    txtFrom.setText(fmt.format(value));
                }
            } else {
                if (txtFrom.getText() != null && !txtFrom.getText().trim().equals("")) {
                    double startValue = new Double(txtFrom.getText()).doubleValue();
                    Double value = new Double(ctd.convert(startValue, fromUnit, toUnit));
                    txtTo.setText(fmt.format(value));
                }
            }
        }
    }

    private void init() {
        Dimension scrollPaneSize = new Dimension(280, 220);

        list = new JList();
        list.setCellRenderer(new ConvertListCellRenderer());
        list.setModel(new ConvertListModel(ctd.getAllFromUnits()));
        JScrollPane scrollPanel = new JScrollPane(list);
        scrollPanel.setPreferredSize(scrollPaneSize);
        scrollPanel.setBorder(BorderFactory.createTitledBorder(Messages.getResource("fromUnitLabel")));

        list2 = new JList();
        list2.setModel(new ConvertListModel(new ArrayList()));
        list2.setCellRenderer(new ConvertListCellRenderer());

        JScrollPane scrollPanel2 = new JScrollPane(list2);
        scrollPanel2.setPreferredSize(scrollPaneSize);
        scrollPanel2.setBorder(BorderFactory.createTitledBorder(Messages.getResource("toUnitLabel")));

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // For now assume one tab
                String selectedValue = getSelectedValue((JList) e.getSource());
                List results = ctd.getToUnits(selectedValue);

                String list2Val = getSelectedValue(list2);

                list2.setModel(new ConvertListModel(results));
                list2.setCellRenderer(new ConvertListCellRenderer());
                labelFromUnit.setText(getSelectedTranslatedValue((JList) e.getSource()));

                if (list2Val != null) {
                    int i = 0;
                    for (int row = 0; row < list2.getModel().getSize(); row++) {
                        ConversionUnitData cud = (ConversionUnitData) list2.getModel().getElementAt(row);
                        if (cud.getUnit().equals(list2Val)) {
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
                String selectedValue = getSelectedTranslatedValue((JList) e.getSource());
                int genAge = getGenerationAge((JList) e.getSource());
                if (genAge >= 2) {
                    labelToUnit.setText(selectedValue + " (" + Messages.getResource("generatedConversion") + "("
                            + genAge + "))");
                } else {
                    labelToUnit.setText(selectedValue);
                }
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

        JLabel labelFrom = new JLabel(Messages.getResource("conversionFromLabel"));
        labelFrom.setBounds(5, 5, 100, 22);
        JLabel labelTo = new JLabel(Messages.getResource("conversionToLabel"));
        labelTo.setBounds(5, 30, 100, 22);
        labelFromUnit = new JLabel("xxx");
        labelFromUnit.setBounds(330, 5, 400, 22);
        labelToUnit = new JLabel("yyy");
        labelToUnit.setBounds(330, 30, 400, 22);

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
        //        txtTo.setEditable(false);
        txtTo.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                convert(true);
            }
        });

        conversionPanel.setLayout(null);
        conversionPanel.add(labelFrom);
        conversionPanel.add(labelTo);
        conversionPanel.add(labelFromUnit);
        conversionPanel.add(labelToUnit);
        conversionPanel.add(txtFrom);
        conversionPanel.add(txtTo);

        Dimension dim = new Dimension(600, 57);
        conversionPanel.setPreferredSize(dim);
        conversionPanel.setMinimumSize(dim);
        conversionPanel.setMaximumSize(new Dimension(1200, 57));

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
