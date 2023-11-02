package de.m_marvin.enginetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.BiFunction;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum ResourceFolders implements ISourceFolder {
	
	SHADERS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/shaders/")),
	TEXTURES((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/textures/")),
	MODELS((loader, namespace) -> new File(ResourceLoader.getRuntimeFolder(), namespace + "/models/"));
	
	private BiFunction<ResourceLoader<?, ?>, String, File> pathResolver;
	
	private ResourceFolders(BiFunction<ResourceLoader<?, ?>, String, File> pathResolver) {
		this.pathResolver = pathResolver;
	}
	
	@Override
	public File getPath(ResourceLoader<?, ?> loader, String namespace) {
		return this.pathResolver.apply(loader, namespace);
	}
	
	@Override
	public InputStream getAsStream(String path) throws FileNotFoundException {
		return new FileInputStream(path);
	}

	@Override
	public String[] listFiles(String path) {
		File folder = new File(path);
		if (folder.isDirectory()) return folder.list();
		return new String[] {};
	}
	
}
