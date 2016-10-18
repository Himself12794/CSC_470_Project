package edu.uncfsu.softwaredesign.f16.r2;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.card.IApplicationCard;

/**
 * Required for finer control. Although a card may be loaded by Spring, this does not mean
 * it is usable.
 * 
 * @author phwhitin
 *
 */
public class CardRegistry {

	private final Map<String, IApplicationCard> cardRegistry = Maps.newHashMap();
	
	/**
	 * Registers a card if it does not already exist.
	 * 
	 * @param card
	 */
	public void registerCard(IApplicationCard card) {
		cardRegistry.putIfAbsent(card.getName(), card);
	}
	
	public List<IApplicationCard> getAllCards() {
		return Lists.newArrayList(cardRegistry.values());
	}
	
}
