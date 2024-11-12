package de.m_marvin.enginetest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.particles.Particle;
import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.gframe.GLStateManager;
import de.m_marvin.gframe.buffers.BufferBuilder;
import de.m_marvin.gframe.buffers.BufferUsage;
import de.m_marvin.gframe.buffers.VertexBuffer;
import de.m_marvin.gframe.framebuffers.Framebuffer;
import de.m_marvin.gframe.inputbinding.UserInput;
import de.m_marvin.gframe.inputbinding.bindingsource.KeySource;
import de.m_marvin.gframe.resources.ResourceLoader;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.gframe.shaders.ShaderInstance;
import de.m_marvin.gframe.shaders.ShaderLoader;
import de.m_marvin.gframe.textures.TextureLoader;
import de.m_marvin.gframe.textures.texture.Texture;
import de.m_marvin.gframe.textures.utility.TextureDataFormat;
import de.m_marvin.gframe.translation.Camera;
import de.m_marvin.gframe.utility.NumberFormat;
import de.m_marvin.gframe.vertices.RenderPrimitive;
import de.m_marvin.gframe.vertices.VertexFormat;
import de.m_marvin.gframe.windows.Window;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;

public class EngineExample {

	public static void main(String... args) {
		new EngineExample().run();
	}
	
	private static EngineExample instance;
	private EngineExample() { instance = this; }
	
	public static EngineExample getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "particles";
	
