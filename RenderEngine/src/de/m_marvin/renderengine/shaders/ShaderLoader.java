package de.m_marvin.renderengine.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
		
		String line;
		BufferedReader vertexShaderInputStream = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceFolder, vertexShaderFile))));
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = vertexShaderInputStream.readLine()) != null) stringBuilder.append(line + "\n");
		String vertexShaderSource = stringBuilder.toString();
		vertexShaderInputStream.close();
		
		BufferedReader fragmentShaderInputStream = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceFolder, fragmentShaderFile))));
		stringBuilder = new StringBuilder();
		while ((line = fragmentShaderInputStream.readLine()) != null) stringBuilder.append(line + "\n");
		String fragmentShaderSource = stringBuilder.toString();
		fragmentShaderInputStream.close();
		
		ShaderInstance shaderInstance = new ShaderInstance(vertexShaderSource, fragmentShaderSource);
		
		JsonArray uniformArray = json.get("Uniforms").getAsJsonArray();
		for (int i = 0; i < uniformArray.size(); i++) {
			JsonObject uniformJson = uniformArray.get(i).getAsJsonObject();
			String uniformName = uniformJson.get("Name").getAsString();
			UniformType type = UniformType.byName(uniformJson.get("Type").getAsString());
			Object defaultValue = gson.fromJson(uniformJson.get("Value").getAsJsonArray(), type.getValueType());
			shaderInstance.createUniform(uniformName, type, defaultValue);
		}

		shaderInstance.setupAttributeNames(vertexFormat);
		
		inputStream.close();
		
		return shaderInstance;
	}
	
}
