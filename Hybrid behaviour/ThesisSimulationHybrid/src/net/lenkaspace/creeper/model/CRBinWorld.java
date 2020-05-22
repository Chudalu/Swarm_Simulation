package net.lenkaspace.creeper.model;

import java.util.ArrayList;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.vo.CRVector3d;

/**
 * Subclass of CRWorld that holds CRSituatedModels in a grid of bins.
 * Contains methods that enable accessing only a subgroup of objects belonging to a 
 * single bin instead of all existing CRSituatedModels. This is useful e.g.
 * for optimizing collision detection execution time when the world
 * contains a lot of objects.
 * 
 * As an example of where this can be used, see
 * http://lenkaspace.net/lab/swarmSystems/controllingAntConstruction
 * 
 * @author      Lenka Pitonakova contact@lenkaspace.net
 * @version     2.0                                      
 */
public class CRBinWorld extends CRWorld {

	private ArrayList<CRBaseSituatedModel>[] situatedModelBins;
	private int numberOfBins;
	private CRVector3d binSize;
	
	/**
	 * Constructor
	 * @param binSize_ CRVector3d [width, height, depth] dimensions of a single bin
	 * @param id_ int nt unique id
	 * @param size_ CRVector3d size [width, height, depth]
	 * @param controller_ CRController holding controller instance
	 */
	public CRBinWorld(CRVector3d binSize_, int id_, CRVector3d size_, CRController controller_) {
		super(id_, size_, controller_);
		binSize = new CRVector3d(binSize_);
		numberOfBins = ((int)Math.ceil(size.x / binSize.x) * (int)Math.ceil(size.y / binSize.y));
		situatedModelBins = new ArrayList[numberOfBins];
		//System.out.println("Created " + numberOfBins + " bins");
		for (int i=0; i<numberOfBins; i++) {
			situatedModelBins[i] = new ArrayList<CRBaseSituatedModel>();
		}
	}
	
	
	/**
	 * Place an object to a bin based on its position.
	 * This method should be called for all subclasses of CRBaseSituatedModel
	 * immediately after they have changed position.
	 * @param dynamicModel_ CRBaseSituatedModel situated model
	 */
	public void sortObjectToABin(CRBaseSituatedModel dynamicModel_) {
		int previousBinIndex = -1;
		if (dynamicModel_.getPreviousPosition().x != CRVector3d.INVALID_COMPONENT) {
			previousBinIndex = getBinIndexForPosition(dynamicModel_.getPreviousPosition());
		}
		int currentBinIndex = getBinIndexForPosition(dynamicModel_.getPosition());
		//-- change to a different bin if should
		if(previousBinIndex != currentBinIndex) {
			//-- take dynamic model out of its old bin
			if (previousBinIndex >= 0) {
				if (situatedModelBins[previousBinIndex].contains(dynamicModel_)) {
					situatedModelBins[previousBinIndex].remove(dynamicModel_);
				}
			}
			
			//-- put it to a new bin
			if (!situatedModelBins[currentBinIndex].contains(dynamicModel_)) {
				situatedModelBins[currentBinIndex].add(dynamicModel_);			
			}
			dynamicModel_.binIndex = currentBinIndex;
		}
	}
	
	//==================================== OBJECT MANIPULATION ==================================
	/**
	 * Add a CRBaseSituatedModel to the list of situatedModels. Sets world pointer of the situatedModel to this.
	 * @param situatedModel_ CRBaseSituatedModel to add
	 */
	public void addSituatedModel(CRBaseSituatedModel situatedModel_) {
		super.addSituatedModel(situatedModel_);
		
		//-- check if position is valid
		CRVector3d position = situatedModel_.getPosition();
		
		if (position.x > size.x ) { position.x = 0;	} 
		else if (position.x < 0 ) { position.x = size.x; }
		if (position.y > size.y ) { position.y = 0;	} 
		else if (position.y < 0 ) { position.y = size.y; }
		
		sortObjectToABin(situatedModel_);
	}
	
	
	/**
	 * Clear all arrays that store any child objects.
	 * Subclasses should override this to implement clearing of any additional arrays
	 */
	public void clearChildren() {
		if (situatedModelBins != null) {
			for (int i=0; i<numberOfBins; i++) {
				situatedModelBins[i].clear();
			}
		}
		super.clearChildren();
	}
	
	//==================================== GETTERS / SETTERS ==================================
	
	/**
	 * Get index of a bin based on a position
	 * @param position_ CRVector3d position
	 * @return int index of a bin
	 */
	public int getBinIndexForPosition(CRVector3d position_) {
		CRVector3d binCoordinates = getBinCoordinatesForPosition(position_);
		return getBinIndexForBinCoordinates(binCoordinates);
	}
	
	/**
	 * Get index of a bin based on its bin coordinates
	 * @param binCoordinates_ CRVector3d bin coordinates in form [column, row ,0]
	 * @return int index of a bin
	 */
	public int getBinIndexForBinCoordinates(CRVector3d binCoordinates_) {
		//-- index as all columns from previous rows + current column
		return (int)(binCoordinates_.y*(int)Math.ceil(size.x / binSize.x) + binCoordinates_.x);
	}
	
