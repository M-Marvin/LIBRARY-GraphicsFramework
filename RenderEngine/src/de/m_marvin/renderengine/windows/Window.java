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

/**
 * Represents a GLFW window that can be used to draw geometry.
 * Also provides handling of events and interaction with the window.
 * 
 * @author Marvin Köhler
 *
 */
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
	
	/**
	 * Internally registers this class as event receiver to the GLFW window.
	 */
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

	/**
	 * Internally removes this class as event receiver to the GLFW window.
	 */
	protected void removeCallbacks() {
		GLFW.glfwSetCursorPosCallback(glWindow, null);
		GLFW.glfwSetCursorEnterCallback(glWindow, null);
		GLFW.glfwSetDropCallback(glWindow, null);
		GLFW.glfwSetWindowCloseCallback(glWindow, null);
		GLFW.glfwSetWindowContentScaleCallback(glWindow, null);
		GLFW.glfwSetWindowFocusCallback(glWindow, null);
		GLFW.glfwSetWindowMaximizeCallback(glWindow, null);
	}
	
	/**
	 * Creates a new GLFW window.
	 * 
	 * @param width The initial with of the window
	 * @param height The initial height of the window
	 * @param title The initial title of the window
	 */
	public Window(int width, int height, String title) {
		this.glWindow = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		setCallbacks();
	}
	
	/**
	 * Sets the Vsync value of the GLFW window, see {@link GLFW#glfwSwapInterval(int)} for more info.
	 * @param i Vsync value
	 */
	public void setVSync(int i) {
		GLFW.glfwSwapInterval(i);
	}
	
	/**
	 * Sets the title of the GLFW window.
	 * @param title The new title
	 */
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(glWindow, title);
	}
	
	/**
	 * Swaps the frame buffers of this window and move the drawn content visible on the window.
	 * Also clears the previous visible frame buffer and makes is ready for new draw calls.
	 */
	public void glSwapFrames() {
		GLStateManager.assertOnRenderThread();
		GLFW.glfwSwapBuffers(glWindow);
		GLStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GLStateManager.flush();
	}
	
	/**
	 * Register a even listener for the courser event.
	 * The event gets fired if the cursor enters, leaves or moves inside the window.
	 * 
	 * @param eventListener The event listener
	 */
	public void registerCourserListener(CourserEventConsumer eventListener) {
		courserListeners.add(eventListener);
	}

	/**
	 * Register a even listener for the window event.
	 * The event gets fired if the window gets or loses focus, gets minimized or restored and if the window size changes.
	 * 
	 * @param eventListener The event listener
	 */
	public void registerWindowListener(WindowEventConsumer eventListener) {
		windowListeners.add(eventListener);
	}

	/**
	 * Register a even listener for the file event.
	 * The event gets fired if files are dropped about the window.
	 * 
	 * @param eventListener The event listener
	 */
	public void registerFileListener(FileDropConsumer eventListener) {
		fileDropWindowListeners.add(eventListener);
	}

	/**
	 * Removes a even listener from the cursor event.
	 * 
	 * @param eventListener The event listener
	 */
	public void removeCourserListener(CourserEventConsumer eventListener) {
		courserListeners.remove(eventListener);
	}

	/**
	 * Removes a even listener from the window event.
	 * 
	 * @param eventListener The event listener
	 */
	public void removeWindowListener(WindowEventConsumer eventListener) {
		windowListeners.remove(eventListener);
	}

	/**
	 * Removes a even listener from the file event.
	 * 
	 * @param eventListener The event listener
	 */
	public void removeFileListener(FileDropConsumer eventListener) {
		fileDropWindowListeners.remove(eventListener);
	}
	
	/**
	 * Binds the OpenGL context of this window to the current thread.
	 * OpenGL and draw calls take only effect to the context currently bound to the calling thread and are only possible if a context is bound.
	 */
	public void makeContextCurrent() {
		GLFW.glfwMakeContextCurrent(glWindow);
		if (this.glCapabilities == null) {
			this.glCapabilities = GL.createCapabilities();
		} else {
			GL.setCapabilities(glCapabilities);
		}
	}

	/**
	 * Sets the position of the upper left corner of the window on the screen.
	 * @param x The x position
	 * @param y The y position
	 */
	public void setPosition(int x, int y) {
		GLFW.glfwSetWindowPos(this.glWindow, x, y);
	}
	
	/**
	 * Returns the position of the upper left corner of the window on the screen.
	 * @return An integer array of the length 2 containing the x and y positions
	 */
	public int[] getPosition() {
		int[] x = new int[1];
		int[] y = new int[1];
		GLFW.glfwGetWindowPos(glWindow, x, y);
		return new int[] {x[0], y[0]};
	}
	
	/**
	 * Sets the width and height of the window on the screen.
	 * @param width The width
	 * @param height The height
	 */
	public void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(glWindow, width, height);
	}
	
	/**
	 * Returns the width and height of window on the screen.
	 * @return An integer array of the length 2 containing the width and height values
	 */
	public int[] getSize() {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(glWindow, width, height);
		return new int[] {width[0], height[0]};
	}
	
	/**
	 * Sets the position of the cursor on the screen.
	 * @param x The x position
	 * @param y The y position
	 */
	public void setCourserPos(int x, int y) {
		GLFW.glfwSetCursorPos(glWindow, x, y);
	}
	
	/**
	 * Returns the position of the cursor on the screen.
	 * @return An integer array of the length 2 containing the x and y positions
	 */
	public double[] getCourserPos() {
		double[] x = new double[1];
		double[] y = new double[1];
		GLFW.glfwGetCursorPos(glWindow, x, y);
		return new double[] {x[0], y[0]};
	}
	
	/**
	 * Sets the opacity of the window.
	 * @param opacity The opacity factor of the window.
	 */
	public void setOpacity(float opacity) {
		GLFW.glfwSetWindowOpacity(glWindow, opacity);
	}
	
	/**
	 * Returns the current opacity of the window.
	 * @return The current opacity factor
	 */
	public float getOpacity() {
		return GLFW.glfwGetWindowOpacity(glWindow);
	}
	
	/**
	 * Returns the GLFW window handle id.
	 * @return The GLFW window id
	 */
	public long windowId() {
		return this.glWindow;
	}
	
	/**
	 * Returns true if the context of this windows is currently bound to the calling thread.
	 * @return true if the context of this windows is currently bound to the calling thread
	 */
	public boolean isCurrent() {
		return this.glWindow == GLFW.glfwGetCurrentContext();
	}
	
	/**
	 * Polls and processes events from the GLFW window.
	 * The events of the window will only fire while this method is called.
	 */
	public void pollEvents() {
		GLFW.glfwPollEvents();
	}
	
	/**
	 * Returns true if the close button of the window was pressed.
	 * @return true if the window was requested to close
	 */
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(glWindow);
	}
	
	/**
	 * Closes and destroys this window.
	 * All registered event listeners are removed from the window and the context of the window gets destroyed.
	 */
	public void destroy() {
		removeCallbacks();
		GLFW.glfwDestroyWindow(glWindow);
	}
	
	/**
	 * Controls if the window is visible to the user.
	 * @param visible True if the window should be visible
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			GLFW.glfwShowWindow(glWindow);
		} else {
			GLFW.glfwHideWindow(glWindow);
		}
	}
	
}
