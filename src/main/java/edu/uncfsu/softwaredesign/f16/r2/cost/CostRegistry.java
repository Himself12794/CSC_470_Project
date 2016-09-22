package edu.uncfsu.softwaredesign.f16.r2.cost;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class CostRegistry {

	private static final float DEFAULT_COST = 500.0F;
	
	private final Map<LocalDate, Float> costs = Maps.newHashMap();
	
	public float getCostForDay(LocalDate date) {
		return costs.getOrDefault(date, DEFAULT_COST);
	}
	
}
