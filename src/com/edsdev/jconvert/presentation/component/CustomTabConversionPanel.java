package com.edsdev.jconvert.presentation.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.edsdev.jconvert.common.CustomConversionDataInterface;
import com.edsdev.jconvert.common.CustomDataUpdatedListener;
import com.edsdev.jconvert.presentation.ConversionPanel;
import com.edsdev.jconvert.presentation.ConversionTypeData;
import com.edsdev.jconvert.util.Messages;

/**
 * This is the proposed way this will work. We will have an interface, CustomConversionDataInterface with the Properties
 * getData, getLastUpdate. For each custom conversion plugin, we will create one of these panels. The panel will be
 * initialized with the Custom interface so that it can poll the data From the mainframe (possibly delegated) the custom
 * interface class will be created.
 * 
 * @author Ed Sarrazin Created on Oct 25, 2007 4:16:53 PM
 */
public class CustomTabConversionPanel extends ConversionPanel implements CustomDataUpdatedListener {

    private CustomConversionDataInterface customAdapter = null;

    //having a lot of problems keeping this field around for updating. Made static and this worked
    //do not like this kind of coding.
    private static JLabel timeLabel = null;

    private CustomTabConversionPanel(ConversionTypeData pCtd) {
        super(pCtd);
    }

    public CustomTabConversionPanel(CustomConversionDataInterface ccdi) {
        this(new ConversionTypeData(ccdi.getConversions()));
        setCustomDataAdapter(ccdi);
        timeLabel.setText(getLastUpdateTimestamp());
    }

    public void setCustomDataAdapter(CustomConversionDataInterface theCustomAdapter) {
        customAdapter = theCustomAdapter;
        customAdapter.addDataUpdatedListener(this);
    }

    private String getLastUpdateTimestamp() {
        if (customAdapter == null) {
            return "";
        }
        Date date = customAdapter.getLastUpdated();
        if (date == null) {
            return "";
        }
        return date.toLocaleString();
    }

    private void updateData() {
        ConversionTypeData data = new ConversionTypeData(customAdapter.getConversions());
        this.setConversionTypeData(data);
        timeLabel.setText(getLastUpdateTimestamp());
    }

    protected Component getTopComponent() {
        JPanel rv = new JPanel();
        Dimension dim = new Dimension(1500, 30);
        rv.setPreferredSize(dim);
        rv.setMaximumSize(dim);
        rv.setLayout(new BoxLayout(rv, BoxLayout.X_AXIS));

        JButton button = new JButton(Messages.getResource("updateDataButton"));
        rv.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateData();
            }
        });
        JLabel updateLabel = new JLabel(Messages.getResource("lastUpdateTime"));
        updateLabel.setMaximumSize(new Dimension(100, 22));
        rv.add(updateLabel);
        timeLabel = new JLabel(getLastUpdateTimestamp());
        rv.add(timeLabel);

        return rv;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edsdev.jconvert.common.CustomDataUpdatedListener#customDataUpdated()
     */
    public void customDataUpdated() {
        updateData();
    }
}
