package de.m_marvin.ln2cs;

import de.m_marvin.ln2cs.windows.StatusMonitorWindow;
import de.m_marvin.simplelogging.printing.Logger;

public class LN2CoolingSystemController {
	
	public static void main(String... args) {
		
		instance = new LN2CoolingSystemController();
		instance.start();
		
	}
	
	private static LN2CoolingSystemController instance;
	
	public static LN2CoolingSystemController getInstance() {
		return instance;
	}
	
	private StatusMonitorWindow statusWindow;
	
	public void start() {
		
		Logger.setDefaultLogger(new Logger());
		
		this.statusWindow = new StatusMonitorWindow();
		this.statusWindow.start();
		
		while (this.statusWindow.isOpen()) {
			this.statusWindow.test();
			try { Thread.sleep(10); } catch (InterruptedException e) {}
		}
		
	}
	
}
