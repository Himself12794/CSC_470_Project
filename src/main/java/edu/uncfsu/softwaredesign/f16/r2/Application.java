package edu.uncfsu.softwaredesign.f16.r2;

import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;

import com.google.common.collect.Maps;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import edu.uncfsu.softwaredesign.f16.r2.components.JImagePanel;
import edu.uncfsu.softwaredesign.f16.r2.components.OpeningImage;
import edu.uncfsu.softwaredesign.f16.r2.components.card.CardRegistry;
import edu.uncfsu.softwaredesign.f16.r2.components.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.components.card.IndexCard;
import edu.uncfsu.softwaredesign.f16.r2.components.card.RegisterReservationCard;
import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.util.MenuBuilder;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

@SpringBootApplication(exclude=JmsAutoConfiguration.class)
public class Application extends JFrame {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getSimpleName());
	private static final long serialVersionUID 		= -3885000271483573087L;
	
	private static Application THE_APP;
	
	public static final String APP_NAME 			= "Reservation Management System";
	
	{
		try { 
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Exception e) {
			LOGGER.error("Could not load separate theme", e);
			LOGGER.error("Using default");
		}
	}
	
	@Autowired
	public CardRegistry cardRegistry;
	@Autowired
	private ReservationRegistry reservationRegistry;
	@Autowired
	private CostRegistry costREgistry;
	
	private final JMenuBar menuBar					= new JMenuBar();
	private final CardLayout mainLayout				= new CardLayout();
	private final JPanel mainContent				= new JPanel(mainLayout);
	private final JMenuItem logIn 					= new JMenuItem("Login");
	private final Map<String, Integer> menus 		= Maps.newHashMap();
	private final Map<JMenu, List<IApplicationCard>> menuToCard = Maps.newHashMap();
	
	private boolean isElevated = false;
	private IApplicationCard currentCard = null;
	
	public Application() {
		super();
		setTitle(APP_NAME);
		setSize(1000, 600);
		setIconImage(new JImagePanel("img/icon.png").getImage());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		THE_APP = this;
	}

	void buildComponentCards() {
		cardRegistry.forEach(IApplicationCard::buildComponent);
	}
	
	void buildMenus() {
		
		MenuBuilder builder = (new MenuBuilder()).addMenu("File");
		
		cardRegistry.forEach(c -> c.buildMenu(builder, this));
		
		builder.addMenu("File").addMenuItem(logIn).addItemAction(a -> {
			if (!isElevated) elevate();
			else deElevate();
		});
		
		builder.addMenu("File").addMenuItem("Close").addItemAction(l -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
			
		setJMenuBar(builder.build());
	}
	
	void buildLayouts() {
		
		add(mainContent);
		
		cardRegistry.getAllCards().forEach(card -> mainContent.add(card.getComponent(), card.getName()));
		
	}
	
	public void setCurrentCard(String name) {

		IApplicationCard card = cardRegistry.getCard(name);
		
		boolean doLoad = currentCard == null || (currentCard != null && card.canBeSelected() && currentCard.onNavigateAway(card));
		
		if (doLoad) {
			
			if (!card.requiresElevation()) {
				loadCard(card);
			} else if (!isElevated) {
				if (elevate()) loadCard(card);
			} else loadCard(card);
		}
	}
	
	private void loadCard(IApplicationCard card) {

		mainLayout.show(mainContent, card.getName());
		
		card.reload();
		currentCard = card;
	}
	
	/**
	 * Grants manager privileges for current session.
	 * 
	 * @return whether or not the user password matches
	 */
	public boolean elevate() {
		
		if (!isElevated) {
			if (Arrays.equals("password".toCharArray(), Utils.queryPassword())) {
				isElevated = true;
			} else {
				Utils.generateErrorMessage("Sorry, the entered password is incorrect", "Invalid Password");
			}
		}
		
		logIn.setText("Logout");
		/*StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		String encryptedPassword = passwordEncryptor.encryptPassword(userPassword);
		
		if (passwordEncryptor.checkPassword(inputPassword, encryptedPassword)) {
		  // correct!
		} else {
		  // bad login!
		}*/
		
		return isElevated;
	}
	
	public void deElevate() {
		isElevated = false;
		logIn.setText("Login");
		if (currentCard != null && currentCard.requiresElevation()) {
			setCurrentCard(IndexCard.TITLE);
		}
	}
	
	public boolean isElevated() {
		return isElevated;
	}
	
	public ReservationRegistry getReservationRegistry() {
		return reservationRegistry;
	}
	
	public CostRegistry getCostRegistry() {
		return costREgistry;
	}
	
	public static Application getApp() {
		return THE_APP;
	}
	
	public static void main(String[] args) {
		
		// An image to display while the application context is loading
		//OpeningImage open = new OpeningImage("img/taco_tuesday.png");
		OpeningImage open = new OpeningImage("img/generic.png");
		open.setVisible(true);
		
		SwingUtilities.invokeLater(() -> {
			
			Application app = SpringApplication.run(Application.class, args).getBean(Application.class);
	
			// We have to do this after the context has been loaded to ensure all components have registered themselves
			app.buildComponentCards();
			app.buildLayouts();
			app.buildMenus();
			app.setCurrentCard(RegisterReservationCard.TITLE);
			app.pack();
			app.setVisible(true);
			open.dispose();
			
		});
	}
}
