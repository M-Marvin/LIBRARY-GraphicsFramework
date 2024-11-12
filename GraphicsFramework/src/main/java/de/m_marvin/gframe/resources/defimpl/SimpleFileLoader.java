package de.m_marvin.gframe.resources.defimpl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.m_marvin.gframe.resources.IClearableLoader;
import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.gframe.resources.ISourceFolder;
import de.m_marvin.gframe.resources.ResourceLoader;
import de.m_marvin.simplelogging.Log;

/**
 * Handles loading of simple files from resources.
 * Intended to be extended.
 * 
 * @author Marvin Köhler
 *
 * @param <R>
 * @param <FE>
 */
public abstract class SimpleFileLoader<R extends IResourceProvider<R>, FE extends ISourceFolder, T> implements IClearableLoader {
	
	protected String itemName;
	protected String fileFormat;
	protected FE sourceFolder;
	protected ResourceLoader<R, FE> resourceLoader;

	protected Map<R, T> itemCache = new HashMap<>();
	
	/**
	 * Creates a new file loader.
	 * @param sourceFolder The source folder
	 * @param resourceLoader The resource loader used for the file access
	 * @param fileFormat The file name ending format
	 * @param itemName The name of the "items" loaded by this loader that appears in the log messages, shader for example use "shader", models "model".
	 */
	public SimpleFileLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader, String fileFormat, String itemName) {
		super();
		this.itemName = itemName;
		this.fileFormat = fileFormat;
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	@Override
	public void clearCached() {
		this.itemCache.clear();
	}
	
	/**
	 * Loads all files in the given folder and caches them.
	 * 
	 * @param folderLocation The location of the folder
	 * @param recursive How deep to search in sub-folders
	 */
	public void loadFilesIn(R folderLocation, int recursive) {
		try {
			loadFilesIn0(folderLocation, recursive);
		} catch (IOException e) {
			Log.defaultLogger().warn("Failed to load some of the %s files from %s!", this.itemName, folderLocation.nameString(), e);
		}
	}
	
	/**
	 * Loads all files in the given folder and caches them.
	 * Non-catch-block version of {@link #loadFilesIn(loadModelsIn)}.
	 * 
	 * @param folderLocation The location of the folder
	 * @param recursive How deep to search in sub-folders
	 * @throws IOException If an error occurs in the loading of a shader
	 */
	public void loadFilesIn0(R folderLocation, int recursive) throws IOException {
		
		for (R fileLoc : this.resourceLoader.listFilesInAllNamespaces(sourceFolder, folderLocation)) {
			
			String filePath = fileLoc.getPath();
			String fileName = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
			if (!fileName.endsWith(this.fileFormat)) continue;
			
			R locationName = folderLocation.locationOfFile(fileName.substring(0, fileName.lastIndexOf('.')));
			if (loadFile(locationName) == null) {
				Log.defaultLogger().warn("Failed to load %s file '%s'!", this.itemName, fileName);
			}
			
		}
		
		if (recursive > 0) {
			for (R folderLoc : this.resourceLoader.listFoldersInAllNamespaces(sourceFolder, folderLocation)) {
				loadFilesIn0(folderLoc, recursive--);
			}
		}
		
	}

	/**
	 * Returns the item cached under the given name.
	 * 
	 * @param location The model name
	 * @return The item under the given name or null if no item was found
	 */
	public T getItem(R location) {
		if (!this.itemCache.containsKey(location)) {
			Log.defaultLogger().warn("%s %s does not exist!", this.itemName, location.nameString());
			this.itemCache.put(location, null);
		}
		return this.itemCache.get(location);
	}
	
	/**
	 * Returns a set containing the resource locations of all loaded items.
	 * 
	 * @return A set containing the resource locations of all loaded items
	 */
	public Set<R> getCachedItems() {
		return this.itemCache.keySet();
	}
	
	/**
	 * Loads the model at the given location and stores it in the cache.
	 * 
	 * @param location The location of the item file
	 * @return The loaded and cached item
	 */
	public T loadFile(R location) {
		if (!this.itemCache.containsKey(location)) {
			try {
				T item = load(location);
				this.itemCache.put(location, item);
			} catch (IOException e) {
				Log.defaultLogger().warn("Failed to load %s file %s", this.itemName, location, e);
			}
		}
		return this.itemCache.get(location);
	}
	
	/**
	 * Loads but does not cache the file from the resources.
	 * 
	 * @param fileLoc The file location
	 * @return The loaded item
	 * @throws IOException If an error occurs accessing the files
	 */
	public abstract T load(R fileLoc) throws IOException;
	
}
