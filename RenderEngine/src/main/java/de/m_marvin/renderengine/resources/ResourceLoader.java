package de.m_marvin.renderengine.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.stream.Stream;

import de.m_marvin.renderengine.utility.Utility;

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
	 * Returns the path (relative to the point defined by the ISourceFolder implementation) to the given source folder without an namespace.
	 * @implNote Passes an empty string as namespace to the {@link ISourceFolder#getPath(ResourceLoader, String)} method and 
	 * should only be used if a {@link IResourceProvider} implementation is used which not requires a namespace.
	 * @param folder The resource folder
	 * @return The full path to the resource folder
	 */
	public String getResourceFolderPath(FE folder) {
		return folder.getPath(this, null);
	}
	
	/**
	 * Returns the path (relative to the point defined by the ISourceFolder implementation) to the given source folder with namespace.
	 * @implNote Should only be used if a {@link IResourceProvider} implementation is used which requires a namespace.
	 * 
	 * @param folder The resource folder
	 * @param namespace The namespace
	 * @return The full path of the resourcefolder of the namespace
	 */
	public String getResourceFolderPath(FE folder, String namespace) {
		return folder.getPath(this, namespace);
	}
	
	/**
	 * Converts the resource location into a full path.
	 * 
	 * @param folder The resource folder in which the location points
	 * @param resourceProvider The resource location to resolve to a full path
	 * @return The full path pointing to the location specified in the resource location
	 */
	public String resolveLocation(FE folder, R resourceProvider) {
		return new File(this.getResourceFolderPath(folder, resourceProvider.getNamespace()), resourceProvider.getPath()).toString();
	}

	/**
	 * Opens an InputStream to the given resource.
	 * 
	 * @param sourceFolder The resource folder in which the location points
	 * @param resourceProvider The resource location to get an InputStream for
	 * @return An InputStream of the given resource
	 * @throws FileNotFoundException if the resource does not exist
	 */
	public InputStream getAsStream(FE sourceFolder, R resourceProvider) throws IOException {
		return sourceFolder.getAsStream(resolveLocation(sourceFolder, resourceProvider));
	}
	
	/**
	 * Lists all files in the given folder
	 * 
	 * @param sourceFolder The resource folder in which the location points
	 * @param resourceProvider The resource location to the folder
	 * @return A array of file names in the folder
	 */
	public String[] listFilesIn(FE sourceFolder, R resourceProvider) {
		return sourceFolder.listFiles(resolveLocation(sourceFolder, resourceProvider));
	}

	/**
	 * Lists all sub-folders in the given folder
	 * 
	 * @param sourceFolder The resource folder in which the location points
	 * @param resourceProvider The resource location to the folder
	 * @return A array of folder names in the folder
	 */
	public String[] listFoldersIn(FE sourceFolder, R resourceProvider) {
		return sourceFolder.listFolders(resolveLocation(sourceFolder, resourceProvider));
	}
	
	/**
	 * Lists all files in the given location in all namespaces (ignoring the one provided)
	 * 
	 * @param sourceFolder The resource folder in which the location points
	 * @param resourceProvider The resource location to the folder, namespace is ignored
	 * @return A array of all files in the given location in all namespaces
	 */
	@SuppressWarnings("unchecked")
	public R[] listFilesInAllNamespaces(FE sourceFolder, R resourceProvider) {
		return Stream.of(sourceFolder.listNamespaces())
				.map(resourceProvider::withNamespace)
				.map(loc -> Stream
						.of(sourceFolder.listFiles(resolveLocation(sourceFolder, loc)))
						.map(loc::locationOfFile)
						.toArray(i -> (R[]) Array.newInstance(IResourceProvider.class, i))
				)
				.reduce((a, b) -> Utility.concatArr(a, b, IResourceProvider.class)).get();
	}
	
	/**
	 * Lists all sub-folders in the given location in all namespaces (ignoring the one provided)
	 * 
	 * @param sourceFolder The resource folder in which the location points
	 * @param resourceProvider The resource location to the folder, namespace is ignored
	 * @return A array of all sub-folders in the given location in all namespaces
	 */
	@SuppressWarnings("unchecked")
	public R[] listFoldersInAllNamespaces(FE sourceFolder, R resourceProvider) {
		return Stream.of(sourceFolder.listNamespaces())
				.map(resourceProvider::withNamespace)
				.map(loc -> Stream
						.of(sourceFolder.listFolders(resolveLocation(sourceFolder, loc)))
						.map(loc::locationOfFile)
						.toArray(i -> (R[]) Array.newInstance(IResourceProvider.class, i))
				)
				.reduce((a, b) -> Utility.concatArr(a, b, IResourceProvider.class)).get();
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
