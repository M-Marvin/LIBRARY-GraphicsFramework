package de.m_marvin.renderengine.resources;

import java.io.File;

/**
 * This interface is intended to be implemented by an enum or something similar, that lists all resource-folders that are used in the application.
 * It provides a single method that should be used to return the complete path to the source folder overriding it.
 * The method gets feed with the ResourceLoader and the namespace that currently wants to get the full path name.
 * The namespace has (if used) to be inserted in the path in this function.
 * 
 * @author Marvin Koehler
 */
@FunctionalInterface
public interface ISourceFolder {
	
	/**
	 * Builds the complete path to the resource folder for the given namespace.
	 * 
	 * @param loader The {@link ResourceLoader} used with this resource folder
	 * @param namespace The namespace requesting its resource folder
	 * @return The full path to the resource folder
	 */
	public File getPath(ResourceLoader<?, ?> loader, String namespace);
	
}
