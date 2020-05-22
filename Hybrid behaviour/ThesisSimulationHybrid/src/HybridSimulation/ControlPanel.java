package HybridSimulation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.view.CRComponentFactory;
import net.lenkaspace.creeper.view.CRControlPanel;
@SuppressWarnings("serial")
public class ControlPanel extends CRControlPanel {
	
	private JTextField numOfLeaderRobotsField;
	private JTextField numOfFollowerRobotsField;
	private JComboBox worldType;
	private JTextField pheromoneMovementThreshold;
	private JTextField PheromoneDiam;
	//private JTextField broodClusterDistance;
	
	public ControlPanel(Dimension size, CRController controller_) {
		super(size, controller_);
		
		Settings settings = Settings.getSingleton();
		
		//-- change default values of textfields
		trialDurationField.setText("6000");
		
		//-- add start simulation settings,
		preStartSimulationPanel.setPreferredSize(new Dimension(preStartSimulationPanel.getPreferredSize().width, 180));
				
		Dimension newSize = startSimulationPanel.getPreferredSize();
		newSize.height = 60;
		startSimulationPanel.setPreferredSize(newSize);
		
		CRComponentFactory.createJLabel("World: ", preStartSimulationPanel);
		worldType = CRComponentFactory.createJComboBox(250, 
				new String[] {World.OPENFREEWORLD, World.OBSTACLEFILLEDWORLD, World.MUTIPLEFOODANDOBSTACLE, World.WORLDWITHMULTIPLEFOOD}, 
				0, null,preStartSimulationPanel);
		CRComponentFactory.createJLabel("                                    ", preStartSimulationPanel);
		
		CRComponentFactory.createJLabel("Leader Robots: ", preStartSimulationPanel);
		numOfLeaderRobotsField = CRComponentFactory.createJTextField("10", 50, preStartSimulationPanel);
		CRComponentFactory.createJLabel("Follower Robots: ", preStartSimulationPanel);
		numOfFollowerRobotsField = CRComponentFactory.createJTextField("10", 50, preStartSimulationPanel);
		
		CRComponentFactory.createJLabel("Internal ants pheromone movement threshold (%):", preStartSimulationPanel);
		pheromoneMovementThreshold = CRComponentFactory.createJTextField(String.valueOf((int)settings.pheromoneMovementThreshold*100), 50, preStartSimulationPanel);
		
		CRComponentFactory.createFlowLayoutJPanel(-1, 15, preStartSimulationPanel);
		
		CRComponentFactory.createJLabel("Standard pheromone cloud diameter:", preStartSimulationPanel);
		PheromoneDiam = CRComponentFactory.createJTextField(String.valueOf((int)settings.PheromoneDiam), 50, preStartSimulationPanel);
		CRComponentFactory.createJLabel("    Cloud distance:", preStartSimulationPanel);
		
		//-- add rendering settings
				JPanel renderingPanel = CRComponentFactory.createFlowLayoutJPanel(-1, 30, this);
				
				CRComponentFactory.createJCheckBox("Render ants", settings.showFoods, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Settings.getSingleton().showFoods = ((JCheckBox)e.getSource()).isSelected();				
					}
				}, renderingPanel);
				
				CRComponentFactory.createJCheckBox("Render stones", settings.showRobots, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Settings.getSingleton().showRobots = ((JCheckBox)e.getSource()).isSelected();				
					}
				}, renderingPanel);
				
				CRComponentFactory.createJCheckBox("Render pheromone", settings.showPheromone, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Settings.getSingleton().showPheromone = ((JCheckBox)e.getSource()).isSelected();				
					}
				}, renderingPanel);
				
		
				
				
	}
	
	//==================================== BUTTON ACTIONS =================================
		protected void onStartSimulationClicked(ActionEvent e) {
			Settings settings = Settings.getSingleton();
			settings.NumberofLeaderRobots = Integer.parseInt(numOfLeaderRobotsField.getText());
			settings.NumberofFollowerRobots = Integer.parseInt(numOfFollowerRobotsField.getText());
			settings.currentWorld = worldType.getSelectedItem().toString();
			settings.pheromoneMovementThreshold = Integer.valueOf(pheromoneMovementThreshold.getText())/100.0;
			settings.PheromoneDiam = Integer.parseInt(PheromoneDiam.getText());
			//settings.broodClusterDistance = Integer.parseInt(broodClusterDistance.getText());
			super.onStartSimulationClicked(e);
		}

}
