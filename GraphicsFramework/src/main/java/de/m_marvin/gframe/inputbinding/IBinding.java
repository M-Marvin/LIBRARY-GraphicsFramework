package de.m_marvin.gframe.inputbinding;

/**
 * Represents some type of user-input for a key-binding.
 * @author Marvin Köhler
 *
 */
public interface IBinding {
	/**
	 * Returns true as long as the key is pressed and the specified window is focused
	 * @param window The GLFW window handle id
	 * @return True as long as the key is pressed on the specified window
	 */
	public boolean isPressed(long window);
}
