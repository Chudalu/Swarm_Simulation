package PursuitSimulation;
import net.lenkaspace.creeper.model.CRSettings;
import net.lenkaspace.creeper.helpers.CRMaths;
import net.lenkaspace.creeper.model.CRBaseSituatedModel;
import net.lenkaspace.creeper.model.CRBinWorld;
import net.lenkaspace.creeper.report.CRTimeSeriesReport;
import net.lenkaspace.creeper.view.CROutputPopup;
import net.lenkaspace.creeper.view.CRRenderer;
import net.lenkaspace.creeper.vo.CRVector3d;
import java.util.ArrayList;




public class Robot extends BaseWorldObject{
	//random walk parameters
	protected static final double RANDOM_WALK_ANGLE_CHANGE_PROBABILITY = 0.3 ;
	protected static final int RANDOM_WALK_ANGLE_CHANGE_MIN = 140;
	protected static final int RANDOM_WALK_ANGLE_CHANGE_MAX = 180;
	
	//walk to location with some randomization or noise
	protected static final double WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY = 0.5 ;
	protected static final int WALK_RANDOMISATION_MIN_ANGLE_CHANGE = 120;//100;
	protected static final int WALK_RANDOMISATION__MAX_ANGLE_CHANGE = 150;//140;//45;
	protected static final double ANGLE_CHANGE_MULTIPLIER = 0.1;
	
	
	
	
	public enum TYPE{
		LEADER,
		FOLLOWER,
	}
	
	protected TYPE type;
	protected ArrayList<BaseWorldObject> pushedObjects, foodRemaining;
	protected boolean foodLocationFound;
	protected boolean isMovingRandomly;
	protected boolean isWithinNest;
	protected boolean isWithinFoodLocation, choiceMade, isWithinTrail;
	protected boolean canDrop, placePheromone, returntoNest;
	protected int startedPushingTimeCounter, updateVal, pher, distancetoPosition, pheromoneSizeRE, LeadingPheromoneRE;
	protected boolean goingtoFoodLocation, goingtoNestAlone, goingtoNestwithTrail, justEntered;
	protected boolean goingtoNest, goingtoRecruit, leadingPheromonePlaced, toCreateTrail;
	protected boolean isRandomlySearching, returntoCollectFood, clearSeenPheromones, readytoForage;
	protected boolean isWaitingtoFollow,isWaitingforLeader, atLeadingPheromone, firstFollow, returnFromExhaustedLocation ;
	protected CRVector3d nestLocation, robotLocation, foodLocation;
	protected ArrayList<Integer>seenPheromoneIDs;
	protected double lastHigherEqualPheromoneConcentration, pheromoneCheck;
	protected CRVector3d lastHigherEqualPheromoneConcentrationPosition;
	protected double lastPheromoneConcentration, newrotate;
	boolean turnLeft, turnRight;
	protected boolean searchingFoodLocation, inNestwithFood;
	ArrayList<Pheromones> newPheromone;
	
	protected double resistanceMultiplier;
	protected double feltResistance;
	
	protected boolean shouldRecordCarryTime;
	
	
	public Robot(int id, CRVector3d pos, double rotation, TYPE Type) {
		super(1, id, pos, new CRVector3d(2,10,0), rotation, setRenderColour(Type));
		type = Type;
		resistance = 100;
		minThrustForce = 0;
		maxSpeed = 2;
		isMovingRandomly = true;
		isWithinNest = false;
		isWithinFoodLocation = false;
		canDrop = false;
		feltResistance = 0;
		startedPushingTimeCounter = 0;
		updateVal = 0;
		isWithinTrail = false;
		choiceMade = false;
		goingtoFoodLocation = false;
		goingtoNest = false;
		isRandomlySearching = false;
		isWaitingtoFollow = false;
		isWaitingforLeader = false;
		goingtoRecruit = false;
		leadingPheromonePlaced = false;
		atLeadingPheromone = false;
		firstFollow = true;
		goingtoNestAlone = false;
		goingtoNestwithTrail = false;
		returntoCollectFood = false;
		foodLocationFound = false;
		clearSeenPheromones = false;
		justEntered = false;
		searchingFoodLocation = false;
		inNestwithFood = false;
		placePheromone = false;
		toCreateTrail = false;
		returnFromExhaustedLocation = true;
		returntoNest = false;
		readytoForage = false;
		pher = 0;
		newrotate = 0;
		turnLeft = turnRight = false;
		pheromoneCheck = 0;
		distancetoPosition = 0;
		nestLocation = new CRVector3d(position);
		robotLocation  = new CRVector3d(position);
		foodLocation = new CRVector3d(position);
		seenPheromoneIDs = new ArrayList<>();
		newPheromone = new ArrayList<>();
		lastHigherEqualPheromoneConcentration = -1;
		lastHigherEqualPheromoneConcentrationPosition = new CRVector3d(position);
		lastPheromoneConcentration = -1;
		resistanceMultiplier = 1;
		maxPushAngleFraction = 0.5;
		basePickUpProbability = 0.5;
		pheromoneSizeRE = 25;
		LeadingPheromoneRE = 50;
		foodRemaining = new ArrayList<BaseWorldObject>();
		pushedObjects =  new ArrayList<BaseWorldObject>();
		
	}
	
