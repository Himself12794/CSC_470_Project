package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Contains all cards. Cards self-register, if loaded as a component
 * 
 * @author phwhitin
 *
 */
@Repository
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
	
	public IApplicationCard getCard(String name) {
		return cardRegistry.get(name);
	}
	
	/**
	 * For convenience
	 * 
	 * @param action
	 */
	public void forEach(Consumer<? super IApplicationCard> action) {
		getAllCards().forEach(action);
	}
}
