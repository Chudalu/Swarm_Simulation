package net.lenkaspace.creeper.model;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.helpers.CRFileIOHelper;

/**
 * A class that stores parameters of the experiments. Models should extensively use a sublcass
 * of CRParameters to read their parameters. CRReportController will automatically write the 
 * parameters into a text file when saving reports.
 * 
 * A sublcass should set the singleton instance of this class and load all its parameters for a 
 * seemless integration. This way, there is always only 1 instance of CRParameters and 
 * all classes that request CRParameters.getSingleton() get the new class singleton.
 * 
 * For example:
 * 
 * private static MyParameters singletonReference;
 * public static MyParameters getSingleton()	  {
	  if (singletonReference == null) {
		  singletonReference = new Parameters();
		  //-- set all variables from the CRParameters
		  CRParameters origParams = CRParameters.getSingleton();
		  singletonReference.scenario = origParams.scenario;
		  singletonReference.reportFolderName = origParams.reportFolderName;
		  singletonReference.numOfTrials = origParams.numOfTrials;
		  singletonReference.numOfRuns = origParams.numOfRuns;
		  singletonReference.trialDuration = origParams.trialDuration;
		  singletonReference.setController(origParams.getController());
		  //-- rewrite CRParameters reference
		  CRParameters.setSingletonReference(singletonReference);
	  }
	  return singletonReference;
	}
	
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRParameters {
	
	//==================================== SINGLETON ====================================
    private static CRParameters singletonReference;
	
	public static CRParameters getSingleton()	  {
	  if (singletonReference == null)
		  singletonReference = new CRParameters();		
	  return singletonReference;
	}
	
	protected static void setSingletonReference(CRParameters ref_) {
		singletonReference= ref_;
	}
	
	//================
	public String scenario = "";
	public String reportFolderName = "";
	public int trialDuration = -1; // in seconds
	public int numOfRuns = -1;
	public int numOfTrials = -1;
	
	private CRController controller;
	
	public CRParameters() {
		
	}
	
	/**
	 * Generate a string that lists all parameters. Subclasses should override this
	 * to add their own parameters.
	 */
	public String toString() {
		String contents = "";
		if (scenario != null) {
			if (controller.getWorld().getHasScenarios()) {
				contents += "Scenario: " + scenario + "\n";
			}
			contents += "Number of runs: " + numOfRuns + "\n";
			contents += "Trial duration: " + trialDuration + "\n";
			contents += "Number of trials: " + numOfTrials + "\n";
		}
		return contents;
	}
	
	/**
	 * Save all parameters to parameters.txt
	 */
	public void saveSelf() {
		CRFileIOHelper.stringToFile(this.toString(), controller.getReportController().getCurrentFilePath() + "parameters");
	}
	
	/**
	 * Check that all parameters have been set. Subclasses should override this
	 * to add their own checks.
	 * @return true if all parameters set
	 */
	public boolean checkSelf() {
		boolean ok = true;
		if (controller == null) {
			System.err.println("CRParameters :: controller not set");
			ok = false;
		}
		if (scenario.length() == 0 && controller.getWorld().getHasScenarios()) {
			System.err.println("CRParameters :: scenario not set");
			ok = false;
		}
		if (reportFolderName.length() == 0) {
			System.err.println("CRParameters :: report folder not set");
			ok = false;
		}
		if (trialDuration <= 0) {
			System.err.println("CRParameters :: trial duration not set or 0");
			ok = false;
		}
		if (numOfRuns <= 0) {
			System.err.println("CRParameters :: number of runs not set or 0");
			ok = false;
		}
		if (numOfTrials <= 0) {
			System.err.println("CRParameters :: number of trials not set or 0");
			ok = false;
		}
		return ok;
	}
	
	public void setController(CRController controller_) {
		controller = controller_;
	}
	
	public CRController getController() { return controller; }
	
}
