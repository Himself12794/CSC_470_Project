package edu.uncfsu.softwaredesign.f16.r2.cost;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;

import org.junit.Test;

public class TestCostRegistry {

	@Test
	public void testCostRegistry() {
		CostRegistry costs = new CostRegistry(new File("data/cost-registry-test.dat"));
		
		costs.setCostForDay(LocalDate.now(), 250);
		costs.saveToDisk();
		
		costs.costs.clear();
		
		costs.loadFromDisk();
		costs.costs.forEach((k,v) -> {
			System.out.println(k + " " + v);
		});
		assertTrue(costs.getCostForDay(LocalDate.now()) == 250);
		
	}
	
}
