package net.lenkaspace.creeper.model;

import net.lenkaspace.creeper.vo.CRVector3d;

/**
 * Represents a model that is situated in a CRWorld, can be rendered and moves / updates
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRBaseDynamicModel extends CRBaseSituatedModel {
	

	protected double thrustForce; //<-1..1> where 1 is maximum speed 
	protected double minThrustForce;
	protected double maxThrustForce;
	
	protected double maxSpeed; //multiplied by thurstForce to determine final speed
	
	
	
	/**
	 * Constructor
	 * @param id_ Id of the model
	 * @param position_ CRVector3d position [x,y,z]
	 * @param size_ CRVector3d size [width, height, depth]
	 * @param rotation_ Double rotation in degrees where 0 is pointing towards top
	 * @param shape_ SHAPE shape
	 * @param imageFileName_ String image name that is used for rendering. If empty or null, the instance doesn't render. Use your own image or one of CRRenderer constants
	 */
	public CRBaseDynamicModel(int id_, CRVector3d position_, CRVector3d size_, double rotation_, SHAPE shape_, String imageFileName_) {
		super(id_, position_, size_, rotation_, shape_, imageFileName_);
		maxSpeed = 1;
		thrustForce = 0;
		minThrustForce = -1;
		maxThrustForce = 1;
		
	}
	
	
	//==================================== SIMULATION EVENTS ====================================
	
	/**
	 * Set variables according to their initial values
	 * @param trialNumber_ int new trial number
	 * @param runNumber_ int new run number
	 */
	public void onTrialStart(int trialNumber_, int runNumber_) {
		super.onTrialStart(trialNumber_, runNumber_);
	}
	
	/**
	 * Called from CRWorld before update() of all objects is called.
	 * Use this to reset any temporary variables. 
	 */
	public void onUpdateLoopStart() {
		
	}
	
	/**
	 * Called from CRWorld after update() of all objects is called.
	 * Use this to save any necessary data
	 */
	public void onUpdateLoopEnd() {
		
	}
	
	//==================================== UPDATE LOOP ==========================================
	
	/**
	 * Main update function.
	 */
	public void update() {
		applyThrusters();
	}
	
	/**
	 * Calculation of new x y position based on thrust forces
	 */
	public void applyThrusters()  {	
			
    	//-- calculate the position change:
		double xVelocity = Math.sin(Math.toRadians(rotation)) * thrustForce * maxSpeed;
    	double yVelocity = -Math.cos(Math.toRadians(rotation)) * thrustForce * maxSpeed;
    	
    	previousPosition.copyFrom(position);
    	position.x += xVelocity;
    	position.y += yVelocity; 
    	
    	//-- adjust position if applicable:
    	if (world.isBorderless()) {
    		adjustPositionInBorderlessWorld();	
    	}
	}
	
	
	//==================================== ADJUSTMENTS ==========================================
	
	
	/**
	 * Turn by a certain angle, i.e. add to the current rotation
	 * @param angle_ value to add to the current rotation
	 */
	public void turnByAngle(double angle_) {
		setRotation(rotation + angle_);
	}
		
	
	//==================================== VECTORS ===============================================
	
	/**
	 * Get current velocity based on rotation and thrustforce.
	 * @return CRVector3d velocity that is always between <0;1>, i.e. does not take maxSpeed into account
	 */
	public CRVector3d getVelocity() {
		CRVector3d v = new CRVector3d(getRotationAsVectorComponent(), thrustForce, 0);
		return v;
	}
	
	/**
	 * Get normalised steering force vector
	 */
	public CRVector3d getOrientationVector() {
		CRVector3d v = new CRVector3d(getRotationAsVectorComponent(), thrustForce,0);
		v.normalize();
		return v;
	}
	
	/**
	 * Get vector that points in front of the situated model
	 */
	public CRVector3d getLookAtVector() {
		CRVector3d v = this.getOrientationVector();
		v.multiplyBy(5);
		return v;
	}
	
	
	

	//==================================== GETTERS / SETTERS==============================
	
	double getMaxSpeed() { return maxSpeed; }
	void setMaxSpeed(double value) { maxSpeed = value; }
	
	double getRotationAsVectorComponent() { return rotation/180.0; }
	public double getThrustForce() { return thrustForce; }
	
	/**
	 * Sets thrust force and makes sure it is within specified bounds.
	 * @param value thrust force
	 */
	public void setThrustForce(double value) {
		thrustForce = value;
		if (thrustForce < minThrustForce) { thrustForce = minThrustForce; } 
		else if (thrustForce > maxThrustForce) { thrustForce = maxThrustForce; }
	}
	
	

}
