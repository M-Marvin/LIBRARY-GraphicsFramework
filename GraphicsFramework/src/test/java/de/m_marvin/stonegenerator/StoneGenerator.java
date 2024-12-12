package de.m_marvin.stonegenerator;

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.ResourceFolders;
import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.gframe.GLStateManager;
import de.m_marvin.gframe.buffers.BufferBuilder;
import de.m_marvin.gframe.buffers.BufferUsage;
import de.m_marvin.gframe.buffers.IBufferSource;
import de.m_marvin.gframe.buffers.VertexBuffer;
import de.m_marvin.gframe.buffers.defimpl.RenderMode;
import de.m_marvin.gframe.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.gframe.framebuffers.Framebuffer;
import de.m_marvin.gframe.inputbinding.UserInput;
import de.m_marvin.gframe.inputbinding.bindingsource.KeySource;
import de.m_marvin.gframe.resources.ResourceLoader;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.gframe.shaders.ShaderInstance;
import de.m_marvin.gframe.shaders.ShaderLoader;
import de.m_marvin.gframe.translation.Camera;
import de.m_marvin.gframe.translation.PoseStack;
import de.m_marvin.gframe.windows.Window;
import de.m_marvin.unimat.MatUtil;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;

public class StoneGenerator {

	public static void main(String... args) {
		new StoneGenerator().run();
	}
	
	private static StoneGenerator instance;
	private StoneGenerator() { instance = this; }
	
	public static StoneGenerator getInstance() {
		return instance;
	}
	
	public static String NAMESPACE = "stonegen";
	public static ResourceLocation SHADER_LOCATION = new ResourceLocation(NAMESPACE, "stone");
	
	private ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	private ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	private UserInput inputHandler;
	
	protected Camera mainCamera;
	protected Matrix4f projectionMatrix = MatUtil.perspectiveF(50, 1000F / 600F, 1F, 1000F);
	
	private Window mainWindow;
	private long timeMillis;
	private int framesPerSecond;
	private int ticksPerSecond;
	private int tickTime;
	private int frameTime;
	
	protected List<Stone> stones;
	
