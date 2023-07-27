package de.m_marvin.uitest;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.ResourceFolders;
import de.m_marvin.enginetest.world.objects.GroundPlateObject;
import de.m_marvin.enginetest.world.objects.KorbuvaObject;
import de.m_marvin.enginetest.world.objects.MotorObject;
import de.m_marvin.enginetest.world.objects.TestBlockObject;
import de.m_marvin.enginetest.world.objects.WorldObject;
import de.m_marvin.openui.UIContainer;
import de.m_marvin.openui.components.ButtonComponent;
import de.m_marvin.openui.components.Compound;
import de.m_marvin.openui.components.GroupBox;
import de.m_marvin.openui.layout.BorderLayout;
import de.m_marvin.openui.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.layout.BorderLayout.CornerStretch;
import de.m_marvin.physicengine.d3.physic.RigidPhysicWorld;
import de.m_marvin.physicengine.d3.util.BroadphaseAlgorithm;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.inputbinding.bindingsource.KeySource;
import de.m_marvin.renderengine.models.OBJLoader;
import de.m_marvin.renderengine.models.RawModel;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.textures.ITextureSampler;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.VoxelEngine;

public class UITest {

	public static void main(String... args) {
		new UITest().run();
	}
	
	private static UITest instance;
	private UITest() { instance = this; }
	
	public static UITest getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "example";
	
