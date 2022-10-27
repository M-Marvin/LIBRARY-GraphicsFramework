package de.m_marvin.renderengine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.inputbinding.bindingsource.KeySource;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.SingleTextureMap;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder.AtlasFrameLayout;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder.AtlasMultiFrameLayout;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader.TextureMetaData;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.renderengine.windows.Window;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec3f;

public class RenderEngineTest {
	
	/*
	 * TODO List
	 * - TextureLoader (automatic atlas building for all textures)
	 */
	
	public static void main(String... args) {
		try {
			new RenderEngineTest().test();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static SingleTextureMap texture;
	public static long currentTickTime;
	
	public void test() throws IOException {

		ResourceLoader<ResourceLocation, TestSourceFolders> resourceLoader = new ResourceLoader<>((loaction) -> loaction.getNamespace() + "/" + loaction.getPath());
		ShaderLoader<ResourceLocation, TestSourceFolders> shaderLoader = new ShaderLoader<ResourceLocation, TestSourceFolders>(TestSourceFolders.SHADERS, resourceLoader);
		TextureLoader<ResourceLocation, TestSourceFolders> textureLoader = new TextureLoader<ResourceLocation, TestSourceFolders>(TestSourceFolders.TEXTURES, resourceLoader);
		
		
		
		File textureFolder = resourceLoader.getResourceFolderPath(TestSourceFolders.TEXTURES); //new File(this.getClass().getClassLoader().getResource("").getPath(), "textures/");
		
		ResourceLocation textureLoc = new ResourceLocation("test", "dust_block");
		
		File texturePath2 = resourceLoader.resolveLocation(TestSourceFolders.TEXTURES, textureLoc);
		System.out.println(texturePath2);
		
		MultiFrameAtlasLayoutBuilder<int[]> builder = new MultiFrameAtlasLayoutBuilder<>();
		
		for (String textureFile : textureFolder.list()) {
			
			if (textureFile.endsWith(".png")) {

				File metaFile = new File(textureFolder, textureFile.replace(".png", ".json"));
				File texturePath = new File(textureFolder, textureFile);
				
				BufferedImage image = ImageIO.read(texturePath);
				TextureMetaData data = metaFile.exists() ? TextureLoader.loadJsonMetaData(new FileInputStream(metaFile)) : TextureLoader.DEFAULT_META_DATA;
				
				System.out.println(data);
				
				int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
				int[] frames = data.frames();
				int frametime = data.frametime();
				
				builder.addAtlasImage(image.getWidth(), image.getHeight(), frames, frametime, data.interpolate(), pixels);
				
			}
			
		}
		
		AtlasMultiFrameLayout<int[]> layout = builder.buildLayout(false);
		
		System.out.println("Create atlas with size " + layout.width() + " " + layout.height());
		BufferedImage atlasImage = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_4BYTE_ABGR);
		
		for (List<AtlasFrameLayout<int[]>> frameLayout : layout.frameLayouts()) {
			for (AtlasFrameLayout<int[]> imageLayout : frameLayout) {
				
				int pixels[] = framePixels(imageLayout.image(), imageLayout.frame(), imageLayout.frameHeight(), imageLayout.width());
				
				if (imageLayout.interpolate()) {
					
					int[] nextPixels = framePixels(imageLayout.image(), imageLayout.nextFrame(), imageLayout.frameHeight(), imageLayout.width());
					
					pixels = interpolatePixels(pixels, nextPixels, imageLayout.subframe());
					
				}
				
				atlasImage.setRGB(imageLayout.x(), imageLayout.y(), imageLayout.width(), imageLayout.frameHeight(), pixels, 0, imageLayout.width());
				
			}
		}
		
		File outputFile = new File(textureFolder.getParentFile(), "/atlas.png");
		System.out.println("Write file to " + outputFile);
		FileOutputStream s = new FileOutputStream(outputFile);
		ImageIO.write(atlasImage, "png", s);
		s.close();
		
	}
	
	public int[] interpolatePixels(int[] pixels1, int[] pixels2, float interpolation) {
		int[] pixels = new int[pixels1.length];
		for (int i = 0; i < pixels1.length; i++) {
			pixels[i] = (int) (pixels1[i] * (1F - interpolation) + pixels2[i] * interpolation); // TODO: Incorrect interpolation, only for testing!
		}
		return pixels;
	}
	
	public int[] framePixels(int[] pixels, int frame, int frameHeight, int width) {
		
		int begin = frame * frameHeight * width;
		int end = begin + frameHeight * width;
		return Arrays.copyOfRange(pixels, begin, end);
		
	}
	
	public void start() throws IOException {
		
		System.out.println("start");
		
		GLStateManager.initialisate(System.err);
		
		Window window2 = new Window(1000, 600, "Test");
		window2.makeContextCurrent();
		Camera camera = new Camera(new Vec3f(0F, 0F, 10F), new Vec3f(0F, 0F, 0F));
		UserInput input = new UserInput();
		input.attachToWindow(window2.windowId());
		
		input.registerBinding("movement.forward").addBinding(KeySource.getKey(GLFW.GLFW_KEY_W));
		input.registerBinding("movement.backward").addBinding(KeySource.getKey(GLFW.GLFW_KEY_S));
		input.registerBinding("movement.leftside").addBinding(KeySource.getKey(GLFW.GLFW_KEY_A));
		input.registerBinding("movement.rightside").addBinding(KeySource.getKey(GLFW.GLFW_KEY_D));
		input.registerBinding("movement.rotateleft").addBinding(KeySource.getKey(GLFW.GLFW_KEY_LEFT));
		input.registerBinding("movement.rotateright").addBinding(KeySource.getKey(GLFW.GLFW_KEY_RIGHT));
		input.registerBinding("movement.rotateup").addBinding(KeySource.getKey(GLFW.GLFW_KEY_UP));
		input.registerBinding("movement.rotatedown").addBinding(KeySource.getKey(GLFW.GLFW_KEY_DOWN));
		
		VertexFormat format = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
		
		BufferBuilder buffer = new BufferBuilder(3200);
		
		PoseStack poseStack = new PoseStack();
		
		poseStack.push();
		buffer.begin(RenderPrimitive.TRIANGLES, format);
		poseStack.translate(0, 0.5F, 0);
		poseStack.scale(2, 2, 1);
		buffer.vertex(poseStack, -1, -1, 0).normal(poseStack, 1, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, 1, -1, 0).normal(poseStack, 0, 1, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, -1, 1, 0).normal(poseStack, 1, 1, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(poseStack, 1, -1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, -1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(poseStack, 1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(1, 1).endVertex();
		buffer.index(0).index(1).index(2);//.index(3);
		buffer.index(3).index(4).index(5);//.index(3);
		buffer.end();
		poseStack.pop();

		poseStack.push();
		buffer.begin(RenderPrimitive.TRIANGLES, format);
		poseStack.translate(0, 6.5F, 0);
		poseStack.scale(2, 2, 1);
		buffer.vertex(poseStack, -1, -1, 0).normal(poseStack, 1, 0, 1).color(1, 1, 0, 1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, 1, -1, 0).normal(poseStack, 0, 1, 1).color(0, 1, 1, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, -1, 1, 0).normal(poseStack, 1, 1, 1).color(0, 1, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(poseStack, 1, -1, -1).normal(poseStack, 0, 0, 1).color(1, 1, 1, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, -1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 1, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(poseStack, 1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
		buffer.index(0).index(1).index(2);//.index(3);
		buffer.index(3).index(4).index(5);//.index(3);
		buffer.end();
		poseStack.pop();

		VertexBuffer vertexBuffer = new VertexBuffer();
		vertexBuffer.upload(buffer, BufferUsage.STATIC);
		
		VertexBuffer vertexBuffer2 = new VertexBuffer();
		vertexBuffer2.upload(buffer, BufferUsage.STATIC);
		buffer.freeMemory();
		
		File shaderFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "shaders/testShader.json");
		ShaderInstance shader = ShaderLoader.load(shaderFile, format);
		
		Matrix4f projectionMatrix = Matrix4f.perspective(65, 1000F / 600F, 1, 1000); //Matrix4f.orthographic(-100, 100, 100, -100, -10F, 10F);
		
		File textureFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "textures/testA.png");
		texture = new SingleTextureMap(new FileInputStream(textureFile), new int[] {0, 1, 2, 3}, true);
		
		window2.registerWindowListener((shouldClose, resized, focused, unfocused, maximized, restored) -> {
			if (resized.isPresent()) GLStateManager.resizeViewport(0, 0, resized.get().x(), resized.get().y());
		});
		
		GLStateManager.clearColor(1, 1, 1, 0.5F);
		GLStateManager.enable(GL33.GL_DEPTH_TEST);
		GLStateManager.enable(GL33.GL_BLEND);
		GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		
		//input.addTextInputListener((character, functionalKey) -> System.out.println(character + " " + functionalKey));
		
		Thread testThread = new Thread(() -> {
						
			while (texture != null) {
				
				texture.nextFrame();
				currentTickTime = System.currentTimeMillis();
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}, "TestThread");
		testThread.start();
		
		while (!window2.shouldClose()) {
			
			long currentTime = System.currentTimeMillis();
			float partialTick = (currentTickTime + 3000 - currentTime) / 3000F;
			
			Vec3f motion = new Vec3f(0F, 0F, 0F);
			float motionSensitivity = 0.2F;
			if (input.isBindingActive("movement.forward")) motion.z = -motionSensitivity;
			if (input.isBindingActive("movement.backward")) motion.z = +motionSensitivity;
			if (input.isBindingActive("movement.leftside")) motion.x = -motionSensitivity;
			if (input.isBindingActive("movement.rightside")) motion.x = +motionSensitivity;
			camera.move(motion);
			
			Vec3f rotation = new Vec3f(0F, 0F, 0F);
			float rotationSensitivity = 2F;
			if (input.isBindingActive("movement.rotateleft")) rotation.y = +rotationSensitivity;
			if (input.isBindingActive("movement.rotateright")) rotation.y = -rotationSensitivity;
//			if (input.isBindingActive("movement.rotateup")) rotation.x = -rotationSensitivity;
//			if (input.isBindingActive("movement.rotatedown")) rotation.x = +rotationSensitivity;
			camera.rotate(rotation);
			
			camera.upadteViewMatrix();
			Matrix4f viewMatrix = camera.getViewMatrix();
			
			shader.useShader();
			shader.getUniform("ModelViewMat").setMatrix4f(viewMatrix);
			shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
			shader.getUniform("Texture").setTextureSampler(texture);
			shader.getUniform("AnimMat").setMatrix3f(texture.frameMatrix());
			shader.getUniform("AnimMatLast").setMatrix3f(texture.lastFrameMatrix());
			shader.getUniform("Interpolation").setFloat(partialTick);
			
			vertexBuffer.bind();
			vertexBuffer.drawAll(RenderPrimitive.TRIANGLES);
			vertexBuffer.unbind();

			vertexBuffer2.bind();
			vertexBuffer2.drawAll(RenderPrimitive.TRIANGLES);
			vertexBuffer2.unbind();
			
			shader.unbindShader();
			
			window2.glSwapFrames();
			window2.pollEvents();
			
		}
		
		vertexBuffer2.discard();
		
		texture = null;
		GLStateManager.terminate();
		
		System.out.println("exit");
		
	}
	
}
