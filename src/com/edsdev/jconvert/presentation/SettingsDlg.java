package com.edsdev.jconvert.presentation;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.edsdev.jconvert.presentation.component.VisibleTabsListCellRenderer;
import com.edsdev.jconvert.util.JConvertSettingsProperties;
import com.edsdev.jconvert.util.Logger;
import com.edsdev.jconvert.util.Messages;

/**
 * @author Ed Sarrazin Created on Oct 22, 2007 5:33:10 PM
 */
public class SettingsDlg extends JDialog {

    private final int WIDTH = 400;

    private final int HEIGHT = 450;

    private JComboBox cboCountry = new JComboBox(getCountries());

    private JComboBox cboLanguage = new JComboBox(getLanguages());

    private JTextField txtVariant = new JTextField();

    private JButton okButton = new JButton(Messages.getResource("okButton"));

    private JButton cancelButton = new JButton(Messages.getResource("cancelButton"));

    private ArrayList listeners = new ArrayList();

    private JScrollPane scrollData = new JScrollPane();

    private JList hiddenTabs = null;

    private List data = null;

    private static final Logger log = Logger.getInstance(AddCustomConversionDlg.class);

    public SettingsDlg(Frame parent, List pData) {
        super(parent);
        data = pData;
        this.setTitle(Messages.getResource("settingsTitle"));

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
    }

    private void init() {
        this.getContentPane().setLayout(null);
        addLabel(Messages.getResource("country"), 5, 5, 100, 22);
        addComponent(cboCountry, 110, 5, 200, 22);

        addLabel(Messages.getResource("language"), 5, 30, 100, 22);
        addComponent(cboLanguage, 110, 30, 200, 22);

        addLabel(Messages.getResource("variant"), 5, 55, 100, 22);
        addComponent(txtVariant, 110, 55, 200, 22);

        addLabel(Messages.getResource("hiddenTabs"), 5, 80, 100, 22);
        hiddenTabs = new JList(getTabs());
        scrollData.getViewport().add(hiddenTabs);
        hiddenTabs.setCellRenderer(new VisibleTabsListCellRenderer());
        scrollData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addComponent(scrollData, 5, 105, 380, 200);

        addComponent(okButton, 5, 310, 100, 25);
        addComponent(cancelButton, 110, 310, 100, 25);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveData();
                fireConversionsChanged();
                closeDialog();
            }
        });
        this.getRootPane().setDefaultButton(okButton);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });

        initializeData();
    }

    private void initializeData() {
        String country = JConvertSettingsProperties.getLocaleCountry();
        if (country == null) {
            country = Locale.getDefault().getCountry();
        }
        for (int i = 0; i < cboCountry.getModel().getSize(); i++) {
            if (cboCountry.getModel().getElementAt(i).toString().startsWith(country)) {
                cboCountry.setSelectedIndex(i);
                break;
            }
        }

        String language = JConvertSettingsProperties.getLocaleLanguage();
        if (language == null) {
            language = Locale.getDefault().getLanguage();
        }
        for (int i = 0; i < cboLanguage.getModel().getSize(); i++) {
            if (cboLanguage.getModel().getElementAt(i).toString().startsWith(language)) {
                cboLanguage.setSelectedIndex(i);
                break;
            }
        }

        txtVariant.setText(JConvertSettingsProperties.getLocaleVariant());
    }

    private void saveData() {
        Locale locale = Locale.getDefault();
        locale = new Locale(cboLanguage.getSelectedItem().toString().substring(0, 2),
            cboCountry.getSelectedItem().toString().substring(0, 2), txtVariant.getText());
        Locale.setDefault(locale);

        String hidden = "";
        for (int i = 0; i < hiddenTabs.getModel().getSize(); i++) {
            if (hiddenTabs.getModel().getElementAt(i).toString().startsWith("x")) {
                hidden = hidden + "," + hiddenTabs.getModel().getElementAt(i).toString().substring(1);
            }
        }
        if (hidden.length() > 0) {
            hidden = hidden.substring(1);
        }
        JConvertSettingsProperties.setHiddenTabs(hidden);
    }

    private void closeDialog() {
        dispose();
    }

    private Vector getCountries() {
        Vector rv = new Vector();
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            if (!locales[i].getCountry().equals("")) {
                rv.add(locales[i].getCountry() + " - " + locales[i].getDisplayCountry());
            }
        }
        return rv;
    }

    private Vector getLanguages() {
        Vector rv = new Vector();
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            String temp = locales[i].getLanguage() + " - " + locales[i].getDisplayLanguage();
            if (!rv.contains(temp)) {
                rv.add(temp);
            }
        }
        return rv;
    }

    private Vector getTabs() {
        Vector rv = new Vector();
        String hiddenTabs = JConvertSettingsProperties.getHiddenTabs();
        if (hiddenTabs == null) {
            hiddenTabs = "";
        }
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            ConversionTypeData ctd = (ConversionTypeData) iter.next();
            rv.add("o" + ctd.getTypeName());
        }
        StringTokenizer token = new StringTokenizer(hiddenTabs, ",");
        while (token.hasMoreTokens()) {
            String hiddenTab = token.nextToken();
            if (rv.contains("o" + hiddenTab)) {
                rv.remove("o" + hiddenTab);
                rv.add("x" + hiddenTab);
            }
        }
        Collections.sort(rv, new Comparator() {
            public int compare(Object o1, Object o2) {
                return o1.toString().substring(1).compareTo(o2.toString().substring(1));
            }
        });
        return rv;
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

    private void addLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        this.getContentPane().add(label);
    }

    private void addComponent(Component c, int x, int y, int width, int height) {
        c.setBounds(x, y, width, height);
        this.getContentPane().add(c);
    }
}
