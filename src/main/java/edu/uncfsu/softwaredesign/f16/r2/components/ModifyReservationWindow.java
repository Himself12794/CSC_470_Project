package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.components.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class ModifyReservationWindow extends JDialog {

	private static final long serialVersionUID = 2726747745041753463L;

	public static final String TITLE = "Modify Reservation #";
	
	private final ReservationForm theForm;
	private final IApplicationCard parent;
	private final Reservation reservation;
	
	public ModifyReservationWindow(IApplicationCard parent, Reservation reservation) {
		super(Application.getApp(), true);
		this.reservation = reservation;
		this.parent = parent;
		
		setTitle(TITLE + reservation.getReservationId());
		setSize(1000, 500);
		setIconImage(new JImagePanel("img/icon.png").getImage());
		setLocationRelativeTo(Application.getApp());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		theForm = new ModifyReservationForm(this, Application.getApp().getReservationRegistry());
		theForm.setReservation(reservation);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (confirmDialog("close without modifying anything")) dispose();
			}

		});
		
		buildLayout();
	}
	
	public void close() {
		dispose();
		parent.reload();
	}
	
	public void buildLayout() {
		add(theForm);
	}
	
	public void cancel() {
		if (confirmDialog("cancel reservation #"+ reservation.getReservationId())) {
			JOptionPane.showMessageDialog(this, "Reservation #" + reservation.getReservationId() + " has been canceled", "Canceled", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		}
	}
	
	public boolean confirmDialog(String message) {

		Object[] options = { "Yes", "No" };
		int res = JOptionPane.showOptionDialog(this, 
				"Are you sure you want to " + message + "?", "Confirm", 
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, 
				null, options, options[1]);
		
		return res == 0;
	}
	
}
