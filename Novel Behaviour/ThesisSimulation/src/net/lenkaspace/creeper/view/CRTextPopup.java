package net.lenkaspace.creeper.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import net.lenkaspace.creeper.model.CRSettings;

/**
 * A basic text popup.
 * For better performance, maintain an instance that opens and closes with (updated) text rather than
 * creating a new popup every time.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRTextPopup {

	protected JTextPane output; 
	protected String outputText;
	protected boolean isOpen;
	protected JFrame baseFrame;
	protected Dimension size;
	protected Point location;
	protected String title;
	
	/**
	 * Constructor
	 * @param title_ String title of the poup
	 * @param size_ Dimension size of the popup window
	 */
	public CRTextPopup(String title_, Dimension size_) {
		size = size_;
		title = title_;
		isOpen = false;
		outputText = "";
		CRSettings settings = CRSettings.getSingleton();
		location = new Point((int)(settings.getWindowSize().width/2 - size.width/2),(int)(settings.getWindowSize().height/2- size.height/2));
		
		baseFrame = new JFrame(title);
		baseFrame.setSize(size.width,size.height);
		baseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		baseFrame.setLocation(location);			
		baseFrame.setEnabled(true); 
		baseFrame.setFocusable(true);
		baseFrame.setResizable(false);
	}
	
	public CRTextPopup(String title_, Dimension size_, Point location_) {
		size = size_;
		title = title_;
		isOpen = false;
		outputText = "";
		location = location_;
		
		baseFrame = new JFrame(title);
		baseFrame.setSize(size.width,size.height);
		baseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		baseFrame.setLocation(location);			
		baseFrame.setEnabled(true); 
		baseFrame.setFocusable(true);
		baseFrame.setResizable(false);
	}
	
	//==================================== DISPLAYING ====================================
	
	/**
	 * Make the output panel visible, create it if it doesn't exist
	 */
	public void open () {
	
		createComponents();
		
		baseFrame.setVisible(true);
		isOpen = true;
	}
	
	/**
	 * Create all displayed components.
	 * Sub classes should override this method to create their own components.
	 */
	protected void createComponents() {
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		
		output = new JTextPane();	
		output.setFont(new Font("Arial", Font.PLAIN, 13));
		output.setPreferredSize(new Dimension(size.width-20, size.height-20));
		output.setText(outputText);
		contentPanel.add(output);
		
		baseFrame.getContentPane().add(contentPanel, BorderLayout.CENTER);
	}
	
	
	//==================================== OUTPUT CONTROL ================================
	
	/**
	 * Set the output text that will be displayed when open() is called
	 * @param text string
	 */
	public void setText(String text) {
		outputText = text;
	}
	
	//==================================== GETTERS / SETTERS==============================
	public void setTitle(String value_) { 
		title = value_;
		baseFrame.setTitle(title);
	}
	public String getTitle() { return title; }
	
	public void setSize(Dimension size_) {
		size = size_;
		baseFrame.setSize(size);
	}
	public Dimension getSize() { return size; }
	
	public void setLocation(Point location_) { location = new Point(location_.x, location_.y); }
	
}
