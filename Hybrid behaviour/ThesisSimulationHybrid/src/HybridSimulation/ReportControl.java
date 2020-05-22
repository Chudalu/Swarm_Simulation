package HybridSimulation;
import java.util.ArrayList;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.CRSettings;
import net.lenkaspace.creeper.helpers.CRFileIOHelper;
import net.lenkaspace.creeper.helpers.CRMaths;
import net.lenkaspace.creeper.report.CRReportController;
import net.lenkaspace.creeper.vo.CRVector3d;


public class ReportControl extends CRReportController{
	
	public static String REPORT_WORLD_SNAPSHOT = "World";
	public static String REPORT_PUSHING_TIME = "Robot pushing time";
	String outputData = "";

	public ReportControl(CRController controller) {
		super(controller);
		
	}
	
	public void onTrialStart(int trialNumber_, int runNumber_) {
		super.onTrialStart(trialNumber_, runNumber_);
	}
	
	/**
	 * Called by CRController each time a trial ends.
	 * Saves all reports into a folder.
	 * @param trialNumber_ int ending trial number
	 * @param runNumber_ int current run number
	 */
	public void onTrialEnd(int trialNumber_, int runNumber_) {
		super.onTrialEnd(trialNumber_, runNumber_);
		
		if (CRSettings.getSingleton().getShouldPrintTextReports()) {
			//-- go through all stones and save their numbers based on their locations
			//   in quarter-circle bins going from the middle to the outside of the world
			World world = (World)controller.getWorld();
			int circleStepSize = 10; //by how much will radius of one cirlce be bigger than previous circle
			int numOfCircles = (int) (Math.hypot(world.getSize().x/2, world.getSize().y/2)/circleStepSize); //num of circles as max diameter divided by circle step
			int[][] numOfFoods = new int[4][numOfCircles];
			for (int q=0;q<4;q++) {
				for (int c=0;c<numOfCircles;c++) {
					numOfFoods[q][c] = 0;
				}
			}
			
			String foodCartesianCoordinates = "";
			String foodPolarCoordinates = "";
			CRVector3d worldCenter = new CRVector3d(world.getSize().x/2,world.getSize().y/2,0);
			for (Foods food : world.getFoods()) {
				CRVector3d position = food.getPosition();
				//-- get which quarter of the world the stone is in. 
				//   Quarters are numbered from north east, going anti-clockwise
				int quarterId = 0;
				if (position.x <= worldCenter.x) {
					if (position.y <= worldCenter.y) {
						quarterId = 1;
					} else {
						quarterId = 2;
					}
				} else {
					if (position.y > worldCenter.y) {
						quarterId = 3;
					}
				}
				
				//-- get which circle it belongs to, based on how far it is from the middle of the world
				int distanceToMiddle = (int)Math.round(CRMaths.getDistanceOfPoints(worldCenter.x, worldCenter.y, position.x, position.y));
				int circleNo = (int) Math.floor(distanceToMiddle/circleStepSize);
				if (circleNo >= numOfCircles) {
					circleNo = numOfCircles-1;
				}
				
				//-- record for this particular quarter of the circle that there is a stone
				numOfFoods[quarterId][circleNo]++;
				
				//-- get angle to the middle
				double angle = Math.toDegrees(Math.atan2( worldCenter.y - position.y, position.x - worldCenter.x));
				
				//-- add to strings
				foodCartesianCoordinates += position.x + "," + position.y + "\n";
				foodPolarCoordinates += distanceToMiddle + "," + angle + "\n";
			}
			
			//-- convert numOfStones array into csv
			String foodDistributionString = "";
			for (int q=0;q<4;q++) {
				for (int c=0;c<numOfCircles;c++) {
					foodDistributionString += numOfFoods[q][c] + ",";
				}
				foodDistributionString += "\n";
			}
			
			int foodNum = numofFoodInNest();
			int PherNum = getPheromoneUse();
			int forageTime = getTimeofForage();
			String experimentData = " "+foodNum +","+PherNum+","+forageTime+"\n";
			outputData += experimentData;
			CRFileIOHelper.stringToFile(outputData, this.currentFilePath + "ExperimentData");
			world.resetOutputs();
		}
		
	}

	public int numofFoodInNest() {
		ArrayList<Foods>foodinNest = new ArrayList<>();
		World world = (World)controller.getWorld();
		ArrayList<Foods>foodAvailable = world.getFoods();
		ArrayList<Pheromones>presentPheromones = world.getPheromones();
		for (Pheromones ph : presentPheromones) {
			if (ph.getsizer() == 200) {
				for (Foods food : foodAvailable) {
					if (ph.getPheromoneConcentrationAt(food.getPos())>0) {
						if (!foodinNest.contains(food)) {
							foodinNest.add(food);
						}
					}
				}
			}
			if (ph.getsizer() == 190) {
				for (Foods food : foodAvailable) {
					if (ph.getPheromoneConcentrationAt(food.getPos())>0 && food.getIsBeingPushed() == true) {
						if (!foodinNest.contains(food)) {
							foodinNest.add(food);
						}
					}
				}
			}
			for (Foods f : foodAvailable) {
				if (f.getIsBeingPushed() == true) {
					if (!foodinNest.contains(f)) {
						foodinNest.add(f);
					}
				}
			}
		}
		return foodinNest.size();
	}
	
	public int getPheromoneUse() {
		World world = (World)controller.getWorld();
		return world.getPheromoneUse();
	}
	
	public int getTimeofForage() {
		World world = (World)controller.getWorld();
		int val = world.gettimeofFoodForage();
		if (val == 50000) {
			return 0;
		}
		else {
			return val;
		}
	}
	
	
}
