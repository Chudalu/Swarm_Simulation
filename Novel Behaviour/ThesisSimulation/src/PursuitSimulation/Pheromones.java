package PursuitSimulation;

import java.util.ArrayList;

import net.lenkaspace.creeper.helpers.CRMaths;
import net.lenkaspace.creeper.model.CRBaseDynamicModel;
import net.lenkaspace.creeper.model.CRBaseSituatedModel;
import net.lenkaspace.creeper.vo.CRVector3d;

public class Pheromones extends CRBaseDynamicModel {
	
	private ArrayList<CRBaseSituatedModel> pheromoneClouds;
	static int idVal = 50000;
	static int Time = 0;
	boolean decayFoodLocation;
	int timeCreated;
	public Pheromones(ArrayList<CRVector3d> positions, ArrayList<CRVector3d> sizes, World world) {
		super(createID(), new CRVector3d(600,600,0), new CRVector3d(200,200,0), 0, CRBaseSituatedModel.SHAPE.CIRCLE, "cloud.png");
		this.isVisible = false;
		
		pheromoneClouds = new ArrayList<CRBaseSituatedModel>();
		int counter = 0;
		for (CRVector3d pos : positions) {
			
			if (counter < 1) {
				CRVector3d siz = new CRVector3d(200,200,0);
				if (sizes.size() > counter) {
					siz.copyFrom(sizes.get(counter));
				}
				
				CRBaseSituatedModel cloud = new CRBaseSituatedModel(createID(), pos, siz, 0, CRBaseSituatedModel.SHAPE.CIRCLE, "cloud.png");
				pheromoneClouds.add(cloud);
				world.addSituatedModel(cloud);
			}
			if (counter > 0) {
				//change the dimension to vary with time.
				CRVector3d siz = new CRVector3d(30,30,0);
				if (sizes.size() > counter) {
					siz.copyFrom(sizes.get(counter));
				}
				//change cloud image to vary based on time changed pheromone
				CRBaseSituatedModel cloud = new CRBaseSituatedModel(createID(), pos, siz, 0, CRBaseSituatedModel.SHAPE.CIRCLE, "cloud.png");
				pheromoneClouds.add(cloud);
				world.addSituatedModel(cloud);
			}
			counter++;
		}
		timeCreated = world.getController().getTimeCounterSinceTrialStart();
		decayFoodLocation = false;
	}
	
	public void onUpdateLoopStart() {
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			cloud.setIsVisible(true); 
		}
		super.onUpdateLoopStart();
	}
	
	public CRVector3d getpos() {
		CRVector3d pos = null;
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			pos = cloud.getPosition();
		}
		return pos;
	}
	
	public int getPhID() {
		int id = 0;
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			id = cloud.getId();
		}
		return id;
	}
	
	public CRVector3d getPhSize() {
		CRVector3d size = null;
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			size = cloud.getSize();
		}
		return size;
	}
	
	//get pheromone concentration at current position.
	public double getPheromoneConcentrationAt(CRVector3d position_) {
		double returnVal = 0;
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			double distance = Math.hypot(cloud.getPosition().x- position_.x, cloud.getPosition().y- position_.y);
			double radius = cloud.getSize().x / 2;
			if (distance <= radius ) {
				//-- 1- (percentage of possible distance within circle), i.e. the closer to the middle the higher the concentration 
				returnVal += 1-(distance/radius);
			}
		}
		
		return returnVal;
	}
	public void removePher() {
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			
			((World)world).removeSituatedModel(cloud);
		}
	}
	
	public void setsizer(CRVector3d size) {
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			cloud.setSize(size);
		}
		((World)world).updatePheromoneUse();
	}
	
	public double getsizer() {
		
		double sizeval = 0;
		for (CRBaseSituatedModel cloud : pheromoneClouds) {
			sizeval = cloud.getSize().x;
		}
		return sizeval;
	}
	
	public static int createID() {
		int val = idVal+1;
		idVal = val;
		return val;
	}
	
	public int getTimeCreated() {
		return timeCreated;
	}
	
	public void setTimeCreated(int val) {
		timeCreated = val;
		timeCreated = world.getController().getTimeCounterSinceTrialStart();
	}
	/**
	 * End of update loop
	 */
	public void onUpdateLoopEnd() {
		super.onUpdateLoopEnd();
		ArrayList<Pheromones>presentPheromones = ((World)world).getPheromones();
		int timePassed = world.getController().getTimeCounterSinceTrialStart();
		if (this.getsizer() <= 50 && this.getsizer() >= 0) {
			Time = this.getTimeCreated();
			if (timePassed - Time >= 3000) {
				this.setsizer(new CRVector3d((int)this.getsizer() - 3, (int)this.getsizer() - 3, 0));
				this.setTimeCreated(Time);
			}
			if(this.getsizer() <=1) {
				((World)world).removePheromone(this);
				((World)world).removeDynamicModel(this);
				this.removePher();
			}
		}
		
		if (this.getsizer() > 50 && this.getsizer() < 200 && decayFoodLocation == false) {
			for (Pheromones p : presentPheromones) {
				if (p.getsizer() > 25 && p.getsizer() <= 50 && this.getPheromoneConcentrationAt(p.getpos())>0) {
					if (p.getsizer() < 44) {
						decayFoodLocation = true;
						timeCreated = world.getController().getTimeCounterSinceTrialStart();
					}
				}
			}
		}
		
		if (decayFoodLocation == true) {
			Time = this.getTimeCreated();
			if (timePassed - Time >= 3000) {
				this.setsizer(new CRVector3d((int)this.getsizer() - 20, (int)this.getsizer() - 20, 0));
				this.setTimeCreated(Time);
			}
			if(this.getsizer() <=1) {
				((World)world).removePheromone(this);
				((World)world).removeDynamicModel(this);
				this.removePher();
			}
		}
		
	}

}
