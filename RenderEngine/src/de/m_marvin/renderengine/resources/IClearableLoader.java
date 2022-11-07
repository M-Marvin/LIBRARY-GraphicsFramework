package de.m_marvin.renderengine.resources;

/**
 * Implemented by resource loaders that cache their loaded data.
 * Implements a method used to clear the loaded data to cleanup memory ore reload the data.
 * 
 * @author Marvin Köhler
 */
public interface IClearableLoader {
	
	/**
	 * Called used to clear the loaded data to cleanup memory ore reload the data.
	 */
	public void clearCached();
	
}
