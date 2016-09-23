package edu.uncfsu.softwaredesign.f16.r2.cost;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;

/**
 * Keeps track of room costs past, present, and future. 
 * 
 * @author phwhitin
 *
 */
@Service
public abstract class CostRegistry implements Reportable {
	
	private static final float DEFAULT_COST = 200.0F;
	
	private final Map<LocalDate, Float> costs = Maps.newHashMap();
	
	private final Map<Season, Modifier> seasonalModifiers = Maps.newEnumMap(Season.class); 
	
	public float getCostForDay(LocalDate date) {
		return seasonalModifiers.getOrDefault(Season.getSeasonFor(date), Modifier.IDENTITY).modify(costs.getOrDefault(date, DEFAULT_COST));
	}
	
	public void setCostForDay(LocalDate localDate, float cost) {
		costs.put(localDate, cost);
	}
	
	/**
	 * Modifiers for specific seasons. These are additive
	 * 
	 * @param season
	 * @param modifier
	 */
	public void setSeasonalModifier(Season season, Modifier modifier) {
		seasonalModifiers.put(season, modifier);
	}
	
	public static class Modifier {
		
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
	
}
