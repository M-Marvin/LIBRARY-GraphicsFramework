package de.m_marvin.renderengine.resources;

import java.io.File;

@FunctionalInterface
public interface ISourceFolder {
	
	public File getPath(ResourceLoader<?, ?> loader, String namespace);
	
}