	//----------Update Loop-----------
	public void onUpdateLoopStart() {
		isVisible = Settings.getSingleton().showRobots;
		super.onUpdateLoopStart();
		
		int rotationChange = 0;
		ArrayList<Pheromones> presentPheromones = new ArrayList<Pheromones>();
		
		//find out if robot is within the nest
		presentPheromones = ((World)world).getPheromones();
		for (int q=0; q<presentPheromones.size(); q++) {
		}
		if(presentPheromones.get(0).getPheromoneConcentrationAt(position) > 0) {
			isWithinNest = true;
			if (!seenPheromoneIDs.contains(presentPheromones.get(0).getId())) {
				seenPheromoneIDs.add(presentPheromones.get(0).getId());
			}
		}
		else {
			isWithinNest = false;
		}
		//all pheromones used for trails
		ArrayList<Pheromones> pheromoneTrails = new ArrayList<Pheromones>();
		for (Pheromones presenttrails : presentPheromones) {
			if (presenttrails.getsizer() <= 25 && presenttrails.getsizer() > 18 ) {
				pheromoneTrails.add(presenttrails);
			}
			if (presenttrails.getsizer() <= 50 && presenttrails.getsizer() > 43 ) {
				pheromoneTrails.add(presenttrails);
			}
		}
		double maxPheromoneCheck = 0;
		//All food Location Pheromones
		ArrayList<Pheromones> FoodLocations = new ArrayList<Pheromones>();
		for (Pheromones phrs : presentPheromones) {
			if (phrs.getsizer() == 190 && phrs != presentPheromones.get(0)) {
				FoodLocations.add(phrs);
			}
			if (phrs.getsizer() <= 50) {
				if(phrs.getPheromoneConcentrationAt(position)>0) {
					if (phrs.getsizer() > maxPheromoneCheck) {
						maxPheromoneCheck = phrs.getsizer();
					}
				}
			}
		}
		double foodPheromoneConc = 0;
		for (Pheromones ph : presentPheromones) {
			if (ph.getsizer() == 190) {
				if (ph.getPheromoneConcentrationAt(position) > 0 && ph.getPheromoneConcentrationAt(position) > foodPheromoneConc) {
					foodPheromoneConc = ph.getPheromoneConcentrationAt(position);
				}
			}
		}
		if (foodPheromoneConc > 0) {
			isWithinFoodLocation = true;
		}
		else {
			isWithinFoodLocation = false;
		}
		
		double maxTrailPheromone = 0;
		for (Pheromones seenTrail : pheromoneTrails) {
			if(seenTrail.getPheromoneConcentrationAt(position) > 0 && seenTrail.getPheromoneConcentrationAt(position) > maxTrailPheromone) {
				maxTrailPheromone = seenTrail.getPheromoneConcentrationAt(position);
			}
		}
		if (maxTrailPheromone > 0) {
			isWithinTrail = true;
		}
		else {
			isWithinTrail = false;
		}
		
		if (pushedObjects.size() > 1) {
			maxThrustForce = 0.5;//1;
		}
		else {
			maxThrustForce = 0.5;
		}
		
		if (pushedObjects.size() == 0) {
			pushedObjects.add(this);
		}
		//check if in contact with food
		pushedObjects.get(pushedObjects.size() - 1).findPushedObjects(pushedObjects);
		
		CRVector3d robotCorners[] = this.getCorners();
		double currentConc = 0;
		double topLeftConc = 0;
		double topRightConc = 0;
		double bottomLeftConc = 0;
		double bottomRightConc = 0;
		CRVector3d currentPos = null;
		CRVector3d topLeftPos = null;
		CRVector3d topRightPos = null;
		CRVector3d bottomLeftPos = null;
		CRVector3d bottomRightPos = null;
		int in=0;
		for (CRVector3d corner : robotCorners) {
			//System.out.println("this is for index: "+in+ ", X axis= "+corner.x+ ", Y axis= "+corner.y);
			if (in == 0) {
				bottomRightPos = corner;
			}
			else if (in == 1) {
				topRightPos = corner;
			}
			else if (in == 2) {
				topLeftPos = corner;
			}
			else {
				bottomLeftPos = corner;
			}
			in++;
		}
		
		for (Pheromones pheroms : presentPheromones) {
			if(pheroms.getPheromoneConcentrationAt(position)>0) {
				if (pheroms.getPheromoneConcentrationAt(position) > currentConc) {
					currentConc = pheroms.getPheromoneConcentrationAt(position);
					currentPos = pheroms.getPosition();
				}
			}
			if(pheroms.getPheromoneConcentrationAt(topLeftPos)>0) {
				if (pheroms.getPheromoneConcentrationAt(topLeftPos) > topLeftConc) {
					topLeftConc = pheroms.getPheromoneConcentrationAt(topLeftPos);
				}
			}
			if(pheroms.getPheromoneConcentrationAt(topRightPos)>0) {
				if (pheroms.getPheromoneConcentrationAt(topRightPos) > topRightConc) {
					topRightConc = pheroms.getPheromoneConcentrationAt(topRightPos);
				}
			}
			if(pheroms.getPheromoneConcentrationAt(bottomLeftPos)>0) {
				if (pheroms.getPheromoneConcentrationAt(bottomLeftPos) > bottomLeftConc) {
					bottomLeftConc = pheroms.getPheromoneConcentrationAt(bottomLeftPos);
				}
			}
			if(pheroms.getPheromoneConcentrationAt(bottomRightPos)>0) {
				if (pheroms.getPheromoneConcentrationAt(bottomRightPos) > bottomRightConc) {
					bottomRightConc = pheroms.getPheromoneConcentrationAt(bottomRightPos);
				}
			}
			
			
		}
		//if leader, go search for food, place leading trail, and forgage.
		if (type == TYPE.LEADER) {
			if (foodLocationFound == true && goingtoRecruit == true && goingtoNest == true ) {
				
				dropPushedObjects();
				rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
				//System.out.println("relative degree to nest from home location: "+rotationChange );
				if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
					rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
				}
				if ((presentPheromones.get(0).getPheromoneConcentrationAt(position) <= 0)) {
					if ((robotLocation.x - this.getPosition().x >= 5) || (robotLocation.y - this.getPosition().y >= 5) || (robotLocation.x - this.getPosition().x <= -5) || (robotLocation.y - this.getPosition().y <= -5)) {
						ArrayList<CRVector3d> pheromonePosition = new ArrayList<>();
						ArrayList<CRVector3d> pheromoneSize = new ArrayList<>();
						pheromonePosition.add(this.getPosition());
						pheromoneSize.add(new CRVector3d(25, 25, 0));
						((World)world).addPheromones(pheromonePosition, pheromoneSize);
						robotLocation.copyFrom(this.getPosition());
						}
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					this.setThrustForce(maxThrustForce);
				}
				//if at edge of the nest place pheromone, turn around and wait
				if ((presentPheromones.get(0).getPheromoneConcentrationAt(position) > 0) && leadingPheromonePlaced == false) {
					ArrayList<CRVector3d> pheromonePosition = new ArrayList<>();
					ArrayList<CRVector3d> pheromoneSize = new ArrayList<>();
					pheromonePosition.add(position);
					pheromoneSize.add(new CRVector3d(50, 50, 0));//100
					((World)world).addPheromones(pheromonePosition, pheromoneSize);
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(robotLocation).x);
					this.setRotation(rotationChange);
					//this.setRotation(0);
					this.setThrustForce(minThrustForce);
					goingtoRecruit = false;
					goingtoFoodLocation = true;
					leadingPheromonePlaced = true;
					goingtoNest = false;
					atLeadingPheromone = true;
					if (clearSeenPheromones == false) {
						seenPheromoneIDs.clear();
						clearSeenPheromones = true;
						//seenPheromoneIDs.add(presentPheromones.get(0).getId());
						
					}
					/*
					for(Pheromones pherom: presentPheromones) {
						if (pherom.getPheromoneConcentrationAt(this.getPosition()) > 0) {
							if (!seenPheromoneIDs.contains(pherom.getId())) {
								
								seenPheromoneIDs.add(pherom.getId());
							}
						}
					}*/
					//notify the other robots
					  
				}
				
				avoidObstacles();
			}
			
