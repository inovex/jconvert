package com.edsdev.jconvert.presentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.edsdev.jconvert.util.Browser;
import com.edsdev.jconvert.util.Logger;
import com.edsdev.jconvert.util.ResourceManager;

/**
 * @author Ed S Created on Sep 10, 2007 8:34:25 PM
 */
public class AboutDialog extends JDialog implements HyperlinkListener {

    private JEditorPane htmlPane;

    private static final Logger log = Logger.getInstance(AboutDialog.class);

    private final String UNKNOWN = "Unknown";

    private final String MAJOR_VERSION = "MajorVersion";

    private final String MINOR_VERSION = "MinorVersion";

    private final String MINI_VERSION = "MiniVersion";

    private final String BUILD_DATE = "BuildDate";

    private final String PROP_FILE = "jconvert.properties";

    public static void main(String[] args) {
        new AboutDialog(null).setVisible(true);
    }

    public AboutDialog(Frame parent) {
        super(parent);
        this.setTitle("About JConvert");

        htmlPane = new JEditorPane("text/html", getContent());
        htmlPane.setEditable(false);
        htmlPane.addHyperlinkListener(this);
        JScrollPane scrollPane = new JScrollPane(htmlPane);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

        //        Dimension screenSize = getToolkit().getScreenSize();
        int width = 300;
        int height = 300;
        setBounds(parent.getX() + (parent.getWidth() - width) / 2, parent.getY() + (parent.getHeight() - height) / 2,
            width, height);
    }

    private JPanel getButtonPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(250, 30));
        JButton btn = new JButton("OK");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog.this.setVisible(false);
            }
        });
        btn.setSize(100, 25);
        panel.add(btn, BorderLayout.EAST);
        return panel;
    }

    private String getContent() {
        String versionNumber = UNKNOWN;
        String buildDate = UNKNOWN;
        try {
            Properties props = new Properties();
            props.load(ResourceManager.getResourceAsStream(PROP_FILE));
            versionNumber = props.getProperty(MAJOR_VERSION) + "." + props.getProperty(MINOR_VERSION) + "_"
                    + props.getProperty(MINI_VERSION);
            buildDate = props.getProperty(BUILD_DATE);
        } catch (Exception e) {
            log.error("Unable to load " + PROP_FILE + ".", e);
        }

        return "<html><body>" + "<H3>About JConvert</H3><BR><font face='ariel' size='2'>" + "Version: " + versionNumber
                + "<BR>" + "Build Date:" + buildDate + "<BR>developed by Ed Sarrazin<BR><BR>"
                + "For additional resources see the <a href='http://jconvert.sourceforge.net/howto.html'>How To</a>, "
                + "<a href=\"https://sourceforge.net/project/showfiles.php?group_id='201265'\">Downloads</a>, "
                + "and <a href='http://jconvert.sourceforge.net/javadoc/index.html'>Java API</a><BR><BR>"
                + "</font></body></html>";
    }

    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (event.getURL() != null) {
                Browser.openURL(event.getURL().toString());
            }
        }
    }

}
