package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Customer;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationType;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationRegistryFullException;
import edu.uncfsu.softwaredesign.f16.r2.util.State;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

public abstract class ReservationForm extends JPanel {
	
	private static final long serialVersionUID 	= 5652047994860330697L;
	private static final Logger LOGGER 			= LoggerFactory.getLogger(ReservationForm.class.getSimpleName());	
	
	public static final String TITLE 			= "RegistrationFormPage";
	
	protected final JTextField nameText 				= new JTextField(); 
	protected final JTextField emailText 				= new JTextField(); 
	protected final JTextField phoneText				= new JPhoneTextField();
	protected final JTextField streetText				= new JTextField(); 
	protected final JTextField cityText					= new JTextField(); 
	protected final JComboBox<State> stateText			= new JComboBox<>(State.values());
	protected final JTextField zipcodeText				= new JLimitedTextField(5, '#');
	
	{
		nameText.setDocument(new LimitedDocument(35));
		emailText.setDocument(new LimitedDocument(40));
		streetText.setDocument(new LimitedDocument(75));
		cityText.setDocument(new LimitedDocument(75));
	}
	
	protected final JTextField cardNumber				= new JLimitedTextField(19, '#');
	protected final JTextField cardName 				= new JTextField();
	protected final JTextField cardSecurity				= new JLimitedTextField(3, '#');
	protected final JMonthChooser cardMonth				= new JMonthChooser();
	protected final JYearChooser cardYear				= new JYearChooser();
	
	protected final SpinnerModel weekModel 				= new SpinnerNumberModel(1, 1, 14, 1);
	protected final JSpinner weekOptions				= new JSpinner(weekModel);
	protected final JDateChooser dateChooser 			= new JDateChooser();
	protected final JComboBox<ReservationType> dropDown = new JComboBox<>(ReservationType.values());

	protected final JLabel totalCostLabel				= new JLabel("Total Cost");
	protected final JTextField totalCost				= new JTextField();
	protected final JButton checkButton					= new JButton("Check");
	protected final JButton confirmButton				= new JButton("Confirm");
	protected final JPanel imageHeader					= new JImagePanel("img/generic.png");
	
	protected final ReservationRegistry reservationRegistry;
	
	protected boolean isViewing = true;
	protected Reservation builtReservation = null;
	
	public ReservationForm(ReservationRegistry reservationRegistry) {
		this.reservationRegistry = reservationRegistry;
		setPreferredSize(new Dimension(800, 600));
		buildComponent();
	}

	public void buildSubComponents() {

		dropDown.addActionListener(this::doTypeValidation);
		confirmButton.addActionListener(a -> doConfirm());
		confirmButton.setToolTipText("Click to register reservation");
		checkButton.addActionListener(a -> doCheck());
		cardSecurity.setDocument(new LimitedDocument(3));
		cardNumber.setDocument(new LimitedDocument(19));
		cardYear.setMinimum(LocalDate.now().getYear());
		totalCost.setEnabled(false);
		totalCost.setEditable(false);
		totalCost.setDisabledTextColor(Color.BLACK);
		
	}
	
	public void buildComponent() {
		removeAll();
		buildSubComponents();
		
		add(imageHeader, "align center, wrap");
		
		FormLayout layout = new FormLayout(
			    "right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref", 
			    "p, 3dlu, p, 3dlu, p, 3dlu, p, $lgap, p, $lgap, p, 9dlu, p, $lgap, p, $lgap, p, 9dlu, p, $lgap, p");       

		layout.setColumnGroups(new int[][]{ {1, 5}, {7, 11}});
		
		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		PanelBuilder builder = new PanelBuilder(layout);
		// Contact Info
		builder.setDefaultDialogBorder();
		builder.addSeparator("Contact Info", 	cc.rcw(1, 1, 11));
		
		builder.addLabel("&Name", 				cc.rc(3, 1));
		builder.add(			nameText,		cc.rc(3, 3));
		builder.addLabel("&Email", 				cc.rc(3, 5));
		builder.add(			emailText,		cc.rcw(3, 7, 5));
		
		builder.addLabel("Phone", 				cc.rc(5, 1));
		builder.add(			phoneText, 		cc.rc(5, 3));
		builder.addLabel("Zip-Code",			cc.rc(5, 5));
		builder.add(			zipcodeText,	cc.rc(5, 7));
		
		builder.addLabel("Street", 				cc.rc(7, 1));
		builder.add(			streetText, 	cc.rc(7, 3));
		builder.addLabel("City", 				cc.rc(7, 5));
		builder.add(			cityText, 		cc.rc(7, 7));
		builder.addLabel("State", 				cc.rc(7, 9));
		builder.add(			stateText, 		cc.rc(7, 11));

		// Registration info
		builder.addSeparator("&Resgistration", 	cc.rcw(9, 1, 11));
		
		builder.addLabel("&Type", 				cc.rc(11, 1)); 
		builder.add(			dropDown, 		cc.rc(11, 3));
		builder.addLabel("Reservation Date", 	cc.rc(11, 5));
		builder.add(			dateChooser, 	cc.rc(11, 7));
		builder.addLabel("Days", 				cc.rc(11, 9));
		builder.add(			weekOptions, 	cc.rc(11, 11));
		
		// Payment
		builder.addSeparator("Payment", 		cc.rcw(13, 1, 11));
		
		builder.addLabel("Name on Card", 		cc.rc(15, 1));
		builder.add(			cardName, 		cc.rc(15, 3));
		builder.addLabel("Card Number", 		cc.rc(15, 5));
		builder.add(			cardNumber, 	cc.rcw(15, 7, 5));
		
		builder.addLabel("Security Code", 		cc.rc(17, 1)).setToolTipText("3 digit code found on card back");
		builder.add(			cardSecurity, 	cc.rc(17, 3));
		builder.addLabel("Month", 				cc.rc(17, 5));
		builder.add(			cardMonth, 		cc.rc(17, 7));
		builder.addLabel("Year", 				cc.rc(17, 9));
		builder.add(			cardYear, 		cc.rc(17, 11));
		
		// Confirmation
		builder.addSeparator("Confirmation", 	cc.rcw(19, 1, 11));
		builder.add(totalCostLabel, 			cc.rc(21, 1));
		builder.add(totalCost, 					cc.rc(21, 3));
		builder.add(checkButton, 				cc.rc(21, 7));
		builder.add(confirmButton, 				cc.rc(21, 11));
		
		add(builder.getContainer());
	}
	
