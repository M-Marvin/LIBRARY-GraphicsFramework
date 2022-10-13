package de.m_marvin.renderengine;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderInstance.Uniform;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.vertecies.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;

public class RenderEngine {
	
	public static void main(String... args) {
		try {
			new RenderEngine().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			GL11.glClearColor(1, 0, 1, 1);
		}
		
		public void swapFrames() {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GLFW.glfwSwapBuffers(glWindow);
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
		
		VertexFormat format = new VertexFormat().appand("vertex", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, true).appand("uv", NumberFormat.FLOAT, 2, true);
		
		BufferBuilder buffer = new BufferBuilder(3200);
		
//		buffer.begin(RenderPrimitive.QUADS, format);
//		
//		buffer.vertex(-1, -1, 0).normal(0, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
//		buffer.vertex(1, -1, 0).normal(0, 0, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
//		buffer.vertex(-1, 1, 0).normal(0, 0, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
//		buffer.vertex(1, 1, 0).normal(0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
//				
//		buffer.end();
		

		buffer.begin(RenderPrimitive.TRIANGLES, format);
		
		buffer.vertex(-1, -1, 0).normal(0, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
		buffer.vertex(1, -1, 0).normal(0, 0, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		buffer.vertex(-1, 1, 0).normal(0, 0, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(1, 1, 0).normal(0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
		
		buffer.index(0).index(1).index(2).index(3);
		buffer.index(3).index(2).index(1).index(0);
		
		buffer.end();
		
		VertexBuffer vertexBuffer = new VertexBuffer();
		vertexBuffer.upload(buffer);
		
		File shaderFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "shaders/testShader.json");
		ShaderInstance shader = ShaderLoader.load(shaderFile, format);
		
		vertexBuffer.discard();
		
		while (!window.shouldClose()) {
			
			shader.useShader();
			
			vertexBuffer.bind();
			GLStateManager.drawElements(RenderPrimitive.TRIANGLES.getGlType(), 8, vertexBuffer.indecieFormat());
			vertexBuffer.unbind();
			
			window.swapFrames();
			window.pollEvents();
		}

		buffer.freeMemory();
		
		GLFW.glfwTerminate();
		System.out.println("exit");
		
	}
	
}
