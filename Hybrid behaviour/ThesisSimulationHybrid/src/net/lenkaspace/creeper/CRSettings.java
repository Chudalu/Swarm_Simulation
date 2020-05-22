
package net.lenkaspace.creeper;

import java.awt.Dimension;

/**
 * A signleton class that stores various settings
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     1.0                                      
 */
public class CRSettings {

    //-- dimensions
	private Dimension windowSize;
	
    //-- flags
    private boolean shouldQuitAfterDone;
	private boolean shouldPrintGraphicReports;
	private boolean shouldPrintTextReports;
	private boolean shouldDraw;
	private boolean isInitDone;
	private boolean isTimeRunning;
		
	//-- numerical values
	private int timeUnitInterval = 60; 			//defines a basic time units in the simulation based on number of update loops. Set this so that timeUnitInterval = 1s
	private int renderingDelay = 100; 			//defines how long the rendering thread waits between 2 repaints
	private int outputDelay = 1; 				//defines how long the output thread waits between 2 output displays
	private int minUpdateDelay = 1; 			//defines min value of how long update thread waits between 2 updates
	private int maxUpdateDelay = 20; 			//defines max value of how long update thread waits between 2 updates
	private int reportFreezeDisplayDelay = 200;	//defines how long a report that should be printed and closed stays opened. This assures successful printing
	private int initialTimeSpeed = 75; 			//defines initial time speed on scale <0;100>
	
    private CRSettings() 	{
    	windowSize = new Dimension(1230, 670);
    	shouldDraw = true;
		shouldPrintGraphicReports = true; 
		shouldPrintTextReports = true;	
		shouldQuitAfterDone = true; 	
	}
        
    //==================================== SINGLETON ====================================
    private static CRSettings singletonReference;
	
	public static CRSettings getSingleton()	  {
	  if (singletonReference == null)
		  singletonReference = new CRSettings();		
	  return singletonReference;
	}
	
	//==================================== OTHER ========================================
	
	/**
	 *  Checks if all necessary settings have been specified. Good to ask before running the program.
	 *  Ran automatically if CRController's contructor is used.
	 *  @return true if everything is OK
	 */
	public boolean checkSettings() {
		return true;
	}
	
	//==================================== GETTERS / SETTERS ==============================
	public void setWindowSize(Dimension dimension_) { windowSize = dimension_; }
	public Dimension getWindowSize() { return windowSize; }
	
	public void setShouldQuitAfterDone(boolean value_) { shouldQuitAfterDone = value_; }
	public boolean getShouldQuitAfterDone() { return shouldQuitAfterDone; }
	
	public void setShouldPrintGraphicReports(boolean value_) { shouldPrintGraphicReports = value_; }
	public boolean getShouldPrintGraphicReports() { return shouldPrintGraphicReports; }
	
	public void setShouldPrintTextReports(boolean value_) { shouldPrintTextReports = value_; }
	public boolean getShouldPrintTextReports() { return shouldPrintTextReports; }
	
	public void setShouldDraw(boolean value_) { shouldDraw = value_; }
	public boolean getShouldDraw() { return shouldDraw; }
	
	public void setIsInitDone(boolean value_) { isInitDone = value_; }
	public boolean getIsInitDone() { return isInitDone; }
	
	public void setIsTimeRunning(boolean value_) { isTimeRunning = value_; }
	public boolean isTimeRunning() { return isTimeRunning; }

	public int getTimeUnitInterval() { return timeUnitInterval;	}
	public void setTimeUnitInterval(int value_) { timeUnitInterval = value_; }

	public int getRenderingDelay() { return renderingDelay; }
	public void setRenderingDelay(int value_) {	renderingDelay = value_; }

	public int getOutputDelay() { return outputDelay; }
	public void setOutputDelay(int value_) { outputDelay = value_; }
	
	public int getMinUpdateDelay() { return minUpdateDelay; }
	public void setMinUpdateDelay(int value_) { minUpdateDelay = value_; }
	
	public int getMaxUpdateDelay() { return maxUpdateDelay; }
	public void setMaxUpdateDelay(int value_) { maxUpdateDelay = value_; }
	
	public int getReportFreezeDisplayDelay() { return reportFreezeDisplayDelay; }
	public void setReportFreezeDisplayDelay(int value_) { reportFreezeDisplayDelay = value_; }
	
	public int getInitialTimeSpeed() { return initialTimeSpeed; }
	public void setInitialTimeSpeed(int value_) { if (value_ < 1) { value_ = 1; } else if (value_ > 100) { value_ = 100; } initialTimeSpeed = value_; }
	
	
}