			//go to the edge of the food pheromone to create trail to nest.
			if (foodLocationFound == true && toCreateTrail == true && isWithinFoodLocation == true) {
				double maxPheromone = 0;
				rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
				//System.out.println("relative degree to nest from home location: "+rotationChange );
				if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
					rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
				}
				this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
				this.setThrustForce(maxThrustForce);
				for (Pheromones foodmax : presentPheromones) {
					if (foodmax.getsizer() == 190) {
						if (foodmax.getPheromoneConcentrationAt(position) > 0 && foodmax.getPheromoneConcentrationAt(position) > maxPheromone) {
							maxPheromone = foodmax.getPheromoneConcentrationAt(position);
						}
					}
				}
				boolean firstPheromone = false;
				for (Pheromones foodphr : presentPheromones) {
					if(foodphr.getsizer() == 190) {
						if(foodphr.getPheromoneConcentrationAt(position) <= 0.1 && foodphr.getPheromoneConcentrationAt(position) == maxPheromone) {
							robotLocation.copyFrom(this.getPosition());
							goingtoRecruit = true;
							firstPheromone = true;
							toCreateTrail = false;
							
						}
					}
				}
				dropPushedObjects();
				if (firstPheromone == true) {
					ArrayList<CRVector3d> pheroPosition = new ArrayList<>();
					ArrayList<CRVector3d> pheroSize = new ArrayList<>();
					pheroPosition.add(position);
					pheroSize.add(new CRVector3d(50, 50, 0));//100
					((World)world).addPheromones(pheroPosition, pheroSize);
					robotLocation.copyFrom(position);
				}
				
			}
			
			
			//keep looking for food location until found, place pheromone when found
			if (foodLocationFound == false ) {
				if (foodLocationFound == false) {
					isRandomlySearching = true;
				}
				else {
					isRandomlySearching = false;
				}
				
				if (!(pushedObjects.size() > 1) && foodLocationFound == false) {
					
					if (Math.random() < RANDOM_WALK_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(RANDOM_WALK_ANGLE_CHANGE_MIN, RANDOM_WALK_ANGLE_CHANGE_MAX, true);	
						this.setRotation(rotation + rotationChange * ANGLE_CHANGE_MULTIPLIER);
					}
					
					if((presentPheromones.get(0).getPheromoneConcentrationAt(position) > 0) && isRandomlySearching == true) {
						rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(previousPosition).x);
						this.setRotation(rotation + rotationChange * ANGLE_CHANGE_MULTIPLIER);
					}
					this.setThrustForce(maxThrustForce);
				}
				
				if(isWithinNest == true && pushedObjects.size()>1) {
					dropPushedObjects();
					moveAwayFromDroppedObject(null, true);
				}
				for(Pheromones trailz : pheromoneTrails) {
					if(trailz.getPheromoneConcentrationAt(position)>0) {
						if (pushedObjects.size()>1) {
							dropPushedObjects();
							moveAwayFromDroppedObject(null, true);
						}
					}
				}
				//pushedObjects.get(pushedObjects.size() - 1).getIsBeingPushed() == true
				if (pushedObjects.size()>1 && isWithinNest == false) {
					this.setThrustForce(minThrustForce);
					boolean isFoodPheromoneSeen = false;
					for (Pheromones foundpher : presentPheromones) {
						if (foundpher.getsizer() == 190) {
							if (foundpher.getPheromoneConcentrationAt(position) > 0) {
								isFoodPheromoneSeen = true;
							}
						}
					}
					if(presentPheromones.get(0).getPheromoneConcentrationAt(position)>0) {
						isFoodPheromoneSeen = true;
					}
					if (isFoodPheromoneSeen == false) {
						ArrayList<CRVector3d> currentPosition = new ArrayList<>();
						ArrayList<CRVector3d> currentSize = new ArrayList<>();
						currentPosition.add(position);
						currentSize.add(new CRVector3d(190, 190, 0));
						((World)world).addPheromones(currentPosition, currentSize);
						foodLocation.copyFrom(position);
						robotLocation.copyFrom(this.getPosition());
						foodLocationFound = true;
						//goingtoRecruit = true;
						toCreateTrail = true;
						goingtoNest = true;
					}
					else {
						dropPushedObjects();
						moveAwayFromDroppedObject(null, true);
					}
					dropPushedObjects();
					//isRandomlySearching = false;
					//remember to drop food when its picked up.
				}
				
			}
			
			//if carrying food to the nest and food remaining
			//we can specifiy to changing the leaders to check only the location they found
			if (pushedObjects.size() > 1 && isWithinNest == true && inNestwithFood == true) {
				dropPushedObjects();
				moveAwayFromDroppedObject(null, true);
				returntoCollectFood = true;
				returntoNest = false;
				resetDecisionVariables();
				seenPheromoneIDs.clear();
			}
			
			if (returntoCollectFood == true && isWithinFoodLocation == false) {
				//add option to pursue if new pheromone is seen
				//move randomly inside nest location.
				returntoNest = false;
				dropPushedObjects();
				if (presentPheromones.get(0).getPheromoneConcentrationAt(position) < 0.1) {
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(lastHigherEqualPheromoneConcentrationPosition).x);
					//this.setRotation(rotationChange);
					this.setThrustForce(maxThrustForce);
					//this.setPosition(robotLocation);
					this.turnByAngle(90);
				}
				else {//move randomly inside nest
					if (Math.random() < RANDOM_WALK_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(RANDOM_WALK_ANGLE_CHANGE_MIN, RANDOM_WALK_ANGLE_CHANGE_MAX, true);	
						this.setRotation(rotation + rotationChange * ANGLE_CHANGE_MULTIPLIER);
						this.setThrustForce(maxThrustForce);
					}
					else {
						this.setThrustForce(maxThrustForce);
					}
					//this.setRotation(rotation);
				}
				
				if (presentPheromones.size() > 1) {
					for (int i=1; i<presentPheromones.size(); i++) {
						//change this to compare current pheromone sensed and the pheromone you are in.
						if (presentPheromones.get(i).getPheromoneConcentrationAt(position) > 0 && presentPheromones.get(i).getPheromoneConcentrationAt(position) > 0.5 && presentPheromones.get(i).getsizer() > 43) {
							rotationChange = (int) Math.toDegrees(this.getRelativeVectorTo(presentPheromones.get(i).getPosition()).x);
							this.setRotation(rotationChange);
							this.setThrustForce(maxThrustForce);
							goingtoFoodLocation = true;
							returntoCollectFood = false;
							robotLocation.copyFrom(this.getPosition());
							if (!seenPheromoneIDs.contains(presentPheromones.get(i).getId())) {
								seenPheromoneIDs.add(presentPheromones.get(i).getId());
							}
						}
						
					}
				}
			}
			//lead to the food location.
			if (goingtoFoodLocation == true && searchingFoodLocation == false && goingtoNest == false) {
				leadingPheromonePlaced = false;
				if (isWithinFoodLocation == false) {
					if (currentConc > 0.5) {
						if (topRightConc < 0.5 || bottomLeftConc < 0.5) {
							this.turnByAngle(-25);
						}
						else if (topLeftConc < 0.5 || bottomRightConc < 0.5) {
							this.turnByAngle(25);
						}
						else {
							this.setThrustForce(maxThrustForce);
						}
						
					}
					else {
						if (topRightConc > topLeftConc || bottomLeftConc > bottomRightConc) {
							this.turnByAngle(30);
						}
						else if (topLeftConc > topRightConc || bottomRightConc > bottomLeftConc) {
							this.turnByAngle(-30);
						}
						else {
							this.setThrustForce(maxThrustForce);
						}
						
					}
				}
				else {
					for (Pheromones phr : FoodLocations) {
						if (phr.getPheromoneConcentrationAt(position) > 0.3) {
							searchingFoodLocation = true;
						}
					}
				}
				
				// add currently seen pheromones to the list
				for(Pheromones pherom: presentPheromones) {
					if (pherom.getPheromoneConcentrationAt(this.getPosition()) > 0) {
						if (!seenPheromoneIDs.contains(pherom.getId())) {
							
							seenPheromoneIDs.add(pherom.getId());
						}
					}
				}
					
				
			}
			
			if (isWithinFoodLocation == true && searchingFoodLocation == true && goingtoNest == false) {
				double conc = 0;
				for (Pheromones ptrails : pheromoneTrails) {
					if (ptrails.getPheromoneConcentrationAt(position)> 0.4 && ptrails.getPheromoneConcentrationAt(position) > conc) {
						conc = ptrails.getPheromoneConcentrationAt(position);
					}
				}
				isWaitingtoFollow = false;
				goingtoFoodLocation = false;
				//randomly look for food and pick up food to send back home.
				seenPheromoneIDs.clear();
				//choose to follow old path or create new one
				double choice = Math.random();
				if (choiceMade == false) {
					if (choice < 0.95) {
						goingtoNestwithTrail = true;
						goingtoNestAlone = false;
						choiceMade = true;
					}
					else {
						goingtoNestAlone = true;
						goingtoNestwithTrail = false;
						choiceMade = true;
					}
				}	
				
				if (foodPheromoneConc < 0.1) {
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(lastHigherEqualPheromoneConcentrationPosition).x);
					this.setThrustForce(maxThrustForce);
					this.turnByAngle(90);
				}
				else {//move randomly inside nest
					if (Math.random() < RANDOM_WALK_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(RANDOM_WALK_ANGLE_CHANGE_MIN, RANDOM_WALK_ANGLE_CHANGE_MAX, true);	
						this.setRotation(rotation + rotationChange * ANGLE_CHANGE_MULTIPLIER);
						this.setThrustForce(maxThrustForce);
					}
					else {
						this.setThrustForce(maxThrustForce);
					}
				}
				if (pushedObjects.size()>1) {
					if (goingtoNestwithTrail == true){
						if (conc > 0.5) {
							goingtoNest = true;
							robotLocation.copyFrom(position);
						}
						else {
							goingtoNest = false;
						}
						
					}
					if (goingtoNestAlone == true) {
						if (foodPheromoneConc <= 0.2 && pushedObjects.size()>1 && goingtoNest == false) {
							goingtoNest = true;
							robotLocation.copyFrom(this.getPosition());
						}
					}
				}
				
				
			}
			if (goingtoNest == true && !(pushedObjects.size()>1) && searchingFoodLocation == true) {
				goingtoNest = false;
			}
			
			//if going to nest with trail, follow trail and drop object when in nest
			if (goingtoNest == true && goingtoNestwithTrail == true && inNestwithFood == false) {
				searchingFoodLocation = false;
				reinforcePheromones();
				if (isWithinNest == false) {
					if (currentConc > 0.5) {
						if (topRightConc < 0.5 || bottomLeftConc < 0.5) {
							this.turnByAngle(-25);
						}
						else if (topLeftConc < 0.5 || bottomRightConc < 0.5) {
							this.turnByAngle(25);
						}
						else {
							this.setThrustForce(maxThrustForce);
						}
						
					}
					else {
						if (topRightConc > topLeftConc || bottomLeftConc > bottomRightConc) {
							this.turnByAngle(30);
						}
						else if (topLeftConc > topRightConc || bottomRightConc > bottomLeftConc) {
							this.turnByAngle(-30);
						}
						else {
							this.setThrustForce(maxThrustForce);
						}
						
					}
				}
				else {
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
					//System.out.println("relative degree to nest from home location: "+rotationChange );
					if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
					}
					this.setThrustForce(maxThrustForce);
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					
					if (presentPheromones.get(0).getPheromoneConcentrationAt(position)>0.5) {
						inNestwithFood = true;
					}
				}
				
				// add currently seen pheromones to the list
				for(Pheromones pherom: presentPheromones) {
					if (pherom.getPheromoneConcentrationAt(this.getPosition()) > 0) {
						if (!seenPheromoneIDs.contains(pherom.getId())) {
							
							seenPheromoneIDs.add(pherom.getId());
						}
					}
				}
			}
			
			
			if (goingtoNest == true && goingtoNestAlone == true && inNestwithFood == false) {
				double max = 0;
				rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
				//System.out.println("relative degree to nest from home location: "+rotationChange );
				if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
					rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
				}
				for (Pheromones foodmax : presentPheromones) {
					if (foodmax.getsizer() == 190) {
						if (foodmax.getPheromoneConcentrationAt(position) > 0 && foodmax.getPheromoneConcentrationAt(position) > max) {
							max = foodmax.getPheromoneConcentrationAt(position);
						}
					}
				}
				if (isWithinNest == false) {
					if (isWithinFoodLocation == true) {
						if (max <= 0.1 && placePheromone == false) {
							ArrayList<CRVector3d> pheroPosition = new ArrayList<>();
							ArrayList<CRVector3d> pheroSize = new ArrayList<>();
							pheroPosition.add(position);
							pheroSize.add(new CRVector3d(50, 50, 0));//100
							((World)world).addPheromones(pheroPosition, pheroSize);
							placePheromone = true;
							robotLocation.copyFrom(this.getPosition());
						}
					}
					if ((presentPheromones.get(0).getPheromoneConcentrationAt(position) <= 0) && leadingPheromonePlaced == false && placePheromone == true) {
						if ((robotLocation.x - this.getPosition().x >= 5) || (robotLocation.y - this.getPosition().y >= 5) || (robotLocation.x - this.getPosition().x <= -5) || (robotLocation.y - this.getPosition().y <= -5)) {
							ArrayList<CRVector3d> pheromonePosition = new ArrayList<>();
							ArrayList<CRVector3d> pheromoneSize = new ArrayList<>();
							pheromonePosition.add(this.getPosition());
							pheromoneSize.add(new CRVector3d(25, 25, 0));
							((World)world).addPheromones(pheromonePosition, pheromoneSize);
							robotLocation.copyFrom(this.getPosition());
							}
					}
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					this.setThrustForce(maxThrustForce);
				}
				else {
					if ((presentPheromones.get(0).getPheromoneConcentrationAt(position) > 0) && leadingPheromonePlaced == false) {
						ArrayList<CRVector3d> pheromonePosition = new ArrayList<>();
						ArrayList<CRVector3d> pheromoneSize = new ArrayList<>();
						pheromonePosition.add(position);
						pheromoneSize.add(new CRVector3d(50, 50, 0));//100
						((World)world).addPheromones(pheromonePosition, pheromoneSize);
						rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(robotLocation).x);
						leadingPheromonePlaced = true;
						placePheromone = false;
						atLeadingPheromone = true;
						if (clearSeenPheromones == false) {
							seenPheromoneIDs.clear();
							clearSeenPheromones = true;
							//seenPheromoneIDs.add(presentPheromones.get(0).getId());
							
						}
					}
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
					//System.out.println("relative degree to nest from home location: "+rotationChange );
					if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
					}
					this.setThrustForce(maxThrustForce);
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					
					if (presentPheromones.get(0).getPheromoneConcentrationAt(position)>0.5) {
						inNestwithFood = true;
					}
					
				}
				
				avoidObstacles();	
			}
			
		}
		
		if (type == TYPE.FOLLOWER) {
			
		
			//if pheromone to food location has been found
			if(foodLocationFound == true && goingtoFoodLocation == false && isWithinNest == true && readytoForage == true) {
				isWaitingtoFollow = true;
				goingtoFoodLocation = true;
			}
			
			
			//move randomly in the nest
			
			if (foodLocationFound == false && isWithinNest == true) {
				//move randomly inside nest location.
				//if about to leave the nest go back to previous location.
				dropPushedObjects();
				if (presentPheromones.get(0).getPheromoneConcentrationAt(position) < 0.1) {
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(lastHigherEqualPheromoneConcentrationPosition).x);
					//this.setRotation(rotationChange);
					this.setThrustForce(maxThrustForce);
					//this.setPosition(robotLocation);
					this.turnByAngle(90);
				}
				else {//move randomly inside nest
					if (Math.random() < RANDOM_WALK_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(RANDOM_WALK_ANGLE_CHANGE_MIN, RANDOM_WALK_ANGLE_CHANGE_MAX, true);	
						this.setRotation(rotation + rotationChange * ANGLE_CHANGE_MULTIPLIER);
						this.setThrustForce(maxThrustForce);
					}
					else {
						this.setThrustForce(maxThrustForce);
					}
					//this.setRotation(rotation);
				}
				
				if (presentPheromones.size() > 1) {
					for (int i=1; i<presentPheromones.size(); i++) {
						//change this to compare current pheromone sensed and the pheromone you are in.
						if (presentPheromones.get(i).getPheromoneConcentrationAt(position) > 0 && presentPheromones.get(i).getPheromoneConcentrationAt(position) > 0.5 && presentPheromones.get(i).getsizer() > 43) {
							rotationChange = (int) Math.toDegrees(this.getRelativeVectorTo(presentPheromones.get(i).getPosition()).x);
							this.setRotation(rotationChange);
							this.setThrustForce(maxThrustForce);
							foodLocationFound = true;
							readytoForage = true;
							robotLocation.copyFrom(this.getPosition());
							if (!seenPheromoneIDs.contains(presentPheromones.get(i).getId())) {
								seenPheromoneIDs.add(presentPheromones.get(i).getId());
							}
						}
						
					}
				}
			}
			
			
			//follow leader to foodlocation
			if (isWaitingtoFollow == true && searchingFoodLocation == false) {
				readytoForage = false;
					if (isWithinFoodLocation == false) {
						if (currentConc > 0.5) {
							if (topRightConc < 0.5 || bottomLeftConc < 0.5) {
								this.turnByAngle(-25);
							}
							else if (topLeftConc < 0.5 || bottomRightConc < 0.5) {
								this.turnByAngle(25);
							}
							else {
								this.setThrustForce(maxThrustForce);
							}
							
						}
						else {
							if (topRightConc > topLeftConc || bottomLeftConc > bottomRightConc) {
								this.turnByAngle(30);
							}
							else if (topLeftConc > topRightConc || bottomRightConc > bottomLeftConc) {
								this.turnByAngle(-30);
							}
							else {
								this.setThrustForce(maxThrustForce);
							}
							
						}
					}
					else {
						for (Pheromones phr : FoodLocations) {
							if (phr.getPheromoneConcentrationAt(position) > 0.3) {
								searchingFoodLocation = true;
								isWaitingtoFollow = false;
							}
						}
					}
					// add currently seen pheromones to the list
					for(Pheromones pherom: presentPheromones) {
						if (pherom.getPheromoneConcentrationAt(this.getPosition()) > 0) {
							if (!seenPheromoneIDs.contains(pherom.getId())) {
								
								seenPheromoneIDs.add(pherom.getId());
							}
						}
					}
				
			}
			
			//if in food location, search for food, pick food and make choice to follow trail or go home alone
			if (isWithinFoodLocation == true && searchingFoodLocation == true && goingtoNest == false) {
				double concentrate = 0;
				for (Pheromones trail : pheromoneTrails) {
					if (trail.getPheromoneConcentrationAt(position)> 0.4 && trail.getPheromoneConcentrationAt(position) > concentrate) {
						concentrate = trail.getPheromoneConcentrationAt(position);
					}
				}
				isWaitingtoFollow = false;
				//randomly look for food and pick up food to send back home.
				seenPheromoneIDs.clear();
				//choose to follow old path or create new one
				double choicef = Math.random();
				if (choiceMade == false) {
					if (choicef < 0.99) {
						goingtoNestwithTrail = true;
						goingtoNestAlone = false;
						choiceMade = true;
					}
					else {
						goingtoNestAlone = true;
						goingtoNestwithTrail = false;
						choiceMade = true;
					}
				}	
				if (foodPheromoneConc < 0.1) {
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(lastHigherEqualPheromoneConcentrationPosition).x);
					this.setThrustForce(maxThrustForce);
					this.turnByAngle(90);
				}
				else {//move randomly inside nest
					if (Math.random() < RANDOM_WALK_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(RANDOM_WALK_ANGLE_CHANGE_MIN, RANDOM_WALK_ANGLE_CHANGE_MAX, true);	
						this.setRotation(rotation + rotationChange * ANGLE_CHANGE_MULTIPLIER);
						this.setThrustForce(maxThrustForce);
					}
					else {
						this.setThrustForce(maxThrustForce);
					}
				}
				if (pushedObjects.size()>1) {
					if (goingtoNestwithTrail == true){
						if (concentrate > 0.5) {
							goingtoNest = true;
							robotLocation.copyFrom(position);
						}
						else {
							goingtoNest = false;
						}
						
					}
					if (goingtoNestAlone == true) {
						if (foodPheromoneConc <= 0.2 && pushedObjects.size()>1 && goingtoNest == false) {
							goingtoNest = true;
							robotLocation.copyFrom(this.getPosition());
						}
					}
				}
				
				
			}
			//set goingtoNest to false if robot do not sense the trail and is not carrying food
			if (goingtoNest == true && !(pushedObjects.size()>1) && searchingFoodLocation == true) {
				goingtoNest = false;
			}
			
			//going to nest with trail
			if (goingtoNest == true && goingtoNestwithTrail == true && inNestwithFood == false) {
				searchingFoodLocation = false;
				reinforcePheromones();
				if (isWithinNest == false) {
					if (currentConc > 0.5) {
						if (topRightConc < 0.5 || bottomLeftConc < 0.5) {
							this.turnByAngle(-25);
						}
						else if (topLeftConc < 0.5 || bottomRightConc < 0.5) {
							this.turnByAngle(25);
						}
						else {
							this.setThrustForce(maxThrustForce);
						}
						
					}
					else {
						if (topRightConc > topLeftConc || bottomLeftConc > bottomRightConc) {
							this.turnByAngle(30);
						}
						else if (topLeftConc > topRightConc || bottomRightConc > bottomLeftConc) {
							this.turnByAngle(-30);
						}
						else {
							this.setThrustForce(maxThrustForce);
						}
						
					}
				}
				else {
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
					//System.out.println("relative degree to nest from home location: "+rotationChange );
					if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
					}
					this.setThrustForce(maxThrustForce);
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					
					if (presentPheromones.get(0).getPheromoneConcentrationAt(position)>0.5) {
						inNestwithFood = true;
					}
				}
				
				// add currently seen pheromones to the list
				for(Pheromones pherom: presentPheromones) {
					if (pherom.getPheromoneConcentrationAt(this.getPosition()) > 0) {
						if (!seenPheromoneIDs.contains(pherom.getId())) {
							
							seenPheromoneIDs.add(pherom.getId());
						}
					}
				}
			}
			
			//if robot is going to nest alone, place pheromnone when at the edge from the food pheromone to the nest.
			if (goingtoNest == true && goingtoNestAlone == true && inNestwithFood == false) {
				double max = 0;
				rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
				//System.out.println("relative degree to nest from home location: "+rotationChange );
				if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
					rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
				}
				for (Pheromones foodmax : presentPheromones) {
					if (foodmax.getsizer() == 190) {
						if (foodmax.getPheromoneConcentrationAt(position) > 0 && foodmax.getPheromoneConcentrationAt(position) > max) {
							max = foodmax.getPheromoneConcentrationAt(position);
						}
					}
				}
				if (isWithinNest == false) {
					if (isWithinFoodLocation == true) {
						if (max <= 0.1 && placePheromone == false) {
							ArrayList<CRVector3d> pheroPosition = new ArrayList<>();
							ArrayList<CRVector3d> pheroSize = new ArrayList<>();
							pheroPosition.add(position);
							pheroSize.add(new CRVector3d(50, 50, 0));//100
							((World)world).addPheromones(pheroPosition, pheroSize);
							placePheromone = true;
							robotLocation.copyFrom(this.getPosition());
						}
					}
					if ((presentPheromones.get(0).getPheromoneConcentrationAt(position) <= 0) && leadingPheromonePlaced == false && placePheromone == true) {
						if ((robotLocation.x - this.getPosition().x >= 5) || (robotLocation.y - this.getPosition().y >= 5) || (robotLocation.x - this.getPosition().x <= -5) || (robotLocation.y - this.getPosition().y <= -5)) {
							ArrayList<CRVector3d> pheromonePosition = new ArrayList<>();
							ArrayList<CRVector3d> pheromoneSize = new ArrayList<>();
							pheromonePosition.add(this.getPosition());
							pheromoneSize.add(new CRVector3d(25, 25, 0));
							((World)world).addPheromones(pheromonePosition, pheromoneSize);
							robotLocation.copyFrom(this.getPosition());
							}
					}
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					this.setThrustForce(maxThrustForce);
				}
				else {
					if ((presentPheromones.get(0).getPheromoneConcentrationAt(position) > 0) && leadingPheromonePlaced == false) {
						ArrayList<CRVector3d> pheromonePosition = new ArrayList<>();
						ArrayList<CRVector3d> pheromoneSize = new ArrayList<>();
						pheromonePosition.add(position);
						pheromoneSize.add(new CRVector3d(50, 50, 0));//100
						((World)world).addPheromones(pheromonePosition, pheromoneSize);
						rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(robotLocation).x);
						leadingPheromonePlaced = true;
						placePheromone = false;
						atLeadingPheromone = true;
						if (clearSeenPheromones == false) {
							seenPheromoneIDs.clear();
							clearSeenPheromones = true;
							//seenPheromoneIDs.add(presentPheromones.get(0).getId());
							
						}
					}
					rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
					//System.out.println("relative degree to nest from home location: "+rotationChange );
					if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
						rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
					}
					this.setThrustForce(maxThrustForce);
					this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
					
					if (presentPheromones.get(0).getPheromoneConcentrationAt(position)>0.5) {
						inNestwithFood = true;
					}
					
				}
				avoidObstacles();
			}
			
			//if carrying food to the nest and food remaining
			//we can specifiy to changing the leaders to check only the location they found
			if (pushedObjects.size() > 1 && isWithinNest == true && inNestwithFood == true) {
				dropPushedObjects();
				moveAwayFromDroppedObject(null, true);
				returntoCollectFood = true;
				returntoNest = false;
				resetDecisionVariables();
				seenPheromoneIDs.clear();
			}
			
			if (returntoCollectFood == true && isWithinFoodLocation == false) {
				foodLocationFound = false;
				returntoNest = false;
				returntoCollectFood = false;
			}
			//end of special follower code.
		}
		
		
		for (BaseWorldObject pushedObject : pushedObjects) {
			if (pushedObject.getClass() != Robot.class) {
				pushedObject.setRotation(this.rotation);
				pushedObject.setThrustForce(this.thrustForce);
			}
		}
	
		for (Pheromones phe : presentPheromones) {
			if (phe.getPheromoneConcentrationAt(position)>0) {
				if ((phe.getPheromoneConcentrationAt(lastHigherEqualPheromoneConcentrationPosition) > 0)) {
					
					if (phe.getPheromoneConcentrationAt(position) - lastHigherEqualPheromoneConcentration > 0) {
						lastHigherEqualPheromoneConcentration = phe.getPheromoneConcentrationAt(position);
						lastHigherEqualPheromoneConcentrationPosition.copyFrom(this.getPosition());
					}
				}
				else {
					lastHigherEqualPheromoneConcentrationPosition.copyFrom(this.getPosition());
					lastHigherEqualPheromoneConcentration = phe.getPheromoneConcentrationAt(position);
				}
			}
		}
		
		if (isWithinFoodLocation == true || isWithinNest == false ) {
			//check if food is present in location.
			//if not head straight to home
			if(searchingFoodLocation == true && goingtoNest == false) {
				foodRemaining.clear();
				ArrayList<CRVector3d>foodPositions = new ArrayList<CRVector3d>();
				for(Pheromones foodpheromonee : FoodLocations) {
					foodPositions.add(foodpheromonee.getpos());
				}
				if (maxPheromoneCheck > 0) {
					if (maxPheromoneCheck < 44) {
						if(pushedObjects.size()>1) {
							returnFromExhaustedLocation = true;
							returntoNest = true;
						}
						else {
							returntoNest = true;
							resetDecisionVariables();
						}
					}
				}
				if (checkforFood(this) == false) {
					((World)world).timeofFoodExhaustion(((World)world).getController().getTimeCounterSinceTrialStart());
				}
				
			}
			if (type == TYPE.LEADER) {
				if (isWithinFoodLocation == false && isWithinNest == false && isWithinTrail == false) {
					if (foodLocationFound == true) {
						if (goingtoNestAlone == true || goingtoNestwithTrail == true || goingtoFoodLocation == true) {
							returntoNest = true;
							resetDecisionVariables();
						}
						
					}
				}
			}
			if (type == TYPE.FOLLOWER ) {
				if (isWithinFoodLocation == false && isWithinNest == false && isWithinTrail == false) {
					returntoNest = true;
					resetDecisionVariables();
				}
			}
		}
		
		if (returntoNest == true) {
			if (returnFromExhaustedLocation = true) {
				rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
				//System.out.println("relative degree to nest from home location: "+rotationChange );
				if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
					rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
				}
				this.setThrustForce(maxThrustForce);
				this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
				
				if (presentPheromones.get(0).getPheromoneConcentrationAt(position)>0.5) {
					inNestwithFood = true;
					returnFromExhaustedLocation = false;
				}
			}
			else {
				rotationChange = -(int) Math.toDegrees(this.getRelativeVectorTo(nestLocation).x);
				//System.out.println("relative degree to nest from home location: "+rotationChange );
				if (Math.random() < WALK_RANDOMISATION_ANGLE_CHANGE_PROBABILITY) {
					rotationChange += CRMaths.getRandomInteger(WALK_RANDOMISATION_MIN_ANGLE_CHANGE, WALK_RANDOMISATION__MAX_ANGLE_CHANGE, true);
				}
				this.setThrustForce(maxThrustForce);
				this.setRotation(rotation + ANGLE_CHANGE_MULTIPLIER*rotationChange);
				
				if (presentPheromones.get(0).getPheromoneConcentrationAt(position)>0.5) {
					returntoCollectFood = true;
					seenPheromoneIDs.clear();
				}
			}
			avoidObstacles();
		}
		
		if (type == TYPE.LEADER) {
			if (foodLocationFound == false) {
				avoidObstacles();
			}
		}
		

	}
	
		
		
	public void Update() {
		if (type == TYPE.FOLLOWER || type == TYPE.LEADER) {
			CROutputPopup.getSingleton().displayOutput(this.id + "is carrying:: " + pushedObjects.size() + "resistance:  " + feltResistance + " random: " + isMovingRandomly);
			for (int i=0; i<pushedObjects.size(); i++) {
				BaseWorldObject object = pushedObjects.get(i);
				if (object != this) {
					CROutputPopup.getSingleton().displayOutput("    " + object.getId() + "   rot " + object.getRotation() + "  pos" + object.getPosition().x + " " + object.getPosition().y);
				}
			}
		}
			
		
		
		//-- move, etc.
		super.update();
		
		//-- sort self and all pushed objects to a possibly new bin
		CRBinWorld binWorld = (CRBinWorld)world;
		for (BaseWorldObject pushedObject : pushedObjects) {
			binWorld.sortObjectToABin(pushedObject);
		}
	}
	
	/**
	 * End of update loop
	 */
	public void onUpdateLoopEnd() {
		super.onUpdateLoopEnd();
		
		
	}
	//obstacle avoidance code.
	public void avoidObstacles() {
		ArrayList<CRBaseSituatedModel>obstacles = ((World)world).getObstacles();
		boolean collided = false;
		CRBaseSituatedModel obstacleCollided = null;
		CRVector3d robotCorners[] = this.getCorners();
		CRVector3d currentPos = null;
		CRVector3d topLeftPos = null;
		CRVector3d topRightPos = null;
		CRVector3d bottomLeftPos = null;
		CRVector3d bottomRightPos = null;
		int in=0;
		for (CRVector3d corner : robotCorners) {
			if (in == 0) {
				bottomRightPos = corner;
			}
			else if (in == 1) {
				topRightPos = corner;
			}
			else if (in == 2) {
				topLeftPos = corner;
			}
			else {
				bottomLeftPos = corner;
			}
			in++;
		}
		double currentHeading = 0;
		for (CRBaseSituatedModel obstacle : obstacles) {
			/*if (this.isCollidingWith(obstacle, 0)) {
				collided = true;
				obstacleCollided = obstacle;
			}*/
			
			if (obstacle.isAt(topLeftPos, 0) || obstacle.isAt(bottomLeftPos, 0)) {
				collided = true;
				obstacleCollided = obstacle;
			}
			else if (obstacle.isAt(topRightPos, 0) || obstacle.isAt(bottomRightPos, 0)){
				collided = true;
				obstacleCollided = obstacle;
			}
		}
		if (collided == true) {
			if (turnLeft == false && turnRight == false) {
				//obstacleCollidedWith = obstacleCollided;
				newrotate = 0;
				double choice = Math.random();
				if (obstacleCollided.isAt(topLeftPos, 1) || obstacleCollided.isAt(bottomLeftPos, 1)) {
					turnRight = true;
				}
				else if (obstacleCollided.isAt(topRightPos, 1) || obstacleCollided.isAt(bottomRightPos, 1)){
					turnLeft = true;
				}
			}
			
			if ((turnLeft == true || turnRight == true) && pher > 5) {
				turnLeft = false;
				turnRight = false;
				newrotate = 0;
				pher = 0;
			}
			
		}
		
		
		if (turnLeft == true && newrotate == 0) {
			newrotate = -90 + this.getRotation();
			//this.turnByAngle(-30);
		}
		if (turnRight == true && newrotate == 0) {
			newrotate = 90 + this.getRotation();
			//this.turnByAngle(30);
		}
		
		if (pher<6 && (turnLeft==true || turnRight == true)) {
			this.setRotation(newrotate);
		}
		pher++;
	}
	//pheromone reinforcement code.
	public void reinforcePheromones() {
		ArrayList<Pheromones>presentPheromones = ((World)world).getPheromones();
		ArrayList<Pheromones>pheromoneTrails = new ArrayList<>();
		for(Pheromones p : presentPheromones) {
			if (p.getsizer() <= 50) {
				pheromoneTrails.add(p);
			}
		}
		//reinforce pheromones if it has decayed since this is the first forage.
		Pheromones reinforcePheromone = null;
		Pheromones reinforcePheromoneLead = null;
		boolean pheromoneReinforcement = false;
		boolean leadingPheromoneReinforcement = false;
		//add new pheromone trail when following trail.
		double maxPheromSize = 0;
		double maxPheromSizeLead = 0;
		for (Pheromones prm : pheromoneTrails) {
			if (prm.getsizer() <= 25) {
				if (prm.getPheromoneConcentrationAt(position)>0 && prm.getsizer() > maxPheromSize) {
					pheromoneReinforcement = false;
					maxPheromSize = prm.getsizer();
					reinforcePheromone = prm;
					if (maxPheromSize <= 25 && maxPheromSize > 18) {
						if (maxPheromSize < pheromoneSizeRE) {
							pheromoneReinforcement = true;
						}
					}
				}
			}
			if (prm.getsizer() > 25) {
				if (prm.getPheromoneConcentrationAt(position)>0 && prm.getsizer() > maxPheromSizeLead) {
					leadingPheromoneReinforcement = false;
					maxPheromSizeLead = prm.getsizer();
					reinforcePheromoneLead = prm;
					if (maxPheromSizeLead <= 50 && maxPheromSizeLead > 43) {
						if (maxPheromSizeLead < LeadingPheromoneRE) {
							leadingPheromoneReinforcement = true;
						}
					}
				}
			}
			
		}
		if (pheromoneReinforcement == true) {
			reinforcePheromone.setsizer(new CRVector3d(25, 25, 0));
		}
		if (leadingPheromoneReinforcement == true) {
			reinforcePheromoneLead.setsizer(new CRVector3d(50, 50, 0));
		}
	}
	//set agent color based on its kind
	public static String setRenderColour(TYPE type) {
		if (type == TYPE.FOLLOWER) {
			return CRRenderer.CR_RED_DOT;
		}
		else {
			return CRRenderer.CR_BLUE_DOT;
		}
	}
	
	public void CheckRemainingFood() {
		
	}
	//reset variables to continue foraging process
	protected void resetDecisionVariables(){
		if (type == TYPE.LEADER) {
			goingtoNestAlone = false;
			goingtoNestwithTrail = false;
			goingtoFoodLocation = false;
			
		}
		else {
			goingtoNestAlone = false;
			goingtoNestwithTrail = false;
			isWaitingtoFollow = false;
			goingtoFoodLocation = false;
			goingtoNest = false;
			choiceMade = false;
			inNestwithFood = false;
		}
		readytoForage = false;
		isWithinNest = false;
		isWithinFoodLocation = false;
		canDrop = false;
		feltResistance = 0;
		startedPushingTimeCounter = 0;
		updateVal = 0;
		choiceMade = false;
		goingtoFoodLocation = false;
		goingtoNest = false;
		isRandomlySearching = false;
		isWaitingtoFollow = false;
		isWaitingforLeader = false;
		goingtoRecruit = false;
		leadingPheromonePlaced = false;
		atLeadingPheromone = false;
		goingtoNestAlone = false;
		goingtoNestwithTrail = false;
		clearSeenPheromones = false;
		justEntered = false;
		searchingFoodLocation = false;
		inNestwithFood = false;
		placePheromone = false;
		toCreateTrail = false;
	}

	public boolean decideToSlide(BaseWorldObject object) {
		// TODO Auto-generated method stub
        double slideProbability = feltResistance; //0.5 ;// 0; //(pushedObjects.size()-2)*0.33;
		
		if (object.getClass() != Robot.class) {
			if (feltResistance == 0) {
				slideProbability = -1; //don't slide if this is the first stone it encountered
			}
			//slideProbability = -1;
			if (Math.random() <= slideProbability) {				
				//-- rotate by about 90 deg
				double angleChange = CRMaths.getRandomInteger(15,35, true);
				this.setRotation(rotation + angleChange);
				
				feltResistance += object.getResistance();
				//System.out.println(kind.toString() + " SLIDING with prob " + slideProbability +" by " + angleChange + " deg");
				return true;	
			}
		}
		return false;
	}

	public boolean decideToPickup(BaseWorldObject object) {
		// TODO Auto-generated method stub
		if (object.getClass() != Robot.class) {
			if (feltResistance < 1) {
				double pickUpProbability = basePickUpProbability;
				if (type == TYPE.LEADER && isRandomlySearching == true) {
					pickUpProbability = 1;
				}
				/*if ((type == TYPE.LEADER || type == TYPE.FOLLOWER ) && isWithinNest && feltResistance == 0) {
					pickUpProbability = -1;
				}*/
			//	pickUpProbability = 1;
				if (Math.random() <= pickUpProbability) {
					return true;
				}
					
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Triggered when an object was added to the chain.
	 * @param object_ BaseWorldObject object
	 */
	protected void onPickedUp(BaseWorldObject object_) {
		super.onPickedUp(object_);
		if (type == TYPE.FOLLOWER || type == TYPE.LEADER) {
			//-- should decide to record carry time when 1st stone added to the queue, 
			//   based on whether it's outside or inside of nest. Only do this the first time it enters nest from outside
			if (pushedObjects.size() == 2 && !shouldRecordCarryTime) {
				if (isWithinNest) {
					shouldRecordCarryTime = false;
				} else {
					shouldRecordCarryTime = true;
				}
			}
			
			
		}
		feltResistance += object_.getResistance();
	}
	
	/**
	 * Triggered when an object should have pushed (i.e. is colliding) but basePickUpProbability decided not to push it.
	 * Rotate away from the object in a random fahsion.
	 * @param object_ BaseWorldObject object
	 */
	protected void onNotPickedUp(BaseWorldObject worldObject_) {
		if (worldObject_.getClass() == Robot.class) {
			moveAwayFromDroppedObject(worldObject_, false);
			//-- the following will result in dropping the stone a little away from an ants it bumbped into
		}
	}
	
	protected boolean decidetoDrop() {
		//canDrop = false;
		
		if (feltResistance == 0) {
			//-- no point of drop testing, not carrying anything
			canDrop = false;
		} else if (pushedObjects.size() > 1 && isWithinNest == true ) {
			//-- all ants can drop if resistance too high
			canDrop = true;
		}
				
		if (canDrop ) {
			double dropProbability = 1;
				if (Math.random() <= dropProbability) {
					dropPushedObjects();
					return true;
				}
			//}
		}
		return false; 
	}
	
	
	/**
	 * Set thrust force of all pushed stones (not ants) to 0 and clear the pushedObjects array.
	 */
	protected void dropPushedObjects() {
		for (BaseWorldObject pushedObject : pushedObjects) {
			if (pushedObject.getClass() != Robot.class) {
				pushedObject.setThrustForce(0);
				pushedObject.setRotation(0);
				pushedObject.setIsBeingPushed(false);
			}
		}
		pushedObjects.clear();
		//moveAwayFromDroppedObject(null, true);
		//isMovingRandomly = true;
		feltResistance = 0;
		
		//-- external ant should report on how long this object was pushed, if it was taken outside of nest and put down inside of nest
		if (shouldRecordCarryTime && isWithinNest) {
			//-- difference between started and ended pushing, times time unit interval, as the report will average over the whole time unit
			int timePassed = (world.getController().getTimeCounterSinceTrialStart() - startedPushingTimeCounter) ;
			if (timePassed > 0) {
				//System.out.println(world.getController().getTimeUnits() + id + "  " + kind + "  TIME PASSED " + timePassed + "   " + world.getController().getTimeCounterSinceTrialStart());
				//((CRTimeSeriesReport)world.getController().getReportController().getReport(ReportController.REPORT_PUSHING_TIME)).addValue(timePassed);
			}
		}
		shouldRecordCarryTime = false;
		//System.out.println("DROPPING OBJECTS AT " + position.toString());
	}
	
	
	
	/**
	 * Adjust own position in a borderless world but also position of all pushed stones.
	 * This is to prevent stones from getting away when their transition happens.
	 */
	public void adjustPositionInBorderlessWorld() {
		CRVector3d oldPosition = new CRVector3d(position);
		super.adjustPositionInBorderlessWorld();
		double yDifference = position.y - oldPosition.y;
		double xDifference = position.x - oldPosition.x;
		if (Math.abs(yDifference) > maxSpeed) {
			for (BaseWorldObject pushedObject : pushedObjects) {
				if (pushedObject.getClass() != Robot.class) {
					pushedObject.setPositionY(pushedObject.getPosition().y + yDifference);
					((CRBinWorld)world).sortObjectToABin(pushedObject);
				}
			}
		}
		if (Math.abs(xDifference) > maxSpeed) {
			for (BaseWorldObject pushedObject : pushedObjects) {
				if (pushedObject.getClass() != Robot.class) {
					pushedObject.setPositionX(pushedObject.getPosition().x + xDifference);
					((CRBinWorld)world).sortObjectToABin(pushedObject);
				}
			}
		}
	}
	
	/**
	 * Override set rotation to translate pushed objects
	 */
	public void setRotation(double rotation_) {
		double oldRotation = rotation;
		super.setRotation(rotation_);
		double change = rotation - oldRotation;
		for (BaseWorldObject pushedObject : pushedObjects) {
			if (pushedObject.getClass() != Robot.class) {
				pushedObject.setRotation(rotation_);
				double distanceXDiff = position.x - pushedObject.getPosition().x;
				double distanceYDiff = position.y - pushedObject.getPosition().y;
				double deltaX = Math.cos(Math.toRadians(change))*distanceXDiff - Math.sin(Math.toRadians(change))*distanceYDiff;
				double deltaY = Math.sin(Math.toRadians(change))*distanceXDiff + Math.cos(Math.toRadians(change))*distanceYDiff;
				pushedObject.setPosition(new CRVector3d(position.x - deltaX, position.y - deltaY, 0));
				((CRBinWorld)world).sortObjectToABin(pushedObject);
				//System.out.println(this.id + "  rotating " + pushedObject.getId());
			}
		}
	}
	
	protected void moveAwayFromDroppedObject(BaseWorldObject object_, boolean randomly_) {
		int angleChange = 180;
		if (randomly_) {
			angleChange = CRMaths.getRandomInteger(140,180, true);
		}
		this.setRotation(rotation + angleChange);
	}
	
	protected void ifCollidewithObstacles(boolean randomly) {
		int angleChange = 90;
		if (randomly) {
			angleChange = CRMaths.getRandomInteger(45,90, true);
		}
		this.setRotation(rotation + angleChange);
	}

}
