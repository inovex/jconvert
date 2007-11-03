package com.edsdev.jconvert.presentation;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

import com.edsdev.jconvert.util.Browser;
import com.edsdev.jconvert.util.Http;
import com.edsdev.jconvert.util.JConvertProperties;
import com.edsdev.jconvert.util.JConvertSettingsProperties;
import com.edsdev.jconvert.util.Logger;

/**
 * This is a helper class that is designed to determine if there is a new version of the application that should be
 * retrieved. If there is, then the user is prompted to get that latest version from the web.
 * 
 * @author Ed Sarrazin Created on Nov 2, 2007 4:11:43 PM
 */
public class UpgradeVersionChecker {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new UpgradeVersionChecker().checkForUpdates(null);
    }

    private static final Logger log = Logger.getInstance(UpgradeVersionChecker.class);

    private String currentVersion = "";

    private String webVersion = "";

    private JDialog dlg = null;

    public void checkForUpdates(Frame parent) {
        if (isNewerVersionAvailable()) {
            String msg = "A newer version (" + webVersion + ") of JConvert is available.\r\nYour current version is "
                    + currentVersion + ".\nDo you want to download the latest from the web?";
            dlg = new JDialog(parent, "Newer Version Available", true);
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setSize(400, 200);
            dlg.getContentPane().setLayout(null);

            JTextArea label = new JTextArea(msg);
            label.setBounds(5, 5, 385, 60);
            label.setEditable(false);
            label.setBackground(dlg.getBackground());
            dlg.getContentPane().add(label);

            JButton yesBtn = new JButton("Yes");
            yesBtn.setBounds(5, 70, 100, 22);
            yesBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openLinkToWeb();
                    //TODO think about closing the application in some way...
                    //possibly change one of the buttons to "close application"
                    //what about looking for the new jar? 
                    //what about trying to delete the old jar if new exists?
                    //what about some buttons to do this?
                }
            });
            dlg.getContentPane().add(yesBtn);

            JButton noBtn = new JButton("No");
            noBtn.setBounds(115, 70, 100, 22);
            noBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
            dlg.getContentPane().add(noBtn);

            JButton dontAskBtn = new JButton("No, and don't ask me anymore.");
            dontAskBtn.setBounds(5, 100, 350, 22);
            dontAskBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dontAskAnymore();
                    close();
                }
            });
            dlg.getContentPane().add(dontAskBtn);

            dlg.show();
        }
    }

    private void close() {
        dlg.dispose();
    }

    private void dontAskAnymore() {
        JConvertSettingsProperties.setCheckForNewerVersion("No");
    }

    private void openLinkToWeb() {
        Browser.openURL("http://sourceforge.net/jconvert/downloads");
    }

    private boolean isNewerVersionAvailable() {
        if ("No".equals(JConvertSettingsProperties.getCheckForNewerVersion())) {
            return false;
        }
        currentVersion = JConvertProperties.getBuidVersion();

        StringBuffer loadedUpdate = null;
        try {
            URLConnection uc = Http.getPage("http://jconvert.sourceforge.net/jconvert.properties");
            loadedUpdate = Http.receivePage(uc, "UTF-8");
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(loadedUpdate.toString().getBytes("UTF-8")));
            webVersion = props.getProperty("MajorVersion") + "." + props.getProperty("MinorVersion") + "."
                    + props.getProperty("Revision");
        } catch (IOException e) {
            log.info("Not able to check for the latest version of JConvert.");
            return false;
        }

        return currentVersion.compareTo(webVersion) < 0;
    }

}
