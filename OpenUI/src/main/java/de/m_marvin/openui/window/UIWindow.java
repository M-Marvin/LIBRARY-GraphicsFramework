package de.m_marvin.openui.window;

import de.m_marvin.openui.UIContainer;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.renderengine.windows.Window.WindowEventType;
import de.m_marvin.univec.impl.Vec2i;

public abstract class UIWindow<R extends IResourceProvider<R>, S extends ISourceFolder> {

	public UIWindow(S shaderFolder, S textureFolder, String windowName) {
		this(
				new UserInput(), 
				shaderFolder,
				textureFolder,
				windowName
		);
	}

	public UIWindow(UserInput userInput, S shaderFolder, S textureFolder, String windowName) {
		this(
				new ResourceLoader<>(),
				userInput, 
				shaderFolder,
				textureFolder,
				windowName
		);
	}
	
	public UIWindow(ResourceLoader<R, S> resourceLoader, UserInput userInput, S shaderFolder, S textureFolder, String windowName) {
		this(
				resourceLoader,
				new ShaderLoader<R, S>(shaderFolder, resourceLoader),
				new TextureLoader<R, S>(textureFolder, resourceLoader),
				userInput,
				windowName,
				true
		);
	}
	
	public UIWindow(ResourceLoader<R, S> resourceLoader, ShaderLoader<R, S> shaderLoader, TextureLoader<R, S> textureLoader, UserInput inputHandler, String windowName, boolean clearCachesOnClose) {
		
		this.windowName = windowName;
		this.clearCachesOnClose = clearCachesOnClose;
		this.inputHandler = inputHandler;
		this.resourceLoader = resourceLoader;
		this.shaderLoader = shaderLoader;
		this.textureLoader = textureLoader;
		
	}
	
	protected String windowName;
	protected final boolean clearCachesOnClose;
	
	private ResourceLoader<R, S> resourceLoader;
	private ShaderLoader<R, S> shaderLoader;
	private TextureLoader<R, S> textureLoader;
	private UserInput inputHandler;

	protected Thread renderThread;
	private Window mainWindow;
	protected UIContainer<R> uiContainer;
	private int framesPerSecond;
	private int frameTime;
	protected boolean shouldClose;
	
	public void start() {
		if (this.renderThread != null)
			return;
		this.renderThread = new Thread(this::init, "RenderThread[" + this.windowName + "]");
		this.renderThread.setDaemon(true);
		this.renderThread.start();
	}
	
	public void stop() {
		this.shouldClose = true;
	}
	
	private void init() {

		// Setup OpenGL and GLFW natives
		GLStateManager.initialize(System.err);

		// Setup main window
		mainWindow = new Window(1000, 600, this.windowName);
		mainWindow.makeContextCurrent();
		GLStateManager.clearColor(1, 0, 1, 1);

		// Setup input handler
		inputHandler.attachToWindow(mainWindow.windowId());

		// Setup and start game loop
		frameTime = 16; // ~60 FPS
		setup();
		startLoop();

		if (this.clearCachesOnClose) {

			// Unload all shaders, textures and models
			shaderLoader.clearCached();
			textureLoader.clearCached();

		}

		// Detach input handler
		inputHandler.detachWindow(mainWindow.windowId());
		
		// Destroy main window
		mainWindow.destroy();

		// Terminate OpenGL and GLFW natives
		GLStateManager.terminate();

	}

	private void startLoop() {

		long timeMillis = System.currentTimeMillis();
		float deltaFrame = 0;

		int frameCount = 0;
		long secondTimer = timeMillis;
		long lastFrameTime = 0;

		while (!mainWindow.shouldClose() && !this.shouldClose) {

			lastFrameTime = timeMillis;
			timeMillis = System.currentTimeMillis();
			deltaFrame += (timeMillis - lastFrameTime) / (float) frameTime;

			if (deltaFrame >= 1) {
				deltaFrame--;
				frameCount++;
				frame(0);
				mainWindow.pollEvents();
			}

			if (timeMillis - secondTimer > 1000) {
				secondTimer += 1000;
				framesPerSecond = frameCount;
				frameCount = 0;
			}

		}

	}

	protected void setup() {
		
		this.uiContainer = new UIContainer<>();
		
		initUI();
		autoSetMinSize();
		
		windowResized(new Vec2i(this.mainWindow.getSize()[0], this.mainWindow.getSize()[1]));
		this.mainWindow.registerWindowListener((windowResize, type) -> {
			if (windowResize.isPresent() && type == WindowEventType.RESIZED)
				windowResized(windowResize.get());
		});

	}
	
	protected void autoSetMinSize() {
		Vec2i minSize = this.uiContainer.calculateMinScreenSize();
		this.mainWindow.setMinSize(minSize.x, minSize.y);
	}
	
	protected void windowResized(Vec2i screenSize) {
		GLStateManager.resizeViewport(0, 0, screenSize.x, screenSize.y);
		this.uiContainer.screenResize(screenSize);
	}

	protected void frame(float partialTick) {

		this.uiContainer.updateOutdatedVAOs();
		this.uiContainer.renderVAOs(shaderLoader, textureLoader);
		mainWindow.glSwapFrames();

	}
	
	protected abstract void initUI();
	
	public ResourceLoader<R, S> getResourceLoader() {
		return resourceLoader;
	}

	public ShaderLoader<R, S> getShaderLoader() {
		return shaderLoader;
	}

	public TextureLoader<R, S> getTextureLoader() {
		return textureLoader;
	}

	public Window getMainWindow() {
		return mainWindow;
	}

	public int getFramesPerSecond() {
		return framesPerSecond;
	}
	
	public String getWindowName() {
		return windowName;
	}
	
	public void setWindowName(String windowName) {
		this.windowName = windowName;
		this.mainWindow.setTitle(windowName);
	}
	
}
