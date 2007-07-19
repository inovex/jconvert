package com.edsdev.jconvert.presentation.component;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class NumericalTextField extends BaseTextField {

	private static final long serialVersionUID = 1L;

	public NumericalTextField(int cols) {
		super(cols);
	}

	public NumericalTextField() {
		super();
	}

	protected Document createDefaultModel() {
		return new NumericDocument();
	}

}

class NumericDocument extends PlainDocument {

	public NumericDocument() {
		super();
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) && !new Character(str.charAt(i)).toString().equals(".")) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
		}
		super.insertString(offs, str, a);
	}

	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i))&& !new Character(text.charAt(i)).toString().equals(".")) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
		}
		super.replace(offset, length, text, attrs);
	}

}
