package de.m_marvin.voxelengine.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.m_marvin.renderengine.resources.IClearableLoader;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.simplelogging.printing.LogType;
import de.m_marvin.simplelogging.printing.Logger;

public abstract class SimpleLoader<R extends IResourceProvider<R>, FE extends ISourceFolder, T> implements IClearableLoader {
	
	protected final boolean activeLoading;
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;

	protected Map<R, T> cache = new HashMap<>();
	
	public SimpleLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader, boolean activeLoading) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
		this.activeLoading = activeLoading;
	}
	
	@Override
	public void clearCached() {
		this.cache.clear();
	}
	
	public void loadAllIn(R folderLocation) {
		try {
			loadAllIn0(folderLocation);
		} catch (IOException e) {
			Logger.defaultLogger().logWarn("Failed to load some of the files from " + folderLocation.toString() + "!");
			Logger.defaultLogger().printException(LogType.WARN, e);
		}
	}
	
	public void loadAllIn0(R folderLocation) throws IOException {
		
		for (String name : listNames(folderLocation)) {
			
			R locationName = folderLocation.locationOfFile(name);
			load0(locationName);
			
		}
		
	}
	
	protected List<String> listNames(R folderLocation) throws FileNotFoundException {
		List<String> names = new ArrayList<>();
		for (String fileName : resourceLoader.listFilesIn(sourceFolder, folderLocation)) {
			String[] fileNameParts = fileName.split("\\.");
			if (fileNameParts.length > 1) {
				int formatEndingLength = fileNameParts[fileNameParts.length - 1].length() + 1;
				String name = fileName.substring(0, fileName.length() - formatEndingLength);
				if (!names.contains(name)) names.add(name);
			}
		}
		return names;
	}
	
	public T load0(R location) {
		if (!cache.containsKey(location)) {
			try {
				cache.put(location, load(location));
			} catch (IOException e) {
				Logger.defaultLogger().logWarn("Failed to load " + location.toString());
				Logger.defaultLogger().printException(LogType.WARN, e);
				return null;
			}
		}
		return cache.get(location);
	}
	
	public boolean save0(R location, T object) {
		try {
			if (save(location, object)) {
				cache.put(location, object);
				return true;
			} else {
				Logger.defaultLogger().logWarn("Failed to save " + location.toString());
				return false;
			}
		} catch (IOException e) {
			Logger.defaultLogger().logWarn("Failed to save " + location.toString());
			Logger.defaultLogger().printException(LogType.WARN, e);
			return false;
		}
	}
	
	public T get(R name) {
		return activeLoading ? load0(name) : this.cache.get(name);
	}
	
	public void store(R name, T object) {
		save0(name, object);
	}
	
	public Set<R> getCached() {
		return this.cache.keySet();
	}
	
	public abstract T load(R location) throws IOException;
	public abstract boolean save(R location, T object) throws IOException;
	
}