package edu.uncfsu.softwaredesign.f16.r2.reporting;

import java.io.OutputStream;

/**
 * Indicates an object that generates a report
 * 
 * @author phwhitin
 *
 */
public interface Reportable {

	void writeReport(OutputStream out);
	
	String getReport();
	
}
