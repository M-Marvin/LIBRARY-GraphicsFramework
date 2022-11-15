package de.m_marvin.voxelengine.resources;

import java.io.File;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum ResourceFolders implements ISourceFolder {
	
	SHADERS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/shaders/")),
	TEXTURES((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/textures/")),
	MODELS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/models/"));
	
	private ISourceFolder pathSource;
	
	private ResourceFolders(ISourceFolder pathSource) {
		this.pathSource = pathSource;
	}
	
	@Override
	public File getPath(ResourceLoader<?, ?> loader, String namespace) {
		return this.pathSource.getPath(loader, namespace);
	}

}
