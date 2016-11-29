package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

import edu.uncfsu.softwaredesign.f16.r2.components.JImagePanel;
import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

@Component
public class CostManagementCard extends AbstractCard {

	private static final long serialVersionUID = 8979451122696222050L;
	private static final GridBagConstraints GBC	 	= new GridBagConstraints();
	
	static {
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
	}
	
	private final JPanel imageHeader 		= new JImagePanel("img/generic.png");
	private final JDateChooser dayPicker 	= new JDateChooser();
	private final NumberFormat priceFormat	= NumberFormat.getNumberInstance();
	{
		priceFormat.setCurrency(Currency.getInstance(getDefaultLocale()));
		priceFormat.setMaximumFractionDigits(2);
	}
	private final JTextField priceBox		= new JFormattedTextField(priceFormat);
	private final JButton getPrice			= new JButton("Get Price");
	private final JButton submitButton		= new JButton("Submit Price");
	
	
	@Autowired
	private CostRegistry costRegistry;
	
	public CostManagementCard() {
		super("Cost Management");
		setLayout(new GridBagLayout());

		dayPicker.setMinSelectableDate(new Date());
		
		submitButton.addActionListener(a -> {
			
			try {
				LocalDate date = Utils.dateToLocal(dayPicker.getDate());
				float cost = Float.valueOf(priceBox.getText().replaceAll(",", ""));
				costRegistry.setCostForDay(date, cost);
			} catch (Exception e) {
				Utils.generateErrorMessage("Invalid Price Entered", "Invalid Price");
			}
			
		});
		
		getPrice.addActionListener(a -> {

			if (dayPicker.getDate() != null) {
				LocalDate date = Utils.dateToLocal(dayPicker.getDate());
				
				float cost = costRegistry.getCostForDay(date);
				
				if (dayPicker.getDate().compareTo(Utils.localToDate(date)) >= 0) {
					priceBox.setText(String.format("%.2f", cost));
				}
			}
		});
		
	}

	@Override
	public void buildComponent() {
		add(imageHeader);
		add(buildForm(), GBC);
		
	}

	public JPanel buildForm() {

		FormLayout layout = new FormLayout(
						  "15dlu, right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref, 7dlu, right:pref, 7dlu, pref", 
				"15dlu, "+
				"p, " 	 +
				"9dlu,"  +
				"p, " 	 +
				"9dlu, " +
				"p");
		
		layout.setColumnGroups(new int[][]{{2, 6}, {4, 8}, {10, 12}});
		
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addSeparator("Cost for Date", cc.rcw(2, 2, 11));
		builder.addLabel("Select Date", cc.rc(4, 2));
		builder.add(dayPicker, cc.rc(4, 4));
		builder.add(getPrice, cc.rc(4, 6));
		
		builder.addLabel("Set Cost ($)", cc.rc(6, 2));
		builder.add(priceBox, cc.rc(6, 4));
		builder.add(submitButton, cc.rc(6, 6));
		
		return builder.getPanel();
	}
	
	@Override
	public String getMenuName() {
		return "Management";
	}

	@Override
	public String getMenuItemName() {
		return "Set Costs";
	}

	@Override
	public void reload() {
		dayPicker.setMinSelectableDate(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

	@Override
	public boolean requiresElevation() {
		return true;
	}
	
}
