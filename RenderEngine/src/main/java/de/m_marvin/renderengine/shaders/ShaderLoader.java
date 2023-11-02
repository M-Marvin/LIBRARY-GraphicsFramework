package de.m_marvin.renderengine.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.m_marvin.renderengine.resources.IClearableLoader;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.VertexFormat;
import de.m_marvin.simplelogging.printing.LogType;
import de.m_marvin.simplelogging.printing.Logger;

/**
 * Handles the loading of shaders from files.
 * 
 * @author Marvin Koehler
 *
 * @param <R> The type of the resource locations
 * @param <FE> The implementation of the source folder list
 */
public class ShaderLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> implements IClearableLoader {
	
	public static final String VERTEX_SHADER_FORMAT = "vsh";
	public static final String FRAGMENT_SHADER_FORMAT = "fsh";
	public static final String GEOMETRY_SHADER_FORMAT = "gsh";
	public static final String SHADER_META_FORMAT = "json";
	public static final String SHADER_LIB_FORMAT = "glsl";
	protected static final String INCLUDE_LINE = "#include ";
	
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;

	protected Map<R, ShaderInstance> shaderCache = new HashMap<>();
	
	/**
	 * Creates a new shader loader.
	 * @param sourceFolder The source folder
	 * @param resourceLoader The resource loader used for the file access
	 */
	public ShaderLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	@Override
	public void clearCached() {
		this.shaderCache.values().forEach(ShaderInstance::delete);
		this.shaderCache.clear();
	}
	
	/**
	 * Loads all shaders in the given folder and caches them.
	 * @param shaderFolderLocation The location of the folder
	 * @param libFolderLocation The location of the source folder containing the GLSL library files
	 */
	public void loadShadersIn(R shaderFolderLocation, R libFolderLocation) {
		try {
			loadShadersIn0(shaderFolderLocation, libFolderLocation);
		} catch (IOException e) {
			Logger.defaultLogger().logWarn("Failed to load some of the shaders from " + shaderFolderLocation.toString() + "!");
			Logger.defaultLogger().printException(LogType.WARN, e);
		}
	}
	
	/**
	 * Loads all shaders in the given folder and caches them.
	 * Non-catch-block version of {@link #loadShadersIn(IResourceProvider)}.
	 * 
	 * @param shaderFolderLocation The location of the folder
	 * @param libFolderLocation The location of the source folder containing the GLSL library files
	 * @throws IOException If an error occurs in the loading of a shader
	 */
	public void loadShadersIn0(R shaderFolderLocation, R libFolderLocation) throws IOException {
		
		// FIXME Rework all "loadIn" methods
		for (String shaderName : resourceLoader.listFilesIn(sourceFolder, shaderFolderLocation)) {
			
			R locationName = shaderFolderLocation.locationOfFile(shaderName.split("\\.")[0]);
			loadShader(locationName, libFolderLocation, locationName, Optional.empty());
			
		}
		
	}

	/**
	 * Lists all shaders in the given folders.
	 * 
	 * @param shaderFolder The shader source folder
	 * @return A list of all shader names in the folder
	 * @throws FileNotFoundException if the path is not valid
	 */
	protected static List<String> listShaderNames(File shaderFolder) throws FileNotFoundException {
		if (!shaderFolder.isDirectory()) throw new FileNotFoundException("The shader folder path '" + shaderFolder + "' ist not valid!");
		List<String> shaderNames = new ArrayList<>();
		for (String fileName : shaderFolder.list()) {
			String[] fileNameParts = fileName.split("\\.");
			if (fileNameParts[fileNameParts.length - 1].equals(SHADER_LIB_FORMAT)) continue;
			if (fileNameParts.length > 1) {
				int formatEndingLength = fileNameParts[fileNameParts.length - 1].length() + 1;
				String shaderName = fileName.substring(0, fileName.length() - formatEndingLength);
				if (!shaderNames.contains(shaderName)) shaderNames.add(shaderName);
			}
		}
		return shaderNames;
	}
	
	/**
	 * Loads the shader at the given location and stores its {@link ShaderInstance} in the cache.
	 * The shaderName parameter can be used to store the shader under a different name than its path.
	 * This allows loading a shader multiple times with different vertex formats.
	 * 
	 * @param shaderLocation The location of the shader JSON file
	 * @param libFolderLocation The location of the source folder containing the GLSL library files
	 * @param shaderName The name under which the shader instance gets cached
	 * @param format The vertex format applied to the shader instance
	 * @return The loaded and cached shader instance
	 */
	public ShaderInstance loadShader(R shaderLocation, R libFolderLocation, R shaderName, Optional<VertexFormat> format) {
		if (!shaderCache.containsKey(shaderName)) {
			try {
				shaderCache.put(shaderName, load(shaderLocation, libFolderLocation, format));
			} catch (IOException e) {
				Logger.defaultLogger().logWarn("Failed to load shader " + shaderLocation.toString());
				Logger.defaultLogger().printException(LogType.WARN, e);
				return null;
			}
		}
		return shaderCache.get(shaderName);
	}
	
	/**
	 * Returns the shader cached under the given name or tries to load it if no shader is found under the name.
	 * 
	 * @param shaderName The shader name
	 * @param libFolderLocation The location of the source folder containing the GLSL library files
	 * @return The shader under the given name or null if no shader was found
	 */
	public ShaderInstance getOrLoadShader(R shaderName, R libFolderLocation, Optional<VertexFormat> format) {
		if (!this.shaderCache.containsKey(shaderName)) {
			return loadShader(shaderName, libFolderLocation, shaderName, format);
		}
		return this.shaderCache.get(shaderName);
	}
	
