package edu.uncfsu.softwaredesign.f16.r2.components;

import java.text.NumberFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

/**
 * A document that can be defined to have a set limit, and to
 * accept only certain types of string values. 
 * 
 * @author phwhitin
 *
 */
public class LimitedDocument extends PlainDocument {
	
	private static final long serialVersionUID = 3324231920825227644L;
	
	public static final int NUMBERS = 0x1;
	public static final int LETTERS = 0x2;
	
	private final int limit;
	private final int type; 
	
	public LimitedDocument(int limit) {
		this(NUMBERS | LETTERS, -1);
	}

	public LimitedDocument(int type, int limit) {
		super();
		this.type = type;
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) return;

		boolean insertFlagLength = limit < 0 || (getLength() + str.length() <= limit);
	    
		boolean flagAll = (type & NUMBERS|LETTERS) != 0;
	    boolean flagNums = (type & NUMBERS) != 0;
	    boolean flagAlpha = (type & LETTERS) != 0;
	    
	    if (insertFlagLength && (flagAll || (flagNums && Utils.isInt(str)) || (flagAlpha && Utils.isAlpha(str)))) {
	    	super.insertString(offset, str, attr);
	    }
	}
}