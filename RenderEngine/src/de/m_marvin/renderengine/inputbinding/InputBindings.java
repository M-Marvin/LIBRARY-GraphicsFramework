package de.m_marvin.renderengine.inputbinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class InputBindings implements GLFWKeyCallbackI {
	
	protected Map<String, InputSet> bindings = new HashMap<>();
	protected List<Long> attachedWindows = new ArrayList<>();
	protected List<KeyEventConsumer> keyboardListeners = new ArrayList<>();
	
	@FunctionalInterface
	public static interface KeyEventConsumer {
		public void keyEvent(int key, int scancode, boolean pressed, boolean repeated);
	}
	
	public void addKeyboardListener(KeyEventConsumer eventConsumer) {
		this.keyboardListeners.add(eventConsumer);
	}
	
	public void removeKeyboardListener(KeyEventConsumer eventConsumer) {
		this.keyboardListeners.remove(eventConsumer);
	}
	
	public void detachWindow(long window) {
		this.attachedWindows.remove(window);
		GLFW.glfwSetKeyCallback(window, null);
	}
	
	public void attachToWindow(long window) {
		this.attachedWindows.add(window);
		GLFW.glfwSetKeyCallback(window, this);
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

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		this.keyboardListeners.forEach((listener) -> listener.keyEvent(key, scancode, action == GLFW.GLFW_PRESS, action == GLFW.GLFW_REPEAT));
	}
		
}
