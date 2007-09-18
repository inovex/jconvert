package com.edsdev.jconvert.presentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.persistence.DataLoader;
import com.edsdev.jconvert.util.ResourceManager;

public class MainFrame extends JFrame implements ConversionsChangedListener {

    private static final long serialVersionUID = 1L;

    private AboutDialog aboutDlg = null;

    private List data;

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setIconImage(ResourceManager.getImage("icon.jpg"));
        frame.setVisible(true);
    }

    public MainFrame() {
        super();
        try {
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // do nothing
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        this.setSize(600, 400);

        this.setTitle("JConvert");
        this.setJMenuBar(getMenu());

        setContent();

    }

    private void setContent() {
        this.getContentPane().removeAll();

        JTabbedPane tabbedPane = new JTabbedPane();
        // tabbedPane.addChangeListener(new ChangeListener() {
        // public void stateChanged(ChangeEvent e) {
        // System.out.println(e.getSource());
        // }
        // });
        this.getContentPane().add(tabbedPane);
        data = getData();
        Collections.sort(data);
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
            ConversionTypeData ctd = (ConversionTypeData) iter.next();
            JPanel panel = getNewPanel(ctd);
            tabbedPane.addTab(ctd.getTypeName(), panel);
        }

    }

    private JPanel getNewPanel(ConversionTypeData ctd) {
        return new ConversionPanel(ctd);
    }

    private JMenuBar getMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem fileMenuCustom = new JMenuItem("Add custom conversion");
        fileMenuCustom.setMnemonic('A');
        fileMenuCustom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddCustomConversionDlg dlg = new AddCustomConversionDlg(MainFrame.this);
                dlg.addConversionsChangedListener(MainFrame.this);
                dlg.show();
            }
        });

        JMenuItem fileMenuExit = new JMenuItem("Exit");
        fileMenuExit.setMnemonic('X');
        fileMenuExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(fileMenuCustom);
        fileMenu.addSeparator();
        fileMenu.add(fileMenuExit);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem helpMenuAbout = new JMenuItem("About");
        helpMenuAbout.setMnemonic('A');
        helpMenuAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAboutDialog();
            }
        });
        helpMenu.add(helpMenuAbout);

        bar.add(fileMenu);
        bar.add(helpMenu);

        return bar;
    }

    private void displayAboutDialog() {
        if (aboutDlg == null) {
            aboutDlg = new AboutDialog(MainFrame.this);
        }
        aboutDlg.setVisible(true);
    }

    private List getData() {
        List rv = new ArrayList();

        List domainData = new DataLoader().loadData();
        Iterator iter = domainData.iterator();
        while (iter.hasNext()) {
            ConversionType type = (ConversionType) iter.next();
            ConversionTypeData ctd = new ConversionTypeData();
            // type.printDetails();
            ctd.setType(type);
            rv.add(ctd);
        }

        return rv;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edsdev.jconvert.presentation.ConversionsChangedListener#conversionsUpdated()
     */
    public void conversionsUpdated() {
        setContent();
    }
}
