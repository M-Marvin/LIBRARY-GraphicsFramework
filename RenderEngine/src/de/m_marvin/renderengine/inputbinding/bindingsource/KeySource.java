package de.m_marvin.renderengine.inputbinding.bindingsource;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.renderengine.inputbinding.IBinding;
import de.m_marvin.renderengine.inputbinding.UserInput;

/**
 * Provides methods to find Keyboard keys and add them as argument to the {@link UserInput} bindings.
 * 
 * @author Marvin Köhler
 */
public class KeySource {
	
	protected static Map<Integer, IBinding> bindingCache = new HashMap<>();
	
	/**
	 * IBinding instance representing a keyboard key.
	 * 
	 * @author Marvin Köhler
	 */
	public static record KeyInput(int key) implements IBinding {
		
		@Override
		public boolean isPressed(long window) {
			return GLFW.glfwGetKey(window, this.key) == GLFW.GLFW_PRESS;
		}
		
	}
	
	/**
	 * Return a {@link IBinding} for the given key.
	 * @implNote The IBinding instances get cached, means every key has only one instance.
	 * 
	 * @param key The GLFW key-id
	 * @return A IBinding instance which can be used as parameter for the {@link UserInput}
	 */
	public static IBinding getKey(int key) {
		if (!bindingCache.containsKey(key)) {
			bindingCache.put(key, new KeyInput(key));
		}
		return bindingCache.get(key);
	}
	
}
