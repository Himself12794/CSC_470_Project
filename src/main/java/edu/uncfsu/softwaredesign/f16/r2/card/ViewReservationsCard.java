package edu.uncfsu.softwaredesign.f16.r2.card;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;

public class ViewReservationsCard implements IApplicationCard {

	private static final Object[] TABLE_LABELS 		= new Object[] {"Id", "Customer", "Reserve Date", "Registered Date", "Days", "Total Cost", "Has Paid"};
	private static final GridBagConstraints GBC	 	= new GridBagConstraints();
	
	static {
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
	}
	
	private final JPanel viewContent		= new JPanel(new GridBagLayout());
	private final JTable reservationTable	= new JTable();
	private final JScrollPane tablePane		= new JScrollPane(reservationTable);
	
	@Autowired
	private ReservationRegistry reservationRegistry;
	
	@Override
	public String getName() {
		return "View";
	}

	@Override
	public Container getComponent() {
		return viewContent;
	}

	@SuppressWarnings("serial")
	@Override
	public void buildComponent() {

		buildTable();
		
		viewContent.add(tablePane, GBC);
		
		reservationTable.setModel(new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		reservationTable.getTableHeader().setReorderingAllowed(false);
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
	
	@Override
	public String getMenuName() {
		// TODO Auto-generated method stub
		return "Reservation";
	}

	@Override
	public String getMenuOptionName() {
		// TODO Auto-generated method stub
		return "View Reservations";
	}

	@Override
	public void reload() {
		buildTable();
	}

}
