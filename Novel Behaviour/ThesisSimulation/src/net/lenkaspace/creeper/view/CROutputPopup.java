package net.lenkaspace.creeper.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import net.lenkaspace.creeper.model.CRSettings;

/**
 * The output panel. Displays in a separate thread in order to provide run-time data. 
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CROutputPopup extends CRTextPopup implements ActionListener  {

	private JTextPane timeOutput;
	private String timeOutputText;
		
	//==================================== SINGLETON ====================================
	private static CROutputPopup ref;
	
	public static CROutputPopup getSingleton()	  {
		  if (ref == null)
			  ref = new CROutputPopup();		
		  return ref;
	}
	
	public CROutputPopup() {
		super("OUTPUT", new Dimension(550,550), new Point((int)(CRSettings.getSingleton().getWindowSize().width - 550),0));
	}
	
	//==================================== DISPLAYING ====================================
	
	/**
	 * Create all components
	 */
	protected void createComponents() {
		JPanel timeOutputPanel = new JPanel();
		timeOutputPanel.setBackground(Color.WHITE);
		
		timeOutput = new JTextPane();	
		timeOutput.setPreferredSize(new Dimension(size.width-20, 40));
		timeOutputPanel.add(timeOutput);
		timeOutput.setText(timeOutputText);
		
		baseFrame.getContentPane().add(timeOutputPanel, BorderLayout.NORTH);
		super.createComponents();
		
		output.setPreferredSize(new Dimension(size.width-20, size.height-20-40));
	}
	
	/**
	 * Called each time interval.
	 */
	public void actionPerformed(ActionEvent e) {
    	this.display();
		
	}
	
	/**
	 * Clear current output and set new one. Force open if closed.
	 */
	public void display() {
		if (!isOpen) {
			this.open();
		}
		
		output.setText(outputText);
		timeOutput.setText(timeOutputText);
	}
	
	//==================================== OUTPUT CONTROL ================================
	
	/**
	 * Clear current output
	 */
	public void clearOutput() {
		outputText = "";
		timeOutputText = "";
	}
		
	
	/**
	 * Add to small top output text that will be displayed when actionPerformed is called
	 * @param text string
	 */
	public void displayTimeOutput(String text) {
		timeOutputText += text +  "\n";
	}
	
	/**
	 * Add to main output text that will be displayed when actionPerformed is called
	 * @param text string
	 */
	public void displayOutput(String text) {
		outputText += text +  "\n";
	}
}
