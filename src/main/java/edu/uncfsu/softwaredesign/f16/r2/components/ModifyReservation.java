package edu.uncfsu.softwaredesign.f16.r2.components;

import javax.swing.JFrame;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class ModifyReservation extends JFrame {

	private static final long serialVersionUID = 2726747745041753463L;

	public static final String TITLE = "Modify Reservation #";
	
	private final ReservationForm theForm;
	
	private final Reservation reservation;
	
	public ModifyReservation(Application app, Reservation reservation) {
		this.reservation = reservation;
		setTitle(TITLE + reservation.getReservationId());
		setSize(600, 500);
		setIconImage(new ImagePanel("img/icon.png").getImage());
		setLocationRelativeTo(app);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		theForm = new ReservationForm(app.getReservationRegistry());
		theForm.setReservation(reservation);
		buildLayout();
	}
	
	public void buildLayout() {
		add(theForm);
	}
	
}
