package de.m_marvin.renderengine.resources;

import java.io.File;

/**
 * Provides different methods to access files on the disc.
 * Can be used with one of two location-implementations that describe the location of a file without working with full paths.
 * Also custom implementations are possible by implementing the {@link IResourceProvider} interface.
 * 
 * @author Marvin Koehler
 *
 * @param <R> The type of location implementation used
 * @param <FE> The resource folder location implementation used
 */
public class ResourceLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> {
	
	public static String runFolder = null;
	
	/**
	 * Returns the full path to the given source folder without an namespace.
	 * @implNote Passes an empty string as namespace to the {@link ISourceFolder#getPath(ResourceLoader, String)} method and 
	 * should only be used if a {@link IResourceProvider} implementation is used which not requires a namespace.
	 * @param folder The resource folder
	 * @return The full path to the resource folder
	 */
	public File getResourceFolderPath(FE folder) {
		return folder.getPath(this, "");
	}
	
	/**
	 * Returns the full path to the given source folder with namespace.
	 * @implNote Should only be used if a {@link IResourceProvider} implementation is used which requires a namespace.
	 * 
	 * @param folder The resource folder
	 * @param namespace The namespace
	 * @return The full path of the resourcefolder of the namespace
	 */
	public File getResourceFolderPath(FE folder, String namespace) {
		return folder.getPath(this, namespace);
	}
	
	/**
	 * Converts the resource location into a full path.
	 * 
	 * @param folder The resource folder in which the location points
	 * @param resourceProvoider The resource location to resolve to a full path
	 * @return The full path pointing to the location specified in the resource location
	 */
	public File resolveLocation(FE folder, R resourceProvoider) {
		return new File(this.getResourceFolderPath(folder, resourceProvoider.getNamespace()), resourceProvoider.getPath());
	}
	
	/**
	 * Returns the folder in which the application is running.
	 * @implNote Uses the {@link ClassLoader#getResource(String)} method to determine the location.
	 * 
	 * @return The path to the folder in which the application is running.
	 */
	public static String getRuntimeFolder() {
		if (runFolder == null) {
			runFolder = ResourceLoader.class.getClassLoader().getResource("").getPath();
		}
		return runFolder;
	}
	
	public static void redirectRuntimeFolder(String folder) {
		runFolder = folder;
	}
	
}