	public static final ResourceLocation OBJECT_MODEL_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS = new ResourceLocation(NAMESPACE, "object_atlas");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS_INTERPOLATED = new ResourceLocation(NAMESPACE, "object_atlas_interpolated");
	public static final ResourceLocation WORLD_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "world");
	
	private ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	private ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	private TextureLoader<ResourceLocation, ResourceFolders> textureLoader;
	private UserInput inputHandler;
	
	protected Camera mainCamera;
	protected Matrix4f projectionMatrix = Matrix4f.perspective(50, 1000F / 600F, 1F, 1000F);
	
	protected Space3D physicWorld;
	protected final VertexFormat objectFormat = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("color", NumberFormat.FLOAT, 4, false).appand("size", NumberFormat.FLOAT, 1, false);
	
	private Window mainWindow;
	private long timeMillis;
	private int framesPerSecond;
	private int ticksPerSecond;
	private int tickTime;
	private int frameTime;
	
	public void run() {
		
		// Redirect run folder (since all resources are located in the test folder)
		ResourceLoader.redirectRuntimeFolder(EngineExample.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/assets/"));
		
		// Setup resource loaders
		resourceLoader = new ResourceLoader<>();
		shaderLoader = new ShaderLoader<ResourceLocation, ResourceFolders>(ResourceFolders.SHADERS, resourceLoader);
		textureLoader = new TextureLoader<ResourceLocation, ResourceFolders>(ResourceFolders.TEXTURES, resourceLoader);
		
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
		textureLoader.clearCached();
		
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
		shaderLoader.loadShadersIn(WORLD_SHADER_LOCATION, 10);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS, false, false, 10, false);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS_INTERPOLATED, false, true, 10, false);

		windowResized(new Vec2i(this.mainWindow.getSize().x, this.mainWindow.getSize().x));
		this.mainWindow.registerWindowListener((windowResize, type) -> { if (windowResize.isPresent()) windowResized(new Vec2i(windowResize.get())); });
		
		// Setup world
		physicWorld = new Space3D();
		
		Random r = new Random();
		int spread = 0;
		int spread2 = 1;
		
		for (int i = 0; i < 30; i++) {

			Vec3d pos = new Vec3d((r.nextFloat() - 0.5F) * spread, (r.nextFloat() - 0.5F) * spread, (r.nextFloat() - 0.5F) * spread);
			Vec3d velocity = new Vec3d((r.nextFloat() - 0.5F) * spread2, (r.nextFloat() - 0.5F) * spread2, (r.nextFloat() - 0.5F) * spread2);
			ParticleType type = r.nextBoolean() ? ParticleType.PROTON : ParticleType.NEUTRON; //ParticleType.values()[r.nextInt(3)];
			
			physicWorld.getParticles().add(type.create(pos, velocity));
			
		}
		
	}
	
	public void windowResized(Vec2i screenSize) {
		GLStateManager.resizeViewport(0, 0, screenSize.x, screenSize.y);
		this.projectionMatrix = Matrix4f.perspective(50, screenSize.x / (float) Math.max(1, screenSize.y), 1F, 100F);
	}
	
	VertexBuffer particleDrawBuffer = new VertexBuffer();
	BufferBuilder particleBuffer = new BufferBuilder(36000);
	
	int fbt = 0;
	
	private void frame(float partialTick) {

		Framebuffer framebuffer = null;
		if (fbt == 100) {
			framebuffer = new Framebuffer(1000, 600);
			framebuffer.bind();
		}
		
		GLStateManager.clearColor(1, 0.5F, 0.25F, 0.1F);
		GLStateManager.clear();
		
		ShaderInstance shader = shaderLoader.getShader(new ResourceLocation(NAMESPACE, "world/particle"));
		
		Matrix4f viewMatrix = mainCamera.getViewMatrix();
		//ITextureSampler texture = textureLoader.getTextureMap(OBJECT_TEXTURE_ATLAS);
		
		shader.useShader();
		shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
		shader.getUniform("ViewMat").setMatrix4f(viewMatrix);
		shader.getUniform("HalfVoxelSize").setFloat(0.001F);
		//shader.getUniform("Texture").setTextureSampler(texture);
		
		GLStateManager.enable(GL33.GL_DEPTH_TEST);
		GLStateManager.enable(GL33.GL_BLEND);
		GLStateManager.enable(GL33.GL_CULL_FACE);
		
		particleBuffer.begin(RenderPrimitive.POINTS, objectFormat);
		
		for (Particle particle : this.physicWorld.getParticles()) {
			
			particleBuffer.vertex((float) particle.getPosition().x, (float) particle.getPosition().y, (float) particle.getPosition().z);
			particleBuffer.color(particle.getColor().x, particle.getColor().y, particle.getColor().z, particle.getColor().w);
			particleBuffer.nextElement().putFloat(particle.getSize());
			particleBuffer.endVertex();
			
		}
		
		particleBuffer.end();
		
		particleDrawBuffer.upload(particleBuffer, BufferUsage.DYNAMIC);
		particleDrawBuffer.bind();
		particleDrawBuffer.drawAll(RenderPrimitive.POINTS);
		particleDrawBuffer.unbind();
		
		if (fbt == 100) {

			framebuffer.unbind();
			
			try {
				Texture texture = framebuffer.getColorTexture();
				int[] pixels = texture.download(TextureDataFormat.INT_RGBA_8_8_8_8);
				
				BufferedImage image = new BufferedImage(texture.getTexWidth(), texture.getTexHeight(), BufferedImage.TYPE_INT_ARGB);
				image.setRGB(0, 0, texture.getTexWidth(), texture.getTexHeight(), pixels, 0, texture.getTexWidth());
				ImageIO.write(image, "PNG", new File("C:\\Users\\marvi\\Desktop\\test.png"));
				
				Texture texture2 = framebuffer.getDepthTexture();
				int[] pixels2 = texture2.download(TextureDataFormat.FLOAT_DEPTH);

				BufferedImage image2 = new BufferedImage(texture2.getTexWidth(), texture2.getTexHeight(), BufferedImage.TYPE_INT_ARGB);
				image2.setRGB(0, 0, texture2.getTexWidth(), texture2.getTexHeight(), pixels2, 0, texture2.getTexWidth());
				ImageIO.write(image2, "PNG", new File("C:\\Users\\marvi\\Desktop\\test2.png"));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			framebuffer.discard();
			
		} else {

			mainWindow.glSwapFrames();
			
		}

		fbt++;
		
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
		
		if (inputHandler.isBindingActive("spawn_object")) {
			
//			WorldObject object = new Random().nextBoolean() ? new TestBlockObject() : new Random().nextBoolean() ? new MotorObject() : new KorbuvaObject();
//			physicWorld.addObject(object);
//			object.getRigidBody().setOrientation(new Quaternionf(new Vec3i(1, 0, 0), 0));
//			object.getRigidBody().setPosition(this.mainCamera.getPosition());
			
		}
		
		mainCamera.upadteViewMatrix();
		
	}
	
	protected void runPhysics() {
		
		double lastTime = 1;
		while (!this.mainWindow.shouldClose()) {
			long timeStart = System.currentTimeMillis();
			physicWorld.stepPhysic(lastTime / 1000);
			lastTime = System.currentTimeMillis() - timeStart;
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
