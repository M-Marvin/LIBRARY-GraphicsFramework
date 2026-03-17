package de.m_marvin.enginetest;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.enginetest.physicengine.d3.physic.RigidPhysicWorld;
import de.m_marvin.enginetest.physicengine.d3.util.BroadphaseAlgorithm;
import de.m_marvin.enginetest.world.objects.GroundPlateObject;
import de.m_marvin.enginetest.world.objects.KorbuvaObject;
import de.m_marvin.enginetest.world.objects.MotorObject;
import de.m_marvin.enginetest.world.objects.TestBlockObject;
import de.m_marvin.enginetest.world.objects.WorldObject;
import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.gframe.GLStateManager;
import de.m_marvin.gframe.buffers.BufferBuilder;
import de.m_marvin.gframe.buffers.BufferUsage;
import de.m_marvin.gframe.buffers.VertexBuffer;
import de.m_marvin.gframe.framebuffers.Framebuffer;
import de.m_marvin.gframe.inputbinding.UserInput;
import de.m_marvin.gframe.inputbinding.bindingsource.KeySource;
import de.m_marvin.gframe.models.ModelData;
import de.m_marvin.gframe.models.ModelData.FragmentWriter;
import de.m_marvin.gframe.models.ModelData.ShaderData;
import de.m_marvin.gframe.models.ModelLoader;
import de.m_marvin.gframe.resources.ResourceLoader;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.gframe.shaders.ShaderInstance;
import de.m_marvin.gframe.shaders.ShaderLoader;
import de.m_marvin.gframe.textures.TextureLoader;
import de.m_marvin.gframe.textures.maps.AbstractTextureMap;
import de.m_marvin.gframe.textures.texture.ITextureSampler;
import de.m_marvin.gframe.textures.texture.Texture;
import de.m_marvin.gframe.textures.utility.TextureDataFormat;
import de.m_marvin.gframe.translation.Camera;
import de.m_marvin.gframe.utility.NumberFormat;
import de.m_marvin.gframe.vertices.RenderPrimitive;
import de.m_marvin.gframe.vertices.VertexFormat;
import de.m_marvin.gframe.windows.Window;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;

public class EngineExample {

	public static void main(String... args) {
		new EngineExample().run();
	}
	
	private static EngineExample instance;
	private EngineExample() { instance = this; }
	
	public static EngineExample getInstance() {
		return instance;
	}
	
	public static final String NAMESPACE = "example";
	
	public static final ResourceLocation OBJECT_MODEL_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_LOCATION = new ResourceLocation(NAMESPACE, "objects");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS = new ResourceLocation(NAMESPACE, "object_atlas");
	public static final ResourceLocation OBJECT_TEXTURE_ATLAS_INTERPOLATED = new ResourceLocation(NAMESPACE, "object_atlas_interpolated");
	public static final ResourceLocation WORLD_SHADER_LOCATION = new ResourceLocation(NAMESPACE, "world");
	
	private ResourceLoader<ResourceLocation, ResourceFolders> resourceLoader;
	private ShaderLoader<ResourceLocation, ResourceFolders> shaderLoader;
	private TextureLoader<ResourceLocation, ResourceFolders> textureLoader;
	private ModelLoader<ResourceLocation, ResourceFolders> modelLoader;
	private UserInput inputHandler;
	
	protected Camera mainCamera;
	protected Matrix4f projectionMatrix = Matrix4f.perspective((float) Math.toRadians(50), 1000F / 600F, 1F, 1000F);
	
	protected RigidPhysicWorld<WorldObject> physicWorld;
	protected Map<ResourceLocation, VertexBuffer> name2vertexMap = new HashMap<>();
	protected final VertexFormat objectFormat = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
	
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
		modelLoader = new ModelLoader<ResourceLocation, ResourceFolders>(ResourceFolders.MODELS, resourceLoader);
		
		// Setup OpenGL and GLFW natives
		GLFWStateManager.initialize(System.err);
		
		// Setup main window
		mainWindow = new Window(1000, 600, "Engine Test");
		mainWindow.makeContextCurrent();
		GLStateManager.clearColor(1, 0, 1, 1);
		GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		mainCamera = new Camera(new Vec3f(6F, 0F, 2F), new Vec3f(0F, 0F, 0F));		

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
		inputHandler.registerBinding("screenshot").addBinding(KeySource.getKey(GLFW.GLFW_KEY_F12));
		
