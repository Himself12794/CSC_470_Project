package edu.uncfsu.softwaredesign.f16.r2.components;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class ModifyReservation extends JFrame {

	private static final long serialVersionUID = 2726747745041753463L;

	public static final String TITLE = "Modify Reservation #";
	
	private final ReservationForm theForm;
	
	private final Reservation reservation;
	
	public ModifyReservation(Reservation reservation) {
		this.reservation = reservation;
		setTitle(TITLE + reservation.getReservationId());
		setSize(600, 500);
		setIconImage(new JImagePanel("img/icon.png").getImage());
		setLocationRelativeTo(Application.getApp());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		theForm = new ReservationForm(Application.getApp().getReservationRegistry());
		theForm.setReservation(reservation);
		buildLayout();
	}
	
	public void buildLayout() {
		add(theForm);
	}
	
	public void cancel() {
		//System.out.println(JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this?"));
		Object[] options = { "Yes", "No" };
		int res = JOptionPane.showOptionDialog(this, "Hello", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		System.out.println(options[res]);
		dispose();
	}
	
}
