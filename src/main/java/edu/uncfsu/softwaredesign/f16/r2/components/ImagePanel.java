package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	private static final long serialVersionUID = 7999242502601932849L;
	
	private BufferedImage image;

	public ImagePanel(String file) {
		this(new File(file));
	}
	
    public ImagePanel(File file) {
       try {                
          this.image = ImageIO.read(file);
          setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
       } catch (IOException ex) {
            // handle exception...
       }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);            
    }

}