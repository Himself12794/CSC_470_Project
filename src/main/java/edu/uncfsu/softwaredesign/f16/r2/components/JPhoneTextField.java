package edu.uncfsu.softwaredesign.f16.r2.components;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class JPhoneTextField extends JFormattedTextField {

	private static final long serialVersionUID = 1394820270927415635L;

	public JPhoneTextField() {
		super(getFormat());
	}
	
	private static MaskFormatter getFormat() {
		MaskFormatter mf;
		try {
			mf = new MaskFormatter("(###) ###-####");
			mf.setPlaceholderCharacter('_');
			mf.setAllowsInvalid(false);
			return mf;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
