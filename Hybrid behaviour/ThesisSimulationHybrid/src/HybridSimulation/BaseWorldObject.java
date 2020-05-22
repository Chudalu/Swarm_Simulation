package HybridSimulation;

import java.util.ArrayList;


import net.lenkaspace.creeper.model.CRBaseDynamicModel;
import net.lenkaspace.creeper.model.CRBaseSituatedModel;
import net.lenkaspace.creeper.vo.CRVector3d;

public class BaseWorldObject extends CRBaseDynamicModel {
	
	protected double resistance;
	protected double maxPushAngleFraction; // max difference in x component of relative vector towards object to be pushed
	protected double basePickUpProbability; //probability that object in the front will be pushed
	private boolean isBeingPushed;
	public BaseWorldObject(double resistance_, int id_, CRVector3d pos_, CRVector3d size_, double rotation_, String imageName_) {
		super(id_, pos_, size_, rotation_, CRBaseSituatedModel.SHAPE.RECTANGLE, imageName_);
		resistance = resistance_;
		maxPushAngleFraction = 0.2;
		basePickUpProbability = 1;
		isBeingPushed = false;
		
	}
	
	//find objects that should be pushed.
	public void findPushedObjects (ArrayList<BaseWorldObject> chainOfObjects) {
		ArrayList<CRBaseSituatedModel> worldObjects = ((World)world).getRelevantObjectsAroundPosition(position, size.y);
		for (CRBaseSituatedModel situatedObject : worldObjects) {
			//find out if object is food or robot
			if(situatedObject.getClass() == Robot.class || situatedObject.getClass() == Foods.class) {
				BaseWorldObject worldObject = (BaseWorldObject) situatedObject;
				
				if (!chainOfObjects.contains(worldObject) && worldObject != this && worldObject.getIsBeingPushed() == false) {
					//find out if colliding with stone and stone is ahead
					CRVector3d relVectorToObject = this.getRelativeVectorTo(worldObject.getPosition());
					boolean collidesWith = this.isCollidingWith(worldObject);
					
					if (collidesWith  && relVectorToObject.y > 0) {
						//-- tell ant that is pushing the queue that new object was added
						Robot pushingRobot = (Robot)chainOfObjects.get(0);
						if (!pushingRobot.decideToSlide(worldObject)) {
							if (pushingRobot.decideToPickup(worldObject)) {
								worldObject.setIsBeingPushed(true);
								chainOfObjects.add(worldObject);
								this.onPickedUp(worldObject);
								if (this != pushingRobot) {
									pushingRobot.onPickedUp(worldObject);
								}
								//find next one in chain
								worldObject.findPushedObjects(chainOfObjects);
							}
							else {
								pushingRobot.onNotPickedUp(worldObject);
							}
							
						}
						continue;
					}
					
				}
			}
		}
		
	}
	
	public ArrayList<Robot> getRobotPosInCluster(BaseWorldObject object){
		ArrayList<Robot>robotsInWorld = ((World)world).getRobots();
		ArrayList<Pheromones> presentPheromones = ((World)world).getPheromones();
		ArrayList<Robot> robotInCluster = new ArrayList<>();
		int id = 0;
		Robot thisRobot = (Robot) object;
		for(Pheromones p : presentPheromones) {
			if (p.getsizer()<= 50 && p.getsizer()>40) {
				if (p.getPheromoneConcentrationAt(thisRobot.getPosition())>0.6) {
					id = p.getPhID();
				}
			}
		}
		for (Pheromones pr : presentPheromones) {
			if (pr.getsizer()<= 50 && pr.getsizer()>40) {
				for (Robot r : robotsInWorld) {
					if (r != thisRobot && r.checkifPushingFood()== false && pr.getThrustForce() == 0 && id == pr.getPhID()  && pr.getPheromoneConcentrationAt(r.getPosition())>0 && pr.getPheromoneConcentrationAt(r.getPosition())>0.6) {
						robotInCluster.add(r);
					}
				}
			}
		}
		
		return robotInCluster;
	}
	
