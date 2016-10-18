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
import java.util.Map;

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
import org.springframework.context.annotation.Import;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.card.RegistrationFormCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;

@SpringBootApplication(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Application extends JFrame {

	private static final long serialVersionUID 		= -3885000271483573087L;
	private static final String APP_NAME 			= "Reservation Management System";
	private static final Object[] TABLE_LABELS 		= new Object[] {"Id", "Customer", "Reserve Date", "Registered Date", "Days", "Total Cost", "Has Paid"};
	private static final GridBagConstraints GBC	 	= new GridBagConstraints();
	private static final String BUTTONS 			= "Buttons";
	private static final String VIEW_RESERVATIONS 	= "View";
	private static final String INDEX 				= "Index";
	
	static {
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
	}
	
	@Autowired
	private ReservationRegistry reservationRegistry;
	
	@Autowired
	private CardRegistry cardRegistry;
	
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
	private final Map<String, Integer> menus = Maps.newHashMap();
	private final Map<JMenu, List<IApplicationCard>> menuToCard = Maps.newHashMap();
	
	private boolean isManager = false;
	
	public Application() {

		setTitle(APP_NAME);
		//buildMenu();
		//buildLayout();
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		
	}
	
	/**
	 * Builds the menu. Called on startup.
	 */
	void buildMenu() {
		
		setJMenuBar(menuBar);
		
		menuBar.add(fileMenu)
			.add(indexOption)
				.addActionListener(this::onIndexClick);
		
			fileMenu.add(closeOption)
				.addActionListener(l -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		
		menuBar.add(reservationMenu)
			.add(makeReservation)
				.addActionListener(this::onMakeReservationClick);
		
			reservationMenu.add(viewReservation)
				.addActionListener(this::onViewReservationClick);
		
	}
	
	void bMs() {

		setJMenuBar(menuBar);
		
		cardRegistry.getAllCards().forEach(card -> {
			
			int ix = menus.compute(card.getMenuName(), (k,v) -> {
			
				int i = v == null ? menuBar.getMenuCount() : v;
				
				if (v == null) {
					menus.put(card.getMenuName(), i);
					menuBar.add(new JMenu(card.getMenuName()));
				} 
				
				return i;
			});
				
			JMenu menu = menuBar.getMenu(ix);
			
			menu.add(card.getMenuOptionName()).addActionListener(action -> {
				card.reload();
				((CardLayout)mainContent.getLayout()).show(mainContent, card.getName());
			});
			
			menuToCard.putIfAbsent(menu, Lists.newArrayList());
			menuToCard.get(menu).add(card);
			
		});
		
		menuToCard.forEach((k,v) ->{
			k.addActionListener(action -> v.forEach(IApplicationCard::reload));
		});
		
		menuBar.getMenu(menus.get("File")).add(closeOption)
			.addActionListener(l -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));;
		
	}
	
	void bLs() {
		
		add(mainContent);
		
		cardRegistry.getAllCards().forEach(card -> add(card.getComponent(), card.getName()));
		
	}
	
	@SuppressWarnings("serial")
	void buildLayout() {
		
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
		
		for (int i = 0; i < reservations.size(); i++) {
			Reservation res = reservations.get(i);  
			tableData[i] = new Object[]{ new Long(res.getReservationId()), res.getCustomer(), res.getReservationDate(),
					res.getRegistrationDate(), new Integer(res.getDays()), String.format("%.2f", res.getTotalCost()), new Boolean(res.isHasPaid())};
		}
		
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

	void loadRegisteredComponents() {
		
	}
	
	public JPanel getMainContent() {
		return mainContent;
	}
	
	public void onIndexClick(ActionEvent e) {
		((CardLayout)mainContent.getLayout()).show(mainContent, INDEX);
	}
	
	public void onMakeReservationClick(ActionEvent e) {
		((CardLayout)mainContent.getLayout()).show(mainContent, RegistrationFormCard.TITLE);
	}
	
	public void onViewReservationClick(ActionEvent e) {
		buildTable();
		((CardLayout)mainContent.getLayout()).show(mainContent, VIEW_RESERVATIONS);
	}
	
	public static void main(String[] args) {
		Application app = SpringApplication.run(Application.class, args).getBean(Application.class);
		
		app.bLs();
		app.bMs();
	}
}
