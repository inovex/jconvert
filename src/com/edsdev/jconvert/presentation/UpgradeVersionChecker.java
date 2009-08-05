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
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.edsdev.jconvert.util.Browser;
import com.edsdev.jconvert.util.Http;
import com.edsdev.jconvert.util.JConvertProperties;
import com.edsdev.jconvert.util.JConvertSettingsProperties;
import com.edsdev.jconvert.util.Logger;
import com.edsdev.jconvert.util.Messages;

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

    private JTextArea messageLabel = null;

    private JButton yesBtn = null;

    private JButton noBtn = null;

    private JButton dontAskBtn = null;

    private ActionListener yesActionListener = null;

    private ActionListener noActionListener = null;

    public void checkForUpdates(Frame parent) {
        if (isNewerVersionAvailable()) {
            log.debug("Newer version of JConvert found.");
            dlg = new JDialog(parent, Messages.getResource("newVersionTitle"), true);
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setSize(400, 200);
            dlg.setLocationRelativeTo(parent);
            dlg.getContentPane().setLayout(null);

            messageLabel = new JTextArea(Messages.getResource("newVersionMsg", webVersion, currentVersion));
            messageLabel.setBounds(5, 5, 385, 60);
            messageLabel.setEditable(false);
            JLabel temp = new JLabel("temp");
            messageLabel.setBackground(temp.getBackground());
            dlg.getContentPane().add(messageLabel);

            yesBtn = new JButton(Messages.getResource("yesButton"));
            yesBtn.setBounds(5, 70, 100, 22);
            yesActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openLinkToWeb();
                    //TODO think about closing the application in some way...
                    //possibly change one of the buttons to "close application"
                    //what about looking for the new jar?
                    //what about trying to delete the old jar if new exists?
                    //what about some buttons to do this?
                }
            };
            yesBtn.addActionListener(yesActionListener);
            dlg.getContentPane().add(yesBtn);

            noBtn = new JButton(Messages.getResource("noButton"));
            noBtn.setBounds(115, 70, 100, 22);
            noActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            };
            noBtn.addActionListener(noActionListener);
            dlg.getContentPane().add(noBtn);

            dontAskBtn = new JButton(Messages.getResource("dontAskButton"));
            dontAskBtn.setBounds(5, 100, 350, 22);
            dontAskBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dontAskAnymore();
                    close();
                }
            });
            dlg.getContentPane().add(dontAskBtn);

            dlg.show();
        } else {
            log.debug("Newer version of JConvert not found.");
        }
    }

    private void close() {
        dlg.dispose();
    }

    private void dontAskAnymore() {
        JConvertSettingsProperties.setCheckForNewerVersion("No");
    }

    private void openLinkToWeb() {
        Browser.openURL("http://sourceforge.net/projects/jconvert/files/latest");
        messageLabel.setText(Messages.getResource("newVersionLaunchedMsg"));
        dontAskBtn.setVisible(false);
        yesBtn.removeActionListener(yesActionListener);
        noBtn.removeActionListener(noActionListener);
        yesActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
                System.exit(0);
            }
        };
        yesBtn.addActionListener(yesActionListener);

        noActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        };
        noBtn.addActionListener(noActionListener);
    }

    private boolean isNewerVersionAvailable() {
        if ("No".equals(JConvertSettingsProperties.getCheckForNewerVersion())) {
            return false;
        }
        currentVersion = JConvertProperties.getBuidVersion();
        Properties props = null;
        StringBuffer loadedUpdate = null;
        try {
            URLConnection uc = Http.getPage("http://jconvert.sourceforge.net/jconvert.properties");
            loadedUpdate = Http.receivePage(uc, "UTF-8");
            props = new Properties();
            props.load(new ByteArrayInputStream(loadedUpdate.toString().getBytes("UTF-8")));
            webVersion = props.getProperty("MajorVersion") + "." + props.getProperty("MinorVersion") + "."
                    + props.getProperty("Revision");
        } catch (IOException e) {
            log.info("Not able to check for the latest version of JConvert.");
            return false;
        }

        Boolean val = compare(props.getProperty("MajorVersion"), JConvertProperties.getMajorVersion());
        if (val != null) {
            return val.booleanValue();
        }
        val = compare(props.getProperty("MinorVersion"), JConvertProperties.getMinorVersion());
        if (val != null) {
            return val.booleanValue();
        }
        val = compare(props.getProperty("Revision"), JConvertProperties.getRevision());
        if (val != null) {
            return val.booleanValue();
        }
        return false;
    }

    private Boolean compare(String first, String second) {
        int intFirst = new Integer(first).intValue();
        int intSecond = new Integer(second).intValue();
        if (intFirst > intSecond) {
            return Boolean.TRUE;
        } else if (intFirst < intSecond) {
            return Boolean.FALSE;
        }

        return null;
    }

}
