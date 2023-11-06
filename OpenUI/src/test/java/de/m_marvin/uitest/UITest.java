package de.m_marvin.uitest;

import java.io.IOException;
import java.net.URISyntaxException;

import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.simplelogging.printing.Logger;

public class UITest {

	public static void main(String... args) throws URISyntaxException, IOException, InterruptedException {
		
		// Start new logger
		Logger.setDefaultLogger(new Logger());
		
		// Redirect run folder (since all resources are located in the test folder)
		//ResourceLoader.redirectRuntimeFolder(VoxelEngine.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/"));
		
		TestWindow window = new TestWindow();
		
		/** TODO
		 * - Minimize/Maximize methods
		 * - Show/Hide methods (if possible)
		 * - 
		 */
		
		window.start();
		
		while (window.isOpen()) {
			Thread.sleep(1000);
		};
	}
	
	private static UITest instance;
	private UITest() { instance = this; }
	
	public static UITest getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "uitest";
	
	public static final ResourceLocation SHADER_LIB_LOCATION = new ResourceLocation(NAMESPACE, "glsl");
	public static final ResourceLocation WORLD_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "world");
	public static final ResourceLocation OPENUI_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "openui");
	
}
