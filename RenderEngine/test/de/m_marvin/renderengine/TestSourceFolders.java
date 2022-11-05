package de.m_marvin.renderengine;

import java.io.File;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum TestSourceFolders implements ISourceFolder {
	
	TEXTURES((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/textures/")),
	SHADERS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/shaders/")),
	MODELS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/models/"));
	
	private final ISourceFolder resolver;
	
	TestSourceFolders(ISourceFolder resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public File getPath(ResourceLoader<?, ?> loader, String namespace) {
		return this.resolver.getPath(loader, namespace);
	}

}
