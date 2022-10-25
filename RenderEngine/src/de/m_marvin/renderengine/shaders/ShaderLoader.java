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

import de.m_marvin.renderengine.vertecies.VertexFormat;

public class ShaderLoader {
	
	public static ShaderInstance load(File shaderFile, VertexFormat vertexFormat) throws IOException {
		File sourceFolder = shaderFile.getParentFile();
		
		Gson gson = new GsonBuilder().create();
		InputStreamReader inputStream = new InputStreamReader(new FileInputStream(shaderFile));
		
		JsonObject json = gson.fromJson(inputStream, JsonObject.class);
		
		String vertexShaderFile = json.get("VertexShaderFile").getAsString();
		String fragmentShaderFile = json.get("FragmentShaderFile").getAsString();
		String vertexShaderSource = loadGLSLFile(sourceFolder, vertexShaderFile);
		String fragmentShaderSource = loadGLSLFile(sourceFolder, fragmentShaderFile);
		
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
	
	protected static final String INCLUDE_LINE = "#include ";
	protected static final String INCLUDE_FILE_TYPE = ".glsl";
	protected static String loadGLSLFile(File sourceFolder, String fileName) throws IOException {
		String line;
		BufferedReader vertexShaderInputStream = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceFolder, fileName))));
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = vertexShaderInputStream.readLine()) != null) {
			if (line.startsWith(INCLUDE_LINE)) {
				String includeCode = loadGLSLFile(sourceFolder, line.substring(INCLUDE_LINE.length()) + INCLUDE_FILE_TYPE);
				stringBuilder.append(includeCode);
			} else {
				stringBuilder.append(line + "\n");
			}
		}
		vertexShaderInputStream.close();
		return stringBuilder.toString();
	}
	
}
