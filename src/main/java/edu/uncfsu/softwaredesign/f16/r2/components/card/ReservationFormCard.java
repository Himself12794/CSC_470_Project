package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.GenericWorker;
import edu.uncfsu.softwaredesign.f16.r2.components.ImagePanel;
import edu.uncfsu.softwaredesign.f16.r2.components.JTextFieldLimit;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry.ReservationBuilder;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationType;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationRegistryFullException;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;
import net.miginfocom.swing.MigLayout;

@Component
public class ReservationFormCard extends AbstractCard {

	private static final long serialVersionUID 	= 5652047994860330697L;
	private static final Logger LOGGER 			= LoggerFactory.getLogger(ReservationFormCard.class.getSimpleName());	
	
	public static final String TITLE 			= "RegistrationFormPage";
	
	private final JTextField nameText 					= new JTextField();
	private final JTextField emailText 					= new JTextField();
	private final JTextField cardNumber					= new JTextField();
	private final JTextField cardName 					= new JTextField();
	private final JTextField cardSecurity				= new JTextField();
	private final JTextField totalCost					= new JTextField();
	private final JMonthChooser cardMonth				= new JMonthChooser();
	private final JYearChooser cardYear					= new JYearChooser();
	private final SpinnerModel weekModel 				= new SpinnerNumberModel(1, 1, 7, 1);
	private final JSpinner weekOptions					= new JSpinner(weekModel);
	private final JButton calculateButton				= new JButton("Calculate");
	private final JButton submitButton					= new JButton("Submit");
	private final JDateChooser dateChooser 				= new JDateChooser();
	private final JComboBox<ReservationType> dropDown 	= new JComboBox<>(ReservationType.values());
	private final JPanel imageHeader					= new ImagePanel("img/generic.jpg");
	
	private ReservationBuilder reservationBuilder = null;
	private Reservation builtReservation = null;
	private boolean hasSubmitted = false;

	@Autowired
	private ReservationRegistry reservationRegistry;
	
	@Autowired
	private Application theApp;
	
	public ReservationFormCard() {
		super(TITLE);
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//setLayout(new GridBagLayout());
		setLayout(new MigLayout());
		
	}

	public void buildSubComponents() {

		dropDown.addActionListener(this::doTypeValidation);
		submitButton.addActionListener(a -> new GenericWorker(this::doSubmit).execute());
		submitButton.setToolTipText("Click to register reservation");
		calculateButton.addActionListener(a -> new GenericWorker(this::doCalculate).execute());
		cardSecurity.setDocument(new JTextFieldLimit(3));
		cardNumber.setDocument(new JTextFieldLimit(19));
		cardYear.setMinimum(LocalDate.now().getYear());
		totalCost.setEnabled(false);
		totalCost.setEditable(false);
		totalCost.setDisabledTextColor(Color.BLACK);
		
	}
	
	@Override
	public void buildComponent() {
		removeAll();
		buildSubComponents();
		
		add(imageHeader, "align center, wrap");
		
		FormLayout layout = new FormLayout(
			    "right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref", 
			    "p, 3dlu, p, $lgap, p, 9dlu, p, $lgap, p, $lgap, p, 9dlu, p, $lgap, p, $lgap, p, 9dlu, p, $lgap, p");       

		layout.setColumnGroups(new int[][]{ {1, 5}, {7, 11}});
		
		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.addSeparator("Contact Info", cc.xyw(1, 1, 11));
		builder.addLabel("&Name", cc.xy(1, 3));
		builder.add(nameText, cc.xy(3, 3));
		builder.addLabel("&Email", cc.xy(5, 3));
		builder.add(emailText, cc.xyw(7, 3, 5));

		builder.addSeparator("&Resgistration", cc.xyw(1, 7, 11));
		builder.addLabel("&Type", cc.xy (1,  9)); 
		builder.add(dropDown, cc.xy (3,  9));
		builder.addLabel("Reservation Date", cc.xy(5, 9));
		builder.add(dateChooser, cc.xy(7, 9));
		builder.addLabel("Days", cc.xy(9, 9));
		builder.add(weekOptions, cc.xy(11, 9));
		//builder.addLabel("Days", cc.xy(1, 11));
		//builder.add(weekOptions, cc.xy(3, 11));
		
		builder.addSeparator("Payment", cc.xyw(1, 13, 11));
		builder.addLabel("Name on Card", cc.xy(1, 15));
		builder.add(cardName, cc.xy(3, 15));
		builder.addLabel("Card Number", cc.xy(5, 15));
		builder.add(cardNumber, cc.xyw(7, 15, 5));
		builder.addLabel("Security Code", cc.xy(1, 17)).setToolTipText("3 digit code found on card back");
		builder.add(cardSecurity, cc.xyw(3, 17, 1));
		builder.addLabel("Month", cc.xy(5, 17));
		builder.add(cardMonth, cc.xy(7, 17));
		builder.addLabel("Year", cc.xy(9, 17));
		builder.add(cardYear, cc.xy(11, 17));
		
		builder.addSeparator("Confirmation", cc.xyw(1, 19, 11));
		builder.addLabel("Total Cost", cc.xy(1, 21));
		builder.add(totalCost, cc.xy(3, 21));
		builder.add(calculateButton, cc.xy(7, 21));
		builder.add(submitButton, cc.xy(11, 21));
		
		add(builder.getContainer(), "align center");
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
		doTypeValidation(null);
		submitButton.setEnabled(false);
	}
	
