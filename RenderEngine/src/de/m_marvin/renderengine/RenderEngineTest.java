package de.m_marvin.renderengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import de.m_marvin.renderengine.buffers.ParalelBufferBuilder;
import de.m_marvin.renderengine.buffers.SerialBufferBuilder;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.inputbinding.InputBindings;
import de.m_marvin.renderengine.inputbinding.KeySource;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.ITextureSampler;
import de.m_marvin.renderengine.textures.SingleTexture;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec3f;

public class RenderEngineTest {
	
	/*
	 * TODO List
	 * - Animated Textures, Atlases, Animated Atlases
	 * - ParalelBufferBuilder fix + Selective VAO upload
	 * - Possibility to select usage of VAO on uploading
	 * - Input-Methods for Mouse-Wheel and Movement
	 * - Multi-Window handling/Window class
	 */
	
	public static void main(String... args) {
		try {
			new RenderEngineTest().start();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static class Window {
		
		protected int width;
		protected int height;
		protected long glWindow;
		protected GLCapabilities glCapabilities;
		
		public Window(int width, int height, String title) {
			this.width = width;
			this.height = height;
			this.glWindow = GLFW.glfwCreateWindow(width, height, title, 0, 0);
			GLFW.glfwMakeContextCurrent(glWindow);
			GLFW.glfwSwapInterval(1);
			this.glCapabilities = GL.createCapabilities();
			GL11.glClearColor(1, 1, 1, 1);
		}
		
		public void makeContextCurrent() {
			GLFW.glfwMakeContextCurrent(glWindow);
			GL.setCapabilities(glCapabilities);
		}
		
		public void swapFrames() {
			GLFW.glfwSwapBuffers(glWindow);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
		}
		
		public void pollEvents() {
			GLFW.glfwPollEvents();
		}
		
		public boolean shouldClose() {
			return GLFW.glfwWindowShouldClose(glWindow);
		}
		
		public void destroy() {
			GLFW.glfwDestroyWindow(glWindow);
		}
		
		public void setVisible(boolean visible) {
			if (visible) {
				GLFW.glfwShowWindow(glWindow);
			} else {
				GLFW.glfwHideWindow(glWindow);
			}
		}
		
	}
	
	public void start() throws IOException {
		
		System.out.println("start");
		GLFW.glfwInit();
		GLFWErrorCallback.createPrint(System.err).set();
		
		Window window = new Window(1000, 600, "Test");
		//Window window2 = new Window(1000, 600, "Tes2t");
		Camera camera = new Camera(new Vec3f(0F, 0F, 10F), new Vec3f(0F, 0F, 0F));
		InputBindings input = new InputBindings();
		input.attachToWindow(window.glWindow);
		
		input.registerBinding("movement.forward").addBinding(KeySource.getKey(GLFW.GLFW_KEY_W));
		input.registerBinding("movement.backward").addBinding(KeySource.getKey(GLFW.GLFW_KEY_S));
		input.registerBinding("movement.leftside").addBinding(KeySource.getKey(GLFW.GLFW_KEY_A));
		input.registerBinding("movement.rightside").addBinding(KeySource.getKey(GLFW.GLFW_KEY_D));
		input.registerBinding("movement.rotateleft").addBinding(KeySource.getKey(GLFW.GLFW_KEY_LEFT));
		input.registerBinding("movement.rotateright").addBinding(KeySource.getKey(GLFW.GLFW_KEY_RIGHT));
		input.registerBinding("movement.rotateup").addBinding(KeySource.getKey(GLFW.GLFW_KEY_UP));
		input.registerBinding("movement.rotatedown").addBinding(KeySource.getKey(GLFW.GLFW_KEY_DOWN));
		
		VertexFormat format = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
		
		SerialBufferBuilder buffer = new SerialBufferBuilder(3200);
		
		ParalelBufferBuilder bufferParalel = new ParalelBufferBuilder(3200, 10);
		
		PoseStack poseStack = new PoseStack();
		
		poseStack.push();
		buffer.begin(RenderPrimitive.TRIANGLES, format);
		poseStack.translate(0, 6.5F, 0);
		poseStack.scale(2, 2, 1);
		buffer.vertex(poseStack, -1, -1, 0).normal(poseStack, 1, 0, 1).color(1, 0, 0, 1).uv(1, 1).endVertex();
		buffer.vertex(poseStack, 1, -1, 0).normal(poseStack, 0, 1, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, -1, 1, 0).normal(poseStack, 1, 1, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(poseStack, 1, -1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, -1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, 1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(0, 0).endVertex();
		buffer.index(0).index(1).index(2);//.index(3);
		buffer.index(3).index(4).index(5);//.index(3);
		buffer.end();
		poseStack.pop();
		
		poseStack.push();
		bufferParalel.begin(RenderPrimitive.TRIANGLES, format);
		poseStack.translate(0, 0F, 0);
		poseStack.scale(2, 2, 1);
		bufferParalel.vertex(poseStack, -1, -1, 0).normal(poseStack, 1, 0, 1).color(1, 0, 0, 1).uv(1, 1).endVertex();
		bufferParalel.vertex(poseStack, 1, -1, 0).normal(poseStack, 0, 1, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		bufferParalel.vertex(poseStack, -1, 1, 0).normal(poseStack, 1, 1, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		bufferParalel.vertex(poseStack, 1, -1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(0, 0).endVertex();
		bufferParalel.vertex(poseStack, -1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(1, 0).endVertex();
		bufferParalel.vertex(poseStack, 1, 1, -1).normal(poseStack, 0, 0, 1).color(1, 0, 1, 1).uv(0, 0).endVertex();
//		bufferParalel.index(0).index(1).index(2);//.index(3);
//		bufferParalel.index(3).index(4).index(5);//.index(3);
		bufferParalel.end();
		poseStack.pop();
		
		
//		VertexBuffer vertexBuffer = new VertexBuffer();
//		vertexBuffer.upload(buffer);
//		buffer.freeMemory();
		
		VertexBuffer vertexBuffer = new VertexBuffer();
		vertexBuffer.upload(bufferParalel);
		bufferParalel.freeMemory();
		
		File shaderFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "shaders/testShader.json");
		ShaderInstance shader = ShaderLoader.load(shaderFile, format);
		
		Matrix4f projectionMatrix = Matrix4f.perspective(65, 1000F / 600F, 1, 1000); //Matrix4f.orthographic(-100, 100, 100, -100, -10F, 10F);
		
		File textureFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "textures/test.png");
		ITextureSampler texture = new SingleTexture(new FileInputStream(textureFile));

		//window.makeContextCurrent();
		
		while (!window.shouldClose()) {
			
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
			
			vertexBuffer.bind();
			shader.useShaderAndFormat();
			shader.getUniform("ModelViewMat").setMatrix4f(viewMatrix);
			shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
			shader.getUniform("Texture").setTextureSampler(texture); // TODO
			
			vertexBuffer.drawAll(RenderPrimitive.TRIANGLES);
			shader.unbindShaderAndRestore();
			vertexBuffer.unbind();
			
			window.swapFrames();
			window.pollEvents();
			
		}
		
		vertexBuffer.discard();
		
		GLFW.glfwTerminate();
		System.out.println("exit");
		
	}
	
}
