package de.m_marvin.renderengine.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.vertecies.VertexFormat;

public class ShaderLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> {
	
	public static final String VERTEX_SHADER_FORMAT = "vsh";
	public static final String FRAGMENT_SHADER_FORMAT = "fsh";
	public static final String SHADER_META_FORMAT = "json";
	public static final String SHADER_LIB_FORMAT = "glsl";
	protected static final String INCLUDE_LINE = "#include ";
	
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;
	
	public ShaderLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	public ShaderInstance load(R shaderLocation, VertexFormat format) {
		File path = resourceLoader.resolveLocation(sourceFolder, shaderLocation);
		try {
			return load(path, format);
		} catch (IOException e) {
			System.err.println("Failed to load shader " + shaderLocation.toString());
			e.printStackTrace();
			return null;
		}
	}
	
	public static ShaderInstance load(File shaderFile, VertexFormat vertexFormat) throws IOException {
		File sourceFolder = shaderFile.getParentFile();
		
		Gson gson = new GsonBuilder().create();
		InputStreamReader inputStream = new InputStreamReader(new FileInputStream(new File(shaderFile + "." + SHADER_META_FORMAT)));
		
		JsonObject json = gson.fromJson(inputStream, JsonObject.class);
		
		String vertexShaderFile = json.get("VertexShaderFile").getAsString();
		String fragmentShaderFile = json.get("FragmentShaderFile").getAsString();
		String vertexShaderSource = loadGLSLFile(sourceFolder, vertexShaderFile + "." + VERTEX_SHADER_FORMAT);
		String fragmentShaderSource = loadGLSLFile(sourceFolder, fragmentShaderFile + "." + FRAGMENT_SHADER_FORMAT);
		
		ShaderInstance shaderInstance = new ShaderInstance(vertexShaderSource, fragmentShaderSource, vertexFormat);
		
		JsonArray uniformArray = json.get("Uniforms").getAsJsonArray();
		for (int i = 0; i < uniformArray.size(); i++) {
			JsonObject uniformJson = uniformArray.get(i).getAsJsonObject();
			String uniformName = uniformJson.get("Name").getAsString();
			UniformType type = UniformType.byName(uniformJson.get("Type").getAsString());
			JsonElement defaultValueJson = uniformJson.get("Value");
			boolean definedAsArray = defaultValueJson.isJsonArray();
			Object defaultValue = gson.fromJson(defaultValueJson, type.getValueType(definedAsArray));
			shaderInstance.createUniform(uniformName, type, defaultValue);
		}
		
		inputStream.close();
		
		return shaderInstance;
	}
	
	protected static String loadGLSLFile(File sourceFolder, String fileName) throws IOException {
		String line;
		BufferedReader vertexShaderInputStream = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceFolder, fileName))));
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = vertexShaderInputStream.readLine()) != null) {
			if (line.startsWith(INCLUDE_LINE)) {
				String includeCode = loadGLSLFile(sourceFolder, line.substring(INCLUDE_LINE.length()) + "." + SHADER_LIB_FORMAT);
				stringBuilder.append(includeCode);
			} else {
				stringBuilder.append(line + "\n");
			}
		}
		vertexShaderInputStream.close();
		return stringBuilder.toString();
	}
	
}
