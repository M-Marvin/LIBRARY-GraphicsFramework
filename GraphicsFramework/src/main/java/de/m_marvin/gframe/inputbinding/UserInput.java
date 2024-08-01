package de.m_marvin.gframe.inputbinding;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.gframe.GLFWStateManager;
import de.m_marvin.univec.impl.Vec2d;

/**
 * This class provides different ways to handle input from multiple GLFW windows.
 * Including:
 * - Custom multi-key-Combination bindings (multiple key-combinations for one "binding")
 * - Methods to get the state of single keys (keyboard and mouse)
 * - Methods to add listeners to specific input-events
 * - Events for text-input providing a {@link char} and {@link FunctionalKey} informations
 * - Mouse movement and scrolling
 * 
 * @author Marvin Köhler
 *
 */
public class UserInput {
	
	protected Map<String, InputSet> bindings = Collections.synchronizedMap(new HashMap<>());
	protected Map<String, Boolean> bindingsState = Collections.synchronizedMap(new HashMap<>());
	protected Map<String, Boolean> bindingsStateLast = Collections.synchronizedMap(new HashMap<>());
	protected List<Long> attachedWindows = Collections.synchronizedList(new ArrayList<>());
	protected List<KeyEventConsumer> keyboardListeners = Collections.synchronizedList(new ArrayList<>());
	protected List<MouseEventConsumer> mouseListeners = Collections.synchronizedList(new ArrayList<>());
	protected List<TextInputConsumer> textInputListeners = Collections.synchronizedList(new ArrayList<>());
	protected List<CursorEventConsumer> cursorListeners = Collections.synchronizedList(new ArrayList<>());
	
	protected Vec2d cursorOffset = new Vec2d(0, 0);
	protected Vec2d cursorScale = new Vec2d(1, 1);
	
	public static Executor getUserInputExecutor() {
		return GLFWStateManager.getGlfwExecutor();
	}
	
	public static boolean isOnUserInputThread() {
		return GLFWStateManager.isOnGlfwThread();
	}
	
	/**
	 * Creates a new user-input class to store key-bindings and event-listeners.
	 * Multiple instances can be created, but mostly it will not make any sense to have multiple handlers since there is only one input.
	 */
	public UserInput() {
		addKeyboardListener((key, scancode, pressed, repeated) -> {
			Optional<FunctionalKey> fkey = Optional.ofNullable(FunctionalKey.getKey(key));
			if (fkey.isPresent() && (pressed || repeated)) this.textInputListeners.forEach((listener) -> listener.input((char) -1, fkey));
		});
	}
	
	/**
	 * Sets an offset that is applied to the cursor position on all callbacks/listeners (after the scale factor is applied)
	 */
	public void setCursorOffset(Vec2d cursorOffset) {
		this.cursorOffset = cursorOffset;
	}
	
	/**
	 * Sets an scale factor that is applied to the cursor position on all callbacks/listeners (before the offset is applied)
	 * @param cursorScale
	 */
	public void setCursorScale(Vec2d cursorScale) {
		this.cursorScale = cursorScale;
	}
	
	/**
	 * Updates the input states of the key-bindings.
	 */
	public void update() {
		this.bindings.forEach((name, input) -> {
			InputSet binding = getBinding(name).get();
			boolean state = false;
			for (long window : this.attachedWindows) {
				if (binding.isActive(window)) {
					state = true;
					break;
				}
			}
			this.bindingsStateLast.put(name, this.bindingsState.get(name));
			this.bindingsState.put(name, state);
		});
	}
	
	/**
	 * Used as parameter for text-input events.
	 * Represents all non character keys that my be important for text input.
	 * 
	 * @author Marvin Köhler
	 */
	public static enum FunctionalKey {
		BACKSPACE(GLFW.GLFW_KEY_BACKSPACE),
		ENTER(GLFW.GLFW_KEY_ENTER),
		DEL(GLFW.GLFW_KEY_DELETE),
		END(GLFW.GLFW_KEY_END),
		INSERT(GLFW.GLFW_KEY_INSERT),
		POS1(GLFW.GLFW_KEY_HOME),
		PAGE_UP(GLFW.GLFW_KEY_PAGE_UP),
		PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN),
		KEY_RIGHT(GLFW.GLFW_KEY_RIGHT),
		KEY_LEFT(GLFW.GLFW_KEY_LEFT),
		KEY_UP(GLFW.GLFW_KEY_UP),
		KEY_DOWN(GLFW.GLFW_KEY_DOWN),
		LEFT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
		RIGHT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
		CAPS_LOCK(GLFW.GLFW_KEY_CAPS_LOCK),
		CONTROL_LEFT(GLFW.GLFW_KEY_LEFT_CONTROL),
		CONTROL_RIGHT(GLFW.GLFW_KEY_RIGHT_CONTROL);
		