	/**
	 * Convert a position into bin coordinates 
	 * @param position_ CRVector3d position
	 * @return CRVector3d in format [column, row, 0]
	 */
	private CRVector3d getBinCoordinatesForPosition(CRVector3d position_) {
		int numRows = (int)Math.ceil(size.y / binSize.y);
		int numCols = (int)Math.ceil(size.x / binSize.x);
		CRVector3d normalisedPosition = new CRVector3d(position_);
		if (normalisedPosition.x >= size.x) { normalisedPosition.x = size.x - 1; }
		else if (normalisedPosition.x <= 0) { normalisedPosition.x = 1; }
		if (normalisedPosition.y >= size.y) { normalisedPosition.y = size.y - 1; }
		else if (normalisedPosition.y <= 0) { normalisedPosition.y = 1; }
		int row = Math.min(numRows, Math.max(0, (int)(Math.floor(normalisedPosition.y / binSize.y))));
		int col = Math.min(numCols, Math.max(0, (int)(Math.floor(normalisedPosition.x / binSize.x))));
		return new CRVector3d(col, row, 0);
	}
	
	/**
	 * Get all CRBaseSituatedModels that are in a bin around an object. It is possible that all objects from neighbouring bins
	 * will be included if object is near intersection of bins.
	 * @param object_ CRBaseSituatedModel object
	 * @return ArrayList<CRBaseSituatedModel> list of objects from applicable bins
	 */
	public ArrayList<CRBaseSituatedModel> getRelevantObjectsObject(CRBaseSituatedModel object_) {
		return getRelevantObjectsAroundPosition(object_.getPosition(), object_.getSize().x);
	}
	
	/**
	 * Get all CRBaseSituatedModels that are in a bin around a position. It is possible that all objects from neighbouring bins
	 * will be included if circle around a position reaches to those bins.
	 * @param position_ CRVector3d position
	 * @param radius_ int radius around a position for testing if other bins should be included
	 * @return ArrayList<CRBaseSituatedModel> list of objects from applicable bins
	 */
	public ArrayList<CRBaseSituatedModel> getRelevantObjectsAroundPosition(CRVector3d position_, double radius_) {
		int binIndex = getBinIndexForPosition(position_);
		ArrayList<CRBaseSituatedModel> returnArrayList = new ArrayList<CRBaseSituatedModel>();
		returnArrayList.addAll(situatedModelBins[binIndex]);
		
		//-- test if other bin indexes are applicable
		CRVector3d binCoordinates = getBinCoordinatesForPosition(position_);
		
		boolean addedNorth = false;
		boolean addedSouth = false;
		boolean addedWest = false;
		boolean addedEast = false;
		
		int northBinY = (int)(binCoordinates.y - 1);
		int southBinY = (int)(binCoordinates.y + 1);
		int westBinX = (int)(binCoordinates.x - 1);
		int eastBinX = (int)(binCoordinates.x + 1);
		
		int numRows = (int)Math.ceil(size.y / binSize.y);
		int numCols = (int)Math.ceil(size.x / binSize.x);
		
		if (position_.y - radius_ <= binCoordinates.y*binSize.y) { // test if reaching to the bin north of here
 			addedNorth = true;
			//-- wrap the coordinate around
			if (northBinY < 0 ) {
				northBinY = numRows-1;
			}
			//-- add the bin to the list
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(binCoordinates.x, northBinY, 0))]);
		}
		if (position_.y + radius_ >= southBinY*binSize.y || position_.y + radius_ >= size.y) { //test bin south of here if north not added
			addedSouth = true;
			//-- wrap the coordinate around
			if (southBinY >= numRows) {
				southBinY = 0;
			}
			//-- add the bin to the list
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(binCoordinates.x, southBinY, 0))]);
		}
		
		
		if (position_.x - radius_ <= binCoordinates.x*binSize.x) { // test bin west of here
			addedWest = true;
			//-- wrap the coordinate around
			if (westBinX < 0 ) {
				westBinX = numCols-1;
			}
			//-- add the bin to the list
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(westBinX, binCoordinates.y, 0))]);
		} 
		if (position_.x + radius_ >= eastBinX*binSize.x || position_.x + radius_ >= size.x) { // test bin east of here
			addedEast = true;
			//-- wrap the coordinate around
			if (eastBinX >= numCols) {
				eastBinX = 0;
			}
			//-- add the bin to the list
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(eastBinX, binCoordinates.y, 0))]);
		}
		
		
		//-- if added north and west, north west should also be added, etc.
		if (addedNorth && addedWest) {
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(westBinX, northBinY, 0))]);
		}
		if (addedNorth && addedEast) {
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(eastBinX, northBinY, 0))]);
		} 
		if (addedSouth && addedWest) {
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(westBinX, southBinY, 0))]);
		}
		if (addedSouth && addedEast) {
			returnArrayList.addAll(situatedModelBins[getBinIndexForBinCoordinates(new CRVector3d(eastBinX, southBinY, 0))]);
		} 
		
		return returnArrayList;
	}
	
	

}
