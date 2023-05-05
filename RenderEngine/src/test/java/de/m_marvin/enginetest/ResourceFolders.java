package de.m_marvin.enginetest;

import java.io.File;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum ResourceFolders implements ISourceFolder {
	
	SHADERS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder() + "/assets", namespace + "/shaders/")),
	TEXTURES((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder() + "/assets", namespace + "/textures/")),
	MODELS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder() + "/assets", namespace + "/models/")),
	VOXELS((loader, namespace) -> new File(new File(ResourceLoader.getRuntimeFolder()), "/voxels/"));
	
	private ISourceFolder pathSource;
	
	private ResourceFolders(ISourceFolder pathSource) {
		this.pathSource = pathSource;
	}
	
	@Override
	public File getPath(ResourceLoader<?, ?> loader, String namespace) {
		return this.pathSource.getPath(loader, namespace);
	}

}