		private final int glkey;
		
		private FunctionalKey(int glkey) {
			this.glkey = glkey;
		}
		
		public int glfwkey() {
			return this.glkey;
		}
		
		/**
		 * Tries to find a functional key with matching GLFW id
		 * @param keycode The GLFW key id
		 * @return A {@link FunctionalKey} or null if no matching key was found
		 */
		public static FunctionalKey getKey(int keycode) {
			for (FunctionalKey key : FunctionalKey.values()) {
				if (key.glfwkey() == keycode) {
					return key;
				}
			}
			return null;
		}
		
	}
	
	/* Functional interfaces for the Events */
	
	@FunctionalInterface
	public static interface KeyEventConsumer {
		public void keyEvent(int key, int scancode, boolean pressed, boolean repeated);
	}
	
	@FunctionalInterface
	public static interface MouseEventConsumer {
		public void mouseEvent(Optional<Vec2d> scroll, int button, boolean pressed, boolean repeated);
	}

	@FunctionalInterface
	public static interface TextInputConsumer {
		public void input(char character, Optional<FunctionalKey> functionalKey);
	}
	
	@FunctionalInterface
	public static interface CursorEventConsumer {
		public void cursorMove(Vec2d position, boolean entered, boolean leaved);
	}
	
	/* End of functional interfaces for the Events */
	
	/**
	 * Register a new listener for the keyboard-key event.
	 * The event is fired if any key on the keyboard is pressed.
	 * @param eventConsumer The consumer of the event
	 */
	public void addKeyboardListener(KeyEventConsumer eventConsumer) {
		this.keyboardListeners.add(eventConsumer);
	}
	
	/**
	 * Removes a listener from the keyboard-key event.
	 * @param eventConsumer The consumer to remove from the event
	 */
	public void removeKeyboardListener(KeyEventConsumer eventConsumer) {
		this.keyboardListeners.remove(eventConsumer);
	}
	
	/**
	 * Register a new listener for the mouse-key event.
	 * The event is fired if any key on the keyboard is pressed.
	 * @param eventConsumer The consumer of the event
	 */
	public void addMouseListener(MouseEventConsumer eventConsumer) {
		this.mouseListeners.add(eventConsumer);
	}

	/**
	 * Register a new listener for the mouse-move event.
	 * The event is fired if the cursor enters or leaves the window, and if it gets moved around.
	 * @param eventConsumer The consumer of the event
	 */
	public void addCursorListener(CursorEventConsumer eventConsumer) {
		this.cursorListeners.add(eventConsumer);
	}
	
	/**
	 * Removes a listener from the mouse-key event.
	 * @param eventConsumer The consumer to remove from the event
	 */
	public void removeMouseListener(MouseEventConsumer eventConsumer) {
		this.mouseListeners.remove(eventConsumer);
	}

	/**
	 * Removes a listener from the mouse-move event.
	 * @param eventConsumer The consumer to remove from the event
	 */
	public void removeCursorListener(CursorEventConsumer eventConsumer) {
		this.cursorListeners.remove(eventConsumer);
	}

	/**
	 * Register a new listener for the text input event.
	 * The event is fired if any key on the keyboard is pressed.
	 * @param eventConsumer The consumer of the event
	 */
	public void addTextInputListener(TextInputConsumer eventConsumer) {
		this.textInputListeners.add(eventConsumer);
	}
	
	/**
	 * Removes a listener from the text input event.
	 * @param eventConsumer The consumer to remove from the event
	 */
	public void removeTextInputListener(TextInputConsumer eventConsumer) {
		this.textInputListeners.remove(eventConsumer);
	}
	
	/**
	 * Copies the string into the clipboard.
	 * @param clipboard The string to copy into the clipboard
	 */
	public void setClipboardString(String clipboard) {
		if (!isOnUserInputThread()) {
			getUserInputExecutor().execute(() -> {
				GLFW.glfwSetClipboardString(0, clipboard);
			});
		} else {
			GLFW.glfwSetClipboardString(0, clipboard);
		}
	}
	
	/**
	 * Returns the string currently in the clipboard.
	 * @return The string currently in the clipboard
	 */
	public String getClipboardString() {
		if (!isOnUserInputThread()) {
			return GLFW.glfwGetClipboardString(0);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				return GLFW.glfwGetClipboardString(0);
			}, getUserInputExecutor()).join();
		}
	}
	
	/**
	 * Remove the GLFW windows from the list of windows to check for input.
	 * @param windowId The GLFW window handle id
	 */
	public void detachWindow(long windowId) {
		this.attachedWindows.remove(windowId);
		if (!isOnUserInputThread()) {
			GLFW.glfwSetKeyCallback(windowId, null);
			GLFW.glfwSetMouseButtonCallback(windowId, null);
			GLFW.glfwSetCursorPosCallback(windowId, null);
			GLFW.glfwSetCursorEnterCallback(windowId, null);
			GLFW.glfwSetScrollCallback(windowId, null);
			GLFW.glfwSetCharCallback(windowId, null);
		} else {
			getUserInputExecutor().execute(() -> {
				GLFW.glfwSetKeyCallback(windowId, null);
				GLFW.glfwSetMouseButtonCallback(windowId, null);
				GLFW.glfwSetCursorPosCallback(windowId, null);
				GLFW.glfwSetCursorEnterCallback(windowId, null);
				GLFW.glfwSetScrollCallback(windowId, null);
				GLFW.glfwSetCharCallback(windowId, null);
			});
		}
	}
	
	/**
	 * Add the GLFW windows to the list of windows to check for input.
	 * @param windowId The GLFW window handle id
	 */
	public void attachToWindow(long windowId) {
		this.attachedWindows.add(windowId);
		if (!isOnUserInputThread()) {
			GLFW.glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> this.keyboardListeners.forEach((listener) -> listener.keyEvent(key, scancode, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT)));		
			GLFW.glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> this.mouseListeners.forEach((listener) -> listener.mouseEvent(Optional.empty(), button, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT)));		
			GLFW.glfwSetCursorPosCallback(windowId, (window, xpos, ypos) -> this.cursorListeners.forEach((listener) -> listener.cursorMove(new Vec2d(xpos, ypos).mul(this.cursorScale).add(this.cursorOffset), false, false)));
			GLFW.glfwSetCursorEnterCallback(windowId, (window, entered) -> this.cursorListeners.forEach((listener) -> listener.cursorMove(getCursorPosition(windowId).mul(this.cursorScale).add(this.cursorOffset), entered, !entered)));
			GLFW.glfwSetScrollCallback(windowId, (window, xoffset, yoffset) ->  this.mouseListeners.forEach((listener) -> listener.mouseEvent(Optional.of(new Vec2d(xoffset, yoffset).mul(this.cursorScale).add(this.cursorOffset)), 0, false, false)));		
			GLFW.glfwSetCharCallback(windowId, (window, codepoint) -> this.textInputListeners.forEach((listener) -> listener.input((char) codepoint, Optional.empty())));
		} else {
			getUserInputExecutor().execute(() -> {
				GLFW.glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> this.keyboardListeners.forEach((listener) -> listener.keyEvent(key, scancode, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT)));		
				GLFW.glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> this.mouseListeners.forEach((listener) -> listener.mouseEvent(Optional.empty(), button, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT)));		
				GLFW.glfwSetCursorPosCallback(windowId, (window, xpos, ypos) -> this.cursorListeners.forEach((listener) -> listener.cursorMove(new Vec2d(xpos, ypos).mul(this.cursorScale).add(this.cursorOffset), false, false)));
				GLFW.glfwSetCursorEnterCallback(windowId, (window, entered) -> this.cursorListeners.forEach((listener) -> listener.cursorMove(getCursorPosition(windowId).mul(this.cursorScale).add(this.cursorOffset), entered, !entered)));
				GLFW.glfwSetScrollCallback(windowId, (window, xoffset, yoffset) ->  this.mouseListeners.forEach((listener) -> listener.mouseEvent(Optional.of(new Vec2d(xoffset, yoffset).mul(this.cursorScale).add(this.cursorOffset)), 0, false, false)));		
				GLFW.glfwSetCharCallback(windowId, (window, codepoint) -> this.textInputListeners.forEach((listener) -> listener.input((char) codepoint, Optional.empty())));
			});
		}
	}
	
	/**
	 * Create a new key-binding
	 * @param name The name for the binding
	 * @return The {@link InputSet} to configure the binding
	 */
	public InputSet registerBinding(String name) {
		InputSet inputSet = new InputSet();
		this.bindings.put(name, inputSet);
		return inputSet;
	}
	
	/**
	 * Returns the {@link InputSet} of the specified key-binding
	 * @param name The name of the binding
	 * @return The {@link InputSet} of the binding
	 */
	public Optional<InputSet> getBinding(String name) {
		return Optional.ofNullable(this.bindings.get(name));
	}
	
	/**
	 * Returns true if the {@link InputSet#isActive(long)} method of the specified binding returns true for at least one of the attached windows.
	 * @param name The name of the binding
	 * @return True if the binding is activated trough user input
	 */
	public boolean isBindingActive(String name) {
		Optional<InputSet> binding = getBinding(name);
		if (binding.isPresent() && this.bindingsState.containsKey(name)) {
			return this.bindingsState.get(name);
		}
		return false;
	}
	
	/**
	 * Returns true if the {@link InputSet#isActive(long)} method of the specified binding returns true for at least one of the attached windows and it was not active the last tick (only returns true for one tick).
	 * @param name The name of the binding
	 * @return True if the binding is activated trough user input
	 */
	public boolean isBindingTyped(String name) {
		Optional<InputSet> binding = getBinding(name);
		if (binding.isPresent()) {
			if (this.bindingsState.get(name) && !this.bindingsStateLast.get(name)) return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the specified key is pressed.
	 * 
	 * @param key The keycode of the key
	 * @return true if the key is pressed
	 */
	public boolean isKeyPressed(int key) {
		if (!isOnUserInputThread()) {
			return CompletableFuture.supplyAsync(() -> {
				for (long windowId : this.attachedWindows) {
					if (GLFW.glfwGetKey(windowId, key) == GLFW.GLFW_PRESS) return true;
				}
				return false;
			}, getUserInputExecutor()).join();
		} else {
			for (long windowId : this.attachedWindows) {
				if (GLFW.glfwGetKey(windowId, key) == GLFW.GLFW_PRESS) return true;
			}
			return false;
		}
	}
	
	/**
	 * Returns true if the specified mouse button is pressed.
	 * 
	 * @param key The button code of the button
	 * @return true if the button is pressed
	 */
	public boolean isMouseButtonPressed(int button) {
		if (!isOnUserInputThread()) {
			return CompletableFuture.supplyAsync(() ->  {
				for (long windowId : this.attachedWindows) {
					if (GLFW.glfwGetMouseButton(windowId, button) == GLFW.GLFW_PRESS) return true;
				}
				return false;
			}, getUserInputExecutor()).join();
		} else {
			for (long windowId : this.attachedWindows) {
				if (GLFW.glfwGetMouseButton(windowId, button) == GLFW.GLFW_PRESS) return true;
			}
			return false;
		}
	}
	
	/**
	 * Checks if caps-lock is activated.
	 * @return True if caps-lock is activated
	 */
	public boolean isCapsLockOn() {
		return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
	}

	/**
	 * Checks if num-lock is activated.
	 * @return True if num-lock is activated
	 */
	public boolean isNumLockOn() {
		return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
	}

	/**
	 * Returns the position of the cursor.
	 * @return A Vec2d with the position of the cursor
	 */
	public Vec2d getCursorPosition(long windowId) {
		if (!isOnUserInputThread()) {
			return CompletableFuture.supplyAsync(() -> {
				double[] x = new double[1];
				double[] y = new double[1];
				GLFW.glfwGetCursorPos(windowId, x, y);
				return new Vec2d(x[0], y[0]);
			}, getUserInputExecutor()).join();
		} else {
			double[] x = new double[1];
			double[] y = new double[1];
			GLFW.glfwGetCursorPos(windowId, x, y);
			return new Vec2d(x[0], y[0]);
		}
	}
	
}
