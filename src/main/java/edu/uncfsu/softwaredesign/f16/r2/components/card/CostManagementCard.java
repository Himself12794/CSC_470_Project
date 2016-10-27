package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toedter.calendar.JDateChooser;

import edu.uncfsu.softwaredesign.f16.r2.components.ImagePanel;
import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;

@Component
public class CostManagementCard extends AbstractCard {

	private static final long serialVersionUID = 8979451122696222050L;

	private final JPanel imageHeader 		= new ImagePanel("img/generic.png");
	private final JDateChooser dayPicker 	= new JDateChooser();
	
	
	@Autowired
	private CostRegistry costRegistry;
	
	public CostManagementCard() {
		super("Cost Management");
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
	}

	@Override
	public void buildComponent() {
		
		add(imageHeader);
		
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

}