	public void reload() {
		doTypeValidation(null);
		confirmButton.setEnabled(false);
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
		
		if ((dateChooser.getDate() == null || dateChooser.getDate().before(day)) && !isViewing) {
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
	 * This should generate a reservation from the fields.
	 */
	public abstract Optional<Reservation> doConfirm();
	
	/**
	 * This should verify the form data.
	 * 
	 * @param e
	 */
	public abstract boolean doCheck();

	public void setReservation(Reservation reservation) {
		
		setCustomer(reservation.getCustomer());
		setCardForFields(reservation.getCreditCard());
		setReservationType(reservation.getType());
		setDays(reservation.getDays());
		setReservationDate(reservation.getReservationDate());
		totalCost.setText(String.format("$%.2f", reservation.getTotalCost()));
		builtReservation = reservation;
		
		doCheck();
	}
	
	public void setCardForFields(Optional<CreditCard> optional) {
		optional.ifPresent(c -> setCardForFields(c.getNameOnCard(), c.getCardNumber(), c.getSecurityCode(), c.getExpirationDate()));	
	}
	
	public void setCardForFields(String name, String number, short code, YearMonth expire) {
		
		cardName.setText(name);
		cardNumber.setText(number);
		cardSecurity.setText(String.valueOf(code));
		cardMonth.setMonth(expire.getMonthValue()-1);
		cardYear.setYear(expire.getYear());
		
	}
	
	public CreditCard getCardFromFields() {
		
		String name = cardName.getText();
		String number = Strings.isNullOrEmpty(cardNumber.getText()) ? "1234567890" : cardNumber.getText();
		short code = Strings.isNullOrEmpty(cardSecurity.getText()) ? 123 : Short.valueOf(cardSecurity.getText());
		YearMonth date = YearMonth.of(cardYear.getYear(), Month.of(cardMonth.getMonth()+1));
		
		return new CreditCard(name, number, date, code);
	}

	public void setCustomer(Customer customer) {
		nameText.setText(customer.getName());
		emailText.setText(customer.getEmail());
		streetText.setText(customer.getAddress());
		phoneText.setText(String.valueOf(customer.getPhone()));
		stateText.setSelectedItem(customer.getState());
		cityText.setText(customer.getCity());
		zipcodeText.setText(String.valueOf(customer.getZipCode()));
	}
	
	public Customer getCustomer() {
		String name = nameText.getText();
		String email = emailText.getText();
		String address = streetText.getText();
		String num = phoneText.getText().replaceAll("(\\(|\\)|\\s|\\-|_)", "");
		num = Strings.isNullOrEmpty(num) ? "0" : num; 
		long phone = Long.valueOf(num);
		State state = (State)stateText.getSelectedItem();
		String city = cityText.getText();
		num = zipcodeText.getText();
		num = Strings.isNullOrEmpty(num) ? "0" : num;
		long zipCode = Long.valueOf(num);
		
		return new Customer(name, phone, email, address, zipCode, city, state);
		
	}
	
	public ReservationType getReservationType() {
		return ReservationType.values()[dropDown.getSelectedIndex()];
	}
	
	public void setReservationType(ReservationType type) {
		dropDown.setSelectedItem(type);
	}
	
	public int getDays() {
		return (int)weekOptions.getValue();
	}
	
	public void setDays(int days) {
		weekOptions.setValue(days);
	}
	
	public LocalDate getReservationDate() {
		return dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public void setReservationDate(LocalDate date) {
		dateChooser.setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}
	
	/**
	 * Uses a popup to display this message as an error.
	 * 
	 * @param except
	 */
	public void showError(ReservationException except) {
		if (except instanceof ReservationRegistryFullException) {
			Utils.generateErrorMessage(WordUtils.wrap(except.getMessage(), 60), "Registry Full");
		} else {
			Utils.generateErrorMessage(except.getMessage(), except.getClass().getSimpleName());
		}
		
	}
	
}