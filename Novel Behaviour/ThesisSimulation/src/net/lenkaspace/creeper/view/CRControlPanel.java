package net.lenkaspace.creeper.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.model.CRParameters;
import net.lenkaspace.creeper.model.CRSettings;
import net.lenkaspace.creeper.report.CRBaseReport;

/**
 * The control panel displayed on the right of the applet by default. 
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
@SuppressWarnings("serial")
public class CRControlPanel extends JPanel {

	protected CRController controller;
	
	protected JPanel helpPanel;
	protected CRTextPopup helpPopup;
	
	protected JPanel reportFolderPanel;
	protected JTextField reportFolderField;
	
	protected JPanel scenarioPanel;
	protected JComboBox scenarioComboBox;
	
	protected JPanel preStartSimulationPanel;
	
	protected JPanel startSimulationPanel;
	protected JTextField numberOfRunsField;
	protected JTextField trialDurationField;
	
	protected JPanel timeSettingsPanel;
	protected JButton timeStartPauseButton;
	protected JSlider timeSpeedSlider;
	
	protected JPanel basicOutputPanel;
	
	protected JPanel reportOutputPanel;
	protected JComboBox reportTypeComboBox;
	
	protected JPanel viewMovementControlsPanel;
	protected JLabel currentViewPosition;
	
	/**
	 * Constructor
	 * @param size_  Dimension size of the panel
	 * @param controller_ CRController holding controller instance
	 */
	public CRControlPanel(Dimension size_, CRController controller_) {
		controller = controller_;
		this.setBounds(0, 0, size_.width, size_.height);
		this.setSize (size_);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//---- create help panel
		helpPopup = new CRTextPopup("Help", new Dimension(300,300));
		helpPopup.setText("Powered by Creeper. \nhttp://lenkaspace.net/code/java/creeper");
		
		helpPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 50, this);
		helpPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		CRComponentFactory.createJButton("?", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpPopup.open();
			}
		}, helpPanel);	

		CRSettings settings = CRSettings.getSingleton();
		
		//---- create report folder panel if this is not build for online applet
		reportFolderPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 80, this);
		CRComponentFactory.createJLabel("Saved reports folder name: ", reportFolderPanel);
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
		Date date = new Date();
		reportFolderField = CRComponentFactory.createJTextField("reports" + dateFormat.format(date) , this.getSize().width - 190, reportFolderPanel);
		
		CRComponentFactory.createFlowLayoutJPanel(-1, 10, reportFolderPanel);
		
		CRComponentFactory.createJCheckBox("Save graphic reports ", settings.getShouldPrintGraphicReports(), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CRSettings.getSingleton().setShouldPrintGraphicReports(((JCheckBox)e.getSource()).isSelected());	
			}
		}, reportFolderPanel);
		CRComponentFactory.createJCheckBox("Save text reports       ", settings.getShouldPrintTextReports(), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CRSettings.getSingleton().setShouldPrintTextReports(((JCheckBox)e.getSource()).isSelected());	
			}
		}, reportFolderPanel);
		CRComponentFactory.createJCheckBox("Quit when done", settings.getShouldQuitAfterDone(), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CRSettings.getSingleton().setShouldQuitAfterDone(((JCheckBox)e.getSource()).isSelected());	
			}
		}, reportFolderPanel);
		
	    //---- create panel with selection of world types
		if (controller.getWorld().getHasScenarios()) {
			scenarioPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 40, this);
			CRComponentFactory.createJLabel("Scenario: ", scenarioPanel);
			scenarioComboBox = CRComponentFactory.createJComboBox(300, controller.getWorld().getScenariosAsArray(), 0, null, scenarioPanel);
		}
		
		//---- create panel above the start simulation panel
		preStartSimulationPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 0, this);
		
		//---- create the start simulation button in a separate panel
		startSimulationPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 40, this);
		
		//-- number of runs
		CRComponentFactory.createJLabel("Number of runs: ", startSimulationPanel);
		numberOfRunsField = CRComponentFactory.createJTextField("1", 50, startSimulationPanel);
		
		//-- trial duration
		CRComponentFactory.createJLabel("  Trial duration: ", startSimulationPanel);
		trialDurationField = CRComponentFactory.createJTextField("120", 50, startSimulationPanel);
		
		//-- start button
		CRComponentFactory.createJLabel("     ", startSimulationPanel);
		CRComponentFactory.createJButton("Start simulation", new ActionListener() {	public void actionPerformed(ActionEvent e) {
                onStartSimulationClicked(e);
        }}, startSimulationPanel);
		
		//---- create divider
		CRComponentFactory.createDividerJPanel(-1, 5, this);
		        
		//---- create time settings
		timeSettingsPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 60, this);
		
		//-- play / pause
		timeStartPauseButton = CRComponentFactory.createJButton(">", new ActionListener() {	public void actionPerformed(ActionEvent e) {
			CRSettings settings = CRSettings.getSingleton();
			if (settings.isTimeRunning()) {
				//---- pause clicked:
				controller.stopTime();
				settings.setIsTimeRunning(false);
				onTimePaused();	
			} else {
				//----- play clicked:	
				controller.startTime();
				settings.setIsTimeRunning(true);
				onTimeStarted();
			}
		}}, timeSettingsPanel);
		timeStartPauseButton.setEnabled(false);
		
		CRComponentFactory.createJLabel("   Time speed:", timeSettingsPanel);
		
		//-- slider
		timeSpeedSlider = CRComponentFactory.createJSlider(this.getSize().width - 170, 0, 100, settings.getInitialTimeSpeed(), new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		if (CRSettings.getSingleton().isTimeRunning()) {
        			controller.setTimeSpeed(timeSpeedSlider.getValue()/100.0);
        		}
        	}
        }, timeSettingsPanel);
		
		setTimeControlsEnabled(false);
		
		
		//---- create basic output panel: console + draw area
		basicOutputPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 40, this);
		
		//-- open console button
		CRComponentFactory.createJButton("Open output console", new ActionListener() { public void actionPerformed(ActionEvent e) {
			CROutputPopup.getSingleton().open();
		}}, basicOutputPanel);
		
		//-- draw check box
		CRComponentFactory.createJCheckBox("Draw world", true, new ActionListener() { public void actionPerformed(ActionEvent e) {
			CRSettings.getSingleton().setShouldDraw(((JCheckBox)e.getSource()).isSelected());
		}}, basicOutputPanel);
		
		
		
		//---- create report output panel
		String[] reportNames = controller.getReportController().getReportNames(true);
		if (reportNames.length > 0) {
			reportOutputPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 40, this);
			
			//-- combo box
			reportTypeComboBox = CRComponentFactory.createJComboBox(this.getSize().width - 135,reportNames, 0, null, reportOutputPanel);
			
			//-- button
			CRComponentFactory.createJButton("Open report", new ActionListener() { public void actionPerformed(ActionEvent e) {
				onOpenReportClicked(reportTypeComboBox.getSelectedItem().toString());
			}}, reportOutputPanel);
		}
		
		//---- create view movement controls panel
		viewMovementControlsPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 60, this);
		this.setShowViewMovementControls(false);
		
		//---- create custom key even dispatcher
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyDispatcher());
        
        redisplayParameters();

	}
	
	//==================================== KEYBOARD ACTIONS ===============================
	private class KeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	//-- ignore if key pressed on a text field
            	if (e.getComponent().getClass() != JTextField.class) {
            		switch(e.getKeyCode()) {
            		case 37:
            			controller.getRenderer().moveView(CRRenderer.DIRECTION.LEFT, CRSettings.getSingleton().getViewMovementSpeedByKey());
            			break;
            		case 38:
            			controller.getRenderer().moveView(CRRenderer.DIRECTION.UP, CRSettings.getSingleton().getViewMovementSpeedByKey());
            			break;
            		case 39:
            			controller.getRenderer().moveView(CRRenderer.DIRECTION.RIGHT, CRSettings.getSingleton().getViewMovementSpeedByKey());
            			break;
            		case 40:
            			controller.getRenderer().moveView(CRRenderer.DIRECTION.DOWN, CRSettings.getSingleton().getViewMovementSpeedByKey());
            			break;
            		}
            	}
            }
            return false;
        }
    }
	
	//==================================== BUTTON ACTIONS =================================
	
	/**
	 * Called when Start Simulation button is pressed
	 * @param e ActionEvent action event
	 */
	protected void onStartSimulationClicked(ActionEvent e) {
		String reportFolderName = "";
        if (reportFolderField != null) {
        	reportFolderName = reportFolderField.getText();
        }
        //-- set parameters
        CRParameters parameters = CRParameters.getSingleton();
        parameters.reportFolderName = reportFolderName;
        parameters.trialDuration = Integer.parseInt(trialDurationField.getText());
        parameters.numOfRuns = Integer.parseInt(numberOfRunsField.getText());
        if (scenarioComboBox != null) {
        	parameters.scenario = scenarioComboBox.getSelectedItem().toString();
        }
        //-- run
		controller.startSimulation(timeSpeedSlider.getValue()/100.0, false);
	}
	
	/**
	 * Called when the Open Report button is pressed.
	 * Tell CRReportController what to do.
	 * @param reportName_ String selected report name
	 */
	protected void onOpenReportClicked(String reportName_) {
		CRBaseReport report = controller.getReportController().getReport(reportName_);
		if (report != null) {
			report.display();	
		}
	}
	
	/**
	 * Make the time slider and the '>' / '||' button enabled / disabled
	 * @param enabled_ boolean true to enable
	 */
	private void setTimeControlsEnabled(boolean enabled_) {
		timeStartPauseButton.setEnabled(enabled_);
		timeSpeedSlider.setEnabled(enabled_);
	}
	
	/**
	 * Called when time is started.
	 * Set the timeStartPauseButton text to '||'
	 */
	public void onTimeStarted() {
		setTimeControlsEnabled(true);
		timeStartPauseButton.setText("||");
	}
	
	/**
	 * Called when time is paused.
	 * Set the timeStartPauseButton text to '>'
	 */
	public void onTimePaused() {
		timeStartPauseButton.setText(">");
	}
	
	
	//==================================== EXTRA PANELS ===================================
	public void setShowViewMovementControls(boolean shouldShow_) {
		viewMovementControlsPanel.removeAll();
		Dimension dim = new Dimension(viewMovementControlsPanel.getSize().width, 0);
		if (shouldShow_) {
			dim.height = 80;
			CRComponentFactory.createDividerJPanel(-1, 5, viewMovementControlsPanel);
			CRComponentFactory.createJLabel("Move view:  ", viewMovementControlsPanel);
			CRComponentFactory.createJButton("/\\ UP", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.getRenderer().moveView(CRRenderer.DIRECTION.UP, CRSettings.getSingleton().getViewMovementSpeedByButton());
					
				}
			}, viewMovementControlsPanel);
			CRComponentFactory.createJButton("\\/ DOWN", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.getRenderer().moveView(CRRenderer.DIRECTION.DOWN, CRSettings.getSingleton().getViewMovementSpeedByButton());
					
				}
			}, viewMovementControlsPanel);
			CRComponentFactory.createJLabel("   ", viewMovementControlsPanel);
			CRComponentFactory.createJButton("< LEFT", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.getRenderer().moveView(CRRenderer.DIRECTION.LEFT, CRSettings.getSingleton().getViewMovementSpeedByButton());
					
				}
			}, viewMovementControlsPanel);
			CRComponentFactory.createJButton("> RIGHT", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.getRenderer().moveView(CRRenderer.DIRECTION.RIGHT, CRSettings.getSingleton().getViewMovementSpeedByButton());
					
				}
			}, viewMovementControlsPanel);
			
			currentViewPosition = CRComponentFactory.createJLabel("", viewMovementControlsPanel);
			redisplayCurrentViewPosition();
		}
		viewMovementControlsPanel.setSize(dim);
		viewMovementControlsPanel.setPreferredSize(dim);
	}
	
	/**
	 * Redisplay current view position if the view position panel is shown
	 */
	public void redisplayCurrentViewPosition() {
		if (currentViewPosition != null) {
			CRRenderer renderer = controller.getRenderer();
			currentViewPosition.setText("                        Current view position (TL corner): [" + renderer.getViewPositionX() + ";" + renderer.getViewPositionY() + "]");
		}
	}
	
	/**
	 * Set all input fields according to parameters coded manually. Called when a simulation
	 * is started by code, not by the control panel. Subclasses should override this
	 * to update display of their own parameter values.
	 */
	public void redisplayParameters() {
		CRParameters parameters = CRParameters.getSingleton();
		reportFolderField.setText(parameters.reportFolderName);
		trialDurationField.setText(Integer.toString(parameters.trialDuration));
		numberOfRunsField.setText(Integer.toString(parameters.numOfRuns));
		if (scenarioComboBox != null) {
			scenarioComboBox.setSelectedItem(new String(parameters.scenario));
		}
	}
	

	//==================================== GETTERS / SETTERS  =============================
	
	public JPanel getHelpPanel() { return helpPanel; }
	public CRTextPopup getHelpPopup() { return helpPopup; }
	public JPanel getReportFolderPanel() { return reportFolderPanel; }
	public JTextField getReportFolderField() { return reportFolderField; }
	public JPanel getStartSimulationPanel() { return startSimulationPanel; }
	public JPanel getPreStartSimulationPanel() { return preStartSimulationPanel; }
	public JTextField getNumberOfRunsField() { return numberOfRunsField; }
	public JTextField getTrialDurationField() {	return trialDurationField; }
	public JPanel getTimeSettingsPanel() { return timeSettingsPanel; }
	public JButton getTimeStartPauseButton() { return timeStartPauseButton;	}
	public JSlider getTimeSpeedSlider() { return timeSpeedSlider;  }
	public JPanel getBasicOutputPanel() { return basicOutputPanel; }	
	public JPanel getReportOutputPanel() { return reportOutputPanel; }
	public JComboBox getReportTypeComboBox() { return reportTypeComboBox; }

		
}
