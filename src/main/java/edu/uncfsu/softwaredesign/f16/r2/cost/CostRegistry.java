package edu.uncfsu.softwaredesign.f16.r2.cost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;

/**
 * Keeps track of room costs past, present, and future. 
 * 
 * @author phwhitin
 *
 */
@Repository
public class CostRegistry implements Reportable {
	
	private static final long serialVersionUID = -883446370785660735L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CostRegistry.class.getSimpleName());

	private static final float DEFAULT_COST = 200.0F;
	private static final String SAVE_LOCATION = "data/";
	private static final File SAVE_FILE	= new File(SAVE_LOCATION, "cost-registry.dat");
	
	final Map<LocalDate, Float> costs = Maps.newHashMap();
	
	private final Map<Season, Modifier> seasonalModifiers = Maps.newEnumMap(Season.class); 
	
	final File saveFile;
	
	public CostRegistry() {
		this(SAVE_FILE);
	}
	
	public CostRegistry(File file) {
		saveFile = file;
	}
	
	public float getCostForDay(LocalDate date) {
		return seasonalModifiers.getOrDefault(Season.getSeasonFor(date), Modifier.IDENTITY).modify(costs.getOrDefault(date, DEFAULT_COST));
	}
	
	public void setCostForDay(LocalDate localDate, float cost) {
		costs.put(localDate, cost);
		saveToDisk();
	}
	
	/**
	 * Modifiers for specific seasons. These are additive
	 * 
	 * @param season
	 * @param modifier
	 */
	public void setSeasonalModifier(Season season, Modifier modifier) {
		seasonalModifiers.put(season, modifier);
		saveToDisk();
	}
	
	public static class Modifier implements Serializable {
		
		private static final long serialVersionUID = -8242814975170627224L;
		
		public static final byte MUPLICATIVE = 1;
		public static final byte ADDITIVE = 0;
		public static final Modifier IDENTITY = Modifier.of(MUPLICATIVE, 1);
		public static final Modifier ZERO = Modifier.of(MUPLICATIVE, 0);
		
		private final byte type;
		private final float value;
		
		private Modifier(byte type, float value) {
			this.type = type;
			this.value = value;
		}
		
		public static Modifier of(byte type, float value) {
			return new Modifier(type, value);
		}
		
		public float modify(float value) {
			return type == Modifier.ADDITIVE ? value + this.value : value * this.value;
		}
		
		public byte getType() {
			return type;
		}
		
		public float getValue() {
			return value;
		}
		
	}

	@Override
	public void writeReport(OutputStream out) {
	}

	@Override
	public String getReport() {
		return "";
	}
	
	void saveToDisk() {
		
		try {

			FileUtils.forceMkdir(saveFile.getParentFile());
			
			FileOutputStream fos = new FileOutputStream(saveFile, false);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(costs);
			oos.writeObject(seasonalModifiers);
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			LOGGER.error("An unexpected exception has occurred during saving data to disk", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void loadFromDisk() {

		if (saveFile.exists()) {
			try {
				
				FileInputStream fis = new FileInputStream(saveFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				Map<LocalDate, Float> costs = (Map<LocalDate, Float>)ois.readObject();
				Map<Season, Modifier> modifiers = (Map<Season, Modifier>)ois.readObject();
				
				ois.close();
				
				this.costs.putAll(costs);
				seasonalModifiers.putAll(modifiers);
				
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
