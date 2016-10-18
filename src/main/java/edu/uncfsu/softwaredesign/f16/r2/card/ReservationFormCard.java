package edu.uncfsu.softwaredesign.f16.r2.card;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.uncfsu.softwaredesign.f16.r2.CardRegistry;

@Component
public class RegistrationFormCard implements IApplicationCard {

	private static final long serialVersionUID = 5652047994860330697L;
	public static final String TITLE 				= "RegistrationFormPage";
	
	private static final JTextField nameText 		= new JTextField(16);
	private static final JTextField emailText 		= new JTextField(16);
	private static final SpinnerModel spinnerModel 	= new SpinnerNumberModel(1, 1, 7, 1);
	private static final JSpinner daysOptions		= new JSpinner(spinnerModel);
	
	@Autowired
	private CardRegistry cardRegistry;
	
	public RegistrationFormCard() {
		System.out.println(cardRegistry);
		cardRegistry.registerCard(this);
	}
	
	public JPanel getComponent() {
		FormLayout layout = new FormLayout(
			    "right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref", 
			    "p, 3dlu, p, $lgap, p, 9dlu, p, $lgap, p, $lgap, p");       

		layout.setColumnGroups(new int[][]{{1, 5}, {3, 7}});
		
		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		// Add a titled separator to cell (1, 1) that spans 7 columns.
		builder.addSeparator("Contact Info", cc.xyw(1, 1, 7));
		builder.addLabel("Name", cc.xy (1, 3));
		builder.add(nameText, cc.xyw(3, 3, 5));
		builder.addLabel("Email", cc.xy (1, 5));
		builder.add(emailText, cc.xyw(3, 5, 5));

		builder.addSeparator("Resgistration", cc.xyw(1, 7, 7));
		builder.addLabel("Days", cc.xy (1,  9));
		builder.add(daysOptions, cc.xy (3,  9));
		builder.addLabel("Type", cc.xy (5,  9));
		//builder.add(powerField,           cc.xy (7,  9));
		/*builder.addLabel(" [mm]",        cc.xy (1, 11));
		builder.add(radiusField,          cc.xy (3, 11));
		builder.addLabel("D [mm]",        cc.xy (5, 11));
		builder.add(diameterField,        cc.xy (7, 11));*/
		
		return builder.getPanel();
	}

	@Override
	public String getName() {
		return TITLE;
	}

	@Override
	public void buildComponent() {
		
	}

	@Override
	public String getMenuName() {
		return "Registration";
	}

	@Override
	public String getMenuOptionName() {
		return "Create Registration";
	}

	@Override
	public boolean canBeSelected() {
		return true;
	}

	@Override
	public void reload() {
		
	}
	
}
