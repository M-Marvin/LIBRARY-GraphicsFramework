package de.m_marvin.openui.design1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.m_marvin.archiveutility.ArchiveUtility;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;

public enum ResourceFolders implements ISourceFolder {
	
	SHADERS("shaders"),
	TEXTURES("textures");

	public static final String ASSETS_PACKAGE = "/de/m_marvin/openui/design1/assets/";
	public static final ArchiveUtility ARCHIVE_ACCESS = new ArchiveUtility(ResourceFolders.class);
	
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
		InputStream is = ARCHIVE_ACCESS.openFile(path);
		if (is == null) throw new FileNotFoundException("Could not find resource " + path + "!");
		return is;
	}

	@Override
	public String[] listFiles(String path) {
		return ARCHIVE_ACCESS.listFiles(path);
	}

	@Override
	public String[] listFolders(String path) {
		return ARCHIVE_ACCESS.listFolders(path);
	}

}
