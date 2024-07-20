package de.m_marvin.gframe.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This interface is intended to be implemented by an enum or something similar, that lists all resource-folders that are used in the application.
 * It provides a single method that should be used to return the complete path to the source folder overriding it.
 * The method gets feed with the ResourceLoader and the namespace that currently wants to get the full path name.
 * The namespace has (if used) to be inserted in the path in this function.
 * 
 * @author Marvin Koehler
 */
public interface ISourceFolder {
	
	/**
	 * Builds the complete path to the resource folder for the given namespace.
	 * 
	 * @param loader The {@link ResourceLoader} used with this resource folder
	 * @param namespace The namespace requesting its resource folder
	 * @return The full path to the resource folder
	 */
	public String getPath(ResourceLoader<?, ?> loader, String namespace);

	/**
	 * Loads an InputStream of the given resource.
	 * 
	 * @param path The path to the resource, in most cases the return value of getPath()
	 * @return An InputStream of the given resource
	 * @throws FileNotFoundException if the resource does not exist
	 */
	public InputStream getAsStream(String path) throws IOException;
	
	/**
	 * Lists all files contained in the given folder.
	 * Returns an empty array if the folder does not exist.
	 * 
	 * @param path  The path to the folder
	 * @return A list of files contained in the given folder
	 */
	public String[] listFiles(String path);
	
	/**
	 * Lists all sub-folders contained in the given folder.
	 * Returns an empty array if the folder does not exist.
	 * 
	 * @param path  The path to the folder
	 * @return A list of sub-folders contained in the given folder
	 */
	public String[] listFolders(String path);

	
	/**
	 * Lists all namespaces available for this folder
	 * 
	 * @return A list of Strings naming all namespaces that exist for this folder
	 */
	public String[] listNamespaces();
	
}
