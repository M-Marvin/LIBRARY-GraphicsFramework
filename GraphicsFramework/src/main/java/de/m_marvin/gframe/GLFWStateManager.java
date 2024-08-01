package de.m_marvin.gframe;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class GLFWStateManager {

	protected static Thread glfwThread;
	protected static Queue<Runnable> glfwTasks = new ArrayDeque<>();
	protected static Executor glfwExecutor = t -> {
		if (glfwThread == null) {
			throw new IllegalStateException("GLFW not yet initialized!");
		}
		synchronized (glfwTasks) {
			glfwTasks.add(t);
		}
	};
	
	/**
	 * Returns an executor, which executes all its tasks on the GLFW thread.
	 * The tasks are executed each time the {@link GLFWStateManager#pollEvents()} method is executed
	 */
	public static Executor getGlfwExecutor() {
		return glfwExecutor;
	}
	
	/**
	 * Returns true if called on the GLFW-Thread, returns false otherwise
	 * @return true when on the GLFW thread, false otherwise
	 */
	public static boolean isOnGlfwThread() {
		return Thread.currentThread() == glfwThread;
	}
	
	/**
	 * Only processes the tasks of the {@link GLFWStateManager#getGlfwExecutor()}
	 * This function must be called on the same thread on which {@link GLFWStateManager#initialize(PrintStream)} was!
	 */
	public static void processTasks() {
		synchronized (glfwTasks) {
			while (!glfwTasks.isEmpty()) glfwTasks.poll().run();
		}
	}
	
	/**
	 * Handles GLFW callbacks such as user input events
	 * This function must be called on the same thread on which {@link GLFWStateManager#initialize(PrintStream)} was!
	 */
	public static void pollEvents() {
		GLFW.glfwPollEvents();
	}
	
	/**
	 * Handles GLFW callbacks such as user input events and also processes the tasks of the {@link GLFWStateManager#getGlfwExecutor()}
	 * This function must be called on the same thread on which {@link GLFWStateManager#initialize(PrintStream)} was!
	 */
	public static void update() {
		pollEvents();
		processTasks();
	}
	
	/**
	 * Initializes GLFW, must be called before any OpenGL or GLFW related methods can be used.
	 * This function should be called on the main thread (the thread that calls the main method), but apperrantly also works with any other thread, except on mac!
	 * 
	 * @param outStream Log output stream to print GLFW and OpenGL messages.
	 * @return True if GLFW could be initialized, false if an error occurred
	 */
	public static boolean initialize(PrintStream outStream) {
		if (glfwThread != null) return true;
		if (!GLFW.glfwInit()) return false;
		glfwThread = Thread.currentThread();
		GLFWErrorCallback.createPrint(outStream).set();
		return true;
	}
	
	/**
	 * Calls the GLFW terminate method.
	 * Should be called to cleanup everything.
	 * This function must be called on the same thread on which {@link GLFWStateManager#initialize(PrintStream)} was!
	 */
	public static void terminate() {
		GLFW.glfwTerminate();
		glfwThread = null;
	}
	
}
