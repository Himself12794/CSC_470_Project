package edu.uncfsu.softwaredesign.f16.r2;

import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import edu.uncfsu.softwaredesign.f16.r2.components.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;

@SpringBootApplication(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Application extends JFrame {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getSimpleName());
	
	private static final long serialVersionUID 		= -3885000271483573087L;
	private static final String APP_NAME 			= "Reservation Management System";
	private static final String BUTTONS 			= "Buttons";
	private static final String VIEW_RESERVATIONS 	= "View";
	private static final String INDEX 				= "Index";
	
	public static final CardRegistry cardRegistry = new CardRegistry();
	
	@Autowired
	private ReservationRegistry reservationRegistry;
	
	private final JMenuBar menuBar					= new JMenuBar();
	private final JPanel mainContent				= new JPanel(new CardLayout());
	private final Map<String, Integer> menus 		= Maps.newHashMap();
	private final Map<JMenu, List<IApplicationCard>> menuToCard = Maps.newHashMap();
	
	private boolean isManager = false;
	private IApplicationCard currentCard = null;
	
	public Application() {

		try { 
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e) {
			LOGGER.error("Could not load separate theme", e);
			LOGGER.error("Using default");
		}
		setTitle(APP_NAME);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
	}

	void buildComponentCards() {
		cardRegistry.getAllCards().forEach(IApplicationCard::buildComponent);
	}
	
	void buildMenus() {

		setJMenuBar(menuBar);
		getMakeMenu("File");
		
		cardRegistry.getAllCards().forEach(card -> {
				
			JMenu menu = getMakeMenu(card.getMenuName());
			
			menu.add(card.getMenuOptionName()).addActionListener(action -> setCurrentCard(card.getName()));
			
			menuToCard.putIfAbsent(menu, Lists.newArrayList());
			menuToCard.get(menu).add(card);
			
		});
		
		menuToCard.forEach((k,v) -> {
			
		});
		
		getMakeMenu("File").add("Close")
			.addActionListener(l -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));;
		
	}
	
	void buildLayouts() {
		
		add(mainContent);
		
		cardRegistry.getAllCards().forEach(card -> mainContent.add(card.getComponent(), card.getName()));
		
	}
	
	private JMenu getMakeMenu(String menu) {
		int ix = menus.compute(menu, (k,v) -> {
			
			int i = v == null ? menuBar.getMenuCount() : v;
			
			if (v == null) {
				menus.put(menu, i);
				menuBar.add(new JMenu(menu));
			} 
			
			return i;
		});
			
		return menuBar.getMenu(ix);
	}
	
	public void setCurrentCard(String name) {

		IApplicationCard card = cardRegistry.getCard(name);
		
		boolean doLoad = currentCard == null || (currentCard != null && card.canBeSelected() && currentCard.onNavigateAway(card));
		
		if (doLoad) {
			((CardLayout)mainContent.getLayout()).show(mainContent, card.getName());
			
			card.reload();
			currentCard = card;
		}
	}
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			Application app = SpringApplication.run(Application.class, args).getBean(Application.class);
	
			// We have to do this after the context has been loaded to ensure all components have registered themselves
			app.buildComponentCards();
			app.buildLayouts();
			app.buildMenus();
			app.setCurrentCard(INDEX);
			app.setVisible(true);
		});
	}
}
