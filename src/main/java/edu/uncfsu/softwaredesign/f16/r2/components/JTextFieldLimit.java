package edu.uncfsu.softwaredesign.f16.r2.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

public class JTextFieldLimit extends PlainDocument {
	
	private static final long serialVersionUID = 3324231920825227644L;
	
	private int limit;
	
	public JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	public JTextFieldLimit(int limit, boolean upper) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) return;

	    if ((getLength() + str.length()) <= limit && Utils.isInt(str)) {
	    	super.insertString(offset, str, attr);
	    }
	}
}