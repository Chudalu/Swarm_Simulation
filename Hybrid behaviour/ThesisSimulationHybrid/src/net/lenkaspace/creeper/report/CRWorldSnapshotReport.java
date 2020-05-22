package net.lenkaspace.creeper.report;

import java.awt.Dimension;

import net.lenkaspace.creeper.helpers.CRFileIOHelper;
import net.lenkaspace.creeper.model.CRSettings;

/**
 * Creates graphical snapshots of the world at specified times / intervals
 * and saves them to the report folder.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRWorldSnapshotReport extends CRBaseReport {

	private int[] snapshotTimeUnits;
	private int snapshotFrequency;
	
	/**
	 * Constructor
	 * @param title_ String title of the report
	 * @param size_ Dimension size of the report window
	 * @param snapshotTimeUnits_ ArrayList<Integer> list of time units at which to save world snapshots
	 */
	public CRWorldSnapshotReport(String title_, Dimension size_, int[] snapshotTimeUnits_) {
		super(title_, size_, false);
		snapshotTimeUnits = snapshotTimeUnits_;
		snapshotFrequency = 0;
	}
	
	/**
	 * Constructor
	 * @param title_ String title of the report
	 * @param size_ Dimension size of the report window
	 * @param snapshotFrequency_ int frequency in time units at which snapshots will be saved. Must be > 0.
	 */
	public CRWorldSnapshotReport(String title_, Dimension size_, int snapshotFrequency_) {
		super(title_, size_, false);
		snapshotTimeUnits = null;
		snapshotFrequency = snapshotFrequency_;
	}
	
	
	//==================================== SIMULATION EVENTS ====================================
	
	/**
	 * Called from CRController after onUpdateLoopEnd() of all world objects is called.
	 * Creates a snapshot of the world if at a specified time unit
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopEnd(int timeCounter_, int timeUnit_) {
		if (CRSettings.getSingleton().getShouldPrintGraphicReports()) {
			boolean shouldSaveReport = false;
			if (snapshotTimeUnits != null) {
				//-- saving based on specified time units
				for (int i=0; i<snapshotTimeUnits.length; i++) {
					if (timeUnit_ == snapshotTimeUnits[i]) {
						shouldSaveReport = true;
						continue;
					}
				}
			} else if (snapshotFrequency > 0) {
				if (timeUnit_ % snapshotFrequency == 0) {
					shouldSaveReport = true;
				}
			}
			
			
			if (shouldSaveReport) {
				createSelf(reportController.getCurrentFilePath() + "_" + this.getFileName() + "_" + timeUnit_, false);
			}
		}
	}
	
	/**
	 * Called by CRController each time a trial ends.
	 * Creates the final snapshot of the world
	 * @param trialNumber_ int ending trial number
	 * @param runNumber_ int current run number
	 */
	public void onTrialEnd(int trialNumber_, int runNumber_) {
		if (CRSettings.getSingleton().getShouldPrintGraphicReports()) {
			createSelf(reportController.getCurrentFilePath() + "_" + this.getFileName() + "_" + controller.getTimeUnits(), false);
		}
	}
	
	
	
	//==================================== REPORT CREATION ======================================
	
	/**
	 * Create a window of the report. Optionally show it on screen as well
	 * @param printToFileName_ String file name to print report into, without extension
	 * @param show_ boolean if false, report is hidden immediately after printed
	 */
	public void createSelf(String printToFileName_, boolean show_) {
		if (!CRSettings.getSingleton().getIsConsoleOnlyBuild()) {
			if (CRSettings.getSingleton().getShouldPrintGraphicReports()) {
				//---- save arena screenshot:
				controller.getRenderer().setShouldOverrideNoPaint(true);
				//controller.getRenderer().repaint();
				CRFileIOHelper.componentToJpeg(this.controller.getRenderer(), printToFileName_);
			}
		}
	}

}
