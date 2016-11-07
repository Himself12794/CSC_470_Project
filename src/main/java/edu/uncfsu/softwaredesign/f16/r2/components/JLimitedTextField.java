package edu.uncfsu.softwaredesign.f16.r2.components;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import com.google.common.base.Strings;

public class JLimitedTextField extends JFormattedTextField {

	private static final long serialVersionUID = 7154376787716339072L;

	public JLimitedTextField(int size) {
		this(size, '*');
	}
	/**
	 * For char types, see {@link MaskFormatter}
	 * 
	 * @param size
	 * @param charType
	 */
	public JLimitedTextField(int size, char charType) {
		this(size, charType, "");
	}

	public JLimitedTextField(int size, char charType, String invalidChars) {
		super(getFormat(size, charType, invalidChars));
	}
	
	private static MaskFormatter getFormat(int size, char charType, String invalidChars) {
		try {
			MaskFormatter mf = new MaskFormatter(Strings.repeat(String.valueOf(charType), size));
			mf.setInvalidCharacters(invalidChars);
			mf.setAllowsInvalid(false);
			return mf;
		} catch (ParseException e) {
			return null;
		}
	}
	
}
