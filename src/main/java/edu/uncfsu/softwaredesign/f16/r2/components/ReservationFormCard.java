package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import com.toedter.calendar.JDateChooser;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;

@Component
public class ReservationFormCard extends AbstractCard implements IApplicationCard {

	private static final long serialVersionUID 	= 5652047994860330697L;
	public static final String TITLE 			= "RegistrationFormPage";
	
	private final JTextField nameText 		= new JTextField(16);
	private final JTextField emailText 		= new JTextField(16);
	private final JTextField cardNumber		= new JTextField(16);
	private final JTextField cardName 		= new JTextField(16);
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 7, 1);
	private final JSpinner daysOptions		= new JSpinner(spinnerModel);
	private final JButton submitButton		= new JButton("Submit");
	private final JDateChooser dateChooser 	= new JDateChooser();
	
	private final JPanel imageHeader		= new ImagePanel("img/generic.jpg");

	@Autowired
	private ReservationRegistry reservationRegistry;
	
	private JComboBox<String> dropDown;
	
	public ReservationFormCard() {
		super(TITLE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public void buildSubComponents() {

		dropDown.addActionListener(this::doTypevalidation);
	}
	
	@Override
	public void buildComponent() {
		removeAll();
		buildSubComponents();
		
		add(imageHeader);
		
		FormLayout layout = new FormLayout(
			    "right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref", 
			    "p, 3dlu, p, $lgap, p, 9dlu, p, $lgap, p, 9dlu, p, $lgap, p");       

		layout.setColumnGroups(new int[][]{{1, 5}, {3, 7}});
		
		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		// Add a titled separator to cell (1, 1) that spans 7 columns.
		builder.addSeparator("Contact Info", cc.xyw(1, 1, 7));
		builder.addLabel("&Name", cc.xy(1, 3));
		builder.add(nameText, cc.xyw(3, 3, 5));
		builder.addLabel("&Email", cc.xy(1, 5));
		builder.add(emailText, cc.xyw(3, 5, 5));

		builder.addSeparator("&Resgistration", cc.xyw(1, 7, 7));
		builder.addLabel("&Type", cc.xy (1,  9));
		builder.add(dropDown, cc.xy (3,  9));
		builder.addLabel("Reservation Date", cc.xy (5,  9));
		builder.add(dateChooser, cc.xy (7,  9));
		builder.addSeparator("Payment", cc.xyw(1, 11, 7));
		builder.addLabel("Name on Card", cc.xy(1, 13));
		builder.add(cardName, cc.xy(3, 13));
		builder.addLabel("Card Number", cc.xy(5, 13));
		builder.add(cardNumber, cc.xy(7, 13));
		
		add(builder.getContainer());
	}

	@Override
	public String getMenuName() {
		return "Reservation";
	}

	@Override
	public String getMenuItemName() {
		return "Create Reservation";
	}

	@Override
	public void reload() {
		doTypevalidation(null);
		dateChooser.setMinSelectableDate(new Date(LocalDate.now().plusDays(1).toEpochDay()));
		
	}
	
	public void doTypevalidation(ActionEvent e) {
		String item = (String)dropDown.getSelectedItem();
		boolean res = reservationRegistry.typeIsPrepaid(item);
		cardName.setEditable(res);
		cardNumber.setEditable(res);
	}
	
	@PostConstruct
	public void populatePanel() {
		dropDown = new JComboBox<>(Reservation.Type.values());
	}
	
}
