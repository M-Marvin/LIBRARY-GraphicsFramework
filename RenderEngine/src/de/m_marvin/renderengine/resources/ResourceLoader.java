package de.m_marvin.renderengine.resources;

import java.io.File;

public class ResourceLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> {
	
	public File getResourceFolderPath(FE folder) {
		return folder.getPath(this, "");
	}
	public File getResourceFolderPath(FE folder, String namespace) {
		return folder.getPath(this, namespace);
	}
	
	public File resolveLocation(FE folder, R resourceProvoider) {
		return new File(this.getResourceFolderPath(folder, resourceProvoider.getNamespace()), resourceProvoider.getPath());
	}
	
	public static String getRuntimeFolder() {
		return ResourceLoader.class.getClassLoader().getResource("").getPath();
	}
	
}