		// Load shader, textures and models
		shaderLoader.loadShadersIn(WORLD_SHADER_LOCATION, 10);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS, false, false, 10, false);
		textureLoader.buildAtlasMapFromTextures(OBJECT_TEXTURE_LOCATION, OBJECT_TEXTURE_ATLAS_INTERPOLATED, false, true, 10, false);
		modelLoader.loadFilesIn(OBJECT_MODEL_LOCATION, 10);
		
		// Setup world
		physicWorld = new RigidPhysicWorld<WorldObject>(new Vec3f(-1000F, -1000F, -1000F), new Vec3f(1000F, 1000F, 1000F), BroadphaseAlgorithm.SIMPLE);
		physicWorld.setGravity(new Vec3f(0F, -9.8F, 0F));
		
		// Compile models to VAO
		modelLoader.getCachedItems().forEach((modelName) -> compileModel(modelName));
		
		// Create ground plate
		WorldObject plate = new GroundPlateObject();
		physicWorld.addObject(plate);
		plate.getRigidBody().setOrientation(new Quaternionf(new Vec3i(1, 0, 0), 0));
		plate.getRigidBody().setPosition(new Vec3f(0F, -1F, 0F));
		
	}
	
	public void compileModel(ResourceLocation name) {
		
		// Get loaded model
		ModelData<ResourceLocation> model = modelLoader.getItem(name);
		
		// Create buffer builder
		BufferBuilder bufferBuilder = new BufferBuilder(32000);
		
		// Draw model to buffer
		bufferBuilder.begin(RenderPrimitive.QUADS, objectFormat);
		AbstractTextureMap<ResourceLocation> textureAtlas = textureLoader.getTextureMap(OBJECT_TEXTURE_ATLAS);
		model.writeToBuffer(new FragmentWriter<ResourceLocation>() {
			
			@Override
			public void writeVertex(Vec3f vertex, Vec4f color, Vec3f normal, Vec3f tangent, Vec3f bitangent, Vec2f uv) {
				bufferBuilder.vertex(vertex.x, vertex.y, vertex.z).normal(normal.x, normal.y, normal.z).color(1, 1, 1, 1).uv(textureAtlas, uv.x, uv.y).endVertex();
			}
			
			@Override
			public void startFragment(ShaderData<ResourceLocation> shaderData) {
				textureAtlas.activateTexture(shaderData.getDiffuesMap());
			}
			
			@Override
			public void endFragment(int[] indecies) {
				
			}
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
	
	public void windowResized(Vec2i screenSize) {
		GLStateManager.resizeViewport(0, 0, screenSize.x, screenSize.y);
		this.projectionMatrix = Matrix4f.perspective((float) Math.toRadians(50), screenSize.x / (float) Math.max(1, screenSize.y), 1F, 100F);
	}
	
	private void frame(float partialTick) {
		
		boolean doScreenshot = inputHandler.isBindingTyped("screenshot");
		Framebuffer framebuffer = null;
		if (doScreenshot) {
			framebuffer = new Framebuffer(1000, 600);
			framebuffer.bind();
		}
		
		GLStateManager.clearColor(1, 0.5F, 0.25F, 0.1F);
		GLStateManager.clear();
		
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
		
		if (doScreenshot) {
			
			framebuffer.unbind();
			
			try {
				Texture screenshotTexture = framebuffer.getColorTexture();
				int[] screenshotPixels = screenshotTexture.download(TextureDataFormat.INT_RGBA_8_8_8_8);
				
				BufferedImage screenshotImage = new BufferedImage(screenshotTexture.getTexWidth(), screenshotTexture.getTexHeight(), BufferedImage.TYPE_INT_ARGB);
				screenshotImage.setRGB(0, 0, screenshotTexture.getTexWidth(), screenshotTexture.getTexHeight(), screenshotPixels, 0, screenshotTexture.getTexWidth());
				
				AffineTransform at = new AffineTransform();
				at.concatenate(AffineTransform.getScaleInstance(1, -1));
				at.concatenate(AffineTransform.getTranslateInstance(0, -screenshotImage.getHeight()));
				AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				BufferedImage image = new BufferedImage(screenshotImage.getWidth(), screenshotImage.getHeight(), screenshotImage.getType());
				
				op.filter(screenshotImage, image);
				ImageIO.write(image, "PNG", new File("C:\\Users\\marvi\\Desktop\\test.png"));
				
				Texture depthTexture = framebuffer.getDepthTexture();
				int[] depthPixels = depthTexture.download(TextureDataFormat.FLOAT_DEPTH);

				BufferedImage depthImage = new BufferedImage(depthTexture.getTexWidth(), depthTexture.getTexHeight(), BufferedImage.TYPE_INT_ARGB);
				depthImage.setRGB(0, 0, depthTexture.getTexWidth(), depthTexture.getTexHeight(), depthPixels, 0, depthTexture.getTexWidth());
				op.filter(depthImage, image);
				ImageIO.write(image, "PNG", new File("C:\\Users\\marvi\\Desktop\\test2.png"));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			framebuffer.discard();
			
		} else {

			mainWindow.glSwapFrames();
			
		}
		
		physicWorld.stepPhysic(tickTime / 1000F, 0, 0);
		
		for (WorldObject object : physicWorld.getObjectList()) {
			if (object.getRigidBody().getPosition().y < -100) object.getRigidBody().setPosition(object.getRigidBody().getPosition().add(0F, 200F, 0F));
		}
		
	}
	
	private void tick() {
		
		this.inputHandler.update();
		
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
		
		if (inputHandler.isBindingActive("spawn_object")) {
			
			WorldObject object = new Random().nextBoolean() ? new TestBlockObject() : new Random().nextBoolean() ? new MotorObject() : new KorbuvaObject();
			physicWorld.addObject(object);
			object.getRigidBody().setOrientation(new Quaternionf(new Vec3i(1, 0, 0), 0));
			object.getRigidBody().setPosition(this.mainCamera.getPosition());
			
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
	
	public ModelLoader<ResourceLocation, ResourceFolders> getModelLoader() {
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
