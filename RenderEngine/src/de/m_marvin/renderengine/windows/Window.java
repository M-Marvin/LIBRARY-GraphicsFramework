package de.m_marvin.renderengine.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;

public class Window {
	
	protected long glWindow;
	protected GLCapabilities glCapabilities;
	protected List<CourserEventConsumer> courserListeners = new ArrayList<>();
	protected List<WindowEventConsumer> windowListeners = new ArrayList<>();
	protected List<FileDropConsumer> fileDropWindowListeners = new ArrayList<>();
	
	@FunctionalInterface
	public static interface CourserEventConsumer {
		public void courserEvent(Vec2d pos, boolean entered, boolean leaved);
	}

	@FunctionalInterface
	public static interface WindowEventConsumer {
		public void windowEvent(boolean shouldClose, Optional<Vec2i> windowResize, boolean focused, boolean unfocused, boolean maximized, boolean restored);
	}
	
	@FunctionalInterface
	public static interface FileDropConsumer {
		public void fileDropEvent(String[] files);
	}
	
	protected void setCallbacks() {
		GLFW.glfwSetCursorPosCallback(glWindow, (window, xpos, ypos) -> this.courserListeners.forEach((listener) -> listener.courserEvent(new Vec2d(xpos, ypos), false, false)));
		GLFW.glfwSetCursorEnterCallback(glWindow, (window, entered) -> this.courserListeners.forEach((listener) -> listener.courserEvent(new Vec2d(0, 0), entered, !entered)));
		GLFW.glfwSetWindowCloseCallback(glWindow, window -> this.windowListeners.forEach((listener) -> listener.windowEvent(true, Optional.empty(), false, false, false, false)));
		GLFW.glfwSetFramebufferSizeCallback(glWindow, (window, width, height) -> this.windowListeners.forEach((listener) -> listener.windowEvent(false, Optional.of(new Vec2i(width, height)), false, false, false, false)));
		GLFW.glfwSetWindowFocusCallback(glWindow, (window, focused) -> this.windowListeners.forEach((listener) -> listener.windowEvent(false, Optional.empty(), focused, !focused, false, false)));
		GLFW.glfwSetWindowMaximizeCallback(glWindow, (window, maximized) -> this.windowListeners.forEach((listener) -> listener.windowEvent(false, Optional.empty(), false, false, maximized, !maximized)));
		GLFW.glfwSetDropCallback(glWindow, (window, count, names) -> {
			String[] fileNames = new String[count];
			for (int i = 0; i < count; i++) fileNames[i] = GLFWDropCallback.getName(names, i);
			this.fileDropWindowListeners.forEach((listeners) -> listeners.fileDropEvent(fileNames));
		});
	}
	
	protected void removeCallbacks() {
		GLFW.glfwSetCursorPosCallback(glWindow, null);
		GLFW.glfwSetCursorEnterCallback(glWindow, null);
		GLFW.glfwSetDropCallback(glWindow, null);
		GLFW.glfwSetWindowCloseCallback(glWindow, null);
		GLFW.glfwSetWindowContentScaleCallback(glWindow, null);
		GLFW.glfwSetWindowFocusCallback(glWindow, null);
		GLFW.glfwSetWindowMaximizeCallback(glWindow, null);
	}
	
	public Window(int width, int height, String title) {
		this.glWindow = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		setCallbacks();
	}
	
	public void setVSync(int i) {
		GLFW.glfwSwapInterval(i);
	}
	
	public void glSwapFrames() {
		GLStateManager.assertOnRenderThread();
		GLFW.glfwSwapBuffers(glWindow);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
	}
	
	public void registerCourserListener(CourserEventConsumer eventListener) {
		courserListeners.add(eventListener);
	}

	public void registerWindowListener(WindowEventConsumer eventListener) {
		windowListeners.add(eventListener);
	}

	public void registerFileListener(FileDropConsumer eventListener) {
		fileDropWindowListeners.add(eventListener);
	}

	public void removeCourserListener(CourserEventConsumer eventListener) {
		courserListeners.remove(eventListener);
	}

	public void removeWindowListener(WindowEventConsumer eventListener) {
		windowListeners.remove(eventListener);
	}

	public void removeFileListener(FileDropConsumer eventListener) {
		fileDropWindowListeners.remove(eventListener);
	}
	
	public void makeContextCurrent() {
		GLFW.glfwMakeContextCurrent(glWindow);
		if (this.glCapabilities == null) {
			this.glCapabilities = GL.createCapabilities();
		} else {
			GL.setCapabilities(glCapabilities);
		}
	}

	public void setPosition(int x, int y) {
		GLFW.glfwSetWindowPos(this.glWindow, x, y);
	}
	public int[] getPosition() {
		int[] x = new int[1];
		int[] y = new int[1];
		GLFW.glfwGetWindowPos(glWindow, x, y);
		return new int[] {x[0], y[0]};
	}
	
	public void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(glWindow, width, height);
	}
	public int[] setSize() {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(glWindow, width, height);
		return new int[] {width[0], height[0]};
	}
	
	public void setCourserPos(int x, int y) {
		GLFW.glfwSetCursorPos(glWindow, x, y);
	}
	public double[] getCourserPos() {
		double[] x = new double[1];
		double[] y = new double[1];
		GLFW.glfwGetCursorPos(glWindow, x, y);
		return new double[] {x[0], y[0]};
	}
	
	public void setOpacity(float opacity) {
		GLFW.glfwSetWindowOpacity(glWindow, opacity);
	}
	public float getOpacity() {
		return GLFW.glfwGetWindowOpacity(glWindow);
	}
	
	public long windowId() {
		return this.glWindow;
	}
	
	public boolean isCurrent() {
		return this.glWindow == GLFW.glfwGetCurrentContext();
	}
	
	public void pollEvents() {
		GLFW.glfwPollEvents();
	}
	
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(glWindow);
	}
	
	public void destroy() {
		removeCallbacks();
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
