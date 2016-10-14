package edu.uncfsu.softwaredesign.f16.r2;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.google.common.collect.Lists;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;

@SpringBootApplication(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Application extends JFrame {

	private static final long serialVersionUID = -3885000271483573087L;
	private static final String APP_NAME = "Reservation Management System";
	private static final Object[] TABLE_LABELS = new Object[] {"Id", "Customer", "Reserve Date", "Registered Date", "Days", "Total Cost", "Has Paid"};
	private static final GridBagConstraints GBC = new GridBagConstraints();
	private static final String BUTTONS = "Buttons";
	private static final String VIEW_RESERVATIONS = "View";
	private static final String INDEX = "Index";
	
	static {
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
	}
	
	@Autowired
	private ReservationRegistry reservationRegistry;
	
	// Menu items
	private final JMenuBar menuBar			= new JMenuBar();
	private final JMenu reservationMenu		= new JMenu("Reservation");
	private final JMenuItem makeReservation = new JMenuItem("Make Reservation");
	private final JMenuItem viewReservation = new JMenuItem("View Reservations");
	private final JMenu fileMenu			= new JMenu("File");
	private final JMenuItem indexOption		= new JMenuItem("Index");
	private final JMenuItem closeOption		= new JMenuItem("Close");

	// Main content
	private final JPanel mainContent		= new JPanel(new CardLayout());
	private final JPanel buttonContent		= new JPanel(new FlowLayout(FlowLayout.CENTER));
	private final JPanel viewContent		= new JPanel(new GridBagLayout());
	private final JButton managerButton		= new JButton("Login as Management");
	private final JButton employeeButton	= new JButton("Login as Employee");
	private final JTable reservationTable	= new JTable();
	private final JScrollPane tablePane		= new JScrollPane(reservationTable);
	private final JLabel indexPane			= new JLabel();
	
	// This holds all label reservation items for view reservations
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
		fileMenu.add(indexOption);
		indexOption.addActionListener(this::onIndexClick);
		fileMenu.add(closeOption);
		closeOption.addActionListener(l -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		
		menuBar.add(reservationMenu);
		reservationMenu.add(makeReservation);
		makeReservation.addActionListener(this::onMakeReservationClick);
		reservationMenu.add(viewReservation);
		viewReservation.addActionListener(this::onViewReservationClick);
		
	}
	
	@SuppressWarnings("serial")
	private void buildLayout() {
		
		add(mainContent);
		
		mainContent.add(buttonContent, BUTTONS);
		buttonContent.add(managerButton);
		managerButton.addActionListener(l -> {
			isManager = true;
			onIndexClick(null);
		});
		
		buttonContent.add(employeeButton);
		employeeButton.addActionListener(l -> {
			isManager = false;
			onIndexClick(null);
		});
		

		mainContent.add(viewContent, VIEW_RESERVATIONS);
		viewContent.add(tablePane, GBC);
		
		reservationTable.setModel(new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		
		reservationTable.getTableHeader().setReorderingAllowed(false);
		
		buildIndex();
		mainContent.add(indexPane, INDEX);
		indexPane.setVerticalAlignment(SwingConstants.TOP);
	}
	
	public void buildTable() {
		
		DefaultTableModel table = (DefaultTableModel) reservationTable.getModel();
		
		List<Reservation> reservations = reservationRegistry.getReservations();
		Object[][] tableData = new Object[reservations.size()][7];
		//Object[][] tableData = new Object[1][7];
		
		for (int i = 0; i < reservations.size(); i++) {
			Reservation res = reservations.get(i);  
			tableData[i] = new Object[]{ new Long(res.getReservationId()), res.getCustomer(), res.getReservationDate(),
					res.getRegistrationDate(), new Integer(res.getDays()), String.format("%.2f", res.getTotalCost()), new Boolean(res.isHasPaid())};
		}
		//tableData[0] = new Object[]{ new Long(1L), "John Doe", LocalDate.now(),
		//		LocalDate.now().plusWeeks(4), new Integer(2), String.format("$%.2f", 1000.45F), new Boolean(true)};
		
		table.setDataVector(tableData, TABLE_LABELS);
	}
	
	public void buildIndex() {
		InputStream in = getClass().getClassLoader().getResourceAsStream("index.html");
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(in, writer);
			indexPane.setText(writer.toString());
		} catch (IOException e) {
		}
	}

	
	public void onIndexClick(ActionEvent e) {
		((CardLayout)mainContent.getLayout()).show(mainContent, INDEX);
	}
	
	public void onMakeReservationClick(ActionEvent e) {
		System.out.println("The make reservation button was clicked");
	}
	
	public void onViewReservationClick(ActionEvent e) {
		System.out.println("The view reservation button was clicked");
		buildTable();
		((CardLayout)mainContent.getLayout()).show(mainContent, VIEW_RESERVATIONS);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
