package de.m_marvin.renderengine.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;

/**
 * Handles model loading from OBJ files.
 *  * 
 * @author Marvin Köhler
 *
 * @param <R>
 * @param <FE>
 */
public class OBJLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> {
	
	public static final String MODEL_FILE_FORMAT = "obj";
	public static final String MATERIAL_FILE_FORMAT = "mtl";
	
	protected FE sourceFolder;
	protected ResourceLoader<R, FE> resourceLoader;

	protected Map<R, RawModel<R>> modelCache = new HashMap<>();
	
	/**
	 * Creates a new model loader.
	 * @param sourceFolder The source folder
	 * @param resourceLoader The resource loader used for the file access
	 */
	public OBJLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		super();
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	/**
	 * Loads all models in the given folder and caches them.
	 * @param modelFolderLocation The location of the folder
	 */
	public void loadModelsIn(R modelFolderLocation, R textureFolderLocation) {
		try {
			loadModelsIn0(modelFolderLocation, textureFolderLocation);
		} catch (IOException e) {
			System.err.println("Failed to load some of the shaders from " + modelFolderLocation.toString() + "!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all models in the given folder and caches them.
	 * Non-catch-block version of {@link #loadShadersIn(loadModelsIn)}.
	 * 
	 * @param modelFolderLocation The location of the folder
	 * @throws IOException If an error occurs in the loading of a shader
	 */
	public void loadModelsIn0(R modelFolderLocation, R textureFolderLocation) throws IOException {
		
		File path = resourceLoader.resolveLocation(sourceFolder, modelFolderLocation);
		for (String modelName : listModelNames(path)) {
			
			R locationName = modelFolderLocation.locationOfFile(modelName);
			if (loadModel(locationName, textureFolderLocation) == null) {
				System.err.println("Failed to load model '" + modelName + "'!");
			}
			
		}
		
	}

	/**
	 * Lists all models in the given folders.
	 * 
	 * @param modelFolder The model source folder
	 * @return A list of all model names in the folder
	 * @throws FileNotFoundException if the path is not valid
	 */
	protected static List<String> listModelNames(File modelFolder) throws FileNotFoundException {
		if (!modelFolder.isDirectory()) throw new FileNotFoundException("The model folder path '" + modelFolder + "' ist not valid!");
		List<String> modelNames = new ArrayList<>();
		for (String fileName : modelFolder.list()) {
			String[] fileNameParts = fileName.split("\\.");
			if (fileNameParts[fileNameParts.length - 1].equals(MATERIAL_FILE_FORMAT)) continue;
			if (fileNameParts.length > 1) {
				int formatEndingLength = fileNameParts[fileNameParts.length - 1].length() + 1;
				String modelName = fileName.substring(0, fileName.length() - formatEndingLength);
				if (!modelNames.contains(modelName)) modelNames.add(modelName);
			}
		}
		return modelNames;
	}
	
	/**
	 * Returns the model cached under the given name.
	 * 
	 * @param modelLocation The model name
	 * @return The model under the given name or null if no model was found
	 */
	public RawModel<R> getModel(R modelLocation) {
		return this.modelCache.get(modelLocation);
	}
	
	/**
	 * Loads the model at the given location and stores it in the cache.
	 * 
	 * @param modelLocation The location of the model OBJ file
	 * @param textureFolderLocation The location of the folder to search for the textures
	 * @return The loaded and cached model instance
	 */
	public RawModel<R> loadModel(R modelLocation, R textureFolderLocation) {
		if (!this.modelCache.containsKey(modelLocation)) {
			File modelPath = this.resourceLoader.resolveLocation(sourceFolder, modelLocation);
			try {
				RawModel<R> model = load(modelPath, textureFolderLocation);
				this.modelCache.put(modelLocation, model);
			} catch (IOException e) {
				System.err.println("Failed to load model " + modelLocation);
				e.printStackTrace();
			}
		}
		return this.modelCache.get(modelLocation);
	}
	
	/**
	 * Loads but does not cache the model from the files specified.
	 * Texture paths are build with the provided location.
	 * 
	 * @param <R> The type of resource locations
	 * @param modelFile The model files
	 * @param textureFolderLocation 
	 * @return The loaded model
	 * @throws IOException If an error occurs accessing the files
	 */
	public static <R extends IResourceProvider<R>> RawModel<R> load(File modelFile, R textureFolderLocation) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(modelFile + "." + MODEL_FILE_FORMAT)));
		
		RawModel<R> model = new RawModel<R>();
		
		Map<String, String> material2texture = new HashMap<>();
		String currentObjectName = "default";
		String currentTexture = null;
		
		String line;
		while ((line = reader.readLine()) != null) {
			switch (line.split(" ")[0]) {
			case "mtllib":
				loadMtlLib(new File(modelFile.getParentFile(), parseString(line)), material2texture);
				break;
			case "v":
				model.vertecies.add(parseVec3(line));
				break;
			case "vn":
				model.normals.add(parseVec3(line));
				break;
			case "vt":
				model.textureUVs.add(parseVec2(line));
				break;
			case "o":
				currentObjectName = parseString(line);
				break;
			case "f":
				Vec3i[] indexes = parseFace(line);
				List<RawModel.ModelFace<R>> faces = model.modelObjects.get(currentObjectName);
				if (faces == null) {
					faces = new ArrayList<>();
					model.modelObjects.put(currentObjectName, faces);
				}
				String textureName = material2texture.get(currentTexture);
				if (textureName != null) {
					String[] tns = textureName.split("\\.");
					textureName = textureName.substring(0, textureName.length() - tns[tns.length - 1].length() - 1);
				} else {
					textureName = "none";
				}
				faces.add(new RawModel.ModelFace<R>(textureFolderLocation.locationOfFile(textureName), indexes));
				break;
			case "usemtl":
				currentTexture = parseString(line);
				break;
			}
		}
		
		reader.close();
		
		return model;
	}
	
	/**
	 * Loads the content of the mtl-lib into the material to texture name map.
	 * 
	 * @param mtlFile The material lib file
	 * @param material2texture The map to fill
	 * @throws IOException If an error occurs accessing the file
	 */
	protected static void loadMtlLib(File mtlFile, Map<String, String> material2texture) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(mtlFile));
		