	/**
	 * Validates that the choices available for selection are 
	 * appropriate for the selected type.
	 * 
	 * @param e
	 */
	public void doTypeValidation(ActionEvent e) {
		
		ReservationType type = ReservationType.values()[dropDown.getSelectedIndex()];
		int minDays = type.minimumDays;
		Date day = Utils.todayPlus(minDays);
		
		if (dateChooser.getDate() == null || dateChooser.getDate().before(day)) {
			dateChooser.setDate(day);
		}
		
		dateChooser.setMinSelectableDate(day);
		
		cardMonth.setEnabled(type.isPrepaid);
		cardYear.setEnabled(type.isPrepaid);
		cardName.setEditable(type.isPrepaid);
		cardName.setEnabled(type.isPrepaid);
		cardNumber.setEnabled(type.isPrepaid);
		cardNumber.setEditable(type.isPrepaid);
		cardSecurity.setEnabled(type.isPrepaid);
		cardSecurity.setEditable(type.isPrepaid);
	}
	
	/**
	 * Called when this form is submitted.
	 * 
	 */
	public synchronized void doSubmit() {
		try {
			reservationRegistry.registerReservation(builtReservation);
			builtReservation = null;
			submitButton.setEnabled(false);
		} catch (ReservationRegistryFullException re) {
			LOGGER.error("The registry is full for this reservation range");
			LOGGER.trace("Trace", re);
			showError(re);
		} 
	}
	
	/**
	 * Called to calculate the price
	 * 
	 * @param e
	 */
	public synchronized void doCalculate() {
		
		LOGGER.info("Calculate button has been pressed");
		
		reservationBuilder = reservationRegistry.createReservationBuilder(ReservationType.values()[dropDown.getSelectedIndex()]);
		
		reservationBuilder.setDays(getDays())
			.setName(getGuestName())
			.setEmail(getGuestEmail())
			.setPayment(getCardFromFields())
			.setReservationDate(getReservationDate());
		
		try {
			
			builtReservation = reservationBuilder.createAndRegister(false);
			totalCost.setText(String.format("$%.2f", builtReservation.getTotalCost()));
			submitButton.setEnabled(true);
			
		} catch (ReservationException e1) {
			
			LOGGER.error("An error occured in attempting to create a reservation");
			LOGGER.trace("Trace", e1);
			showError(e1);
		}
		
	}

	public CreditCard getCardFromFields() {
		
		String name = cardName.getText();
		String number = Strings.isNullOrEmpty(cardNumber.getText()) ? "1234567890" : cardNumber.getText();
		short code = Strings.isNullOrEmpty(cardSecurity.getText()) ? 123 : Short.valueOf(cardSecurity.getText());
		YearMonth date = YearMonth.of(cardYear.getYear(), Month.of(cardMonth.getMonth()+1));
		
		return new CreditCard(name, number, date, code);
	}

	public String getGuestName() {
		return nameText.getText();
	}
	
	public String getGuestEmail() {
		return emailText.getText();
	}
	
	public ReservationType getReservationType() {
		return ReservationType.values()[dropDown.getSelectedIndex()];
	}
	
	public int getDays() {
		return (int)weekOptions.getValue();
	}
	
	public LocalDate getReservationDate() {
		return dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public void showError(ReservationException except) {
		if (except instanceof ReservationRegistryFullException) {
			generateErrorMessage(WordUtils.wrap(except.getMessage(), 60), "Registry Full");
		} else {
			generateErrorMessage(except.getMessage(), except.getClass().getSimpleName());
		}
		
	}
	
}
