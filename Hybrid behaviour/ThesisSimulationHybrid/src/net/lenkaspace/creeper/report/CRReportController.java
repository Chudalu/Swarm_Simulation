package net.lenkaspace.creeper.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.model.CRParameters;

import org.apache.commons.io.FileUtils;

/**
 * Maintains and provides all reports
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRReportController {

	protected Dictionary <String, CRBaseReport> reports;
	protected CRController controller;
	protected String reportFolderName;
	protected String baseFolderName;
	protected String currentFilePath;
	
	
	//==================================== CONSTRUCTORS =================================
	
	/**
	 * Constructor
	 * @param controller_ CRController holding controller instance
	 */
	public CRReportController(CRController controller_) {
		controller = controller_;
		reports = new Hashtable <String, CRBaseReport>();
		baseFolderName = "reports";
	}
	
	
	//==================================== SIMULATION EVENTS ====================================
	
	/**
	 * Called by CRController each time a new simulation starts
	 * Use this to reset any simulation-persisting data
	 */
	public void onSimulationStart() {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onSimulationStart();
		}
	}
	
	/**
	 * Called by CRController each time a simulation ends
	 * Use this to save any simulation-persisting data
	 */
	public void onSimulationEnd() {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onSimulationEnd();
		}
	}
	
	/**
	 * Called by CRController each time a run starts. 
	 * Use this to reset self for a brand new simulation and call onNewTrial of children models.
	 * @param runNumber_ int new run number
	 */
	public void onRunStart(int runNumber_) {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onRunStart(runNumber_);
		}
	}
	
	/**
	 * Called by CRController each time a run ends.
	 * Use this to save any necessary data.
	 * @param runNumber_ int ending run number
	 */
	public void onRunEnd(int runNumber_) {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onRunEnd(runNumber_);
		}
	}
	
	/**
	 * Called by CRController each time a trial starts. 
	 * Use this to reset self for a new world environment and call onNewTrial of children models.
	 * @param trialNumber_ int new trial number
	 * @param runNumber_ int new run number
	 */
	public void onTrialStart(int trialNumber_, int runNumber_) {
		//-- first time run: create and clean the root reports directory
		if (trialNumber_ == 1 && runNumber_ == 1) {
			File reportsDir = new File(baseFolderName + "/" + reportFolderName);
			try {
				FileUtils.forceMkdir(reportsDir);
			} catch (IOException e1) {
				System.err.println("Could not create reports directory");
			}
			try {
				FileUtils.cleanDirectory(reportsDir);
			} catch (IOException e1) {
				System.err.println("Could not clean reports directory");
			}
			//-- write down the parameters
			currentFilePath = reportsDir + "/";
			CRParameters.getSingleton().saveSelf();			
		}
		
		//-- create the reports
		currentFilePath = baseFolderName + "/" + reportFolderName+"/Run" + String.valueOf(runNumber_);
		try {
			//-- if first trial of run, create run directory & add run number to output string:
			if (trialNumber_ == 1) {
				FileUtils.forceMkdir(new File(currentFilePath));
			}
			currentFilePath += "/Trial" + String.valueOf(trialNumber_);		
		} catch (IOException exception) {
			System.err.println("ERROR CREATING DIRECTORY "+ currentFilePath);
		}	
		
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onTrialStart(trialNumber_, runNumber_);
		}
	}
	
	/**
	 * Called by CRController each time a trial ends.
	 * Saves all reports into a folder.
	 * @param trialNumber_ int ending trial number
	 * @param runNumber_ int current run number
	 */
	public void onTrialEnd(int trialNumber_, int runNumber_) {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onTrialEnd(trialNumber_, runNumber_);
		}
	}
	
	
	/**
	 * Called from CRController before onUpdateLoopStart() of all world objects is called.
	 * Use this to reset any temporary variables.
	 * @param timeCounter_ int current time counter, i.e. part of the timeUnit_ 
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopStart(int timeCounter_, int timeUnit_) {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onUpdateLoopStart(timeCounter_, timeUnit_);
		}
	}
	
	/**
	 * Called from CRController after onUpdateLoopEnd() of all world objects is called.
	 * Use this to save any necessary data
	 * @param timeCounter_ int current time counter, i.e. part of the timeUnit_
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopEnd(int timeCounter_, int timeUnit_) {
		//-- cascade to reports
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().onUpdateLoopEnd(timeCounter_, timeUnit_);
		}
	}
	
	
	
	//==================================== HOUSE KEEPING ================================
	
	/**
	 * Add a new report
	 * @param reportName_ String a unique report name
	 * @param report_ CRBaseReport instance of a CRBaseReport subclass report
	 */
	public void addReport(String reportName_, CRBaseReport report_) {
		report_.setController(controller);
		report_.setReportController(this);
		reports.put(reportName_, report_);
	}
	
	//==================================== GETTERS / SETTERS ============================
	
	/**
	 * Get all registered report names
	 * @param displayableOnly_ boolean if true, only return a list of reports that display a window when called
	 * @return String[] report names
	 */
	public String[] getReportNames(boolean displayableOnly_) { 
		//-- first, fill in a dynamic array as it is not known how many reports to display there will be
		ArrayList<String> namesDynamicArray = new ArrayList<String>();
		Enumeration<String> e = reports.keys();
		while(e.hasMoreElements()) {
			boolean canAdd = true;
			String reportName = (String) e.nextElement();
			if (displayableOnly_) {
				if (!getReport(reportName).getDisplaysWindow()) {
					canAdd = false;
				}
			}
			if (canAdd) {
				namesDynamicArray.add(reportName);
			}
		}
		//-- copy over to a static array
		String[] names = new String[namesDynamicArray.size()];
		int i = 0;
		for (String name : namesDynamicArray) {
			names[i++] = name;
		}
		return names;
	}
	
	/**
	 * Get a report of a specific unique name
	 * @param reportName_ String report name
	 * @return CRBaseReport instance of a CRBaseReport subclass
	 */
	public CRBaseReport getReport(String reportName_) {
		return reports.get(reportName_);
	}
	
	/**
	 * Set reference to the controller and pass it to all reports
	 * @param controller_ CRController controller
	 */
	public void setController(CRController controller_) {
		controller = controller_;
		Enumeration<CRBaseReport> e = reports.elements();
		while(e.hasMoreElements()) {
			e.nextElement().setController(controller_);
		}
	}
	
	/**
	 * Set where reports will be saved automatically
	 * @param value_ String report folder name
	 */
	public void setReportFolderName(String value_) { reportFolderName = value_; }
	
	public String getReportFolderName() { return reportFolderName; }
	
	/**
	 * Get where reports are current being saved, taking into account report folder name, current run and trial
	 * @return String current file path
	 */
	public String getCurrentFilePath() { return currentFilePath; }
	
	/**
	 * Return name of the folder where all report folders will be created. 
	 * Default is "bin".
	 * @return String folder name
	 */
	public String getBaseFolderName() { return baseFolderName; }
	
	/**
	 * Set name of the folder where all report folders will be created.
	 * If folder doesn't exist, it will be created
	 * @param folderName_ String folder name
	 */
	public void setBaseFolderName(String folderName_) { baseFolderName = folderName_; }
    
}