		String currentMtlName = null;
		String currentTextureName = null;
		
		String line = "";
		while ((line = reader.readLine()) != null) {
			switch (line.split(" ")[0]) {
			case "newmtl":
				if (currentMtlName != null) {
					material2texture.put(currentMtlName, currentTextureName);
					currentTextureName = null;
				}
				currentMtlName = parseString(line);
				break;
			case "map_Kd":
				currentTextureName = parseString(line);
				break;
			}
		}
		
		reader.close();
		
	}
	
	protected static Vec3i[] parseFace(String s) {
		String[] is = s.split(" ");
		if (is.length < 4) throw new IllegalArgumentException("Maleformed face '" + s + "' in source file!");
		Vec3i[] indexes = new Vec3i[is.length - 1];
		for (int i = 1; i < is.length; i++) {
			String[] vs = is[i].split("/");
			if (vs.length != 3) throw new IllegalArgumentException("Maleformed face '" + s + "' in source file!");
			indexes[i - 1] = new Vec3i(Integer.parseInt(vs[0]) - 1, Integer.parseInt(vs[1]) - 1, Integer.parseInt(vs[2]) - 1);
		}
		return indexes;
	}
	
	protected static String parseString(String s) {
		String[] sa = s.split(" ");
		if (sa.length != 2) throw new IllegalArgumentException("Maleformed string '" + s + "' in source file!");
		return sa[1];
	}
	
	protected static Vec3f parseVec3(String s) {
		String[] sa = s.split(" ");
		if (sa.length != 4) throw new IllegalArgumentException("Maleformed vec3 '" + s + "' in source file!");
		return new Vec3f(Float.parseFloat(sa[1]), Float.parseFloat(sa[2]), Float.parseFloat(sa[3]));
	}

	protected static Vec2f parseVec2(String s) {
		String[] sa = s.split(" ");
		if (sa.length != 3) throw new IllegalArgumentException("Maleformed vec2 '" + s + "' in source file!");
		return new Vec2f(Float.parseFloat(sa[1]), Float.parseFloat(sa[2]));
	}
	
}