	public static final ResourceLocation OBJECT_MODEL_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS = new ResourceLocation(NAMESPACE, "object_atlas");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS_INTERPOLATED = new ResourceLocation(NAMESPACE, "object_atlas_interpolated");
	public static final ResourceLocation SHADER_LIB_LOCATION = new ResourceLocation(NAMESPACE, "glsl");
	public static final ResourceLocation WORLD_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "world");
	public static final ResourceLocation OPENUI_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "openui");
		
	private ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	private ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	private TextureLoader<ResourceLocation, ResourceFolders> textureLoader;
	private OBJLoader<ResourceLocation, ResourceFolders> modelLoader;
	private UserInput inputHandler;
	
	protected Camera mainCamera;
	protected Matrix4f projectionMatrix;
	
	protected RigidPhysicWorld<WorldObject> physicWorld;
	protected Map<ResourceLocation, VertexBuffer> name2vertexMap = new HashMap<>();
	protected final VertexFormat objectFormat = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
	
	private Window mainWindow;
	private long timeMillis;
	private int framesPerSecond;
	private int ticksPerSecond;
	private int tickTime;
	private int frameTime;
	
	protected UIContainer<ResourceLocation> uiContainer;
	
	public void run() {
		
		// Start new logger
		Logger.setDefaultLogger(new Logger());

		// Redirect run folder (since all resources are located in the test folder)
		ResourceLoader.redirectRuntimeFolder(VoxelEngine.class.getClassLoader().getResource("").getPath().replace("bin/main/", "run/"));
		
		// Setup resource loaders
		resourceLoader = new ResourceLoader<>();
		shaderLoader = new ShaderLoader<ResourceLocation, ResourceFolders>(ResourceFolders.SHADERS, resourceLoader);
		textureLoader = new TextureLoader<ResourceLocation, ResourceFolders>(ResourceFolders.TEXTURES, resourceLoader);
		modelLoader = new OBJLoader<ResourceLocation, ResourceFolders>(ResourceFolders.MODELS, resourceLoader);
		
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
		modelLoader.clearCached();
		
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
		shaderLoader.loadShadersIn(WORLD_SHADER_LOCATION, SHADER_LIB_LOCATION);
		shaderLoader.loadShadersIn(OPENUI_SHADER_LOCATION, SHADER_LIB_LOCATION);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS, false, false);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS_INTERPOLATED, false, true);
		modelLoader.loadModelsIn(OBJECT_MODEL_LOCATION, OBJECT_TEXTURE_LOCATION);
		
		// Setup world
		physicWorld = new RigidPhysicWorld<WorldObject>(new Vec3f(-1000F, -1000F, -1000F), new Vec3f(1000F, 1000F, 1000F), BroadphaseAlgorithm.SIMPLE);
		physicWorld.setGravity(new Vec3f(0F, -9.8F, 0F));
		
		// Compile models to VAO
		modelLoader.getCachedModels().forEach((modelName) -> compileModel(modelName));
		
		// Create ground plate
		WorldObject plate = new GroundPlateObject();
		physicWorld.addObject(plate);
		plate.getRigidBody().setOrientation(new Quaternion(new Vec3i(1, 0, 0), 0));
		plate.getRigidBody().setPosition(new Vec3f(0F, -1F, 0F));
		
		this.uiContainer = new UIContainer<>();
		this.uiContainer.getRootCompound().setLayout(new BorderLayout(CornerStretch.VERTICAL));
		
		Compound<ResourceLocation> b = new GroupBox<ResourceLocation>();
		b.setLayout(new BorderLayout());
		b.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.LEFT));
		
		ButtonComponent<ResourceLocation> bb1 = new ButtonComponent<>(Color.WHITE);
		bb1.setSizeMin(new Vec2i(90, 25));
		bb1.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		b.addComponent(bb1);
		
		ButtonComponent<ResourceLocation> b1 = new ButtonComponent<ResourceLocation>(Color.RED);
		b1.setSizeMin(new Vec2i(100, 100));
		b1.setSize(new Vec2i(100, 100));
		b1.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		
		ButtonComponent<ResourceLocation> b2 = new ButtonComponent<ResourceLocation>(Color.GREEN);
		b2.setSizeMin(new Vec2i(100, 100));
		b2.setSize(new Vec2i(100, 100));
		b2.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.RIGHT));
		
		ButtonComponent<ResourceLocation> b3 = new ButtonComponent<ResourceLocation>(Color.BLUE);
		b3.setSizeMin(new Vec2i(100, 100));
		b3.setSize(new Vec2i(100, 100));
		b3.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.BOTTOM));

		ButtonComponent<ResourceLocation> b4 = new ButtonComponent<ResourceLocation>(Color.CYAN);
		b4.setSizeMin(new Vec2i(100, 100));
		b4.setSize(new Vec2i(100, 100));
		
		this.uiContainer.getRootCompound().addComponent(b);
		this.uiContainer.getRootCompound().addComponent(b1);
		this.uiContainer.getRootCompound().addComponent(b2);
		this.uiContainer.getRootCompound().addComponent(b3);
		this.uiContainer.getRootCompound().addComponent(b4);
		
		windowResized(new Vec2i(this.mainWindow.getSize()[0], this.mainWindow.getSize()[1]));
		this.mainWindow.registerWindowListener((shouldClose, windowResize, focused, unfocused, maximized, restored) -> { if (windowResize.isPresent()) windowResized(windowResize.get()); });
		
	}
	 
	public void windowResized(Vec2i screenSize) {
		GLStateManager.resizeViewport(0, 0, screenSize.x, screenSize.y);
		this.projectionMatrix = Matrix4f.perspective(50, screenSize.x / screenSize.y, 1F, screenSize.x);
		this.uiContainer.screenResize(screenSize);
	}
	
	public void compileModel(ResourceLocation name) {
		
		// Get loaded model
		RawModel<ResourceLocation> model = modelLoader.getModel(name);
		
		// Create buffer builder
		BufferBuilder bufferBuilder = new BufferBuilder(32000);
		
		// Draw model to buffer
		bufferBuilder.begin(RenderPrimitive.QUADS, objectFormat);
		
		AbstractTextureMap<ResourceLocation> textureAtlas = textureLoader.getTextureMap(OBJECT_TEXTURE_ATLAS);
		model.drawModelToBuffer((texture, vertex, normal, uv) -> {
			textureAtlas.activateTexture(texture);
			bufferBuilder.vertex(vertex.x, vertex.y, vertex.z).normal(normal.x, normal.y, normal.z).color(1, 1, 1, 1).uv(textureAtlas, uv.x, uv.y).endVertex();
		});
		
		bufferBuilder.end();
		
		// Upload to VAO
		VertexBuffer objectModel = new VertexBuffer();
		objectModel.upload(bufferBuilder, BufferUsage.STATIC);
		
		// Free buffer builder
		bufferBuilder.freeMemory();
		
		// Store in map
		name2vertexMap.put(name, objectModel);
		
	}
	
	private void frame(float partialTick) {
		
		this.uiContainer.updateOutdatedVAOs();
		this.uiContainer.renderVAOs(shaderLoader, textureLoader);
		
		ShaderInstance shader = shaderLoader.getShader(new ResourceLocation("example:world/solid"));
		
		Matrix4f viewMatrix = mainCamera.getViewMatrix();
		ITextureSampler texture = textureLoader.getTextureMap(OBJECT_TEXTURE_ATLAS);
		
		shader.useShader();
		shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
		shader.getUniform("ModelViewMat").setMatrix4f(viewMatrix);
		shader.getUniform("Texture").setTextureSampler(texture);
		
		GLStateManager.enable(GL33.GL_DEPTH_TEST);
		GLStateManager.enable(GL33.GL_BLEND);
		GLStateManager.enable(GL33.GL_CULL_FACE);
		
		this.physicWorld.getObjectList().forEach((worldObject) -> {
			
			shader.getUniform("ObjectMat").setMatrix4f(worldObject.getModelTranslation());
			
			VertexBuffer objectModel = name2vertexMap.get(worldObject.getModel());
			objectModel.bind();
			objectModel.drawAll(RenderPrimitive.TRIANGLES);
			objectModel.unbind();
			
		});
		
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
			
			WorldObject object = new Random().nextBoolean() ? new TestBlockObject() : new Random().nextBoolean() ? new MotorObject() : new KorbuvaObject();
			physicWorld.addObject(object);
			object.getRigidBody().setOrientation(new Quaternion(new Vec3i(1, 0, 0), 0));
			object.getRigidBody().setPosition(this.mainCamera.getPosition());
			
		}
		
		mainCamera.upadteViewMatrix();

		physicWorld.stepPhysic(tickTime / 1000F, 0, 0);
		
		for (WorldObject object : physicWorld.getObjectList()) {
			if (object.getRigidBody().getPosition().y < -100) object.getRigidBody().setPosition(object.getRigidBody().getPosition().add(0F, 200F, 0F));
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
