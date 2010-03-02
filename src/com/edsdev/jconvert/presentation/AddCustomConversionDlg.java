package com.edsdev.jconvert.presentation;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.edsdev.jconvert.domain.Conversion;
import com.edsdev.jconvert.util.Logger;
import com.edsdev.jconvert.util.Messages;
import com.edsdev.jconvert.util.ResourceManager;

/**
 * @author Ed Sarrazin Created on Sep 13, 2007 5:14:10 PM
 */
public class AddCustomConversionDlg extends JDialog {

    private final int WIDTH = 400;

    private final int HEIGHT = 450;

    private final String FILE_NAME = "convert_custom.dat";

    private JTextField txtConversionType = new JTextField();

    private JTextField txtFrom = new JTextField();

    private JTextField txtFromAbbrev = new JTextField();

    private JTextField txtTo = new JTextField();

    private JTextField txtToAbbrev = new JTextField();

    private JTextField txtFactor = new JTextField();

    private JTextField txtOffset = new JTextField();

    private JLabel lblFormula = new JLabel(Messages.getResource("conversionFormula"));

    private JLabel lblExample = new JLabel();

    private JButton okButton = new JButton(Messages.getResource("addButton"));

    private JButton cancelButton = new JButton(Messages.getResource("closeButton"));

    private ArrayList listeners = new ArrayList();

    private JTextArea txtData = new JTextArea();

    private JScrollPane scrollData = new JScrollPane();

    private static final Logger log = Logger.getInstance(AddCustomConversionDlg.class);

    public AddCustomConversionDlg(Frame parent) {
        super(parent);
        this.setTitle(Messages.getResource("addCustomTitle"));

        if (parent != null) {
            int x = parent.getX() + (parent.getWidth() - WIDTH) / 2;
            int y = parent.getY() + (parent.getHeight() - HEIGHT) / 2;

            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }

            setBounds(x, y, WIDTH, HEIGHT);
        } else {
            setBounds(10, 10, WIDTH, HEIGHT);
        }

