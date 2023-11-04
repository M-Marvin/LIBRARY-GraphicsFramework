package de.m_marvin.uitest;

import java.io.IOException;
import java.net.URISyntaxException;

import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.simplelogging.printing.Logger;

public class UITest {

	public static void main(String... args) throws URISyntaxException, IOException {
		
		// Start new logger
		Logger.setDefaultLogger(new Logger());
		
		// Redirect run folder (since all resources are located in the test folder)
		//ResourceLoader.redirectRuntimeFolder(VoxelEngine.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/"));
		
		new TestWindow().start();
		
		/** TODO
		 * - Minimize/Maximize methods
		 * - Show/Hide methods (if possible)
		 * - 
		 */
		
		while (true);
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
