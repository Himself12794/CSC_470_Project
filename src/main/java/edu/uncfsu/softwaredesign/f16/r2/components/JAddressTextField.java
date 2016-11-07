package edu.uncfsu.softwaredesign.f16.r2.components;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import com.google.common.base.Strings;

public class JAddressTextField extends JFormattedTextField {

	private static final long serialVersionUID = 1394820270927415635L;

	public JAddressTextField() {
		super(getFormat());
	}
	
	private static MaskFormatter getFormat() {
		MaskFormatter mf;
		try {
			mf = new MaskFormatter("##### " + Strings.repeat("*", 69));
			mf.setAllowsInvalid(false);
			return mf;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