	public void run() {

		// Redirect run folder (since all resources are located in the test folder)
		ResourceLoader.redirectRuntimeFolder(StoneGenerator.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/assets/"));
		
		// Setup resource loaders
		resourceLoader = new ResourceLoader<>();
		shaderLoader = new ShaderLoader<ResourceLocation, ResourceFolders>(ResourceFolders.SHADERS, resourceLoader);
		
		// Setup OpenGL and GLFW natives
		GLFWStateManager.initialize(System.err);
		
		// Setup main window
		mainWindow = new Window(1000, 600, "Engine Test");
		mainWindow.makeContextCurrent();
		GLStateManager.clearColor(1, 0, 1, 1);
		GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		mainCamera = new Camera(new Vec3f(6F, 1F, 2F), new Vec3f(0F, 90F, 0F));		

		// Setup input handler
		inputHandler = new UserInput();
		inputHandler.attachToWindow(mainWindow.windowId());
		
		// Setup and start game loop
		tickTime = 20; // 50 TPS
		frameTime = 16; // ~60 FPS
		setup();
		startLoop();
		
		// Unload all shaders, textures and models
		shaderLoader.clearCached();
		
		// Destroy main window
		mainWindow.destroy();
		
		// Terminate OpenGL and GLFW natives
		GLFWStateManager.terminate();
		
	}
	
	private void startLoop() {
		
		timeMillis = System.currentTimeMillis();
		float deltaTick = 0;
		float deltaFrame = 0;
		
		int frameCount = 0;
		int tickCount = 0;
		long secondTimer = timeMillis;
		long lastFrameTime = 0;
		
		Thread physicsThread = new Thread(this::runPhysics);
		physicsThread.setDaemon(true);
		physicsThread.start();
		
		while (!mainWindow.shouldClose()) {
			
			lastFrameTime = timeMillis;
			timeMillis = System.currentTimeMillis();
			deltaTick += (timeMillis - lastFrameTime) / (float) tickTime;
			deltaFrame += (timeMillis - lastFrameTime) / (float) frameTime;

			GLFWStateManager.update();
			
			if (deltaTick >= 1) {
				deltaTick--;
				tickCount++;
				tick();
			}
			
			if (deltaFrame >= 1) {
				deltaFrame--;
				frameCount++;
				frame(deltaTick);
			}
			
			if (timeMillis - secondTimer > 1000) {
				secondTimer += 1000;
				ticksPerSecond = tickCount;
				tickCount = 0;
				framesPerSecond = frameCount;
				frameCount = 0;
				
				this.mainWindow.setTitle("TPS: " + ticksPerSecond + " ; FPS: " + framesPerSecond);
			}
			
		}
		
	}
	
	public void setup() {
		
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
		
		// Load shader, textures and models
		shaderLoader.loadShadersIn(SHADER_LOCATION, 10);
		
		windowResized(new Vec2i(this.mainWindow.getSize().x, this.mainWindow.getSize().x));
		this.mainWindow.registerWindowListener((windowResize, type) -> { if (windowResize.isPresent()) windowResized(new Vec2i(windowResize.get())); });
		
		// Setup world
		Color[] colors = { 
				Color.RED, 
				Color.green, 
				
				Color.black, 
				Color.blue, 
				Color.yellow, 
				Color.orange, 
				Color.pink 
			};
		Vec3f[] offsets = { 
				new Vec3f(-20, 0, 0),
				new Vec3f(+20, 10, 20),
				new Vec3f(-15, 20, 10),
				new Vec3f(+25, 30, 0)
			};
		
		stones = IntStream.range(0, 400).mapToObj(indx -> {
			Stone s = new Stone(colors[indx % colors.length], 1.0F, 12.0F, 2.0F, 200, 3);
			
			Vec3f offset = offsets[indx % offsets.length];
			int group = indx / offsets.length;
			Vec3f position = new Vec3f((group % 10) * 100, 0, (group / 10) * 100);
			s.pos = position.add(offset);
			
			return s;
		}).toList();
		
		mainCamera.getPosition().setI(500F, -600F, 500F);
		mainCamera.getRotation().setI(new Quaternionf(new Vec3f(90, 0, 0), EulerOrder.XYZ, true));
		
	}
	
	public void windowResized(Vec2i screenSize) {
		GLStateManager.resizeViewport(0, 0, screenSize.x, screenSize.y);
		this.projectionMatrix = MatUtil.perspectiveF(50, screenSize.x / (float) Math.max(1, screenSize.y), 1F, 100F);
	}
	
	VertexBuffer particleDrawBuffer = new VertexBuffer();
	BufferBuilder particleBuffer = new BufferBuilder(36000);
	
	IBufferSource<RenderMode<ResourceLocation>> bufferSource = new SimpleBufferSource<>(36000);
	PoseStack matrix = new PoseStack();
	VertexBuffer renderBuffer = new VertexBuffer();
	
	int fbt = 0;
	
	private void frame(float partialTick) {
		
		Framebuffer framebuffer = null;
		if (fbt == 100) {
			framebuffer = new Framebuffer(1000, 600);
			framebuffer.bind();
		}
		
		GLStateManager.clearColor(1, 0.5F, 0.25F, 0.1F);
		GLStateManager.clear();
		
		ShaderInstance shader = shaderLoader.getShader(new ResourceLocation(NAMESPACE, "stone/draw_stone"));
		
		Matrix4f viewMatrix = mainCamera.getViewMatrix();
		
		shader.useShader();
		shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
		shader.getUniform("ViewMat").setMatrix4f(viewMatrix);
		
		GLStateManager.enable(GL33.GL_DEPTH_TEST);
		//GLStateManager.enable(GL33.GL_BLEND);
		//GLStateManager.enable(GL33.GL_CULL_FACE);
		
		
		matrix.push();
		
		for (Stone stone : stones) {
			stone.drawStone(this.bufferSource, this.matrix);
		}
		
		matrix.pop();
		matrix.assertCleared();
		
		
		BufferBuilder b = this.bufferSource.getBuffer(RenderTypes.stone());
		while (b.completedBuffers() > 0) {
			renderBuffer.upload(b, BufferUsage.DYNAMIC);
			renderBuffer.bind();
			renderBuffer.drawAll(RenderTypes.stone().primitive());
		}
		b.discardStored();
		
//		if (fbt == 100) {
//
//			framebuffer.unbind();
//			
//			try {
//				Texture texture = framebuffer.getColorTexture();
//				int[] pixels = texture.download(TextureDataFormat.INT_RGBA_8_8_8_8);
//				
//				BufferedImage image = new BufferedImage(texture.getTexWidth(), texture.getTexHeight(), BufferedImage.TYPE_INT_ARGB);
//				image.setRGB(0, 0, texture.getTexWidth(), texture.getTexHeight(), pixels, 0, texture.getTexWidth());
//				ImageIO.write(image, "PNG", new File("C:\\Users\\marvi\\Desktop\\test.png"));
//				
//				Texture texture2 = framebuffer.getDepthTexture();
//				int[] pixels2 = texture2.download(TextureDataFormat.FLOAT_DEPTH);
//
//				BufferedImage image2 = new BufferedImage(texture2.getTexWidth(), texture2.getTexHeight(), BufferedImage.TYPE_INT_ARGB);
//				image2.setRGB(0, 0, texture2.getTexWidth(), texture2.getTexHeight(), pixels2, 0, texture2.getTexWidth());
//				ImageIO.write(image2, "PNG", new File("C:\\Users\\marvi\\Desktop\\test2.png"));
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			framebuffer.discard();
//			
//		} else {
//
//			mainWindow.glSwapFrames();
//			
//		}
//
//		fbt++;

		mainWindow.glSwapFrames();
		
	}
	
	private void tick() {
		
		this.inputHandler.update();
		
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
		
		for (Stone s : this.stones) {
			s.rotation.z += 0.7F;
		}
		
	}
	
	protected void runPhysics() {
		
//		double lastTime = 1;
//		while (!this.mainWindow.shouldClose()) {
//			long timeStart = System.currentTimeMillis();
//			physicWorld.stepPhysic(lastTime / 1000);
//			lastTime = System.currentTimeMillis() - timeStart;
//		}
		
	}
	
	public ResourceLoader<ResourceLocation, ResourceFolders> getResourceLoader() {
		return resourceLoader;
	}
	
	public ShaderLoader<ResourceLocation, ResourceFolders> getShaderLoader() {
		return shaderLoader;
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
	
}
