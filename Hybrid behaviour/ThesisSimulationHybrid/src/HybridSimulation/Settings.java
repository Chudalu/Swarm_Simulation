package HybridSimulation;

public class Settings {
	
	public boolean showRobots;
	public boolean showFoods;
	public boolean showPheromone;
	
	public int NumberofLeaderRobots;
	public int NumberofFollowerRobots;
	public double pheromoneMovementThreshold;
	public int PheromoneDiam;
	public String currentWorld;
	
	private static Settings singletonReference;
	
	public static Settings getSingleton()	  {
		  if (singletonReference == null)
			  singletonReference = new Settings();		
		  return singletonReference;
		}
		
		private Settings() {
			showRobots = true;
			showFoods = true;
			showPheromone = true;
			currentWorld = World.OPENFREEWORLD;
			pheromoneMovementThreshold = 1;
			PheromoneDiam = 200;
			//broodClusterDistance=100;
		}
	

}
