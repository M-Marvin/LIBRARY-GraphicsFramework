package de.m_marvin.enginetest;

import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.world.ClientLevel;
import de.m_marvin.enginetest.world.objects.WorldObject;
import de.m_marvin.physicengine.d3.BroadphaseAlgorithm;
import de.m_marvin.physicengine.d3.RigidPhysicSolver;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.models.OBJLoader;
import de.m_marvin.renderengine.models.RawModel;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec3f;

public class EngineTest {

	public static void main(String... args) {
		new EngineTest().run();
	}
	
	private static EngineTest instance;
	private EngineTest() { instance = this; }
	
	public static EngineTest getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "example";
	
	public static final ResourceLocation OBJECT_MODEL_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS = new ResourceLocation(NAMESPACE, "object_atlas");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS_INTERPOLATED = new ResourceLocation(NAMESPACE, "object_atlas_interpolated");
	public static final ResourceLocation SHADER_LIB_LOCATION = new ResourceLocation(NAMESPACE, "glsl");
	public static final ResourceLocation WORLD_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "world");
		
	private ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	private ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	private TextureLoader<ResourceLocation, ResourceFolders> textureLoader;
	private OBJLoader<ResourceLocation, ResourceFolders> modelLoader;
	
	private Camera mainCamera;
	private RigidPhysicSolver<WorldObject> physicWorld;
	
	private Window mainWindow;
	private long timeMillis;
	private int framesPerSecond;
	private int ticksPerSecond;
	private int tickTime;
	private int frameTime;
	
	public void run() {
		
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
		mainCamera = new Camera();
		
		// Load shader, textures and models
		shaderLoader.loadShadersIn(WORLD_SHADER_LOCATION, SHADER_LIB_LOCATION);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS, false, false);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS_INTERPOLATED, false, true);
		modelLoader.loadModelsIn(OBJECT_MODEL_LOCATION, OBJECT_TEXTURE_LOCATION);
		
		physicWorld = new RigidPhysicSolver<WorldObject>(new Vec3f(-1000F, -1000F, -1000F), new Vec3f(1000F, 1000F, 1000F), BroadphaseAlgorithm.SIMPLE);
		
		// Setup and start game loop
		tickTime = 20; // 50 TPS
		frameTime = 16; // ~60 FPS
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
		
		compileModel();
		
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
	
	protected final VertexFormat objectFormat = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
	protected VertexBuffer objectModel;
	
	public void compileModel() {
		
		RawModel<ResourceLocation> model = modelLoader.getModel(new ResourceLocation("example:objects/motor_item"));
		
		BufferBuilder bufferBuilder = new BufferBuilder(32000);
		
		bufferBuilder.begin(RenderPrimitive.QUADS, objectFormat);
		
		model.drawModelToBuffer((s, vertex, normal, uv) -> {
			bufferBuilder.vertex(vertex.x, vertex.y, vertex.z).normal(normal.x, normal.y, normal.z).color(1, 1, 1, 1).uv(uv.x, uv.y).endVertex();
		});
		
		bufferBuilder.end();
		
		objectModel = new VertexBuffer();
		objectModel.upload(bufferBuilder, BufferUsage.STATIC);
		
		bufferBuilder.freeMemory();
		
	}
	
	protected Matrix4f projectionMatrix = Matrix4f.perspective(50, 1000 / 600, 0.1F, 100F);
	
	private void frame(float partialTick) {
		
		ShaderInstance shader = shaderLoader.getShader(new ResourceLocation("example:world/testShader"));
		
		shader.useShader();
		//shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
		
		objectModel.bind();
		objectModel.drawAll(RenderPrimitive.QUADS);
		objectModel.unbind();
		
		mainWindow.glSwapFrames();
		
	}
	
	private void tick() {
		
		//System.out.println(millisSinceLastFrame);
		
		mainWindow.pollEvents();
		
		physicWorld.stepPhysic(tickTime, tickTime * 2, 1);
		
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
