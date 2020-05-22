package PursuitSimulation;

import java.util.ArrayList;

import PursuitSimulation.Robot.TYPE;
import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.helpers.CRMaths;
import net.lenkaspace.creeper.model.CRBaseSituatedModel;
import net.lenkaspace.creeper.model.CRBinWorld;
import net.lenkaspace.creeper.model.CRBaseSituatedModel.SHAPE;
import net.lenkaspace.creeper.view.CRRenderer;
import net.lenkaspace.creeper.vo.CRVector3d;


public class World extends CRBinWorld {

	public static final String OPENFREEWORLD = "Open-Spaced World";
	public static final String OBSTACLEFILLEDWORLD = "World filled with Obstacles";
	public static final String WORLDWITHMULTIPLEFOOD = "Mutiple Food-Filled World";
	public static final String MUTIPLEFOODANDOBSTACLE = "Mutiple Food with Obstacle in World";
	static int PheromoneResourceUsed = 0;
	static int timeofFoodExhaustion = 50000;
	private ArrayList<Foods> foods;
	private ArrayList<Robot> robots;
	private ArrayList<Pheromones> pheromones;
	private ArrayList<CRBaseSituatedModel>obstacles;
	int numberofPheromones;
	//-- create Pheromones
	
	
	public World(CRController controller) {
		super(new CRVector3d(25,25,0), 0, new CRVector3d(800,800,0), controller);
		robots = new ArrayList<Robot>();
		foods = new ArrayList<Foods>();
		pheromones = new ArrayList<Pheromones>();
		obstacles = new ArrayList<>();
		numberofPheromones = 0;
	}
	
