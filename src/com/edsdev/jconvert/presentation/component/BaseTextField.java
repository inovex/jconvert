package com.edsdev.jconvert.presentation.component;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Base text field that you should use instead of using JTextField directly
 * 
 * @author Ed Sarrazin
 */
public class BaseTextField extends JTextField {

    private static final long serialVersionUID = 1L;

    private int maxTextLength = -1;

    /**
     * Stores the commitedValue of the field. This way we can roll that value back if the user desires
     */
    private String commitedValue;

    public BaseTextField() {
        super();
        init();
    }

    public BaseTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        setText(text);
        init();
    }

    public BaseTextField(int columns) {
        super(columns);
        init();
    }

    public BaseTextField(String text, int columns) {
        super(text, columns);
        setText(text);
        init();
    }

    public BaseTextField(String text) {
        super(text);
        setText(text);
        init();
    }

    public void setText(String text) {
        super.setText(text);
        commitedValue = text;
    }

    /**
     * Highlights the text in the text box. Please note that the text does not appear highlighted unless the component
     * has focus.
     */
    public void selectText() {
        if (this.getText() != null) {
            this.setSelectionStart(0);
            this.setSelectionEnd(this.getText().length());
        }
    }

    /**
     * Sets the maximum length of text allowed in this field
     * 
     * @param maxLength set to -1 to indicate removal of the requirement if you have already set it.
     */
    public void setMaxTextLength(int maxLength) {
        this.maxTextLength = maxLength;
        if (this.getDocument() instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument) this.getDocument();
            if (doc.getDocumentFilter() instanceof DocumentSizeFilter) {
                if (maxLength < 0) {
                    doc.setDocumentFilter(null);
                } else {
                    ((DocumentSizeFilter) doc.getDocumentFilter()).setMaxCharacters(maxLength);
                }
            } else {
                doc.setDocumentFilter(new DocumentSizeFilter(maxLength));
            }
        }
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    private void init() {
        commitedValue = this.getText();
        // Enable the focus lost event so we can listen to that
        enableEvents(AWTEvent.FOCUS_EVENT_MASK);

        // listend to the escape key so that we can revert the data
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isEnabled()) {
                    setText(commitedValue);
                }
            }

        });
    }

    protected void processFocusEvent(FocusEvent e) {
        super.processFocusEvent(e);

        // ignore temporary focus event
        if (e.isTemporary()) {
            return;
        }

        if (e.getID() == FocusEvent.FOCUS_LOST) {
            setText(this.getText());
        }
    }

}

class DocumentSizeFilter extends DocumentFilter {
    int maxCharacters;

    public DocumentSizeFilter(int maxChars) {
        maxCharacters = maxChars;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {

        // This rejects the entire insertion if it would make
        // the contents too long. Another option would be
        // to truncate the inserted string so the contents
        // would be exactly maxCharacters in length.
        int newLen = str == null ? 0 : str.length();
        if ((fb.getDocument().getLength() + newLen) <= maxCharacters)
            super.insertString(fb, offs, str, a);
        else
            Toolkit.getDefaultToolkit().beep();
    }

    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {

        // This rejects the entire replacement if it would make
        // the contents too long. Another option would be
        // to truncate the replacement string so the contents
        // would be exactly maxCharacters in length.
        int newLen = str == null ? 0 : str.length();
        if ((fb.getDocument().getLength() + newLen - length) <= maxCharacters)
            super.replace(fb, offs, length, str, a);
        else
            Toolkit.getDefaultToolkit().beep();
    }
}
