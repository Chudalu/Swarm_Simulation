package net.lenkaspace.creeper.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import net.lenkaspace.creeper.helpers.CRFileIOHelper;
import net.lenkaspace.creeper.model.CRSettings;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Creates graphical time series reports, usually of a single run / trial.
 * 
 * For better performance, maintain an instance that opens and closes with (updated) data rather than
 * creating a new popup every time.
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRTimeSeriesReport extends CRBaseReport {
	
	public static enum FUNCTION {
		AVERAGE,
		MEDIAN
	}
	
	protected String xAxisTitle;
	protected String yAxisTitle;
	
	protected List<XYSeries> dataSeriesArray;
	protected double[] totalDataSeriesValuesOfSecond;
	protected String[] variableNames;
	protected Range yAxisDisplayRange;
	protected boolean shouldDisplayLegend;
	protected int[] numOfValuesAddedInCurrentSecond;
	protected boolean isScatterPlot;
	protected boolean shouldOuputVariableNames;
	
	protected JFreeChart jFreeChart;
	protected ChartPanel chartPanel;
	
	protected Color [] colors = {Color.blue, Color.green, Color.red, Color.gray, Color.magenta, Color.black, Color.orange, Color.pink};
	protected Shape[] shapes = {new Ellipse2D.Double(-1, -1.0, 2.0, 2.0)};
	protected Stroke[] strokes = {new BasicStroke(1)};

	/**
	 * Constructor
	 * @param title_ String title of the report
	 * @param variableNames_ String array of variable names to identify different time series lines
	 */
	public CRTimeSeriesReport(String title_, String[] variableNames_) {
		super(title_, new Dimension(1000,500), true);
		xAxisTitle = "Time";
		yAxisTitle = "Value";
		variableNames = variableNames_;
		shouldDisplayLegend = true;
		isScatterPlot = false;
		shouldOuputVariableNames = false;
	}
	
	/**
	 * Constructor
	 * @param title_ String title of the report
	 * @param variableNames_ String array of variable names to identify different time series lines
	 * @param size_ Dimension size of the report window
	 */
	public CRTimeSeriesReport(String title_, String[] variableNames_, Dimension size_) {
		super(title_, size_, true);
		xAxisTitle = "Time";
		yAxisTitle = "Value";
		variableNames = variableNames_;
		shouldDisplayLegend = true;
		isScatterPlot = false;
		shouldOuputVariableNames = false;
	}
	
	
	//==================================== SIMULATION EVENTS ====================================
	
	/**
	 * Called by CRController each time a run starts. 
	 * Use this to reset self for a brand new simulation and call onNewTrial of children models.
	 * @param runNumber_ int new run number
	 */
	public void onRunStart(int runNumber_) {
		
	}
	
	/**
	 * Called by CRController each time a trial starts.
	 * Set all variables to 0 and create new data series. 
	 * @param trialNumber_ int new trial number
	 * @param runNumber_ int current run number
	 */
	public void onTrialStart(int trialNumber_, int runNumber_) {
		dataSeriesArray = new ArrayList <XYSeries>();
		totalDataSeriesValuesOfSecond = new double[variableNames.length];
		numOfValuesAddedInCurrentSecond = new int[variableNames.length];
		//-- create elements of the dataSeriesArray
		for (String seriesName : variableNames) {
			XYSeries series = new XYSeries(seriesName);
			series.setDescription(seriesName);
			dataSeriesArray.add(series);
		}
	}
	
	/**
	 * Save self to an image if allowed
	 * @param trialNumber_ int ending trial number
	 * @param runNumber_ int current run number
	 */
	public void onTrialEnd(int trialNumber_, int runNumber_) {
		CRSettings settings = CRSettings.getSingleton();
		if (settings.getShouldPrintGraphicReports()) {
			createSelf(reportController.getCurrentFilePath() + "_" + this.getFileName(), false);
		}
		if (CRSettings.getSingleton().getShouldPrintTextReports()) {
        	createSelfAsText(reportController.getCurrentFilePath() + "_" + this.getFileName());
        }
	}
	
	/**
	 * Called from CRController before onUpdateLoopStart() of all world objects is called.
	 * Set the value for current time to 0 
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopStart(int timeCounter_, int timeUnit_) {
		super.onUpdateLoopStart(timeCounter_, timeUnit_);
		//-- add 0 at the beginning, so that there is at least some value for each second.
		if (timeCounter_ == 0) {
			for (int variableIndex=0; variableIndex < dataSeriesArray.size(); variableIndex++) {
				addValue(0,variableIndex);
				//-- reset the number of values added this second to 0
				numOfValuesAddedInCurrentSecond[variableIndex] = 0;
			}
		}
		
	}
	
	
	/**
	 * Called from CRController after onUpdateLoopEnd() of all world objects is called.
	 * Calculate average over the passed second
	 * @param timeUnit_ int current time unit
	 */
	public void onUpdateLoopEnd(int timeCounter_, int timeUnit_) {
		super.onUpdateLoopEnd(timeCounter_, timeUnit_);
		for (int variableIndex=0; variableIndex < dataSeriesArray.size(); variableIndex++) {			
			//-- only add value if at the end of a new second
			if (timeCounter_ == CRSettings.getSingleton().getTimeUnitInterval()-1) {
				//-- add an average of previous second, average over the number of values there can be for each second
				double registeredValue =  totalDataSeriesValuesOfSecond[variableIndex];
				if (numOfValuesAddedInCurrentSecond[variableIndex] > 1) {
					registeredValue = totalDataSeriesValuesOfSecond[variableIndex]/(double)numOfValuesAddedInCurrentSecond[variableIndex];
				}
				dataSeriesArray.get(variableIndex).add(timeUnit_, registeredValue);
				//-- reset total value:
				totalDataSeriesValuesOfSecond[variableIndex] = 0.0;
			}
		}
	}
	
	
	
	
	//==================================== DATA COLLECTION =================================
	/**
	 * Register new value set for the current time step.
	 * The value set size should be the same as size of set names the report was initialised with.
	 * @param values_ double[] array of values
	 */
	public void addValueSet(double[] values_) {
		for (int i=0; i<variableNames.length; i++) {
			addValue(values_[i], variableNames[i]);
		}
	}
	
	
	/**
	 * Register a new value for a specific variable name from the overal value set
	 * @param value_ double new value
	 * @param variableName_ String variable name from the variable name set this object was initialised with
	 */
	public void addValue(double value_, String variableName_) {
		int index = getVariableNameIndex(variableName_);
		addValue(value_, index);
	}
	
	/**
	 * Register a new value for a 0th index of the overal value set
	 * @param value_ double new value
	 */
	public void addValue(double value_) {
		addValue(value_, 0);
	}
	
	/**
	 * Register a new value for a specific index of the overal value set
	 * @param value_ double new value
	 * @param variableIndex_ int 0 <= index < size of set names
	 */
	public void addValue(double value_, int variableIndex_) {
		
		if (variableIndex_ >= 0 && variableIndex_ < variableNames.length) {
			//-- add to temporary value:
			totalDataSeriesValuesOfSecond[variableIndex_] += value_;
			numOfValuesAddedInCurrentSecond[variableIndex_]++;
			
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
		
		//-- test if there is any data to show
		if (dataSeriesArray == null) {
			System.err.println("CRTimeSeries" + title + " has no data. Hint: Have you called onNewTrial to initialise?");
			return;
		}
		//-- test if there is at least 1 color:
		if (colors.length <= 0) {
			System.err.println("CRTimeSeries" + title + " no graph colors specified.");
			return;
		}
		//-- test if there is at least 1 shape:
		if (shapes.length <= 0) {
			System.err.println("CRTimeSeries" + title + " no graph line shapes specified.");
			return;
		}
		//-- test if there is at least 1 stroke:
		if (strokes.length <= 0) {
			System.err.println("CRTimeSeries" + title + " no graph line strokes specified.");
			return;
		}
		
		if (!CRSettings.getSingleton().getIsConsoleOnlyBuild()) {
			//-- prepare data
			XYDataset dataset = prepareXYSeriesForDisplay(dataSeriesArray);	
			
			//-- create report object or update it
			if (jFreeChart == null) {	
				jFreeChart = createChart(title, xAxisTitle, yAxisTitle, dataset);
				jFreeChart.getXYPlot().setFixedLegendItems(null);
				jFreeChart.getXYPlot().getDomainAxis().setLabelFont(new Font("Tahoma", Font.BOLD, 12));
				ValueAxis yAxis = jFreeChart.getXYPlot().getRangeAxis();
				if (yAxis.getUpperBound() > 1) {
					yAxis.setAutoTickUnitSelection(false);
					NumberTickUnit rUnit = new NumberTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 5.0);
					((NumberAxis) yAxis).setTickUnit(rUnit);
				} else {
					yAxis.setAutoTickUnitSelection(false);
					NumberTickUnit rUnit = new NumberTickUnit( 0.1);
					((NumberAxis) yAxis).setTickUnit(rUnit);
				}
				chartPanel = new ChartPanel(jFreeChart);
				chartPanel.setPreferredSize(new java.awt.Dimension(this.size.width, this.size.height-35));
				basePanel.add(chartPanel);
			} else {
				jFreeChart.getXYPlot().setDataset(dataset);
			}
		
			//-- set range if specified
			if (yAxisDisplayRange != null) {
				jFreeChart.getXYPlot().getRangeAxis().setRange(yAxisDisplayRange);
			}
	        
			//-- format legend
			jFreeChart.getLegend().visible = shouldDisplayLegend;
			
	    	//-- set shape and paint of series
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) jFreeChart.getXYPlot().getRenderer();
		//	XYLineAndShapeRenderer lineAndShapeRenderer = null;
	        for (int i=1; i<dataset.getSeriesCount(); i++) {
	        	//-- make sure colors, shapes and paint strokes wrap around their arrays in case not enough specified.
	        	//   the first series is just dummy titled 'Legend' so don't count it (therefore use i-1) when calculating
	        	int colorId = ((i-1)%colors.length);
	        	int shapeId = ((i-1)%shapes.length);
	        	int strokeId = ((i-1)%strokes.length);
				renderer.setSeriesPaint(i,colors[colorId] );
	    		renderer.setSeriesShape(i, shapes[shapeId] );
	    		renderer.setSeriesStroke(i, strokes[strokeId]);
	    		if (isScatterPlot) {
	    			renderer.setSeriesLinesVisible(i,false);
	    		} else {
	    			renderer.setSeriesLinesVisible(i,true);
	    		}
	    		
			}
	        
			//-- has to be visible for printing:
	        baseFrame.setVisible(true);
	        if (printToFileName_.length() > 0) {
	        	freezeDisplay(); //pause the program for a while, otherwise reports may come out incomplete
				CRFileIOHelper.componentToJpeg(chartPanel, printToFileName_);
				
				//-- hide afterwards:
				if (!show_) {
					baseFrame.setVisible(false);
				}
	        }
		}
	}
	
	/**
	 * Save a textual representation of data, where columns represent individual variables (tab-separated) 
	 * and rows represent different time steps.
	 * @param printToFileName_ String path + file name
	 */
	public void createSelfAsText(String printToFileName_) {
		String outputString = "";
		if (shouldOuputVariableNames) {
			outputString += "t\t";
			for (int variableIndex=0; variableIndex < variableNames.length; variableIndex++) {
				outputString += variableNames[variableIndex].replace(" ", "_");
				if (variableIndex < variableNames.length - 1) {
					outputString += "\t";
				}
			}
			outputString += "\n";
		}
		for (int t=0; t<dataSeriesArray.get(0).getItemCount(); t++) {
			//-- time
			outputString += dataSeriesArray.get(0).getX(t) +  "\t";
			//-- variables
			for (int variableIndex=0; variableIndex < dataSeriesArray.size(); variableIndex++) {	
				outputString += dataSeriesArray.get(variableIndex).getY(t);
				if (variableIndex < dataSeriesArray.size() - 1) {
					outputString += "\t";
				}
			}
			outputString += "\n";
		}
		
		CRFileIOHelper.stringToFile(outputString, printToFileName_);
	}
	

	
	/**
	 * Create a chart based on a dataset.
	 * @param title_ : name of the chart
	 * @param xAxisTitle_ : label for x axis
	 * @param yAxisTitle_ : label for y axis
	 * @param dataset : data
	 * @return chart
	 */
	private JFreeChart createChart(String title_, String xAxisTitle_, String yAxisTitle_, final XYDataset dataset) {
        
        //-- create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
        	title_,      				// chart title
        	xAxisTitle_,                // x axis label
        	yAxisTitle_,                // y axis label
            dataset,                  	// data
            PlotOrientation.VERTICAL,
            true,                     	// include legend
            true,                     	// tooltips
            false                     	// urls
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        
        //-- get a reference to the plot for further customisation
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        //-- change the auto tick unit selection to integer units only
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                
        return chart; 
    }
	
	/**
	 * Convert data for display in a line graph
	 */
	private XYDataset prepareXYSeriesForDisplay(List<XYSeries> rawData) {
		
		if (rawData == null || rawData.size() == 0) {
			return null;
		} else {
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(new XYSeries("Legend: ")); //needs a series at the beginning, otherwise won't display...
			
			//-- go through the list of rawData (e.g. multiple variables the report should show:			
			for (int var=0; var < rawData.size(); var++) {
				dataset.addSeries(this.prepareDisplaySeries(rawData.get(var)));
			} 
		    return dataset;
		}
	}
	
	/**
	 * Process raw data series to get rid of data cluttered in time
	 */
	private XYSeries prepareDisplaySeries(XYSeries rawSeries) {
		XYSeries displaySeries = new XYSeries(rawSeries.getDescription());
		try {
			displaySeries = (XYSeries) rawSeries.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return displaySeries;
	}
	
	
	//==================================== GETTERS / SETTERS =================================
	
	/**
	 * Get mean of a data series identified by a variable name
	 * @param variableName_ String variable name
	 * @param timeRange_ Range time range
	 * @return double variable mean
	 */
	public double getVariableMean(String variableName_, Range timeRange_) {
		int index = getVariableNameIndex(variableName_);
		return getVariableMean(index, timeRange_);
	}
	
	/**
	 * Get mean of a data series identified by a variable index.
	 * @param variableIndex_ int variable index
	 * @param timeRange_ Range time range
	 * @return double variable mean
	 */
	public double getVariableMean(int variableIndex_, Range timeRange_) {
		if (variableIndex_ < 0 || variableIndex_ >= dataSeriesArray.size()) {
			throw new IndexOutOfBoundsException();
		}
		XYSeries dataSeries = this.dataSeriesArray.get(variableIndex_);
		
		int start, end;
		if (timeRange_ != null) {
			start = Math.max(0, (int) timeRange_.getLowerBound());
			end = Math.min((int) dataSeries.getMaxX(), (int) timeRange_.getUpperBound());
		} else {
			start = 0;
			end = (int) dataSeries.getMaxX();
		}
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (int i = start; i<=end; i++) {
			stats.addValue(dataSeries.getY(i).doubleValue());
		}
		return stats.getMean();
		
	}
	
	/**
	 * Get index of a variable name
	 * @param variableName_ String variable name. Set to empty string to get the 1st index.
	 * @return int index of the variable name
	 */
	protected int getVariableNameIndex(String variableName_) {
		for (int i=0; i<variableNames.length; i++) {
			if (variableNames[i].equals(variableName_)) {
				return i;
			}
		}
		return -1;
	}
	
	public void setIsScatterPlot(boolean value_) { isScatterPlot = value_; }
	public void setColors(Color[] colors_) { colors = colors_; }
	public void setShapes(Shape[] shapes_) { shapes = shapes_; }
	public void setStrokes(Stroke[] strokes_) { strokes = strokes_; }
	
	public void setShouldDisplayLegend(boolean value_) { shouldDisplayLegend = value_; }
	
	public void setYAxisTitle(String value_) { yAxisTitle = value_; }
	public void setXAxisTitle(String value_) { xAxisTitle = value_; }
	public void setYAxisDisplayRange(Range range_) { if (range_ != null) { yAxisDisplayRange = new Range(range_.getLowerBound(), range_.getUpperBound()); }}
	
	/**
	 * If set to true, variable names will be printed as the 1st row in the text ouput
	 * @param value_ boolean 
	 */
	public void setShouldOutputVariableNames(boolean value_) { shouldOuputVariableNames = value_; }
	public boolean getShouldOutputVariableNames() { return shouldOuputVariableNames; }
	
}
