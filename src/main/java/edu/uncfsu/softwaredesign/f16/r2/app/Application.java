package edu.uncfsu.softwaredesign.f16.r2.app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.google.common.collect.Lists;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

@SpringBootApplication(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Application extends JFrame {

	private static final long serialVersionUID = -3885000271483573087L;
	private static final String APP_NAME = "Reservation Management System";
	
	// Menu items
	private final JMenuBar menuBar			= new JMenuBar();
	private final JMenu reservationMenu		= new JMenu("Reservation");
	private final JMenuItem makeReservation = new JMenuItem("Make Reservation");
	private final JMenuItem viewReservation = new JMenuItem("View Reservations");
	private final JMenu fileMenu			= new JMenu("File");
	private final JMenuItem closeOption		= new JMenuItem("Close");

	// Main content
	private final JPanel mainContent		= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private final JButton managerButton		= new JButton("Login as Management");
	private final JButton employeeButton	= new JButton("Login as Employee");
	//private final JTextArea textContent		= new JTextArea(33, 71);
	//private final JLabel labelContent		= new JLabel("Test");
	//private final JScrollPane scrolled 		= new JScrollPane(textContent);
	
	// This holds all label reservation items for view reservations
	private final List<JLabel> reserLabels  = Lists.newArrayList();
	private final List<Reservation> rerserv = Lists.newArrayList();
	
	private boolean isManager = false;
	
	public Application() {

		setTitle(APP_NAME);
		buildMenu();
		buildLayout();
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		
	}
	
	/**
	 * Builds the menu. Called on startup.
	 */
	private void buildMenu() {
		
		setJMenuBar(menuBar);
		
		menuBar.add(fileMenu);
		fileMenu.add(closeOption);
		closeOption.addActionListener(l -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		
		menuBar.add(reservationMenu);
		reservationMenu.add(makeReservation);
		makeReservation.addActionListener(this::onMakeReservationClick);
		reservationMenu.add(viewReservation);
		viewReservation.addActionListener(this::onViewReservationClick);
		
	}
	
	private void buildLayout() {
		
		add(mainContent);
		
		mainContent.add(managerButton);
		managerButton.addActionListener(l -> {
			isManager = true;
			switchToScrolled();
		});
		
		mainContent.add(employeeButton);
		employeeButton.addActionListener(l -> {
			isManager = false;
			switchToScrolled();
		});
		
		/*textContent.setEditable(false);
		textContent.setVisible(false);
		labelContent.setBorder(BorderFactory.createEmptyBorder());
		labelContent.setVisible(false);*/
	}
	
	public void switchToScrolled() {
		employeeButton.setVisible(false);
		managerButton.setVisible(false);
		//mainContent.add(scrolled);
		//textContent.setVisible(true);
		mainContent.remove(employeeButton);
		mainContent.remove(managerButton);
		mainContent.setLayout(new FlowLayout(FlowLayout.LEFT));

	}
	
	public void onMakeReservationClick(ActionEvent e) {
		System.out.println("The make reservation button was clicked");
		//textContent.append("The make reservation button was clicked\n");
	}
	
	public void onViewReservationClick(ActionEvent e) {
		System.out.println("The view reservation button was clicked");
		//textContent.append("The view reservation button was clicked\n");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
