package de.m_marvin.renderengine.resources;

import java.io.File;
import java.io.InputStream;
import java.util.function.Function;

public class ResourceLoader<R, FE extends ISourceFolder> {
	
	protected final Function<R, String> resourceResolver;
	
	public ResourceLoader(Function<R, String> resourceResolver) {
		this.resourceResolver = resourceResolver;
	}
	
	public File getResourceFolderPath(FE folder) {
		return folder.getPath(this);
	}
	
	public File resolveLocation(FE folder, R resourceProvoider) {
		String resourcePath = this.resourceResolver.apply(resourceProvoider);
		return new File(this.getResourceFolderPath(folder), resourcePath);
	}
	
	public InputStream resolveLocationStream(FE folder, R resourceLoaction) {
		return null; // TODO
	}
	
	public static String getRuntimeFolder() {
		return ResourceLoader.class.getClassLoader().getResource("").getPath();
	}
	
}
