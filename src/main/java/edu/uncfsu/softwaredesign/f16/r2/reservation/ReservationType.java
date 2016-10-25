package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.Serializable;

public enum ReservationType implements Serializable {

	PRE_PAID("Pre-paid", 1.1F, 0.75F, 90, true),
	DAYS_ADVANCED_60("60 Days Advanced", 1.1F, 0.85F, 60, false),
	CONVENTIONAL("Conventional", 1.0F, 1.0F, 1, true);
	
	public final String typeName;
	public final float changeFee;
	public final float costModifier;
	public final int minimumDays;
	public final boolean isPrepaid;
	
	ReservationType(String name, float changeFee, float costModifier, int minimumDays, boolean isPrepaid) {
		this.typeName = name;
		this.changeFee = changeFee;
		this.costModifier = costModifier;
		this.minimumDays = minimumDays;
		this.isPrepaid = isPrepaid;
	}
	
	public static ReservationType getByIdentifier(String identifier) {

		for (ReservationType type : values()) {
			if (type.typeName.equals(identifier)) {
				return type;
			}
		}
		
		return null;
	}
	
	public String toString() {
		return typeName;
	}
	
	public static String[] getNames() {
		String[] names = new String[values().length];
		
		int i = 0;
		for (ReservationType type :  values()) {
			names[i] = type.name();
		}
		
		return names;
	}
	
}
