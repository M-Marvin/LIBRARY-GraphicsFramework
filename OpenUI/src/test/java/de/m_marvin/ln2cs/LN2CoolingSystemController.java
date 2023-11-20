package de.m_marvin.ln2cs;

import de.m_marvin.ln2cs.windows.StatusMonitorWindow;
import de.m_marvin.simplelogging.printing.Logger;

public class LN2CoolingSystemController {
	
	public static void main(String... args) {
		
		instance = new LN2CoolingSystemController();
		instance.start();
		
	}
	
	private static LN2CoolingSystemController instance;

	private ParameterDataSet parameterData;
	private StatusMonitorWindow statusWindow;
	
	public static LN2CoolingSystemController getInstance() {
		return instance;
	}
	
	public void start() {
		
		Logger.setDefaultLogger(new Logger());
		
		this.parameterData = new ParameterDataSet();

		this.parameterData.parseData("EVT22\nEVP988\nCDT22\nCDF192\nCMP0\nEXP10\n");
		
		this.statusWindow = new StatusMonitorWindow(this.parameterData);
		this.statusWindow.start();
		
		while (this.statusWindow.isOpen()) {
			try { Thread.sleep(10); } catch (InterruptedException e) {}
			this.statusWindow.update();
		}
		
	}
	
}
