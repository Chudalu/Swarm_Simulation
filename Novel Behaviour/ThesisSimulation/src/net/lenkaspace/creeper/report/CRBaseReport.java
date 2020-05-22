package net.lenkaspace.creeper.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.model.CRSettings;

/**
 * A base report object of unspecified type. Provides interface and components for other report objects.
 * For better performance, maintain an instance that opens and closes with (updated) data rather than
 * creating a new popup every time.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRBaseReport {

	protected Dimension size;
	protected String title;
	protected CRController controller;
	protected CRReportController reportController;
	
	protected JFrame baseFrame;
	protected JPanel basePanel;
	
	protected boolean displaysWindow;
	
	/**
	 * Constructor
	 * @param title_ String title of the report
	 * @param size_ Dimension size of the report window
	 * @param displaysWindow_ boolean if true, the report displays a window when its createSelf() is called
	 */
	public CRBaseReport(String title_, Dimension size_, boolean displaysWindow_) {
		size = size_;	
		title = title_;
		displaysWindow = displaysWindow_;
	}
	
	//==================================== REPORT CREATION ======================================
	
	/**
	 * Display the report on screen in popup
	 */
	public void display() {
		createSelf("",true);
	}
	
	/**
	 * Create a window of the report. Optionally show it on screen as well
	 * @param printToFileName_ String file name to print report into, without extension
	 * @param show_ boolean if false, report is hidden immediately after printed
	 */
	public void createSelf(String printToFileName_, boolean show_) {
		CRSettings settings = CRSettings.getSingleton();
		
		if (!settings.getIsConsoleOnlyBuild()) {
			//-- create frame
			if (baseFrame == null) {
				baseFrame = new JFrame(title);	
				baseFrame.setSize((int)size.width,(int)size.height);
				baseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				baseFrame.setLocation((int)(settings.getWindowSize().width/2 - size.width/2),(int)(settings.getWindowSize().height/2- size.height/2));
				baseFrame.setEnabled(true); 
				baseFrame.setFocusable(true);
				baseFrame.setResizable(false);
				
			}
			
			if (basePanel == null) {
				basePanel = new JPanel();
				basePanel.setBackground(Color.WHITE);
				baseFrame.getContentPane().add(basePanel, BorderLayout.CENTER);
			}
			
			baseFrame.setVisible(true);
		}
	}
	
	/**
	 * Sleep for a while. This is used to assure successful automated printing
	 */
	protected void freezeDisplay() {
		try {
			Thread.sleep(CRSettings.getSingleton().getReportFreezeDisplayDelay());
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}  
	
	
	//==================================== SIMULATION EVENTS ====================================
	
	/**
	 * Called by CRController each time a new simulation starts
	 * Use this to reset any simulation-persisting data
	 */
	public void onSimulationStart() {
		
	}
	
	/**
	 * Called by CRController each time a simulation ends
	 * Use this to save any simulation-persisting data
	 */
	public void onSimulationEnd() {
		
	}
	
	/**
	 * Called by CRController each time a run starts. 
	 * Use this to reset self for a brand new simulation run and call onNewRun of children.
	 * @param runNumber_ int new run number
	 */
	public void onRunStart(int runNumber_) {
		
	}
	
	/**
	 * Called by CRController each time a run ends.
	 * User this to save any necessary data.
	 * @param runNumber_ int ending run number
	 */
	public void onRunEnd(int runNumber_) {
		
	}
	
	/**
	 * Called by CRController each time a trial starts. 
	 * Use this to reset self for a new world environment and call onNewTrial of children.
	 * @param trialNumber_ int new trial number
	 * @param runNumber_ int new run number
	 */
	public void onTrialStart(int trialNumber_, int runNumber_) {
		
	}
	
	/**
	 * Called by CRController each time a trial ends.
	 * Use this to save any necessary data.
	 * @param trialNumber_ int ending trial number
	 * @param runNumber_ int current run number
	 */
	public void onTrialEnd(int trialNumber_, int runNumber_) {
		
	}
	
	/**
	 * Called from CRController before onUpdateLoopStart() of all world objects is called.
	 * Use this to reset any temporary variables. 
	 * @param timeCounter_ int current time counter, i.e. part of the timeUnit_
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopStart(int timeCounter_, int timeUnit_) {
		
	}
	
	/**
	 * Called from CRController after onUpdateLoopEnd() of all world objects is called.
	 * Use this to save any necessary data
	 * @param timeCounter_ int current time counter, i.e. part of the timeUnit_
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopEnd(int timeCounter_, int timeUnit_) {
		
	}
	
	//==================================== GETTERS / SETTERS ====================================
	public void setController(CRController controller_) { controller = controller_; }
	public String getFileName() { return title.replace(" ", "_"); }
	
	public void setReportController(CRReportController reportController_) { reportController = reportController_; }
	
	public boolean getDisplaysWindow() { return displaysWindow; }
	
}