	public ArrayList<Robot> getRobotPosInClusterToNest(BaseWorldObject object){
		ArrayList<Robot>robotsInWorld = ((World)world).getRobots();
		ArrayList<Pheromones> presentPheromones = ((World)world).getPheromones();
		ArrayList<Robot> robotInCluster = new ArrayList<>();
		Robot thisRobot = (Robot) object;
		int id = 0;
		for(Pheromones p : presentPheromones) {
			if (p.getsizer()<= 50 && p.getsizer()>40) {
				if (p.getPheromoneConcentrationAt(thisRobot.getPosition())>0.6) {
					id = p.getPhID();
				}
			}
		}
		for (Pheromones pr : presentPheromones) {
			if (pr.getsizer()<= 50 && pr.getsizer()>40) {
				for (Robot r : robotsInWorld) {
					if (r != thisRobot && r.checkifPushingFood()== true && id == pr.getPhID() && pr.getThrustForce() == 0 && pr.getPheromoneConcentrationAt(r.getPosition())>0 && pr.getPheromoneConcentrationAt(r.getPosition())>0.6) {
						robotInCluster.add(r);
					}
				}
			}
		}
		
		return robotInCluster;
	}
	
	public ArrayList<Double> getRobotRotationInCluster(BaseWorldObject object, ArrayList<Robot> robots){
		ArrayList<Robot> robotsInWorld = robots;
		ArrayList<Double> robRot = new ArrayList<>();
		Robot thisRobot = (Robot) object;
		for(Robot r : robotsInWorld) {
			if (r != thisRobot && r.foodLocationFound == true) {
				double dist = thisRobot.getRelativeVectorTo(r.getPositionr()).y;
				if (dist <= 30) {
					robRot.add(r.getRotationr());
				}
			}
		}
		return robRot;
	}
	
	public ArrayList<Double> getRobotThrustInCluster(BaseWorldObject object, ArrayList<Robot> robots){
		ArrayList<Robot> robotsInWorld = robots;
		ArrayList<Double> robTh = new ArrayList<>();
		Robot thisRobot = (Robot) object;
		for(Robot r : robotsInWorld) {
			if (r != thisRobot) {
				double dist = thisRobot.getRelativeVectorTo(r.getPositionr()).y;
				if (dist <= 50) {
					robTh.add(r.getThrustForcer());
				}
			}
		}
		
		return robTh;
	}
	
	
	public boolean checkforFood(BaseWorldObject robot) {
		boolean foodRemains = false;
		ArrayList<Foods>foodAvailable = ((World)world).getFoods();
		ArrayList<Pheromones>presentPheromones = ((World)world).getPheromones();
		for(Pheromones ph : presentPheromones) {
			if (ph.getsizer() == 190) {
				for (Foods food : foodAvailable) {
					BaseWorldObject f = (BaseWorldObject)food;
					if (ph.getPheromoneConcentrationAt(food.getPos())>0 && ph.getPheromoneConcentrationAt(robot.getPosition())>0  && f.getIsBeingPushed() == false) {
						foodRemains = true;
					}
				}
			}
		}
		return foodRemains;
	}
	
	/**
	 * Triggered when an object was just added to the queue of pushed objects.
	 * @param object_ BaseWorldObject object
	 */
	protected void onPickedUp(BaseWorldObject object_) {
		
	}
	
	/**
	 * Triggered when an object should have pushed (i.e. is colliding) but basePickUpProbability decided not to push it
	 * @param object_ BaseWorldObject object
	 */
	protected void onNotPickedUp(BaseWorldObject object_) {
		
	}
	
	
	//==================================== GETTERS / SETTERS ====================================
	public double getResistance() {
		return resistance; 
	}
	
	public boolean getIsBeingPushed() {
		return isBeingPushed; 
	}
	
	public void setIsBeingPushed(boolean val_) { 
		isBeingPushed = val_;
	}
	
}
