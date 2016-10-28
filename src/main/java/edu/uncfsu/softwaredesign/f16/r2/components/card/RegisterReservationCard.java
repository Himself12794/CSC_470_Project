package edu.uncfsu.softwaredesign.f16.r2.components.card;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.uncfsu.softwaredesign.f16.r2.components.RegisterReservationForm;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationRegistryFullException;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;
import net.miginfocom.swing.MigLayout;

@Component
public class RegisterReservationCard extends AbstractCard {

	private static final long serialVersionUID 	= 5652047994860330697L;
	private static final Logger LOGGER 			= LoggerFactory.getLogger(RegisterReservationCard.class.getSimpleName());	
	
	public static final String TITLE 			= "RegistrationFormPage";

	private final RegisterReservationForm registrationForm;
	
	@Autowired
	private ReservationRegistry reservationRegistry;
	
	public RegisterReservationCard() {
		super(TITLE);
		setLayout(new MigLayout());
		registrationForm = new RegisterReservationForm(reservationRegistry);
	}

	@Override
	public void buildComponent() {
		add(registrationForm);
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
	}

	public void showError(ReservationException except) {
		if (except instanceof ReservationRegistryFullException) {
			Utils.generateErrorMessage(WordUtils.wrap(except.getMessage(), 60), "Registry Full");
		} else {
			Utils.generateErrorMessage(except.getMessage(), except.getClass().getSimpleName());
		}
		
	}
	
}
