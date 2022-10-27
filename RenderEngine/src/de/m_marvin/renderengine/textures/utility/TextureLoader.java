package de.m_marvin.renderengine.textures.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.textures.SingleTextureMap;

public class TextureLoader<R, FE extends ISourceFolder> {
	
	public static record TextureMetaData(int frametime, int[] frames, boolean interpolate, String fileFormat) {}
	public static record TexturePack(TextureMetaData metaData, BufferedImage texture) {}
	
	public static final String TEXTURE_META_DATA_FORMAT = "json";
	public static final String DEFAULT_TEXTURE_FORMAT = "png";
	public static final TextureMetaData DEFAULT_META_DATA = new TextureMetaData(1, new int[] {0}, false, DEFAULT_TEXTURE_FORMAT);
	
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;
	
	protected Map<R, AbstractTextureMap> textureCache = new HashMap<>();
	
	public TextureLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	// TODO Getter method for the texture maps
	
	public void buildSingleMapsFromTextures(R textureFolderLocation) {
		File path = resourceLoader.resolveLocation(sourceFolder, textureFolderLocation);
		try {
			buildSingleMapsFromTextures(path);
		} catch (IOException e) {
			System.err.println("Failed to read some of the textures in " + textureFolderLocation);
		}
	}
	
	public void buildAtlasMapFromTextures(R textureFolderLocation) {
		File path = resourceLoader.resolveLocation(sourceFolder, textureFolderLocation);
		try {
			buildAtlasMapFromTexutes(path);
		} catch (IOException e) {
			System.err.println("Failed to read some of the textures in " + textureFolderLocation);
		}
	}
	
	public static SingleTextureMap buildSingleMapsFromTextures(File textureFolder) throws IOException {
		
		
		for (String textureFileName : textureFolder.list()) {
			if (!textureFileName.endsWith(TEXTURE_META_DATA_FORMAT)) { // TODO Das wird so nicht funktionieren, lade nur texturen mit json oder standartformat
				
				String[] s = textureFileName.split("\\.");
				String textureName = textureFileName.substring(0, textureFileName.length() - s[s.length - 1].length());
				
				TexturePack texturePack = loadTexture(new File(textureFolder, textureName));
				
			}
		}
		
		
	}
	
	public static void buildAtlasMapFromTexutes(File textureFolder) throws IOException {
		
		
		
	}
	
	public static TexturePack loadTexture(File texturePath) throws IOException {
		File textureMeta = new File(texturePath, "." + TEXTURE_META_DATA_FORMAT);
		TextureMetaData metaData = textureMeta.isFile() ? loadJsonMetaData(new FileInputStream(textureMeta)) : DEFAULT_META_DATA;
		BufferedImage texture = loadBufferedTexture(new FileInputStream(new File(texturePath, metaData.fileFormat())));
		return new TexturePack(metaData, texture);
	}
	
	public static BufferedImage loadBufferedTexture(InputStream inputStream) throws IOException {
		return ImageIO.read(inputStream);
	}
	
	public static TextureMetaData loadJsonMetaData(InputStream inputSteam) throws IOException {
		
		Gson gson = new Gson();
		JsonObject metaJson = gson.fromJson(new InputStreamReader(inputSteam), JsonObject.class);
		inputSteam.close();
		
		int frameTime = metaJson.get("FrameTime").getAsInt();
		
		JsonElement framesJson = metaJson.get("Frames");
		int[] frames = null;
		if (framesJson instanceof JsonPrimitive) {
			frames = IntStream.range(0, framesJson.getAsInt()).toArray();
		} else {
			frames = gson.fromJson(framesJson, int[].class);
		}
		
		boolean interpolate = metaJson.has("Interpolate") ? metaJson.get("Interpolate").getAsBoolean() : false;
		
		String formatName = metaJson.has("TextureFormat") ? metaJson.get("TextureFormat").getAsString() : DEFAULT_TEXTURE_FORMAT;
		
		return new TextureMetaData(frameTime, frames, interpolate, formatName);
		
	}
	
}
