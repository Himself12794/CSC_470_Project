package edu.uncfsu.softwaredesign.f16.r2.util;

import java.awt.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.google.common.collect.Range;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public final class Utils {

	private Utils() {}
	
	public static <K, V> K getFirstKeyOfType(Map<K, V> map, V value) {
		return map.entrySet().stream().reduce((r1,r2) -> r1.getValue().equals(value) ? r1 : r2).get().getKey();
	}
	
	/**
	 * Rethrows as a {@link NoSuchReservationTypeException}
	 * 
	 * @param ex
	 * @param type
	 * @throws NoSuchReservationTypeException
	 */
	public static void rethrowException(NoSuchElementException ex, String type) throws NoSuchReservationTypeException {
		throw new NoSuchReservationTypeException(type);
	}
	
	public static Date todayPlus(int days) {
		return Date.from(LocalDate.now().plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public static Range<Date> getAppropriateRange(final Date start, final Date end) {

		if (nonNullBoth(start, end)) {
			return nonNullComparison(start, end);
		} else if (nullFirstOnly(start, end)) {
			return Range.atMost(end);
		} else if (nullLastOnly(start, end)) {
			return Range.atLeast(start);
		} else {
			return Range.all();
		}

	}
	
	/**
	 * This checks the range and swaps them if they were given in the wrong
	 * order.
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static Range<Date> nonNullComparison(final Date start, final Date end) {

		final int c = start.compareTo(end);

		if (c < 0) {
			return Range.closed(start, end);
		} else if (c > 0) {
			return Range.closed(end, start);
		} else {
			return Range.singleton(start);
		}

	}

	public static boolean nonNullBoth(final Object arg0, final Object arg1) {
		return arg0 != null && arg1 != null;
	}

	public static boolean nullFirstOnly(final Object arg0, final Object arg1) {
		return arg0 == null && arg1 != null;
	}

	public static boolean nullLastOnly(final Object arg0, final Object arg1) {
		return arg0 != null && arg1 == null;
	}
	
	public static boolean isInt(String text) {
		try {
			Integer.valueOf(text);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static boolean isAlpha(String text) {
		return text.length() == text.replaceAll("\\d", "").length();
	}
	
	public static String createErrorMessageForInvalidValues(Map<String, Object> values) {
		StringBuilder msg = new StringBuilder("Invalid values: ");
		
		values.forEach((k,v) -> msg.append("attribute \"").append(k).append("\" has invalid value \"").append(v).append("\"; ")	);
		
		return msg.toString();
	}
	
	public static String createErrorMessageForFullRegistry(List<LocalDate> dates) {
		
		StringBuilder builder = new StringBuilder("Could not register/update reservation, the following days are full: ");
		
		dates.forEach(d -> builder.append(d).append(", "));
		
		return builder.toString();
	}
	
	public static Stream<LocalDate> dateStream(Reservation reserve) {
		return dateStream(reserve.getReservationDate(), reserve.getDays());
	}
	
	public static Stream<LocalDate> dateStream(LocalDate start, int days) {
		return IntStream.range(0, days).boxed().map(start::plusDays);
	}
	
	public static void sleep(long millis) {
		
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
		
	}
	
	public static void generateErrorMessage(String msg, String title) {
		JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public static char[] queryPassword() {
		
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter manager password: ");
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[]{"OK", "Cancel"};
		int option = JOptionPane.showOptionDialog(null, panel, "Login",
		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		                         null, options, options[1]);
		
		char[] password = "".toCharArray();
		
		if(option == 0) {
		    password = pass.getPassword();
		}
		
		return password;
	}
	
	public static boolean confirmDialog(Component frame, String message) {

		Object[] options = { "Yes", "No" };
		int res = JOptionPane.showOptionDialog(frame, 
				"Are you sure you want to " + message + "?", "Confirm", 
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, 
				null, options, options[1]);
		
		return res == 0;
	}
	
}
