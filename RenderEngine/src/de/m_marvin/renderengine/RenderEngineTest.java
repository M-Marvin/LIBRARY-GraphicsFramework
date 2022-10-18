package de.m_marvin.renderengine;

import java.io.File;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.inputbinding.InputBindings;
import de.m_marvin.renderengine.inputbinding.KeySource;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.translation.Camera;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;

public class RenderEngineTest {
	
	public static void main(String... args) {
		try {
			new RenderEngineTest().start();
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
		Camera camera = new Camera();
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
		
		VertexFormat format = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false); //.appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
		
		BufferBuilder buffer = new BufferBuilder(3200);
		
//		buffer.begin(RenderPrimitive.QUADS, format);
//		
//		buffer.vertex(-1, -1, 0).normal(0, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
//		buffer.vertex(1, -1, 0).normal(0, 0, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
//		buffer.vertex(-1, 1, 0).normal(0, 0, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
//		buffer.vertex(1, 1, 0).normal(0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
//				
//		buffer.end();
		
		
		PoseStack poseStack = new PoseStack();
		
		poseStack.push();
		poseStack.translate(0, 0, -100);
		
		buffer.begin(RenderPrimitive.TRIANGLES, format);
		
		buffer.vertex(poseStack, -100, -100, 0).endVertex(); //.normal(poseStack, 0, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, 100, -100, -200).endVertex(); //.normal(poseStack, 0, 0, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		buffer.vertex(poseStack, -100, 100, 0).endVertex(); //.normal(poseStack, 0, 0, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(poseStack, 100, 100, 200).endVertex(); //.normal(poseStack, 0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
		
//		buffer.index(0).index(1).index(2).index(3);
//		buffer.index(3).index(2).index(1).index(0);
		
		buffer.end();
		
		poseStack.pop();
		
		VertexBuffer vertexBuffer = new VertexBuffer();
		vertexBuffer.upload(buffer);
		
		File shaderFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "shaders/testShader.json");
		ShaderInstance shader = ShaderLoader.load(shaderFile, format);
		
		vertexBuffer.discard();
		
		Matrix4f projectionMatrix = Matrix4f.perspective(60, 1000 / 600, 1000, 0.1F); //Matrix4f.orthographic(-100, 100, 100, -100, 0F, 100F);
		
		while (!window.shouldClose()) {
			
			Vec3f motion = new Vec3f(0F, 0F, 0F);
			float motionSensitivity = 0.2F;
			if (input.isBindingActive("movement.forward")) motion.z = +motionSensitivity;
			if (input.isBindingActive("movement.backward")) motion.z = -motionSensitivity;
			if (input.isBindingActive("movement.leftside")) motion.x = -motionSensitivity;
			if (input.isBindingActive("movement.rightside")) motion.x = +motionSensitivity;
			camera.move(motion);
			
			Vec3f rotation = new Vec3f(0F, 0F, 0F);
			float rotationSensitivity = 2F;
			if (input.isBindingActive("movement.rotateleft")) rotation.y = -rotationSensitivity;
			if (input.isBindingActive("movement.rotateright")) rotation.y = +rotationSensitivity;
//			if (input.isBindingActive("movement.rotateup")) rotation.x = -rotationSensitivity;
//			if (input.isBindingActive("movement.rotatedown")) rotation.x = +rotationSensitivity;
			camera.rotate(rotation);
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			camera.upadteViewMatrix();
			Matrix4f viewMatrix = camera.getViewMatrix();
			
			vertexBuffer.bind();
			shader.useShaderAndFormat();
			shader.getUniform("ModelViewMat").setMatrix4f(viewMatrix);
			shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
			
			//GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);
			GLStateManager.drawElements(RenderPrimitive.TRIANGLES.getGlType(), 8, vertexBuffer.indecieFormat());
			vertexBuffer.unbind();
			
			window.swapFrames();
			window.pollEvents();
			
			System.out.println(camera + " " + motion);
		}

		buffer.freeMemory();
		
		GLFW.glfwTerminate();
		System.out.println("exit");
		
	}
	
}
