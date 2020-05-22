package net.lenkaspace.creeper.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeListener;

/**
 * Provides static functions for creating components. Use e.g. by CRControlPanel
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */

public class CRComponentFactory {

	
	//===================================== CONTROL ===============================
	/**
	 * Create a JButton that will resize with text and optionally add it to a Container
	 * @param buttonText_ String text of the button
	 * @param actionListener_ ActionListener action listener. Can be null.
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JButton the button
	 */
	public static JButton createJButton(String buttonText_, ActionListener actionListener_, Container parent_) {
		JButton button = new JButton();
		button.setText(buttonText_);
		if (actionListener_ != null) {
			button.addActionListener(actionListener_);
		}
		//-- add to parent
		if (parent_ != null) {
			parent_.add(button);
		}
		return button;
	}
	
	//===================================== INPUT =================================
	
	/**
	 * Create JTextField
	 * @param defaultVal_ String default value of the text field
	 * @param width_ int width
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JTextField text field
	 */
	public static JTextField createJTextField(String defaultVal_, int width_, Container parent_) {
		JTextField textField = new JTextField();
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		textField.setPreferredSize(new Dimension(width_, 30));
		textField.setText(defaultVal_);
		//-- add to parent
		if (parent_ != null) {
			parent_.add(textField);
		}
		return textField;
	}
	
	/**
	 * Create a JCheckBox and optionally add it to a Container
	 * @param text_ String text to the check box
	 * @param isSelected_ boolean set true to pre-select
	 * @param actionListener_ ActionListener action listener. Can be null.
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JCheckBox check box
	 */
	public static JCheckBox createJCheckBox(String text_, boolean isSelected_, ActionListener actionListener_, Container parent_) {
		JCheckBox checkBox = new JCheckBox(text_);
		checkBox.setSelected(isSelected_);
		if (actionListener_ != null) {
			checkBox.addActionListener(actionListener_);
		}
		//-- add to parent
		if (parent_ != null) {
			parent_.add(checkBox);
		}
		return checkBox;
	}
	
	/**
	 * Create a JSlider and optionally add it to a Container
	 * @param width_ int width of the panel. Set to < 0 to set the width according to the width of the parent Container
	 * @param min_  int minimum value
	 * @param max_ int maximum value
	 * @param value_ int default value
	 * @param changeListener_ ChangeListener listener object. Can be null.
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JSlider slider
	 */
	public static JSlider createJSlider(int width_, int min_, int max_, int value_, ChangeListener changeListener_, Container parent_) {
		//-- check width
		if (width_ < 0 && parent_ != null) {
			width_ = parent_.getSize().width;
		}
		//-- create the slider
		JSlider slider = new JSlider(min_, max_, value_);
		slider.setPreferredSize(new Dimension (width_,40));
		slider.setMinorTickSpacing((max_-min_)/25);
		slider.setMajorTickSpacing((max_-min_)/5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		if (changeListener_ != null) {
			slider.addChangeListener(changeListener_);
		}
		//-- add to parent
		if (parent_ != null) {
			parent_.add(slider);
		}
		
		return slider;
	}
	
	/**
	 * Create a JComboBox and optionally add it to a Container
	 * @param width_ int width of the panel. Set to < 0 to set the width according to the width of the parent Container
	 * @param items_ String[] items in the combo box
	 * @param selectedIndex_ int preselected item index 
	 * @param actionListener_ ActionListener action listener. Can be null.
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JComboBox combo box
	 */
	public static JComboBox createJComboBox(int width_, String[] items_, int selectedIndex_, ActionListener actionListener_, Container parent_) {
		//-- check width
		if (width_ < 0 && parent_ != null) {
			width_ = parent_.getSize().width;
		}
		//-- create the combo box
		JComboBox comboBox = new JComboBox(items_);
		comboBox.setPreferredSize(new Dimension (width_,40));
		if (selectedIndex_ < 0 && selectedIndex_ >= items_.length) {
			selectedIndex_ = 0;
		}
		comboBox.setSelectedIndex(selectedIndex_);
		if (actionListener_ != null) {
			comboBox.addActionListener(actionListener_);
		}
		//-- add to parent
		if (parent_ != null) {
			parent_.add(comboBox);
		}
		return comboBox;
	}
	
	//===================================== OUTPUT ================================
	
	/**
	 * Create a JLabel and optionally add it to a Container
	 * @param text_ String text for the label
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JLabel label
	 */
	public static JLabel createJLabel(String text_, Container parent_) {
		JLabel label = new JLabel(text_);
		//-- add to parent
		if (parent_ != null) {
			parent_.add(label);
		}
		return label;
	}
	
	/**
	 * Create a JTextPane (multiline output text) and optionally add it to a container
	 * @param text_ String text
	 * @param width_ int width of the panel. Set to < 0 to set the width according to the width of the parent Container 
	 * @param height_ int height of the panel. Set to < 0 to set the width according to the height of the parent Container
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JTextPane jTextPane
	 */
	public static JTextPane createJTextPane(String text_, int width_, int height_, Container parent_) {
		//-- check width
		if (width_ < 0 && parent_ != null) {
			width_ = parent_.getSize().width;
		}
		//-- check height
		if (height_ < 0 && parent_ != null) {
			height_ = parent_.getSize().height;
		}
	
		JTextPane jTextPane = new JTextPane();	
		jTextPane.setPreferredSize(new Dimension(width_, height_));
		jTextPane.setText(text_);
		jTextPane.setFont(new Font("Arial", Font.PLAIN, 13));
		//-- add to parent
		if (parent_ != null) {
			parent_.add(jTextPane);
		}
		return jTextPane;
	}
	
	//===================================== JPANELS ===============================
	/**
	 * Create a JPanel with FlowLayout.LEFT and optionally add it to a Container
	 * @param width_ int width of the panel. Set to < 0 to set the width according to the width of the parent Container
	 * @param height_ int height of the panel
	 * @param parent_ Container instance to add the new component to. Can be null. 
	 * @return JPanel panel
	 */
	public static JPanel createFlowLayoutJPanel(int width_, int height_, Container parent_) {
		//-- check width
		if (width_ < 0 && parent_ != null) {
			width_ = parent_.getSize().width;
		}
		//-- create the panel
		JPanel panel = new JPanel();	
		Dimension panelSize = new Dimension (width_, height_);
		panel.setSize(panelSize);
		panel.setPreferredSize(panelSize);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		//panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		//-- add to parent
		if (parent_ != null) {
			parent_.add(panel);
		}
		return panel;
	}
	
	/**
	 * Create a horizontal line to divide components
	 * @param width_ int width of the panel. Set to < 0 to set the width according to the width of the parent Container
	 * @param spaceAround_ space above and under the line
	 * @param parent_ Container instance to add the new component to. Can be null.
	 * @return JPanel divider panel
	 */
	public static JPanel createDividerJPanel(int width_, int spaceAround_, Container parent_) {
		//-- check width
		if (width_ < 0 && parent_ != null) {
			width_ = parent_.getSize().width;
		}
		
		//-- create containing JPanel
		JPanel panel = CRComponentFactory.createFlowLayoutJPanel(width_, spaceAround_*2 + 10, parent_);
		
		//-- create empty space above the line
		CRComponentFactory.createFlowLayoutJPanel(width_, spaceAround_, panel);
		
		//-- create the line JPanel
		JPanel dividerPanel = CRComponentFactory.createFlowLayoutJPanel(width_, 1, panel);
		dividerPanel.setBackground(Color.LIGHT_GRAY);
		
		//-- add to parent
		if (parent_ != null) {
			parent_.add(panel);
		}
		
		return panel;
	}

}
