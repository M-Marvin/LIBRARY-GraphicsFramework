package de.m_marvin.voxelengine;

import java.io.File;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.openui.dep.ScreenUI;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.inputbinding.bindingsource.KeySource;
import de.m_marvin.renderengine.inputbinding.bindingsource.MouseSource;
import de.m_marvin.renderengine.models.OBJLoader;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.simplelogging.filehandling.LogFileHandler;
import de.m_marvin.simplelogging.printing.LogType;
import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.uitest.ResourceFolders;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.rendering.GameRenderer;
import de.m_marvin.voxelengine.rendering.RenderStage;
import de.m_marvin.voxelengine.rendering.RenderType;
import de.m_marvin.voxelengine.resources.ReloadState;
import de.m_marvin.voxelengine.screens.ComponentEditorScreen;
import de.m_marvin.voxelengine.utility.VoxelComponentLoader;
import de.m_marvin.voxelengine.utility.VoxelMaterialRegistry;
import de.m_marvin.voxelengine.world.ClientLevel;
import de.m_marvin.voxelengine.world.VoxelComponent;
import de.m_marvin.voxelengine.world.VoxelMaterial;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class VoxelEngine {
	
	private static LogFileHandler logFileHandler;
	
	public static void main(String... args) {

		// Start new logger
		try {
			logFileHandler = new LogFileHandler(new File(new File(ResourceLoader.getRuntimeFolder()).getParentFile().getParentFile(), "run/logs"), "EngineTest");
			Logger logger = logFileHandler.beginLogging();
			Logger.setDefaultLogger(logger);
		} catch (IOException e) {
			System.err.println("Failed to create logger!");
			e.printStackTrace();
			logFileHandler = null;
			Logger.setDefaultLogger(new Logger());
		}
		
		// Redirect run folder (since all resources are located in the test folder)
		ResourceLoader.redirectRuntimeFolder(VoxelEngine.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/"));
		
		// Start application
		try {
			new VoxelEngine().run();
		} catch (Exception e) {
			Logger.defaultLogger().logError("CRASH", "The engine has crashed!");
			Logger.defaultLogger().printException(LogType.ERROR, "CRASH", e);
		}
		
		// Terminate logger
		if (logFileHandler != null) {
			try {
				logFileHandler.endLogging();
			} catch (IOException e) {
				System.err.println("Failed to terminate logger, the log files might be corrupted!");
				e.printStackTrace();
			}
		}
		
	}
	
	private static VoxelEngine instance;
	private VoxelEngine() { instance = this; }
	
	public static VoxelEngine getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "example";
	
	protected ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	protected ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	protected TextureLoader<ResourceLocation, ResourceFolders> textureLoader;
	protected OBJLoader<ResourceLocation, ResourceFolders> modelLoader;
	protected ReloadState clientReloadState;
	
	protected VoxelComponentLoader<ResourceLocation, ResourceFolders> voxelLoader;
	protected VoxelMaterialRegistry materialRegistry;
	
	protected UserInput inputHandler;
	protected Window mainWindow;
	// Time in milliseconds
	protected long timeMillis;
	// Frames per second
	protected int framesPerSecond;
	// Ticks per second
	protected int ticksPerSecond;
	// Target time for one tick in milliseconds
	protected int tickTime;
	// Target time for one frame in milliseconds
	protected int frameTime;
	// Time in ticks
	protected long ticks;
	// Partial tick time
	protected float deltaTick;
	
	protected Camera mainCamera;
	protected Thread renderThread;
	
	protected ScreenUI screen;
	protected ClientLevel level;
	protected GameRenderer gameRenderer;
	
	public void run() {
		
		Logger.defaultLogger().logInfo("Start!");
		
		// Setup resource loaders
		resourceLoader = new ResourceLoader<>();
		shaderLoader = new ShaderLoader<ResourceLocation, ResourceFolders>(ResourceFolders.SHADERS, resourceLoader);
		textureLoader = new TextureLoader<ResourceLocation, ResourceFolders>(ResourceFolders.TEXTURES, resourceLoader);
		modelLoader = new OBJLoader<ResourceLocation, ResourceFolders>(ResourceFolders.MODELS, resourceLoader);
		clientReloadState = ReloadState.RELOAD_RENDER_THREAD;
		
		// Setup additional loaders
		materialRegistry = new VoxelMaterialRegistry();
		voxelLoader = new VoxelComponentLoader<ResourceLocation, ResourceFolders>(ResourceFolders.VOXELS, resourceLoader);
		
		// Setup and loop timings
		tickTime = 20; // 50 TPS
		frameTime = 16; // ~60 FPS

		// Setup GLFW
		GLStateManager.initialize(System.err);

		// Setup main window and camera
		mainWindow = new Window(1000, 600, "Engine Test");
		mainCamera = new Camera(new Vec3f(0F, 0F, 0F), new Vec3f(0F, 0F, 0F));
		
		Logger.defaultLogger().logInfo("Start Render-Thread");
		
		// Start and initialize render thread
		startRenderThread(() -> {
			
			// Take control over rendering on main window
			mainWindow.makeContextCurrent();
			
			// Setup rendering
			setupRenderThread();
			
			// Start frame loop
			startRenderLoop();
			
			// Cleanup after termination
			cleanupRenderThread();
			
			// Tell main thread that render thread is ready for termination of GLFW
			synchronized (renderThread) {
				renderThread.notifyAll();
			}
			
		});
		
		// Setup input handler
		inputHandler = new UserInput();
		inputHandler.attachToWindow(mainWindow.windowId());
		
		// Setup main thread
		setupUpdateThread();
		
		Logger.defaultLogger().logInfo("Start game loop");
		
		// Start game loop
		startUpdateLoop();

		Logger.defaultLogger().logInfo("Stop, wait for Render-Thread to shutdown");
		
		// Wait for render thread to terminate
		synchronized (renderThread) {
			try {
				renderThread.wait();
			} catch (InterruptedException e) {
				Logger.defaultLogger().logError("Fatel error on termination of application!");
				Logger.defaultLogger().printException(LogType.ERROR, e);
			}
		}
		
		// Terminate GLFW
		GLStateManager.terminate();
		
		Logger.defaultLogger().logInfo("Exit");
		
	}
	
	protected void startRenderThread(Runnable threadTask) {
		this.renderThread = new Thread(() -> {
			try {
				threadTask.run();
			} catch (Exception e) {
				Logger.defaultLogger().logError("CRASH", "Render thread crashed!");
				Logger.defaultLogger().printException(LogType.ERROR, "CRASH", e);
			}
		}, "RenderThread");
		this.renderThread.start();
	}
	
	private void startRenderLoop() {

		long frameTimeMillis = System.currentTimeMillis();
		long lastFrameTime = 0;
		float deltaFrame = 0;
		
		int frameCount = 0;
		long secondTimer = frameTimeMillis;
		
		while (!mainWindow.shouldClose()) {
			
			lastFrameTime = frameTimeMillis;
			frameTimeMillis = System.currentTimeMillis();
			deltaFrame += (frameTimeMillis - lastFrameTime) / (float) frameTime;
			
			if (deltaFrame >= 1) {
				deltaFrame--;
				frameCount++;
				frame(deltaTick);
			}
			
			if (frameTimeMillis - secondTimer > 1000) {
				secondTimer += 1000;
				framesPerSecond = frameCount;
				frameCount = 0;
				
				this.mainWindow.setTitle("TPS: " + ticksPerSecond + " ; FPS: " + framesPerSecond);
			}
			
		}
		
	}

	private void startUpdateLoop() {
		
		long timeMillis = System.currentTimeMillis();
		long lastTick = 0;
		deltaTick = 0;
		
		int tickCount = 0;
		long secondTimer = timeMillis;
		
		while (!mainWindow.shouldClose()) {
			
			lastTick = timeMillis;
			timeMillis = System.currentTimeMillis();
			deltaTick += (timeMillis - lastTick) / (float) tickTime;
			
			if (deltaTick >= 1) {
				deltaTick--;
				tickCount++;
				ticks++;
				tick();
				
				if (!this.renderThread.isAlive()) throw new IllegalStateException("Render-Thread terminated unexpected!");
			}
			
			if (timeMillis - secondTimer > 1000) {
				secondTimer += 1000;
				ticksPerSecond = tickCount;
				tickCount = 0;
			}
			
		}
		
	}
	
	protected void setupRenderThread() {
		
		// Setup OpenGL
		GLStateManager.clearColor(1, 0, 0, 1);
		GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		
		// Setup renderer
		this.gameRenderer = new GameRenderer(36000);
		this.gameRenderer.fov = 70;
		this.gameRenderer.updatePerspective();
		this.gameRenderer.resetRenderCache();
		
	}
	
	protected void cleanupRenderThread() {
		
		// Clear render cache
		gameRenderer.resetRenderCache();
		
		// Unload all shaders, textures and models from GPU and cache
		shaderLoader.clearCached();
		textureLoader.clearCached();
		modelLoader.clearCached();
		
		// Destroy main window
		mainWindow.destroy();
		
	}
	
	public void setupUpdateThread() {
		
		// Setup window resize callback
		mainWindow.registerWindowListener((shouldClose, windowResize, focused, unfocused, maximized, restored) -> {
			if (windowResize.isPresent()) {
				VoxelEngine.getInstance().getGameRenderer().updatePerspective();
			}
		});
		
		// Setup keybindings
		inputHandler.registerBinding("movement.forward").addBinding(KeySource.getKey(GLFW.GLFW_KEY_W));
		inputHandler.registerBinding("movement.backward").addBinding(KeySource.getKey(GLFW.GLFW_KEY_S));
		inputHandler.registerBinding("movement.left").addBinding(KeySource.getKey(GLFW.GLFW_KEY_A));
		inputHandler.registerBinding("movement.right").addBinding(KeySource.getKey(GLFW.GLFW_KEY_D));
		inputHandler.registerBinding("movement.rollleft").addBinding(KeySource.getKey(GLFW.GLFW_KEY_Q));
		inputHandler.registerBinding("movement.rollright").addBinding(KeySource.getKey(GLFW.GLFW_KEY_E));
		inputHandler.registerBinding("movement.orientate").addBinding(KeySource.getKey(GLFW.GLFW_KEY_LEFT_ALT));
		inputHandler.registerBinding("physic.activate").addBinding(KeySource.getKey(GLFW.GLFW_KEY_P));
		inputHandler.registerBinding("misc.click.primary").addBinding(MouseSource.getKey(GLFW.GLFW_MOUSE_BUTTON_1));
		inputHandler.registerBinding("misc.editor.camera").addBinding(MouseSource.getKey(GLFW.GLFW_MOUSE_BUTTON_1));
		
		// Setup world
		level = new ClientLevel();
		
		VoxelMaterial m_test = materialRegistry.registerMaterial(new ResourceLocation(NAMESPACE, "test"), new VoxelMaterial(RenderType.voxelSolid(), new ResourceLocation("example:materials/ground_anim"), 0.5F));
		VoxelMaterial m_metal = materialRegistry.registerMaterial(new ResourceLocation(NAMESPACE, "metal"), new VoxelMaterial(RenderType.voxelSolid(), new ResourceLocation("example:materials/metal"), 1F));
		VoxelMaterial m_dirt = materialRegistry.registerMaterial(new ResourceLocation(NAMESPACE, "dirt"), new VoxelMaterial(RenderType.voxelSolid(), new ResourceLocation("example:materials/dirt"), 1F));
		
		// Testing
		VoxelComponent c = voxelLoader.get(new ResourceLocation("example:test")); //
		VoxelStructure s = new VoxelStructure();
		s.addComponent(c, new Vec3f(0F, 0F, 0F), new Quaternion(new Vec3i(1, 0, 0), (float) Math.toRadians(45)));
		level.addStructure(s);
		s.setPosition(new Vec3f(-20F, 0F, 0F));
		//s.setOrientation(new Quaternion(new Vec3i(1, 0, 0), (float) Math.toRadians(45)));
		
		VoxelComponent c2 = voxelLoader.get(new ResourceLocation("example:metal_block"));
		VoxelComponent c22 = voxelLoader.get(new ResourceLocation("example:metal_block"));
		VoxelStructure s4 = new VoxelStructure();
		s4.addComponent(c2, new Vec3f(0F, 0F, 0F), new Quaternion(new Vec3i(1, 0, 0), 0));
		s4.addComponent(c22, new Vec3f(0F, 20F, 0F), new Quaternion(new Vec3i(1, 0, 0), 45));
		level.addStructure(s4);
		s4.setPosition(new Vec3f(0F, 40F, 0F));
		
		VoxelComponent c3 = voxelLoader.get(new ResourceLocation("example:ground"));
		VoxelStructure s5 = new VoxelStructure();
		s5.addComponent(c3, new Vec3f(0F, 0F, 0F), new Quaternion(new Vec3i(1, 0, 0), 0));
		level.addStructure(s5);
		s5.setStatic(true);
		s5.setPosition(new Vec3f(-20F, -80F, -20F));
		
		level.setGravity(new Vec3f(0F, -9.81F, 0F));
		
		
		openScreen(new ComponentEditorScreen(c));
		
	}
	
	// https://learnopengl.com/Advanced-OpenGL/Geometry-Shader
	
	private void frame(float partialTick) {

		this.gameRenderer.switchStage(RenderStage.UTIL);

		if (clientReloadState == ReloadState.RELOAD_RENDER_THREAD) {
			
			try {
				
				shaderLoader.clearCached();
				textureLoader.clearCached();
				modelLoader.clearCached();
				
				// Load shaders
				ResourceLocation shaderLibLocation = new ResourceLocation(NAMESPACE, "glsl");
				Utility.executeForEachFolder(resourceLoader.getResourceFolderPath(ResourceFolders.SHADERS, NAMESPACE), 
						(folder) -> shaderLoader.loadShadersIn(new ResourceLocation(NAMESPACE, folder), shaderLibLocation));
				
				// Load textures
				Utility.executeForEachFolder(resourceLoader.getResourceFolderPath(ResourceFolders.TEXTURES, NAMESPACE),
						(folder) -> {
							textureLoader.buildAtlasMapFromTextures(new ResourceLocation(NAMESPACE, folder), new ResourceLocation(NAMESPACE, folder), false, false);
							textureLoader.buildAtlasMapFromTextures(new ResourceLocation(NAMESPACE, folder), new ResourceLocation(NAMESPACE, folder + "_interpolated"), false, true);
						});
				
				gameRenderer.resetRenderCache();
				
				clientReloadState = ReloadState.COMPLETED;
				
			} catch (Exception e) {
				Logger.defaultLogger().logWarn("Failed to reload resources! " + e.getMessage());
				Logger.defaultLogger().printException(LogType.WARN, "assets", e);
				clientReloadState = ReloadState.FAILED;
			}
			
		}
		
		this.gameRenderer.switchStage(RenderStage.LEVEL);
		
		if (this.level != null) this.gameRenderer.renderLevel(level, partialTick);

		this.gameRenderer.switchStage(RenderStage.UI);
		
		if (this.screen != null) this.gameRenderer.renderScreen(this.screen, partialTick);
		
		this.gameRenderer.finishLastStage();
		
		mainWindow.glSwapFrames();
		
	}
	
	private void tick() {
		
		mainWindow.pollEvents();
		inputHandler.update();
		
		this.textureLoader.getTextureMaps().forEach((texture) -> {
			if (this.getTicksElapsed() % texture.getFrametime() == 0) texture.nextFrame();
		});
		
		if (inputHandler.isBindingActive("movement.orientate")) {
			if (inputHandler.isBindingActive("movement.forward")) mainCamera.rotate(new Vec3i(1, 0, 0), 1);
			if (inputHandler.isBindingActive("movement.backward")) mainCamera.rotate(new Vec3i(-1, 0, 0), 1);
			if (inputHandler.isBindingActive("movement.left")) mainCamera.rotate(new Vec3i(0, 1, 0), 1);
			if (inputHandler.isBindingActive("movement.right")) mainCamera.rotate(new Vec3i(0, -1, 0), 1);
			if (inputHandler.isBindingActive("movement.rollleft")) mainCamera.rotate(new Vec3i(0, 0, 1), 1);
			if (inputHandler.isBindingActive("movement.rollright")) mainCamera.rotate(new Vec3i(0, 0, -1), 1);
		} else {
			if (inputHandler.isBindingActive("movement.forward")) mainCamera.move(new Vec3f(0F, 0F, -1F));
			if (inputHandler.isBindingActive("movement.backward")) mainCamera.move(new Vec3f(0F, 0F, 1F));
			if (inputHandler.isBindingActive("movement.left")) mainCamera.move(new Vec3f(-1F, 0F, 0F));
			if (inputHandler.isBindingActive("movement.right")) mainCamera.move(new Vec3f(1F, 0F, 0F));
		}
		mainCamera.upadteViewMatrix();
		
		if (this.screen != null) this.screen.update();
		
		// Testing
		if (GLFW.glfwGetKey(mainWindow.windowId(), GLFW.GLFW_KEY_P) == GLFW.GLFW_PRESS) level.tick();
		
	}
	
	public void openScreen(ScreenUI screen) {
		closeScreen();
		this.screen = screen;
		this.screen.onOpen(this.inputHandler);
	}
	
	public void closeScreen() {
		if (this.screen != null) {
			this.screen.onClose(this.inputHandler);
			this.screen = null;
		}
	}
	
	public ResourceLoader<ResourceLocation, ResourceFolders> getResourceLoader() {
		return resourceLoader;
	}
	
	public ShaderLoader<ResourceLocation, ResourceFolders> getShaderLoader() {
		return shaderLoader;
	}
	
	public TextureLoader<ResourceLocation, ResourceFolders> getTextureLoader() {
		return textureLoader;
	}
	
	public OBJLoader<ResourceLocation, ResourceFolders> getModelLoader() {
		return modelLoader;
	}
	
	public Window getMainWindow() {
		return mainWindow;
	}
	
	public long getTicksElapsed() {
		return ticks;
	}
	
	public long getTickTime() {
		return tickTime;
	}

	public long getFrameTime() {
		return frameTime;
	}
	
	public int getFramesPerSecond() {
		return framesPerSecond;
	}
	
	public int getTicksPerSecond() {
		return ticksPerSecond;
	}
	
	public Camera getMainCamera() {
		return mainCamera;
	}
	
	public UserInput getInputHandler() {
		return inputHandler;
	}
	
	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}
	
	public VoxelComponentLoader<ResourceLocation, ResourceFolders> getVoxelLoader() {
		return voxelLoader;
	}
	
	public VoxelMaterialRegistry getMaterialRegistry() {
		return materialRegistry;
	}
	
	public void reloadResources() {
		this.clientReloadState = ReloadState.RELOAD_RENDER_THREAD;
	}
	
}
