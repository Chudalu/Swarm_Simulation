package PursuitSimulation;

import java.awt.Dimension;

import net.lenkaspace.creeper.CRController;
import net.lenkaspace.creeper.model.CRSettings;
import net.lenkaspace.creeper.model.CRBaseSituatedModel;
import net.lenkaspace.creeper.model.CRBaseSituatedModel.SHAPE;
import net.lenkaspace.creeper.model.CRParameters;
import net.lenkaspace.creeper.report.CRTimeSeriesReport;
import net.lenkaspace.creeper.report.CRWorldSnapshotReport;
import net.lenkaspace.creeper.view.CRRenderer;
import net.lenkaspace.creeper.vo.CRVector3d;


public class PursuitModel {
	
	private CRController controller;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new PursuitModel();

	}
	
	public PursuitModel() {
        CRSettings settings = CRSettings.getSingleton();
		
		
		//-- setup CRSettings:
        settings.setWindowSize(new Dimension(1400,900));
		settings.setInitialTimeSpeed(75);
		settings.setRenderingDelay(1);
		settings.setTimeUnitInterval(50);
		
		controller = new CRController("Novel Foraging Behaviour Simulation");
		
		ImageProvider imageprovider = new ImageProvider();
		controller.setImageProvider(imageprovider);
		
		World world = new World(controller);
		controller.setWorld(world);
		controller.getRenderer().setPreferredSize(new Dimension(800,800));
		
		//-- setup reports
		ReportControl reportController = new ReportControl(controller);
		controller.setReportController(reportController);
		int[] snapshotTimes = {5,10,20,30, 40, 50, 60, 80, 90, 100, 150, 200, 250, 300,350,400,450,500,650,700,750,800,850,900,950,1000,1200,1400,1600,1800,2000,2200,2400,2600,2800,3000};				
		CRWorldSnapshotReport worldSnapshotReport = new CRWorldSnapshotReport(ReportControl.REPORT_WORLD_SNAPSHOT, new Dimension((int)world.getSize().x, (int)world.getSize().y),snapshotTimes);
		reportController.addReport(ReportControl.REPORT_WORLD_SNAPSHOT, worldSnapshotReport);
						
		CRTimeSeriesReport pushingTimeReport = new CRTimeSeriesReport(ReportControl.REPORT_PUSHING_TIME, new String[] {"Time"}, new Dimension(800,400));
		pushingTimeReport.setIsScatterPlot(true);
		reportController.addReport(ReportControl.REPORT_PUSHING_TIME, pushingTimeReport);
						
		//-- create new control panel that extends from CRControlPanel and set it
		Dimension size = new Dimension(485, 600);
		ControlPanel controlPanel = new ControlPanel(size, controller);
		controller.setControlPanel(controlPanel);
				
	}

}
