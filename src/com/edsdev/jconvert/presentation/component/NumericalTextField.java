package com.edsdev.jconvert.presentation.component;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author Ed Sarrazin
 */
public class NumericalTextField extends BaseTextField {

    private static final long serialVersionUID = 1L;

    public NumericalTextField(int cols) {
        super(cols);
    }

    public NumericalTextField() {
        super();
    }

    protected Document createDefaultModel() {
        return new NumericDocument(this);
    }
}

class NumericDocument extends PlainDocument {

    NumericalTextField parent;

    public NumericDocument(NumericalTextField theParent) {
        super();
        parent = theParent;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);
    }

    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        String resultString = "";
        int negativeCount = 0;
        // iterate through each character in the string and validate it.
        for (int i = 0; i < text.length(); i++) {
            String ch = new Character(text.charAt(i)).toString();
            if (!Character.isDigit(text.charAt(i)) && !ch.equals(".") && !ch.equals("-") && !ch.equals(",") && !ch.equals("/")) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            if (!ch.equals("/") && resultString.indexOf("/") > -1) {
				Toolkit.getDefaultToolkit().beep();
				return;
            }
            // if the character is a minus sign, it is ok, but lets remove it and count how many
            if (ch.equals("-")) {
                negativeCount++;
            } else {
                resultString += ch;
            }
        }
        // stick the value into the field, minus negatives
        super.replace(offset, length, resultString, attrs);

        // now handle the negatives (if odd number-two negatives make a positive) that you just pulled out.
        if (negativeCount != 0 && negativeCount % 2 == 1) {
            String parentText = parent.getText();
            if (parentText == null || parentText.equals("")) {
                parentText = "-";
            } else {
                // sign needs to change - pull out neg if there is one, add neg if there is not one
                int pos = parentText.indexOf('-');
                if (pos >= 0) {
                    parentText = parentText.replaceAll("-", "");
                } else {
                    parentText = "-" + parentText;
                }
            }
            // stick the work that you just did back into the field
            super.replace(0, parent.getText().length(), parentText, attrs);
        }

    }

}