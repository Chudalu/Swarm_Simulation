package net.lenkaspace.creeper.model;

import java.awt.Point;
import java.awt.Polygon;

import net.lenkaspace.creeper.helpers.CRMaths;
import net.lenkaspace.creeper.vo.CRVector3d;

/**
 * Represents a model that is situated in a CRWorld and can optionally be rendered but doesn't update
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRBaseSituatedModel extends CRBaseModel {

	
	public enum SHAPE {
		CIRCLE,
		RECTANGLE
	}
	
	protected double rotation;
	protected double alpha;
	protected CRVector3d position;
	protected CRVector3d previousPosition;
	protected CRVector3d size;
	protected String imageFileName;
	protected CRWorld world;
	protected boolean isVisible;
	protected SHAPE shape;
	protected String toolTip;
	
	public int binIndex;
	
	
	
	/**
	 * Constructor
	 * @param id_ Id of the model
	 * @param position_ CRVector3d position [x,y,z]
	 * @param size_ CRVector3d size [width, height, depth]
	 * @param rotation_ Double rotation in degrees where 0 is pointing towards top
	 * @param shape_ SHAPE shape
	 * @param imageFileName_ String image name that is used for rendering. If empty or null, the instance doesn't render. Use your own image or one of CRRenderer constants
	 */
	public CRBaseSituatedModel(int id_, CRVector3d position_, CRVector3d size_, double rotation_, SHAPE shape_, String imageFileName_) {
		super(id_);		
		imageFileName = imageFileName_;
		rotation = rotation_;
		position = new CRVector3d(position_);
		previousPosition = CRVector3d.invalidVector();
		size = new CRVector3d(size_);
		isVisible = true;
		shape = shape_;
		alpha = 1.0;
		
		binIndex = -1;
		toolTip = "";
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
	
	//==================================== COLLISION DETECTION===================================
	
	/**
	 * Find out if colliding with another object
	 * @param anotherObject_ CRBaseSituatedModel another object
	 * @return boolean true if collision detected
	 */
	public boolean isCollidingWith(CRBaseSituatedModel anotherObject_) {
		return isCollidingWith(anotherObject_, 0);
	}
	
	/**
	 * Find out if colliding with another object
	 * @param anotherObject_ CRBaseSituatedModel another object
	 * @param extraSpace_ int extra space around this object that will also count as collision. Not implemented for RECRANGLEs
	 * @return boolean true if collision detected
	 */
	public boolean isCollidingWith(CRBaseSituatedModel anotherObject_, int extraSpace_) {
		if (!anotherObject_.getIsRemoved()) {
			if (this.shape == SHAPE.RECTANGLE || anotherObject_.getShape() == SHAPE.RECTANGLE){
				//-- rectangle-polygon collision detection
				CRVector3d[] selfCorners = this.getCorners();
				CRVector3d[] anotherObjectCorners = anotherObject_.getCorners();
						
				int[] anotherObjectsXCorrds = new int[4];
				int[] anotherObjectsYCorrds = new int[4];
				int[] selfXCoords = new int[4];
				int[] selfYCoords = new int[4];
				
				for (int i=0; i<4; i++) {
					anotherObjectsXCorrds[i] = (int)anotherObjectCorners[i].x;
					anotherObjectsYCorrds[i] = (int)anotherObjectCorners[i].y;
					selfXCoords[i] = (int)selfCorners[i].x;
					selfYCoords[i] = (int)selfCorners[i].y;
				}
				
				//-- another object contains this
				Polygon polygon = new Polygon(anotherObjectsXCorrds, anotherObjectsYCorrds,4);
				for (int i=0; i<4; i++) {
					if (polygon.contains(new Point((int)selfCorners[i].x, (int) selfCorners[i].y))) {
						return true;			
					}
				}
				//-- or this contains another object
				polygon = new Polygon(selfXCoords, selfYCoords,4);
				for (int i=0; i<4; i++) {
					if (polygon.contains(new Point((int)anotherObjectCorners[i].x, (int) anotherObjectCorners[i].y))) {
						return true;			
					}
				}
				
	
			} else {
				//---- circle-circle collision detection
				double distanceOfMiddles = Math.hypot(position.x - anotherObject_.getPosition().x, position.y - anotherObject_.getPosition().y);
		    	if (distanceOfMiddles <= (size.x /2 + anotherObject_.getSize().x/2) + extraSpace_) {
		        	return true;	
		    	}
			}
		}
    	return false;
	}
	
	/**
	 * Find out whether specific coordinates have been reached by any part of the body
	 * @param coords_ CRVector3d coordinates to hit test with
	 * @param extraSpace_ int extra space around the object that should be included in the hit test !TODO: not implemented for Rectangles
	 * @return boolean true if at coordinates
	 */
	public boolean isAt(CRVector3d coords_, int extraSpace_) {
		if (shape == SHAPE.RECTANGLE) {
			CRVector3d[] selfCorners = this.getCorners();
			int[] selfXCoords = new int[4];
			int[] selfYCoords = new int[4];
			for (int i=0; i<4; i++) {
				selfXCoords[i] = (int)selfCorners[i].x;
				selfYCoords[i] = (int)selfCorners[i].y;
			}
			Polygon polygon = new Polygon(selfXCoords, selfYCoords,4);
			return (polygon.contains(new Point((int)coords_.x, (int) coords_.y)));
		} else {
			return (CRMaths.getDistanceOfPoints(position.x, position.y, coords_.x, coords_.y) < size.x + extraSpace_);
		}
	}
	
	//=========================== VECTORS & ADJUSTMENTS ===========================
	/**
	 * Get coordinates of corners, based on shape and current rotation
	 * @return CRVector3d[4] corners if shape is RECTANGLE, otherwise return NULL
	 */
	public CRVector3d[] getCorners() {
		if (shape == SHAPE.RECTANGLE) {
			CRVector3d[] corners = new CRVector3d[4];
			
			//-- top right
			corners[0] =  new CRVector3d(position.x + size.x/2, position.y - size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[0]);
			
			//-- bottom right
			corners[1] =  new CRVector3d(position.x + size.x/2, position.y + size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[1]);
			
			//-- bottom left
			corners[2] =  new CRVector3d(position.x - size.x/2, position.y + size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[2]);
			
			//-- top left
			corners[3] =  new CRVector3d(position.x - size.x/2, position.y - size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[3]);
		
			return corners;
		} else if (shape == SHAPE.CIRCLE) {
			//-- approximate as octagon
			CRVector3d[] corners = new CRVector3d[4];
			
			//-- top right
			corners[0] =  new CRVector3d(position.x + size.x/2, position.y - size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[0]);
			
			//-- bottom right
			corners[1] =  new CRVector3d(position.x + size.x/2, position.y + size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[1]);
			
			//-- bottom left
			corners[2] =  new CRVector3d(position.x - size.x/2, position.y + size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[2]);
			
			//-- top left
			corners[3] =  new CRVector3d(position.x - size.x/2, position.y - size.y/2, 0);
			rotateVectorInLocalCoordinates(corners[3]);
			
			return corners;
			
		} else {
			return null;
		}
	}
	
	/**
	 * Rotate a vector around own middle
	 * @param vector_ CRVector3d vector to rotate
	 */
	public void rotateVectorInLocalCoordinates(CRVector3d vector_) {
		CRVector3d temp = new CRVector3d();
		double xDiff = position.x - vector_.x;
		double yDiff = position.y - vector_.y;
		temp.x = position.x - xDiff*Math.cos(Math.toRadians(rotation)) - yDiff*Math.sin(Math.toRadians(rotation));
		temp.y = position.y - xDiff*Math.sin(Math.toRadians(rotation)) + yDiff*Math.cos(Math.toRadians(rotation));
		vector_.copyFrom(temp);
	}
	
	/**
	 * Get vector to an object translated into local coordinates, based on middle of own body as pivot
	 * @param vector_ CRVector3d vector to point towards
	 * @return CRVector3d vector where X component represents angle towards the object and y component distance
	 */
	public CRVector3d getRelativeVectorTo(CRVector3d vector_) {
		return getRelativeVectorTo(vector_, position);
	}
	
	/**
	 * Get vector to an object translated into local coordinates
	 * @param vector_ CRVector3d vector to point towards
	 * @param pivot_ CRVector3d vector (within own body) to measure from.
	 * @return CRVector3d vector where X component represents angle towards the object and y component distance
	 */
	public CRVector3d getRelativeVectorTo(CRVector3d vector_, CRVector3d pivot_) {
		CRVector3d tempVector = CRVector3d.subtractVectors(pivot_, vector_);
		CRVector3d u = this.getVectorInLocalCoordinateSystem(tempVector);
		u.normalize();
		
		//distance from radial border of self to radial border of another object
		double distance = Math.hypot(position.x- vector_.x, position.y- vector_.y);
		
		//add direction:
		if (u.y < 0) {
			distance = -distance;
		}
		u.y = distance;		
		return u;
	}
	
	/**
	 * Translate a vector into local coordinates
	 * @param vector_ CRVector3d vector to convert
	 * @return CRVector3d vector translated to local coordinates
	 */
	protected CRVector3d getVectorInLocalCoordinateSystem(CRVector3d vector_) {
		return getVectorInLocalCoordinateSystem(vector_, false);
	}
	
	/**
	 * Translate a vector into local coordinates
	 * @param vector_ CRVector3d vector to convert
	 * @param invertRotation_ boolean true if -rotation should be used instead of rotation
	 * @return CRVector3d vector translated to local coordinates
	 */
	protected CRVector3d getVectorInLocalCoordinateSystem(CRVector3d vector_, boolean invertRotation_) {
		double rot = Math.toRadians(rotation);
		if (invertRotation_) { rot = -rot; }
		CRVector3d tempVec = new CRVector3d();
		tempVec.x = vector_.x*Math.cos(rot) + vector_.y*Math.sin(rot);
		tempVec.y = - vector_.x*Math.sin(rot) + vector_.y*Math.cos(rot);
		return tempVec;
	}
	
	
	/**
	 * Sends agent to the left side when went away through right, etc.
	 */
	public void adjustPositionInBorderlessWorld() {
		if (position.x - size.x/4 > world.getSize().x ) { position.x = - size.x / 4;	} 
		else if (position.x + size.x/4 < 0 ) { position.x = world.getSize().x + size.x / 4; }
		
		if (position.y - size.y/4 > world.getSize().y ) { position.y = - size.y / 4; }
		else if (position.y + size.y /4   < 0 ) { position.y = world.getSize().y  + size.y / 4; }
	}
	
	//==================================== GETTERS / SETTERS ====================================
	public double getRotation() { return rotation; }
	public void setRotation(double rotation_) { 
		rotation = rotation_; 
		if (rotation > 180){
			rotation = -360 + rotation; // reached 180 deg, set to -180 deg
		} else if (rotation < -180){
			rotation = 360 + rotation; // reached -180 deg, set to 180 deg
		}
	}


	public CRVector3d getPosition() { return position; }
	public void setPosition(CRVector3d position_) {
		CRVector3d curPos = new CRVector3d(position);
		position.copyFrom(position_);
		previousPosition.copyFrom(curPos);
	}
	public void setPositionX(double value_) {
		previousPosition.copyFrom(position);
		position.x = value_;
	}
	public void setPositionY(double value_) {
		previousPosition.copyFrom(position);
		position.y = value_;
	}
	
	public CRVector3d getPreviousPosition() { return previousPosition; }

	public CRVector3d getSize() { return size; }
	public void setSize(CRVector3d size_) { size = size_; }

	public CRWorld getWorld() { return world; }
	public void setWorld(CRWorld world_) { world = world_; }

	public String getImageFileName() { return imageFileName; }
	public void setImageFileName(String imageFileName_) { imageFileName = imageFileName_; }
	
	public boolean isVisible() { return isVisible; }
	public void setIsVisible(boolean value_) { isVisible = value_; }
	
	public SHAPE getShape() { return shape; }
	
	public void setIsRemoved(boolean is_) { 
		super.setIsRemoved(is_);
		if (isRemoved) {
			isVisible = false;
		}
	}
	
	public void setToolTop(String toolTip_) { toolTip = toolTip_; }
	public String getToolTip() { return toolTip; }
	
	public void setAlpha(double alpha_) { alpha = alpha_; }
	public double getAlpha() { return alpha; }
	
}
