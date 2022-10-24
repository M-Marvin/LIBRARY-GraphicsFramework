package de.m_marvin.renderengine.inputbinding.bindingsource;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.renderengine.inputbinding.IBinding;

public class MouseSource {
	
	protected static Map<Integer, IBinding> bindingCache = new HashMap<>();
	
	public static record MouseInput(int key) implements IBinding {
		
		@Override
		public boolean isPressed(long window) {
			return GLFW.glfwGetMouseButton(window, this.key) == GLFW.GLFW_PRESS;
		}
		
	}
	
	public static IBinding getKey(int key) {
		if (!bindingCache.containsKey(key)) {
			bindingCache.put(key, new MouseInput(key));
		}
		return bindingCache.get(key);
	}
	
}
