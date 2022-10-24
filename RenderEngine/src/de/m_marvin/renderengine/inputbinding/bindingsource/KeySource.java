package de.m_marvin.renderengine.inputbinding.bindingsource;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.renderengine.inputbinding.IBinding;

public class KeySource {
	
	protected static Map<Integer, IBinding> bindingCache = new HashMap<>();
	
	public static record KeyInput(int key) implements IBinding {

		@Override
		public boolean isPressed(long window) {
			return GLFW.glfwGetKey(window, this.key) == GLFW.GLFW_PRESS;
		}
		
	}
	
	public static IBinding getKey(int key) {
		if (!bindingCache.containsKey(key)) {
			bindingCache.put(key, new KeyInput(key));
		}
		return bindingCache.get(key);
	}
	
}
