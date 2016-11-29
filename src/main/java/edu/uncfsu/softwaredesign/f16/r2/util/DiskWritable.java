package edu.uncfsu.softwaredesign.f16.r2.util;

/**
 * A class that saves and loads data to disc
 * 
 * @author phwhitin
 *
 */
public interface DiskWritable {

	void loadFromDisk();
	
	void saveToDisk();
	
}
