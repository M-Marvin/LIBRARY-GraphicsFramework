package de.m_marvin.renderengine.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.voxelengine.utility.SimpleLoader;
import de.m_marvin.voxelengine.world.VoxelComponent;

public class VoxelComponentLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> extends SimpleLoader<R, FE, VoxelComponent> {

	public VoxelComponentLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		super(sourceFolder, resourceLoader, true);
	}
	
	@Override
	public VoxelComponent load(File path) throws IOException {
		
		InputStream inputStream = new FileInputStream(path);
		
		
		
		inputStream.close();
		
		return null;
		
	}
	
}