	public void onTrialStart(int trialNumber_, int runNumber_) {
		this.clearChildren();
		
		Settings settings = Settings.getSingleton();
		String currentWorld = settings.currentWorld;
		
		ArrayList<CRVector3d> positions = new ArrayList<CRVector3d>();
		ArrayList<CRVector3d> sizes = new ArrayList<CRVector3d>();
		
		if (currentWorld == OPENFREEWORLD) {
			positions.add(new CRVector3d(550,600,0));
			sizes.add(new CRVector3d(settings.PheromoneDiam, settings.PheromoneDiam, 0));
		}
		else if (currentWorld == OBSTACLEFILLEDWORLD) {
			positions.add(new CRVector3d(550,600,0));
			sizes.add(new CRVector3d(settings.PheromoneDiam, settings.PheromoneDiam, 0));
			
			CRBaseSituatedModel obstacle = new CRBaseSituatedModel(0, new CRVector3d(650,435,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle);
			CRBaseSituatedModel obstacle1 = new CRBaseSituatedModel(0, new CRVector3d(420,390,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle1);
			CRBaseSituatedModel obstacle2 = new CRBaseSituatedModel(0, new CRVector3d(300,700,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle2);
			CRBaseSituatedModel obstacle3 = new CRBaseSituatedModel(0, new CRVector3d(150,395,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle3);
		}
		else if (currentWorld == WORLDWITHMULTIPLEFOOD) {
			positions.add(new CRVector3d(550,600,0));
			sizes.add(new CRVector3d(settings.PheromoneDiam, settings.PheromoneDiam, 0));
		}
		else {
			positions.add(new CRVector3d(550,600,0));
			sizes.add(new CRVector3d(settings.PheromoneDiam, settings.PheromoneDiam, 0));
			
			CRBaseSituatedModel obstacle = new CRBaseSituatedModel(0, new CRVector3d(650,435,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle);
			CRBaseSituatedModel obstacle1 = new CRBaseSituatedModel(0, new CRVector3d(420,390,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle1);
			CRBaseSituatedModel obstacle2 = new CRBaseSituatedModel(0, new CRVector3d(300,700,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle2);
			CRBaseSituatedModel obstacle3 = new CRBaseSituatedModel(0, new CRVector3d(150,395,0), new CRVector3d(100,100,0), 180, SHAPE.CIRCLE, CRRenderer.CR_GREEN_DOT);
			this.addObstacles(obstacle3);
		}
		//Pheromones pheromone = new Pheromones(positions,sizes, this);
		//this.addDynamicModel(pheromone);
		this.addPheromones(positions, sizes);
		
		//create food in specific area span.
		if (currentWorld == OPENFREEWORLD || currentWorld == OBSTACLEFILLEDWORLD ) {
			for (int a=0; a<50; a++) {
				double xPos = CRMaths.getRandomInteger(150, (int)(200));
				double yPos = CRMaths.getRandomInteger(150, (int)(200));
				Foods food = new Foods(a,new CRVector3d(xPos, yPos, 0));
				this.addFood(food);
			}
		}                
		
		if (currentWorld == MUTIPLEFOODANDOBSTACLE || currentWorld == WORLDWITHMULTIPLEFOOD) {
			int count = 300;
			for (int b=0; b<50; b++) {
				double xPos = CRMaths.getRandomInteger(150, (int)(200));
				double yPos = CRMaths.getRandomInteger(150, (int)(200));
				Foods food = new Foods(b,new CRVector3d(xPos, yPos, 0));
				this.addFood(food);
			}
			
			for (int c=300; c<350; c++) {
				double xPos = CRMaths.getRandomInteger(600, (int)(650));
				double yPos = CRMaths.getRandomInteger(200, (int)(250));
				Foods food = new Foods(c,new CRVector3d(xPos, yPos, 0));
				this.addFood(food);
			}
		}
		
		// Create leader robot 
		int idcount = 10000;
		for (int d=0; d< settings.NumberofLeaderRobots; d++) {
			double xPos = CRMaths.getRandomInteger((int)size.x - 300, (int)size.x - 200);
			double yPos = CRMaths.getRandomInteger((int)size.y - 250, (int)size.y - 150);
			double rotation = CRMaths.getRandomInteger(-180, 180);
			Robot robot = new Robot(10000+d,new CRVector3d(xPos, yPos, 0),rotation, TYPE.LEADER);
			this.addRobot(robot);
		}
		
		// Create follower robot
		for (int e=0; e< settings.NumberofFollowerRobots; e++) {
			double xPos = CRMaths.getRandomInteger((int)size.x - 300, (int)size.x - 200);
			double yPos = CRMaths.getRandomInteger((int)size.y - 250, (int)size.y - 150);
			double rotation = CRMaths.getRandomInteger(-180, 180);
			Robot robot = new Robot(30000+e,new CRVector3d(xPos, yPos, 0),rotation, TYPE.FOLLOWER);
			this.addRobot(robot);
		}
		
		super.onTrialStart(trialNumber_, runNumber_);
	}
	
	//Simulation
	public void addRobot(Robot robot) {
		if(!robots.contains(robot)) {
			//add to child model
			addDynamicModel(robot);
			//set its world to this
			robot.setWorld(this);
			//-- add it to the list
			robots.add(robot);		
		}
	}
	
	public void addFood(Foods food) {
		if(!foods.contains(food)) {
			//add food to child model
			addDynamicModel(food);
			//set its world to this one.
			food.setWorld(this);
			//add it ot the list;
			foods.add(food);
		}
	}
	
	
	public void removePheromone(Pheromones pheromone) {
		pheromones.remove(pheromone);
	}
	
	public void addPheromones(ArrayList<CRVector3d> position,ArrayList<CRVector3d> size) {
		//later add option to inprove size of pheromone when added to the same location.
		if (position.size() > 0 && position.size() == size.size()) {
			Pheromones pheromone = new Pheromones(position,size, this);
			//add pheromone to list
			pheromones.add(pheromone);
			//add pheromone to child model
			this.addDynamicModel(pheromone);
		}
		/*
		if (!pheromones.contains(pheromone)) {
			//add pheromone to child model
			this.addDynamicModel(pheromone);
			//set it to this world;
			pheromone.setWorld(this);
			//add it to list of pheromones
			pheromones.add(pheromone);
		}*/
	}
	public void addObstacles(CRBaseSituatedModel obstacle) {
		obstacles.add(obstacle);
		this.addSituatedModel(obstacle);
	}
	public void clearChildren() {
		robots.clear();
		foods.clear();
		pheromones.clear();
		super.clearChildren();
	}
	
	public void timeofFoodExhaustion(int num) {
		if (num < timeofFoodExhaustion) {
			timeofFoodExhaustion = num;
		}
	}
	
	public void updatePheromoneUse() {
		PheromoneResourceUsed++;
	}
	//getters
	public ArrayList<Robot> getRobot() { 
		return robots; 
	}
	public ArrayList<Foods> getFoods() { 
		return foods;
		}
	public ArrayList<Pheromones> getPheromones() { 
		return pheromones;
	}
	public int getPheromoneUse() {
		return PheromoneResourceUsed;
	}
	public int gettimeofFoodForage() {
		return timeofFoodExhaustion;
	}
	public void resetOutputs() {
		PheromoneResourceUsed = 0;
		timeofFoodExhaustion = 50000;
	}
	public ArrayList<CRBaseSituatedModel> getObstacles(){
		return obstacles;
	}
}
