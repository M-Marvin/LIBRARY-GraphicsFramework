package de.m_marvin.enginetest;

import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.particles.Particle;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.inputbinding.bindingsource.KeySource;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.simplelogging.printing.Logger;
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
		
		// Start new logger
		Logger.setDefaultLogger(new Logger());

		// Redirect run folder (since all resources are located in the test folder)
		ResourceLoader.redirectRuntimeFolder(EngineExample.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/assets/"));
		
		// Setup resource loaders
		resourceLoader = new ResourceLoader<>();
		shaderLoader = new ShaderLoader<ResourceLocation, ResourceFolders>(ResourceFolders.SHADERS, resourceLoader);
		textureLoader = new TextureLoader<ResourceLocation, ResourceFolders>(ResourceFolders.TEXTURES, resourceLoader);
		
		// Setup OpenGL and GLFW natives
		GLStateManager.initialize(System.err);
		
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
		GLStateManager.terminate();
		
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
		shaderLoader.loadShadersIn(WORLD_SHADER_LOCATION);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS, false, false);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS_INTERPOLATED, false, true);

		windowResized(new Vec2i(this.mainWindow.getSize()[0], this.mainWindow.getSize()[1]));
		this.mainWindow.registerWindowListener((windowResize, type) -> { if (windowResize.isPresent()) windowResized(windowResize.get()); });
		
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
		
//		spread = 10;
//		for (int i = 0; i < 30; i++) {
//
//			Vec3d pos = new Vec3d((r.nextFloat() - 0.5F) * spread, (r.nextFloat() - 0.5F) * spread, (r.nextFloat() - 0.5F) * spread);
//			Vec3d velocity = new Vec3d((r.nextFloat() - 0.5F) * spread2, (r.nextFloat() - 0.5F) * spread2, (r.nextFloat() - 0.5F) * spread2);
//			ParticleType type = ParticleType.ELECTRON;
//			
//			physicWorld.getParticles().add(type.create(pos, velocity));
//			
//		}
		
//		physicWorld.getParticles().add(new Particle(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), 4E13, -1, Particle.COLOR_CHARGE_WHITE.get(), Particle.WEAK_CHARGE_Z));
//		
//		physicWorld.getParticles().add(new Particle(new Vec3d(10, 0, 0), new Vec3d(0, 0, 3), 1000, +1, Particle.COLOR_CHARGE_WHITE.get(), Particle.WEAK_CHARGE_Z));
//
//		physicWorld.getParticles().add(new Particle(new Vec3d(40, 10, -5), new Vec3d(0, 2, 0), 4E13, +1, Particle.COLOR_CHARGE_WHITE.get(), Particle.WEAK_CHARGE_Z));
//		
//		physicWorld.getParticles().add(new Particle(new Vec3d(40, 10, -5), new Vec3d(1, 1, 1), 0.001, 0, Particle.COLOR_CHARGE_WHITE.get(), Particle.WEAK_CHARGE_Z));
//		
//		physicWorld.getParticles().add(new Particle(new Vec3d(10, 0, 20), new Vec3d(0, 0, -3), 99E10, 0, Particle.COLOR_CHARGE_WHITE.get(), Particle.WEAK_CHARGE_Z));
//		for (int i = 0; i < 30; i++) {
//
//			Vec3d pos = new Vec3d((r.nextFloat() - 0.5F) * spread, (r.nextFloat() - 0.5F) * spread, (r.nextFloat() - 0.5F) * spread);
//			Vec3d velocity = new Vec3d((r.nextFloat() - 0.5F) * spread2, (r.nextFloat() - 0.5F) * spread2, (r.nextFloat() - 0.5F) * spread2);
//			double mass = r.nextFloat() * 1000;
//			double charge = (r.nextFloat() > 0.3F) ? (r.nextDouble() - 0.5) * 0.0004 : 0;
//			
//			physicWorld.getParticles().add(new Particle(pos, velocity, mass, charge, Particle.COLOR_CHARGE_WHITE.get(), Particle.WEAK_CHARGE_Z));
//			
//		}
		
	}
	
	public void windowResized(Vec2i screenSize) {
		GLStateManager.resizeViewport(0, 0, screenSize.x, screenSize.y);
		this.projectionMatrix = Matrix4f.perspective(50, screenSize.x / (float) Math.max(1, screenSize.y), 1F, 100F);
		//this.uiContainer.screenResize(screenSize);
	}
	
	VertexBuffer particleDrawBuffer = new VertexBuffer();
	BufferBuilder particleBuffer = new BufferBuilder(36000);
	
	private void frame(float partialTick) {
		
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
		
		mainWindow.glSwapFrames();
		
	}
	
	private void tick() {
		
		mainWindow.pollEvents();
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
