package de.m_marvin.gframe.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.gframe.GLStateManager;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2f;
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
	protected List<CourserEventConsumer> courserListeners = Collections.synchronizedList(new ArrayList<>());
	protected List<WindowEventConsumer> windowListeners = Collections.synchronizedList(new ArrayList<>());
	protected List<FileDropConsumer> fileDropWindowListeners = Collections.synchronizedList(new ArrayList<>());
	
	public static enum WindowEventType {
		CLOSED,RESIZED,FOCUSED,UNFOCUSED,MAXIMIZED,MINIMIZED,RESTORED,REFRESH,DPI_CHANGE;
	} 
	
	@FunctionalInterface
	public static interface CourserEventConsumer {
		public void courserEvent(Vec2d pos, boolean entered, boolean leaved);
	}

	@FunctionalInterface
	public static interface WindowEventConsumer {
		public void windowEvent(Optional<Vec2f> windowResize, WindowEventType type);
	}
	
	@FunctionalInterface
	public static interface FileDropConsumer {
		public void fileDropEvent(String[] files);
	}
	
	@FunctionalInterface
	public static interface WindowRefreshConsumer {
		public void windowRefresh();
	}
	
	/**
	 * Internally registers this class as event receiver to the GLFW window.
	 */
	protected void setCallbacks() {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetCursorPosCallback(glWindow, (window, xpos, ypos) -> this.courserListeners.forEach((listener) -> listener.courserEvent(new Vec2d(xpos, ypos), false, false)));
			GLFW.glfwSetCursorEnterCallback(glWindow, (window, entered) -> this.courserListeners.forEach((listener) -> listener.courserEvent(new Vec2d(0, 0), entered, !entered)));
			GLFW.glfwSetWindowCloseCallback(glWindow, window -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), WindowEventType.CLOSED)));
			GLFW.glfwSetWindowSizeCallback (glWindow, (window, width, height) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.of(new Vec2f(width, height)), WindowEventType.RESIZED)));
			GLFW.glfwSetWindowFocusCallback(glWindow, (window, focused) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), focused ? WindowEventType.FOCUSED : WindowEventType.UNFOCUSED)));
			GLFW.glfwSetWindowMaximizeCallback(glWindow, (window, maximized) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), maximized ? WindowEventType.MAXIMIZED : WindowEventType.MINIMIZED)));
			GLFW.glfwSetWindowRefreshCallback(glWindow, (window) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), WindowEventType.REFRESH)));
			GLFW.glfwSetWindowContentScaleCallback(glWindow, (window, xscale, yscale) -> this.windowListeners.forEach(listener -> listener.windowEvent(Optional.of(new Vec2f(xscale, yscale)), WindowEventType.DPI_CHANGE)));
			GLFW.glfwSetDropCallback(glWindow, (window, count, names) -> {
				String[] fileNames = new String[count];
				for (int i = 0; i < count; i++) fileNames[i] = GLFWDropCallback.getName(names, i);
				this.fileDropWindowListeners.forEach((listeners) -> listeners.fileDropEvent(fileNames));
			});
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetCursorPosCallback(glWindow, (window, xpos, ypos) -> this.courserListeners.forEach((listener) -> listener.courserEvent(new Vec2d(xpos, ypos), false, false)));
				GLFW.glfwSetCursorEnterCallback(glWindow, (window, entered) -> this.courserListeners.forEach((listener) -> listener.courserEvent(new Vec2d(0, 0), entered, !entered)));
				GLFW.glfwSetWindowCloseCallback(glWindow, window -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), WindowEventType.CLOSED)));
				GLFW.glfwSetWindowSizeCallback (glWindow, (window, width, height) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.of(new Vec2f(width, height)), WindowEventType.RESIZED)));
				GLFW.glfwSetWindowFocusCallback(glWindow, (window, focused) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), focused ? WindowEventType.FOCUSED : WindowEventType.UNFOCUSED)));
				GLFW.glfwSetWindowMaximizeCallback(glWindow, (window, maximized) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), maximized ? WindowEventType.MAXIMIZED : WindowEventType.MINIMIZED)));
				GLFW.glfwSetWindowRefreshCallback(glWindow, (window) -> this.windowListeners.forEach((listener) -> listener.windowEvent(Optional.empty(), WindowEventType.REFRESH)));
				GLFW.glfwSetWindowContentScaleCallback(glWindow, (window, xscale, yscale) -> this.windowListeners.forEach(listener -> listener.windowEvent(Optional.of(new Vec2f(xscale, yscale)), WindowEventType.DPI_CHANGE)));
				GLFW.glfwSetDropCallback(glWindow, (window, count, names) -> {
					String[] fileNames = new String[count];
					for (int i = 0; i < count; i++) fileNames[i] = GLFWDropCallback.getName(names, i);
					this.fileDropWindowListeners.forEach((listeners) -> listeners.fileDropEvent(fileNames));
				});
			});
		}
	}

	/**
	 * Internally removes this class as event receiver to the GLFW window.
	 */
	protected void removeCallbacks() {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetCursorPosCallback(glWindow, null);
			GLFW.glfwSetCursorEnterCallback(glWindow, null);
			GLFW.glfwSetDropCallback(glWindow, null);
			GLFW.glfwSetWindowCloseCallback(glWindow, null);
			GLFW.glfwSetWindowContentScaleCallback(glWindow, null);
			GLFW.glfwSetWindowFocusCallback(glWindow, null);
			GLFW.glfwSetWindowMaximizeCallback(glWindow, null);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetCursorPosCallback(glWindow, null);
				GLFW.glfwSetCursorEnterCallback(glWindow, null);
				GLFW.glfwSetDropCallback(glWindow, null);
				GLFW.glfwSetWindowCloseCallback(glWindow, null);
				GLFW.glfwSetWindowContentScaleCallback(glWindow, null);
				GLFW.glfwSetWindowFocusCallback(glWindow, null);
				GLFW.glfwSetWindowMaximizeCallback(glWindow, null);
			});
		}
	}
	
	/**
	 * Creates a new GLFW window.
	 * 
	 * @param width The initial with of the window
	 * @param height The initial height of the window
	 * @param title The initial title of the window
	 */
	public Window(int width, int height, String title) {
		this(width, height, title, true, true, false);
	}
	
	/**
	 * Creates a new GLFW window.
	 * 
	 * @param width The initial with of the window
	 * @param height The initial height of the window
	 * @param title The initial title of the window
	 * @param resizable If the window can be resized
	 * @param decorated If the window has the platform dependent frame and title bar
	 * @param transparent If the window allows transparency
	 */
	public Window(int width, int height, String title, boolean resizable, boolean decorated, boolean transparent) {
		if (GLFWStateManager.isOnGlfwThread()) {
			this.glWindow = createWindow(width, height, title, resizable, decorated, transparent);
		} else {
			this.glWindow = CompletableFuture.supplyAsync(() -> {
				return createWindow(width, height, title, resizable, decorated, transparent);
			}, GLFWStateManager.getGlfwExecutor()).join();
		}
		setCallbacks();
	}
	
	protected long createWindow(int width, int height, String title, boolean resizable, boolean decorated, boolean transparent) {
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);
		return GLFW.glfwCreateWindow(width, height, title, 0, 0);
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
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetWindowTitle(glWindow, title);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetWindowTitle(glWindow, title);
			});
		}
	}
	
	/**
	 * Swaps the frame buffers of this window and move the drawn content visible on the window.
	 * Also clears the previous visible frame buffer and makes is ready for new draw calls.
	 */
	public void glSwapFrames() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.flush();
		GLFW.glfwSwapBuffers(glWindow);
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
	public void setPosition(Vec2i pos) {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetWindowPos(this.glWindow, pos.x, pos.y);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetWindowPos(this.glWindow, pos.x, pos.y);
			});
		}
	}
	
	/**
	 * Returns the position of the upper left corner of the window on the screen.
	 * @return An integer array of the length 2 containing the x and y positions
	 */
	public Vec2i getPosition() {
		if (GLFWStateManager.isOnGlfwThread()) {
			int[] x = new int[1];
			int[] y = new int[1];
			GLFW.glfwGetWindowPos(glWindow, x, y);
			return new Vec2i(x[0], y[0]);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				int[] x = new int[1];
				int[] y = new int[1];
				GLFW.glfwGetWindowPos(glWindow, x, y);
				return new Vec2i(x[0], y[0]);
			}, GLFWStateManager.getGlfwExecutor()).join();
		}
	}
	
	/**
	 * Sets the width and height of the window on the screen.
	 * @param width The width
	 * @param height The height
	 */
	public void setSize(int width, int height) {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetWindowSize(glWindow, width, height);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetWindowSize(glWindow, width, height);
			});
		}
	}
	
	/**
	 * Sets the minimum width and height of the window on the screen.
	 * @param widthMin The min width
	 * @param heightMin The min height
	 */
	public void setMinSize(int widthMin, int heightMin) {
		setSizeLimits(widthMin, heightMin, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
	}
	
	/**
	 * Sets the minimum and maximum width and height of the window on the screen.
	 * @param widthMin The min width
	 * @param heightMin The min height
	 * @param widthMax The max width
	 * @param heightMax The max height
	 */
	public void setSizeLimits(int widthMin, int heightMin, int widthMax, int heightMax) {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetWindowSizeLimits(glWindow, widthMin, heightMin, widthMax, heightMax);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetWindowSizeLimits(glWindow, widthMin, heightMin, widthMax, heightMax);
			});
		}
	}
	
	/**
	 * Returns the width and height of window on the screen.
	 * @return An integer array of the length 2 containing the width and height values
	 */
	public int[] getSize() {
		if (GLFWStateManager.isOnGlfwThread()) {
			int[] width = new int[1];
			int[] height = new int[1];
			GLFW.glfwGetWindowSize(glWindow, width, height);
			return new int[] {width[0], height[0]};
		} else {
			return CompletableFuture.supplyAsync(() -> {
				int[] width = new int[1];
				int[] height = new int[1];
				GLFW.glfwGetWindowSize(glWindow, width, height);
				return new int[] {width[0], height[0]};
			}, GLFWStateManager.getGlfwExecutor()).join();
		}
	}
	
	/**
	 * Sets the position of the cursor on the screen.
	 * @param x The x position
	 * @param y The y position
	 */
	public void setCourserPos(Vec2d pos) {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetCursorPos(glWindow, pos.x, pos.y);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetCursorPos(glWindow, pos.x, pos.y);
			});
		}
	}
	
	/**
	 * Returns the position of the cursor on the screen.
	 * @return An integer array of the length 2 containing the x and y positions
	 */
	public Vec2d getCourserPos() {
		if (GLFWStateManager.isOnGlfwThread()) {
			double[] x = new double[1];
			double[] y = new double[1];
			GLFW.glfwGetCursorPos(glWindow, x, y);
			return new Vec2d(x[0], y[0]);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				double[] x = new double[1];
				double[] y = new double[1];
				GLFW.glfwGetCursorPos(glWindow, x, y);
				return new Vec2d(x[0], y[0]);
			}, GLFWStateManager.getGlfwExecutor()).join();
		}
	}
	
	/**
	 * Sets the opacity of the window.
	 * @param opacity The opacity factor of the window.
	 */
	public void setOpacity(float opacity) {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwSetWindowOpacity(glWindow, opacity);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwSetWindowOpacity(glWindow, opacity);
			});
		}
	}
	
	/**
	 * Returns the current opacity of the window.
	 * @return The current opacity factor
	 */
	public float getOpacity() {
		if (GLFWStateManager.isOnGlfwThread()) {
			return GLFW.glfwGetWindowOpacity(glWindow);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				return GLFW.glfwGetWindowOpacity(glWindow);
			}, GLFWStateManager.getGlfwExecutor()).join();
		}
	}
	
	/**
	 * Returns the content scale of this window
	 * @return The content scale of this window on the x and y axis
	 */
	public Vec2f getContentScale() {
		if (GLFWStateManager.isOnGlfwThread()) {
			float[] x = new float[1];
			float[] y = new float[1];
			GLFW.glfwGetWindowContentScale(this.windowId(), x, y);
			return new Vec2f(x[0], y[0]);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				float[] x = new float[1];
				float[] y = new float[1];
				GLFW.glfwGetWindowContentScale(this.windowId(), x, y);
				return new Vec2f(x[0], y[0]);
			}, GLFWStateManager.getGlfwExecutor()).join();
		}
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
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwDestroyWindow(glWindow);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				GLFW.glfwDestroyWindow(glWindow);
			});
		}
	}
	
	/**
	 * Controls if the window is visible to the user.
	 * @param visible True if the window should be visible
	 */
	public void setVisible(boolean visible) {
		if (GLFWStateManager.isOnGlfwThread()) {
			if (visible) {
				GLFW.glfwShowWindow(glWindow);
			} else {
				GLFW.glfwHideWindow(glWindow);
			}
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				if (visible) {
					GLFW.glfwShowWindow(glWindow);
				} else {
					GLFW.glfwHideWindow(glWindow);
				}
			});
		}
	}
	
	/**
	 * Maximizes the window (sets it to the largest allowed width and height)
	 */
	public void maximize() {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwMaximizeWindow(glWindow);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				
			});
		}
	}
	
	/**
	 * Minimizes the window (puts it in the task bar)
	 */
	public void minimize() {
		if (GLFWStateManager.isOnGlfwThread()) {
			GLFW.glfwIconifyWindow(glWindow);
		} else {
			GLFWStateManager.getGlfwExecutor().execute(() -> {
				
			});
		}
	}
	
}
