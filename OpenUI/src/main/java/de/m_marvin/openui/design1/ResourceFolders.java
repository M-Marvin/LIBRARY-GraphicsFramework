package de.m_marvin.openui.design1;

import java.io.IOException;
import java.io.InputStream;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum ResourceFolders implements ISourceFolder {
	
	SHADERS("shaders"),
	TEXTURES("textures");

	public static final String ASSETS_PACKAGE = "/de/m_marvin/openui/design1/assets/";
	
	private final String folderName;
	
	private ResourceFolders(String folderName) {
		this.folderName = folderName;
	}

	@Override
	public String getPath(ResourceLoader<?, ?> loader, String namespace) {
		return ASSETS_PACKAGE + this.folderName;
	}

	@Override
	public InputStream getAsStream(String path) throws IOException {
		return this.getClass().getClassLoader().getResourceAsStream(path);
	}

	@Override
	public String[] listFiles(String path) {
		// FIXME
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] listFolders(String path) {
		// FIXME
		throw new UnsupportedOperationException();
	}

}
