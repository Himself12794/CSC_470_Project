package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;

@Component
public class ViewReservationsCard extends AbstractCard implements IApplicationCard {

	private static final long serialVersionUID = 2369089937389802912L;
	
	private static final Object[] TABLE_LABELS 		= new Object[] {"Id", "Customer", "Reserve Date", "Registered Date", "Days", "Total Cost", "Has Paid"};
	private static final GridBagConstraints GBC	 	= new GridBagConstraints();
	
	static {
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
	}
	
	private final JTable reservationTable	= new JTable();
	private final JScrollPane tablePane		= new JScrollPane(reservationTable);
	private final List<Reservation> reserve	= Lists.newArrayList();
	
	@Autowired
	private ReservationRegistry reservationRegistry;
	
	@Autowired
	private Application theApp;
	
	public ViewReservationsCard() {
		super("View");
		setLayout(new GridBagLayout());
	}

	@SuppressWarnings("serial")
	@Override
	public void buildComponent() {

		buildTable();
		
		add(tablePane, GBC);
		
		reservationTable.setModel(new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		reservationTable.getTableHeader().setReorderingAllowed(false);
		reservationTable.addMouseListener(new MouseAdapter(){
			
			public void mousePressed(MouseEvent e) {
				
			}
			
		});
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
		reserve.clear();
		reserve.addAll(reservations);
		revalidate();
		repaint();
	}
	
	@Override
	public String getMenuName() {
		return "Reservation";
	}

	@Override
	public String getMenuItemName() {
		return "View Reservations";
	}

	@Override
	public void reload() {
		SwingUtilities.invokeLater(this::buildTable);
		SwingUtilities.invokeLater(() -> {
			revalidate();
			repaint();
			
			//new ModifyReservation(theApp, reservationRegistry.getReservations().get(0)).setVisible(true);
			
		});
	}
	
}
