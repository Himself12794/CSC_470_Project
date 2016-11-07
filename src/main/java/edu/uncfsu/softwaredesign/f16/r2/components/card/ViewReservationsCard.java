package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.components.JReservationPopup;
import edu.uncfsu.softwaredesign.f16.r2.components.JTextFieldLimit;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

@Component
public class ViewReservationsCard extends AbstractCard implements IApplicationCard {

	private static final long serialVersionUID = 2369089937389802912L;
	
	private static final Object[] TABLE_LABELS 		= new Object[] {"Id", "Customer", "Room", "Checked-In", "Checked-Out", "Reservation Date", "Days", "Total Cost", "Has Paid", "Canceled"};
	private static final GridBagConstraints GBC	 	= new GridBagConstraints();
	
	static {
		GBC.weightx = GBC.weighty = 1.0;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.gridx = 0;
	}

	private final JDateChooser startChooser	= new JDateChooser();
	private final JDateChooser endChooser	= new JDateChooser();
	private final JButton searchButton		= new JButton("Search");
	private final JButton clearButton		= new JButton("Clear");
	private final JTextField idField		= new JTextField();
	private final JCheckBox showPast		= new JCheckBox("Show Past Entries", false);
	private final JCheckBox showFuture		= new JCheckBox("Show Future Entries", false);
	private final JCheckBox showCanceled	= new JCheckBox("Show Canceled Entries", false);
	
	private final JTable reservationTable	= new JTable();
	private final JScrollPane tablePane		= new JScrollPane(reservationTable);
	private final List<Reservation> reserve	= Lists.newArrayList();
	
	private Predicate<Reservation> inclusionFilter = r -> r.isCurrent();
	
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
		
		showPast.addActionListener(a -> reload());
		showFuture.addActionListener(a -> reload());
		showCanceled.addActionListener(a -> reload());
		
		idField.setDocument(new JTextFieldLimit(10));
		searchButton.addActionListener(e -> {
			
			Predicate<Reservation> include = r -> r.isCurrent();
			
			Range<Date> range = Utils.getAppropriateRange(startChooser.getDate(), endChooser.getDate());
			include = r -> range.contains(Date.from(r.getReservationDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			
			String id = idField.getText().replaceAll(" ", "");
			if (!Strings.isNullOrEmpty(id)) include = include.and(r -> r.getReservationId() == Long.valueOf(id));
			
			include = include.and(r -> r.isCanceled() == showCanceled.isSelected());
			include = include.and(r -> (r.isPast() && showPast.isSelected()) || (r.isFuture() && showFuture.isSelected()));
			
			setInclusionFilter(include);
			
			reload();
			idField.setText(id);
		});
		
		clearButton.addActionListener(e -> {
			startChooser.setDate(null);
			endChooser.setDate(null);
			buildTable();
			reload();
		});
		
		add(buildFormLayout(), GBC);
		
		buildTable();
		add(tablePane, GBC);
		
		reservationTable.setModel(new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		reservationTable.getTableHeader().setReorderingAllowed(false);
		//reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		reservationTable.addMouseListener(new MouseAdapter(){
			
			public void mousePressed(MouseEvent e) {
				
				if (SwingUtilities.isRightMouseButton(e)) {
					
					int row = reservationTable.rowAtPoint(e.getPoint());
					if (!reservationTable.isRowSelected(row)) {
						reservationTable.setRowSelectionInterval(row, row);
					}
				}
				
			}
			
			public void mouseReleased(MouseEvent e) {
				
				if (SwingUtilities.isRightMouseButton(e)) {
					int row = reservationTable.getSelectedRow();
					if (row > -1) {
						int[] rows = reservationTable.getSelectedRows();
						
						JPopupMenu p = new JReservationPopup(ViewReservationsCard.this, reserve.subList(row, row+rows.length));
						p.show(reservationTable, e.getX(), e.getY());
					}
				}
			}
			
		});
		searchButton.doClick();
	}

	public void buildTable() {

		DefaultTableModel table = (DefaultTableModel) reservationTable.getModel();
		
		List<Reservation> reservations = reservationRegistry.getReservations();
		reservations.removeIf(inclusionFilter.negate());
		
		Object[][] tableData = new Object[reservations.size()][7];
		
		for (int i = 0; i < reservations.size(); i++) {
			Reservation res = reservations.get(i);  
			tableData[i] = new Object[]{ res.getReservationId(), res.getCustomer(), res.getRoomNumber() <= 0 ? "Not Assigned" : res.getRoomNumber(), 
					res.isHasCheckedIn(), res.isHasCheckedOut(), res.getReservationDate(), new Integer(res.getDays()), String.format("%.2f", res.getTotalCost()), 
					res.isHasPaid(),res.isCanceled()};
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
		});
	}
	
	/**
	 * Determines which values will be included in the list. Uses
	 * AND logic.
	 * 
	 * @param filter
	 */
	public void setInclusionFilter(Predicate<Reservation> filter) {
		inclusionFilter = filter;
	}
	
	/**
	 * Determines what values will be excluded from the list. Uses
	 * OR logic.
	 * 
	 * @return
	 */
	public Predicate<Reservation> getInclusionFilter() {
		return inclusionFilter;
	}
	
	private JPanel buildFormLayout() {

		FormLayout layout = new FormLayout(
				"15dlu, right:pref, $lcgap, pref, 7dlu, right:pref, $lcgap, pref, 7dlu, right:pref, 7dlu, pref, 7dlu, pref, 7dlu, pref", 
				"10dlu, p, 9dlu, p, 9dlu, p");
		layout.setColumnGroups(new int[][]{{2, 6}, {4, 8}, {10, 12}});
		
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.addSeparator("Search Reservations", cc.rcw(2, 2, 12));
		builder.addLabel("Reservation Date: Start", cc.rc(4, 2));
		builder.add(startChooser, cc.rc(4, 4));
		builder.addLabel("Reservation Date: End", cc.rc(4, 6));
		builder.add(endChooser, cc.rc(4, 8));
		builder.addLabel("Id Number", cc.rc(4, 10));
		builder.add(idField, cc.rc(4, 12));
		builder.add(searchButton, cc.rc(4, 14));
		builder.add(clearButton, cc.rc(4, 16));
		
		builder.add(showPast, cc.rc(6, 2));
		builder.add(showCanceled, cc.rc(6, 6));
		builder.add(showFuture, cc.rc(6, 10));
		
		return builder.getPanel();
	}
	
}