        init();

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }

    private void init() {
        this.getContentPane().setLayout(null);
        addLabel(Messages.getResource("conversionTypeLabel"), 5, 5, 100, 22);
        addComponent(txtConversionType, 110, 5, 200, 22);
        this.getContentPane().add(txtConversionType);

        addLabel(Messages.getResource("fromUnitLabel"), 5, 30, 100, 22);
        addComponent(txtFrom, 110, 30, 150, 22);
        addLabel(Messages.getResource("abbrevLabel"), 270, 30, 50, 22);
        addComponent(txtFromAbbrev, 330, 30, 50, 22);

        addLabel(Messages.getResource("toUnitLabel"), 5, 55, 100, 22);
        addComponent(txtTo, 110, 55, 150, 22);
        addLabel(Messages.getResource("abbrevLabel"), 270, 55, 50, 22);
        addComponent(txtToAbbrev, 330, 55, 50, 22);

        addLabel(Messages.getResource("factorLabel"), 5, 80, 100, 22);
        addComponent(txtFactor, 110, 80, 150, 22);

        addLabel(Messages.getResource("offsetLabel"), 5, 105, 100, 22);
        addComponent(txtOffset, 110, 105, 150, 22);
        txtOffset.setText("0");

        addComponent(okButton, 5, 180, 100, 25);
        addComponent(cancelButton, 110, 180, 100, 25);

        addComponent(lblFormula, 5, 130, 380, 22);
        addComponent(lblExample, 5, 155, 380, 22);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addConversion();
            }
        });
        this.getRootPane().setDefaultButton(okButton);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });

        scrollData.getViewport().add(txtData);
        scrollData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addComponent(scrollData, 5, 210, 380, 200);
        initializeData();
    }

    private void addKeyListenerToTextField(Component c) {
        if (c instanceof JTextField) {
            JTextField textField = (JTextField) c;
            textField.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    updateExample();
                }
            });
        }
    }

    private void updateExample() {
        String result = Messages.getResource("questionMark");
        double offset = 0;
        try {
            offset = new Double(txtOffset.getText()).doubleValue();
            Conversion c = Conversion.createInstance(txtFrom.getText(), "", txtTo.getText(), "", txtFactor.getText(),
                offset);
            result = new Double(c.convertValue(10, txtFrom.getText())).toString();
        } catch (Exception e) {
            //throw away, we are just trying all the time.
        }
        lblExample.setText("10 " + txtFrom.getText() + " " + Messages.getResource("multiplySymbol") + " "
                + txtFactor.getText() + " " + Messages.getResource("plusSign") + " " + txtOffset.getText() + " "
                + Messages.getResource("equalsSign") + " " + result + " " + txtTo.getText());
    }

    private String getFilePath() {
        String jarPath = ResourceManager.getJarPath();
        log.debug("Here is the jar path: " + jarPath);
        return jarPath + FILE_NAME;
    }

    private void initializeData() {

        StringBuffer buf = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(getFilePath()));
            String line = reader.readLine();
            while (line != null) {
                buf.append(line + "\n");
                line = reader.readLine();
            }
        } catch (Exception e) {
            log.warn("No custom data file found here:" + getFilePath());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                log.error("Failed to close the datafile after reading.", e);
            }
        }

        txtData.setText(buf.toString());
        scrollData.scrollRectToVisible(new Rectangle(0, txtData.getHeight()));
    }

    private void saveData() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(getFilePath(), false));
            writer.write(txtData.getText());
            writer.flush();
        } catch (Exception e) {
            log.error("Failed to write the custom conversion.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    log.error("Failed to close the file", ex);
                }
            }
        }

        fireConversionsChanged();
    }

    private void closeDialog() {
        saveData();
        dispose();
    }

    public void addConversionsChangedListener(ConversionsChangedListener listener) {
        listeners.add(listener);
    }

    public void removeConversionsChangedListener(ConversionsChangedListener listener) {
        listeners.remove(listener);
    }

    private void fireConversionsChanged() {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ConversionsChangedListener listener = (ConversionsChangedListener) iter.next();
            listener.conversionsUpdated();
        }
    }

    private boolean isEmpty(JTextComponent c) {
        String text = c.getText();
        return text == null || text.trim().equals("");
    }

    private void displayRequiredError(String field) {
        JOptionPane.showConfirmDialog(this, Messages.getResource("fieldIsRequired", field),
            Messages.getResource("fieldRequiredTitle"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    private boolean validComponent(JTextComponent c, String name) {
        if (isEmpty(c)) {
            displayRequiredError(name);
            c.grabFocus();
            return false;
        }
        return true;
    }

    private void addConversion() {
        //validate required fields
        if (!validComponent(txtConversionType, Messages.getResource("conversionTypeLabel"))) {
            return;
        }
        if (!validComponent(txtFrom, Messages.getResource("fromUnitLabel"))) {
            return;
        }
        if (!validComponent(txtTo, Messages.getResource("toUnitLabel"))) {
            return;
        }
        if (!validComponent(txtFactor, Messages.getResource("factorLabel"))) {
            return;
        }
        if (!validComponent(txtOffset, Messages.getResource("offsetLabel"))) {
            return;
        }

        StringBuffer buf = new StringBuffer();
        buf.append("\n").append(Messages.getReverseLookup(txtConversionType.getText())).append(",")
        	.append(Messages.getReverseLookup(txtFrom.getText())).append(",").append(
            txtFromAbbrev.getText()).append(",").append(Messages.getReverseLookup(txtTo.getText()))
            .append(",").append(txtToAbbrev.getText()).append(
            ",").append(txtFactor.getText()).append(",").append(txtOffset.getText());
        txtData.setText(txtData.getText() + buf.toString());

        scrollData.scrollRectToVisible(new Rectangle(0, txtData.getHeight()));
    }

    private void addLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        this.getContentPane().add(label);
    }

    private void addComponent(Component c, int x, int y, int width, int height) {
        c.setBounds(x, y, width, height);
        this.getContentPane().add(c);
        addKeyListenerToTextField(c);
    }
}