	/**
	 * Returns the shader cached under the given name.
	 * 
	 * @param shaderName The shader name
	 * @return The shader under the given name or null if no shader was found
	 */
	public ShaderInstance getShader(R shaderName) {
		return this.shaderCache.get(shaderName);
	}
	
	/**
	 * Returns a set containing the resource locations of all loaded shaders.
	 * 
	 * @return A set containing the resource locations of all loaded shaders
	 */
	public Set<R> getCachedShaders() {
		return this.shaderCache.keySet();
	}
	
	/**
	 * Loads the shader under the given path with the given vertex format.
	 * If no vertex format is specified the default from the shader JSON is applied.
	 * 
	 * @param shaderLocation The path to the shader JSON (without the .json ending)
	 * @param libFolderLocation The path to the source folder containing the GLSL library files
	 * @param vertexFormat The (optional) applied vertex format
	 * @return The loaded uncached shader instance
	 * @throws IOException If an error occurs accessing the files
	 */
	public ShaderInstance load(R shaderLocation, R libFolderLocation, Optional<VertexFormat> vertexFormat) throws IOException {
		
		try {

			Gson gson = new GsonBuilder().create();
			InputStreamReader inputStream = new InputStreamReader(resourceLoader.getAsStream(sourceFolder, shaderLocation.append("." + SHADER_META_FORMAT)));
			
			JsonObject json = gson.fromJson(inputStream, JsonObject.class);
			
			String vertexShaderFile = json.get("VertexShaderFile").getAsString();
			String fragmentShaderFile = json.get("FragmentShaderFile").getAsString();
			Optional<String> geometryShaderFile = json.has("GeometryShaderFile") ? Optional.of(json.get("GeometryShaderFile").getAsString()) : Optional.empty();
			String vertexShaderSource = loadGLSLFile(libFolderLocation, shaderLocation.getParent().locationOfFile(vertexShaderFile + "." + VERTEX_SHADER_FORMAT));
			String fragmentShaderSource = loadGLSLFile(libFolderLocation, shaderLocation.getParent().locationOfFile(fragmentShaderFile + "." + FRAGMENT_SHADER_FORMAT));
			Optional<String> geometryShaderSource = geometryShaderFile.isPresent() ? Optional.of(loadGLSLFile(libFolderLocation, shaderLocation.getParent().locationOfFile(geometryShaderFile.get() + "." + GEOMETRY_SHADER_FORMAT))) : Optional.empty();
			
			VertexFormat attributeFormat = vertexFormat.isPresent() ? vertexFormat.get() : null;
			if (vertexFormat.isEmpty()) {

				JsonArray defaultFormatArray = json.get("Attributes").getAsJsonArray();
				attributeFormat = new VertexFormat();
				
				for (int i = 0; i < defaultFormatArray.size(); i++) {
					JsonObject elementJson = defaultFormatArray.get(i).getAsJsonObject();
					String name = elementJson.get("Name").getAsString();
					NumberFormat format = NumberFormat.byName(elementJson.get("Type").getAsString());
					int count = elementJson.get("Count").getAsInt();
					if (count > 4) throw new IllegalArgumentException("Failed to parse shader json '" + shaderLocation.nameString() +"': A attribute can not be larger than 4. Larger attributes should be split up into multiple attributes directly behind each other.");
					boolean normalize = elementJson.get("Normalize").getAsBoolean();
					
					attributeFormat.appand(name, format, count, normalize);
				}
				
			}
			
			try {
				
				ShaderInstance shaderInstance = new ShaderInstance(vertexShaderSource, fragmentShaderSource, geometryShaderSource, attributeFormat);
				
				JsonArray uniformArray = json.get("Uniforms").getAsJsonArray();
				for (int i = 0; i < uniformArray.size(); i++) {
					JsonObject uniformJson = uniformArray.get(i).getAsJsonObject();
					String uniformName = uniformJson.get("Name").getAsString();
					UniformType type = UniformType.byName(uniformJson.get("Type").getAsString());
					JsonElement defaultValueJson = uniformJson.get("Value");
					Object defaultValue = gson.fromJson(defaultValueJson, type.getValueType());
					shaderInstance.createUniform(uniformName, type, defaultValue);
				}
				
				inputStream.close();
				
				return shaderInstance;
				
			} catch (IllegalArgumentException e) {
				throw new IOException("Failed to load shader '" + shaderLocation.nameString() + "' with exception:\n" + e.getMessage());
			}
			
		} catch (NullPointerException e) {
			throw new IOException("Failed to load shader definition file '" + shaderLocation.nameString() + "'! Maleformed JSON!");
		}
		
	}
	
	/**
	 * Loads GLSL code from a library file.
	 * Resolves any #includes in the library file.
	 * Used by {@link #load(File, Optional)}.
	 * 
	 * @param libFolderLocation The folder containing the library files
	 * @param fileLocation The name of the library to load
	 * @return The GLSL code of the library with all #includes resolved
	 * @throws IOException If an error occurs accessing the files
	 */
	protected String loadGLSLFile(R libFolderLocation, R fileLocation) throws IOException {
		String line;
		BufferedReader vertexShaderInputStream = new BufferedReader(new InputStreamReader(resourceLoader.getAsStream(sourceFolder, fileLocation)));
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = vertexShaderInputStream.readLine()) != null) {
			if (line.startsWith(INCLUDE_LINE)) {
				String includeCode = loadGLSLFile(libFolderLocation, libFolderLocation.locationOfFile(line.substring(INCLUDE_LINE.length()) + "." + SHADER_LIB_FORMAT));
				stringBuilder.append(includeCode);
			} else {
				stringBuilder.append(line + "\n");
			}
		}
		vertexShaderInputStream.close();
		return stringBuilder.toString();
	}
	
}
