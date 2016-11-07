package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class OpeningImage extends JFrame {

	private static final long serialVersionUID = 6780329545467950660L;

	private final JPanel panel = new JPanel();
	private final JImagePanel image;
	
	public OpeningImage(String location) {
		this(location, null);
	}
	
	public OpeningImage(String imageLocation, Color color) {
		super();
		image = new JImagePanel(imageLocation);
		setIconImage(new JImagePanel("img/icon.png").getImage());
		setLayout(new FlowLayout(FlowLayout.CENTER));
		if (color != null) getContentPane().setBackground(color);
		setUndecorated(true);
		add(image);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
}
