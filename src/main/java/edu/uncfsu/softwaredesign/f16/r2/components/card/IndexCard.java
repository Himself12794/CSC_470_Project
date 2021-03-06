package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.FlowLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class IndexCard extends AbstractCard {

	private static final long serialVersionUID = 8747253147759007310L;
	
	public static final String TITLE = "Index";
	
	private final JPanel buttonContent		= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private final JLabel innerHtml			= new JLabel();
	//private final ImagePanel tacoTuesday	= new ImagePanel("img/taco_tuesday.jpg");
	private final JButton managerButton		= new JButton("Login as Management");
	private final JButton employeeButton	= new JButton("Login as Employee");
	
	public IndexCard() {
		super(TITLE);
	}

	@Override
	public void buildComponent() {
		try {
			InputStream in = new FileInputStream("html/index.html");
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			innerHtml.setText(writer.toString());
		} catch (IOException e) {}

		innerHtml.setVerticalAlignment(SwingConstants.TOP);

		add(innerHtml);
	}

	@Override
	public String getMenuName() {
		return "File";
	}

	@Override
	public String getMenuItemName() {
		return "Index";
	}

	@Override
	public boolean canBeSelected() {
		return true;
	}

	@Override
	public void reload() {
		
	}

	@Override
	public boolean onNavigateAway(IApplicationCard target) {
		return true;
	}

}
