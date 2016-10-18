package edu.uncfsu.softwaredesign.f16.r2.card;

import java.awt.Container;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import edu.uncfsu.softwaredesign.f16.r2.Application;

@Component
public class IndexCard implements IApplicationCard {

	private final JLabel indexPane = new JLabel();
	private final JPanel buttonContent		= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private final JButton managerButton		= new JButton("Login as Management");
	private final JButton employeeButton	= new JButton("Login as Employee");
	
	public IndexCard() {
		Application.cardRegistry.registerCard(this);
	}
	
	@Override
	public String getName() {
		return "Index";
	}

	@Override
	public Container getComponent() {
		return indexPane;
	}

	@Override
	public void buildComponent() {
		InputStream in = getClass().getClassLoader().getResourceAsStream("index.html");
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(in, writer);
			indexPane.setText(writer.toString());
		} catch (IOException e) {
		}

		indexPane.setVerticalAlignment(SwingConstants.TOP);

		/*mainContent.add(buttonContent, BUTTONS);
		buttonContent.add(managerButton);
		managerButton.addActionListener(l -> {
			isManager = true;
			onIndexClick(null);
		});
		
		buttonContent.add(employeeButton);
		employeeButton.addActionListener(l -> {
			isManager = false;
			onIndexClick(null);
		});*/
	}

	@Override
	public String getMenuName() {
		return "File";
	}

	@Override
	public String getMenuOptionName() {
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
