package HybridSimulation;


import net.lenkaspace.creeper.view.CRRenderer;
import net.lenkaspace.creeper.vo.CRVector3d;


public class Foods extends BaseWorldObject{
	
	public Foods(int id_, CRVector3d pos_) {
		super(0.15, id_, pos_, new CRVector3d(2,2,0), 0, CRRenderer.CR_GRAY_DOT);
		this.maxSpeed = 2;
	}
	
	public void onUpdateLoopStart() {
		isVisible = Settings.getSingleton().showFoods;
		this.thrustForce = 0;
		super.onUpdateLoopStart();
	}
	
    public void update() {
		
		super.update();
		//System.out.println("AFTER UPDATE STONE " + id + " PREV" + previousPosition.toString() + "  " + position.toString());
	}
	
	public CRVector3d getPos() {
		return this.getPosition();
	}
	public void adjustPositionInBorderlessWorld() {
		// ant will do this for the stone if it is being pushed to prevent irregularities
	}
	

}
