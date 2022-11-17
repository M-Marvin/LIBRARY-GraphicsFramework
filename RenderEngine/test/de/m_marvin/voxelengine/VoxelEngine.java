package de.m_marvin.voxelengine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.ResourceFolders;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.inputbinding.bindingsource.KeySource;
import de.m_marvin.renderengine.models.OBJLoader;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.rendering.LevelRenderer;
import de.m_marvin.voxelengine.resources.ReloadState;
import de.m_marvin.voxelengine.world.ClientLevel;
import de.m_marvin.voxelengine.world.VoxelComponent;
import de.m_marvin.voxelengine.world.VoxelMaterial;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class VoxelEngine {

	public static void main(String... args) {
		new VoxelEngine().run();
	}
	
	private static VoxelEngine instance;
	private VoxelEngine() { instance = this; }
	
	public static VoxelEngine getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "example";
	
	public static final ResourceLocation OBJECT_MODEL_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS = new ResourceLocation(NAMESPACE, "object_atlas");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS_INTERPOLATED = new ResourceLocation(NAMESPACE, "object_atlas_interpolated");
	public static final ResourceLocation SHADER_LIB_LOCATION = new ResourceLocation(NAMESPACE, "glsl");
	public static final ResourceLocation WORLD_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "world");
		
	protected ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	protected ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	protected TextureLoader<ResourceLocation, ResourceFolders> textureLoader;
	protected OBJLoader<ResourceLocation, ResourceFolders> modelLoader;
	protected ReloadState clientReloadState;
	
	protected UserInput inputHandler;
	protected Window mainWindow;
	protected long timeMillis;
	protected long lastTick;
	protected int framesPerSecond;
	protected int ticksPerSecond;
	protected int tickTime;
	protected int frameTime;
	
	protected Camera mainCamera;
	protected Thread renderThread;
	
	protected ClientLevel level;
	protected LevelRenderer levelRenderer;
	
	public void run() {
		
		// Setup resource loaders
		resourceLoader = new ResourceLoader<>();
		shaderLoader = new ShaderLoader<ResourceLocation, ResourceFolders>(ResourceFolders.SHADERS, resourceLoader);
		textureLoader = new TextureLoader<ResourceLocation, ResourceFolders>(ResourceFolders.TEXTURES, resourceLoader);
		modelLoader = new OBJLoader<ResourceLocation, ResourceFolders>(ResourceFolders.MODELS, resourceLoader);
		clientReloadState = ReloadState.RELOAD_RENDER_THREAD;
		
		// Setup and loop timings
		tickTime = 20; // 50 TPS
		frameTime = 16; // ~60 FPS

		// Setup GLFW
		GLStateManager.initialize(System.err);

		// Setup main window and camera
		mainWindow = new Window(1000, 600, "Engine Test");
		mainCamera = new Camera(new Vec3f(0F, 0F, 0F), new Vec3f(0F, 0F, 0F));
		
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
		
		// Start game loop
		startUpdateLoop();
		
		// Wait for render thread to terminate
		synchronized (renderThread) {
			try {
				renderThread.wait();
			} catch (InterruptedException e) {
				System.err.println("Fatel error on termination of application!");
				e.printStackTrace();
			}
		}
		
		// Terminate GLFW
		GLStateManager.terminate();
		
	}
	
	protected void startRenderThread(Runnable threadTask) {
		this.renderThread = new Thread(threadTask, "RenderThread");
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
				frame((tickTime - lastTick) / (float) tickTime);
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
		
		timeMillis = System.currentTimeMillis();
		lastTick = 0;
		float deltaTick = 0;
		
		int tickCount = 0;
		long secondTimer = timeMillis;
		
		while (!mainWindow.shouldClose()) {
			
			lastTick = timeMillis;
			timeMillis = System.currentTimeMillis();
			deltaTick += (timeMillis - lastTick) / (float) tickTime;
			
			if (deltaTick >= 1) {
				deltaTick--;
				tickCount++;
				tick();
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
		GLStateManager.clearColor(1, 0, 1, 1);
		GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		
		// Setup renderer
		this.levelRenderer = new LevelRenderer(36000);
		this.levelRenderer.fov = 70;
		this.levelRenderer.updatePerspective();
		this.levelRenderer.resetRenderCache();
		
	}
	
	protected void cleanupRenderThread() {
		
		// Clear render cache
		levelRenderer.resetRenderCache();
		
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
				VoxelEngine.getInstance().getLevelRenderer().updatePerspective();
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
		inputHandler.registerBinding("spawn_object").addBinding(KeySource.getKey(GLFW.GLFW_KEY_O));
		
		// Setup world
		level = new ClientLevel();
		
		// Testing
		VoxelStructure s = new VoxelStructure();
		List<VoxelMaterial> materials = new ArrayList<>();
		materials.add(new VoxelMaterial());
		List<int[][][]> voxels = new ArrayList<>();
		int[][][] vc = new int[4][4][4];
		vc[0][0][0] = 1;
		vc[1][0][0] = 1;
		vc[2][0][0] = 1;
		vc[3][0][0] = 1;
		vc[2][1][1] = 1;
		vc[3][1][1] = 1;
		vc[2][2][1] = 1;
		vc[3][2][1] = 1;
		vc[2][1][2] = 1;
		vc[3][1][2] = 1;
		vc[2][2][2] = 1;
		vc[3][2][2] = 1;
		
		voxels.add(vc);
		VoxelComponent c = new VoxelComponent(voxels, materials);
		s.addComponent(c, new Vec3f(0F, 0F, 0F), new Quaternion(new Vec3i(1, 0, 0), 0));
		level.addStructure(s);
		
	}
	
	// https://learnopengl.com/Advanced-OpenGL/Geometry-Shader
	
	private void frame(float partialTick) {
		
		if (clientReloadState == ReloadState.RELOAD_RENDER_THREAD) {
			
			shaderLoader.clearCached();
			textureLoader.clearCached();
			modelLoader.clearCached();
			
			shaderLoader.loadShadersIn(WORLD_SHADER_LOCATION, SHADER_LIB_LOCATION);
			// Textures
			// Models
			
			levelRenderer.resetRenderCache();
			
			clientReloadState = ReloadState.COMPLETED;
			
		}
		
		this.levelRenderer.renderLevel(level);
		
		mainWindow.glSwapFrames();
		
	}
	
	private void tick() {
		
		mainWindow.pollEvents();
		
		if (inputHandler.isBindingActive("movement.orientate")) {
			if (inputHandler.isBindingActive("movement.forward")) mainCamera.rotate(new Vec3i(-1, 0, 0), 1);
			if (inputHandler.isBindingActive("movement.backward")) mainCamera.rotate(new Vec3i(1, 0, 0), 1);
			if (inputHandler.isBindingActive("movement.left")) mainCamera.rotate(new Vec3i(0, -1, 0), 1);
			if (inputHandler.isBindingActive("movement.right")) mainCamera.rotate(new Vec3i(0, 1, 0), 1);
			if (inputHandler.isBindingActive("movement.rollleft")) mainCamera.rotate(new Vec3i(0, 0, -1), 1);
			if (inputHandler.isBindingActive("movement.rollright")) mainCamera.rotate(new Vec3i(0, 0, 1), 1);
		} else {
			if (inputHandler.isBindingActive("movement.forward")) mainCamera.move(new Vec3f(0F, 0F, -1F));
			if (inputHandler.isBindingActive("movement.backward")) mainCamera.move(new Vec3f(0F, 0F, 1F));
			if (inputHandler.isBindingActive("movement.left")) mainCamera.move(new Vec3f(-1F, 0F, 0F));
			if (inputHandler.isBindingActive("movement.right")) mainCamera.move(new Vec3f(1F, 0F, 0F));
		}
		mainCamera.upadteViewMatrix();
		
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
	
	public int getTickTime() {
		return tickTime;
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
	
	public LevelRenderer getLevelRenderer() {
		return levelRenderer;
	}
	
	public void reloadResources() {
		this.clientReloadState = ReloadState.RELOAD_RENDER_THREAD;
	}
	
}
