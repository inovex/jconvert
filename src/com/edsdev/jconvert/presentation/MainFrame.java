package com.edsdev.jconvert.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.persistence.DataLoader;
import com.edsdev.jconvert.util.ResourceManager;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

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
}
