package de.m_marvin.renderengine.inputbinding;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.univec.impl.Vec2d;

public class InputBindings {
	
	protected Map<String, InputSet> bindings = new HashMap<>();
	protected List<Long> attachedWindows = new ArrayList<>();
	protected List<KeyEventConsumer> keyboardListeners = new ArrayList<>();
	protected List<MouseEventConsumer> mouseListeners = new ArrayList<>();
	protected List<TextInputConsumer> textInputListeners = new ArrayList<>();
	
	protected boolean caps = false;
	
	public InputBindings() {
		addKeyboardListener((key, scancode, pressed, repeated) -> handleTextInput(key, pressed, repeated, (character, fkey) -> this.textInputListeners.forEach((listener) -> listener.input(character, fkey))));
	}
	
	public static enum FunctionalKey {
		BACKSPACE(GLFW.GLFW_KEY_BACKSPACE),
		ENTER(GLFW.GLFW_KEY_ENTER),
		DEL(GLFW.GLFW_KEY_DELETE),
		END(GLFW.GLFW_KEY_END),
		INSERT(GLFW.GLFW_KEY_INSERT),
		POS1(-1), // FIXME
		PAGE_UP(GLFW.GLFW_KEY_PAGE_UP),
		PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN),
		KEY_RIGHT(GLFW.GLFW_KEY_RIGHT),
		KEY_LEFT(GLFW.GLFW_KEY_LEFT),
		KEY_UP(GLFW.GLFW_KEY_UP),
		KEY_DOWN(GLFW.GLFW_KEY_DOWN),
		LEFT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
		RIGHT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
		CAPS_LOCK(GLFW.GLFW_KEY_CAPS_LOCK);
		
		private final int glkey;
		
		private FunctionalKey(int glkey) {
			this.glkey = glkey;
		}
		
		public int glfwkey() {
			return this.glkey;
		}
		
		public static FunctionalKey getKey(int keycode) {
			for (FunctionalKey key : FunctionalKey.values()) {
				if (key.glfwkey() == keycode) {
					return key;
				}
			}
			return null;
		}
		
	}
	
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
	
	// TODO: Shift/Capslock does not work with all keys, Alt not implemented
	protected void handleTextInput(int keycode, boolean pressed, boolean repeated, BiConsumer<Character, Optional<FunctionalKey>> target) {
		FunctionalKey fkey = FunctionalKey.getKey(keycode);
		if (fkey != null) {
			if (!repeated && (fkey == FunctionalKey.LEFT_SHIFT || fkey == FunctionalKey.RIGHT_SHIFT)) caps = pressed;
			target.accept((char) -1, Optional.of(fkey));
		} else {
			char character = isShiftActive() ? Character.toUpperCase((char) keycode) : Character.toLowerCase((char) keycode);
			target.accept(character, Optional.empty());
		}
	}
	
	public void addKeyboardListener(KeyEventConsumer eventConsumer) {
		this.keyboardListeners.add(eventConsumer);
	}
	
	public void removeKeyboardListener(KeyEventConsumer eventConsumer) {
		this.keyboardListeners.remove(eventConsumer);
	}
	
	public void addMouseListener(MouseEventConsumer eventConsumer) {
		this.mouseListeners.add(eventConsumer);
	}
	
	public void removeMouseListener(MouseEventConsumer eventConsumer) {
		this.mouseListeners.remove(eventConsumer);
	}
	
	public void addTextInputListener(TextInputConsumer eventConsumer) {
		this.textInputListeners.add(eventConsumer);
	}
	
	public void removeTextInputListener(TextInputConsumer eventConsumer) {
		this.textInputListeners.remove(eventConsumer);
	}
	
	public void setClipboardString(String clipboard) {
		GLFW.glfwSetClipboardString(0, clipboard);
	}
	public String getClipboardString() {
		return GLFW.glfwGetClipboardString(0);
	}
	
	public void detachWindow(long windowId) {
		this.attachedWindows.remove(windowId);
		GLFW.glfwSetKeyCallback(windowId, null);		
		GLFW.glfwSetMouseButtonCallback(windowId, null);		
		GLFW.glfwSetScrollCallback(windowId, null);
	}
	
	public void attachToWindow(long windowId) {
		this.attachedWindows.add(windowId);
		GLFW.glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> this.keyboardListeners.forEach((listener) -> listener.keyEvent(key, scancode, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT)));		
		GLFW.glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> this.mouseListeners.forEach((listener) -> listener.mouseEvent(Optional.empty(), button, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT)));		
		GLFW.glfwSetScrollCallback(windowId, (window, xoffset, yoffset) ->  this.mouseListeners.forEach((listener) -> listener.mouseEvent(Optional.of(new Vec2d(xoffset, yoffset)), 0, false, false)));		
	}
	
	public InputSet registerBinding(String name) {
		InputSet inputSet = new InputSet();
		this.bindings.put(name, inputSet);
		return inputSet;
	}
	
	public Optional<InputSet> getBinding(String name) {
		return Optional.ofNullable(this.bindings.get(name));
	}
	
	public boolean isBindingActive(String name) {
		Optional<InputSet> binding = getBinding(name);
		if (binding.isPresent()) {
			for (long window : this.attachedWindows) {
				if (binding.get().isActive(window)) return true;
			}
		}
		return false;
	}
	
	public boolean isCapsLockOn() {
		return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
	}

	public boolean isNumLockOn() {
		return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
	}
	
	public boolean isShiftActive() {
		return isCapsLockOn() || caps;
	}
	
}
