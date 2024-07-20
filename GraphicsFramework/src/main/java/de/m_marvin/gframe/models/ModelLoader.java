package de.m_marvin.gframe.models;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.gframe.resources.ISourceFolder;
import de.m_marvin.gframe.resources.ResourceLoader;
import de.m_marvin.gframe.resources.defimpl.SimpleFileLoader;

public class ModelLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> extends SimpleFileLoader<R, FE, ModelData<R>> {

	public static final String MODEL_FILE_FORMAT = "obj";
	
	public ModelLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		super(sourceFolder, resourceLoader, MODEL_FILE_FORMAT, "model");
	}

	@Override
	public ModelData<R> load(R modelFile) throws IOException {
		
		InputStream input = this.resourceLoader.getAsStream(this.sourceFolder, modelFile.append("." + MODEL_FILE_FORMAT));
		
		OBJParser<R> parser = new OBJParser<>();
		
		List<String> mtlFiles = parser.parseOBJFile(input);
		
		for (String mtlFile : mtlFiles) {
			
			R mtlLoc = modelFile.getParent().locationOfFile(mtlFile);
			
			InputStream mtlInput = this.resourceLoader.getAsStream(this.sourceFolder, mtlLoc);
			
			parser.parseMTLFile(mtlInput);
			
		}
		
		return parser.makeModelData(modelFile.getParent());
		
	}

	
	
}
