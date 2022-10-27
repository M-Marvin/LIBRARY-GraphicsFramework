package de.m_marvin.renderengine;

import java.io.File;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum TestSourceFolders implements ISourceFolder {
	
	TEXTURES((loader) -> new File(ResourceLoader.getRuntimeFolder(), "textures/")),
	SHADERS((loader) -> new File(ResourceLoader.getRuntimeFolder(), "shaders/"));
	
	private final ISourceFolder resolver;
	
	TestSourceFolders(ISourceFolder resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public File getPath(ResourceLoader<?, ?> loader) {
		return this.resolver.getPath(loader);
	}

}
