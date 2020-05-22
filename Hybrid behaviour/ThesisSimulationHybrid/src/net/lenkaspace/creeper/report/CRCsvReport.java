package net.lenkaspace.creeper.report;

import java.awt.Dimension;

import net.lenkaspace.creeper.helpers.CRFileIOHelper;
import net.lenkaspace.creeper.model.CRParameters;
import net.lenkaspace.creeper.model.CRSettings;
import net.lenkaspace.creeper.view.CRComponentFactory;

/**
 * Can create csv reports of the whole simulation and organize values as:
 * 
 * Run1_Trial1_value1, Run1_Trial1_value2, Run1_Trial1_value3, ...
 * Run2_Trial1_value1, Run2_Trial1_value2, Run2_Trial1_value3, ...
 * ...
 * 
 * Run1_Trial2_value1, Run1_Trial2_value2, Run1_Trial2_value3, ...
 * Run2_Trial2_value1, Run2_Trial2_value2, Run2_Trial2_value3, ...
 * 
 * For better performance, maintain an instance that opens and closes with (updated) data rather than
 * creating a new popup every time.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRCsvReport extends CRBaseReport {

	protected String[][][] values;	// values in array [trialNo, RunNo, variableIndex]
	protected String[] variableNames;
	
	/**
	 * Constructor
	 * @param title_ String title of the report
	 * @param variableNames_ String array of variable names to identify different sets
	 * @param size_ Dimension size of the report window
	 */
	public CRCsvReport(String title_, String[] variableNames_, Dimension size_) {
		super(title_, size_, true);
		variableNames = variableNames_;
	}
	
	
	//==================================== SIMULATION EVENTS ====================================
	
	/**
	 * Called by CRController each time a new simulation starts
	 * Use this to reset any simulation-persisting data
	 */
	public void onSimulationStart() {
		CRParameters parameters = CRParameters.getSingleton();
		values = new String[parameters.numOfTrials][parameters.numOfRuns][variableNames.length];
		//-- clear trial strings:
		for (int t=0; t<parameters.numOfTrials; t++) {
			for (int r=0; r<parameters.numOfRuns; r++) {
				for (int v=0; v<variableNames.length; v++) {
					values[t][r][v] = "";
				}
			}
		}
	}
	
	/**
	 * Called by CRController each time a simulation ends
	 * Use this to save any simulation-persisting data
	 */
	public void onSimulationEnd() {
		if (CRSettings.getSingleton().getShouldPrintGraphicReports()) {
			createSelf(reportController.getReportFolderName() + "/" + this.getFileName(), false);
		}
	}
	
	
	//==================================== DATA COLLECTION =================================
	
	/**
	 * Put array of values for a specific trial and run to the value set
	 * The value set size should be the same as size of set names the report was initialised with.
	 * @param values_ double[] array of values
	 */
	public void addValueSet(double[] values_) {
		addValueSet(values_, controller.getTrialNumber(), controller.getRunNumber());
	}
	
	/**
	 * Put array of values for a specific trial and run to the value set
	 * The value set size should be the same as size of set names the report was initialised with.
	 * @param values_ double[] array of values
	 * @param trialNumber_ int trial number <1;totalTrials>
	 * @param runNumber_ int run number <1;totalRuns>
	 */
	public void addValueSet(double[] values_, int trialNumber_, int runNumber_) {
		String[] convertedValues = new String[values_.length];
		for (int i=0; i<values_.length; i++) {
			convertedValues[i] = String.valueOf(values_[i]);
		}
		addValueSet(convertedValues, trialNumber_, runNumber_);
	}
	
	/**
	 * Put array of values for a specific trial and run to the value set
	 * The value set size should be the same as size of set names the report was initialised with.
	 * @param values_ String[] array of values
	 */
	public void addValueSet(String[] values_) {
		addValueSet(values_, controller.getTrialNumber(), controller.getRunNumber());
	}
	
	/**
	 * Put array of values for a specific trial and run to the value set
	 * The value set size should be the same as size of set names the report was initialised with.
	 * @param values_ String[] array of values
	 */
	public void addValueSet(String[] values_, int trialNumber_, int runNumber_) {
		//-- adjust trial and run numbers to match array indexing
		trialNumber_ -= 1;
		runNumber_ -= 1;
		//-- add values
		CRParameters parameters = CRParameters.getSingleton();
		if (trialNumber_ >= 0 && trialNumber_ < parameters.numOfTrials && runNumber_ >= 0 && runNumber_ < parameters.numOfRuns) {
			values[trialNumber_][runNumber_] = values_;
		}
	}
	
	/**
	 * Put a new value for a specific trial, run and variable name to the value set
	 * @param variableName_ String new value name
	 * @param value_ double new value
	 */
	public void addValue(String variableName_, double value_) {
		addValue(variableName_, String.valueOf(value_));
	}
	
	/**
	 * Put a new value for a specific trial, run and variable name to the value set
	 * @param variableName_ String new value name
	 * @param value_ String new value
	 */
	public void addValue(String variableName_, String value_) {
		addValue(variableName_, value_, controller.getTrialNumber(), controller.getRunNumber());
	}
	
	/**
	 * Put a new value for a specific trial, run and variable name to the value set
	 * @param variableName_ String new value name
	 * @param value_ String new value
	 * @param trialNumber_ int trial number <1;totalTrials>
	 * @param runNumber_ int run number <1;totalRuns>
	 */
	public void addValue(String variableName_, String value_, int trialNumber_, int runNumber_) {
		//-- adjust trial and run numbers to match array indexing
		trialNumber_ -= 1;
		runNumber_ -= 1;
		//-- add value
		CRParameters parameters = CRParameters.getSingleton();
		if (trialNumber_ >= 0 && trialNumber_ < parameters.numOfTrials && runNumber_ >= 0 && runNumber_ < parameters.numOfRuns) {
			//-- search where to put the value
			for (int v=0; v<variableNames.length; v++) {
				if (variableNames[v] == variableName_) {
					values[trialNumber_][runNumber_][v] = value_;
					continue;
				}
			}
		}
	}
	
	
	//==================================== REPORT CREATION =================================
	
	/**
	 * Create a window of the report. Optionally show it on screen as well
	 * @param printToFileName_ String file name to print report into, without extension
	 * @param show_ boolean if false, report is hidden immediately after printed
	 */
	public void createSelf(String printToFileName_, boolean show_) {
		super.createSelf(printToFileName_, show_);
		
		//-- construct output text
		String outputText = "";
		CRParameters parameters = CRParameters.getSingleton();
		for (int t=0; t<parameters.numOfTrials; t++) {
			//-- add heading of this trial
			outputText += "\"Trial " + (t+1) + "\",";
			for (int v=0; v<variableNames.length; v++) {
				outputText += "\"" + variableNames[v] + "\",";
			} 
			outputText += "\n";
			
			//-- add values, run by run
			for (int r=0; r<values[t].length; r++) {
				//-- add run number
				outputText += "\"" + (r+1) + "\",";
				//-- add run values
				for (int v=0; v<variableNames.length; v++) {
					outputText += "\"" + values[t][r][v] + "\","; //values[t][r][v]
				}
				outputText += "\n";
			}
			outputText += "\n";
		}
		if (!CRSettings.getSingleton().getIsConsoleOnlyBuild()) {
			CRComponentFactory.createJTextPane(outputText, -1, -1, basePanel);
		}
		
		//-- print
		if (printToFileName_.length() > 0) {
        	freezeDisplay(); //pause the program for a while, otherwise reports may come out incomplete
			CRFileIOHelper.stringToFile(outputText, printToFileName_, "csv");
			
			//-- hide afterwards:
			if (!show_) {
				baseFrame.setVisible(false);
			}
        }
	}

}
