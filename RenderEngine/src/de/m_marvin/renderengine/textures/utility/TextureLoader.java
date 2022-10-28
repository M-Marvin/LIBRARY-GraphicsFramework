package de.m_marvin.renderengine.textures.utility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.w3c.dom.Text;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.textures.SingleTextureMap;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder.AtlasFrameLayout;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder.AtlasMultiFrameLayout;

public class TextureLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> {
	
	public static record TextureMetaData(int frametime, int[] frames, boolean interpolate, String fileFormat) {}
	public static record TexturePack(TextureMetaData metaData, BufferedImage texture) {}
	
	public static final String TEXTURE_META_DATA_FORMAT = "json";
	public static final String DEFAULT_TEXTURE_FORMAT = "png";
	public static final TextureMetaData DEFAULT_META_DATA = new TextureMetaData(1, new int[] {0}, false, DEFAULT_TEXTURE_FORMAT);

	public static final Supplier<SingleTextureMap> INVALID_TEXTURE_FALLBACK = () -> new SingleTextureMap(2, 2, new int[] {0}, 1, new int[] {
			new Color(255, 0, 255, 255).getRGB(),
			new Color(0, 0, 0, 255).getRGB(),
			new Color(0, 0, 0, 255).getRGB(),
			new Color(255, 0, 255, 255).getRGB()
	}, false);
	
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;
	
	protected Map<R, AbstractTextureMap> textureCache = new HashMap<>();
	
	public TextureLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	// TODO Getter method for the texture maps
	
	public void buildSingleMapsFromTextures(R textureFolderLocation) {
		try {
			buildSingleMapsFromTextures0(textureFolderLocation);
		} catch (IOException e) {
			System.err.println("Failed to read some of the textures in " + textureFolderLocation);
			e.printStackTrace();
		}
	}
	
	public void buildAtlasMapFromTextures(R textureFolderLocation, boolean prioritizeAtlasHeight) {
		try {
			buildAtlasMapFromTexutes0(textureFolderLocation, prioritizeAtlasHeight);
		} catch (IOException e) {
			System.err.println("Failed to read some of the textures in " + textureFolderLocation);
			e.printStackTrace();
		}
	}
	
	public void buildSingleMapsFromTextures0(R textureFolderLocation) throws IOException {
		
		File path = resourceLoader.resolveLocation(sourceFolder, textureFolderLocation);
		for (String textureName : listTextureNames(path)) {
			
			try {

				File texturePath = new File(path, textureName);
				TexturePack textureData = loadTexture(texturePath);
				R locationName = textureFolderLocation.locationOfFile(textureName);
				
				SingleTextureMap map = new SingleTextureMap(textureData.texture(), textureData.metaData().frames(), textureData.metaData().frametime(), textureData.metaData().interpolate());
				this.textureCache.put(locationName, map);
				
			} catch (FileNotFoundException e) {
				System.err.println("Warning: A texture could not be loaded!");
				e.printStackTrace();	
			}
			
		}
		
	}
	
	public void buildAtlasMapFromTexutes0(R textureFolderLocation, boolean prioritizeAtlasHeight) throws IOException {

		File path = resourceLoader.resolveLocation(sourceFolder, textureFolderLocation);
		
		MultiFrameAtlasLayoutBuilder<int[]> layoutBuilder = new MultiFrameAtlasLayoutBuilder<>();
		List<R> locationsToLink = new ArrayList<>();
		
		for (String textureName : listTextureNames(path)) {
			
			try {

				File texturePath = new File(path, textureName);
				TexturePack textureData = loadTexture(texturePath);
				R locationName = textureFolderLocation.locationOfFile(textureName);
				
				BufferedImage image = textureData.texture();
				int width = image.getWidth();
				int height= image.getHeight();
				int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
								
				layoutBuilder.addAtlasImage(
						width, 
						height, 
						textureData.metaData().frames(), 
						textureData.metaData().frametime(), 
						textureData.metaData().interpolate(), 
						pixels
					);
				locationsToLink.add(locationName);
				
			} catch (FileNotFoundException e) {
				System.err.println("Warning: A texture could not be loaded!");
				e.printStackTrace();	
			}
			
		}
		
		// TODO Move code into atlas map
		 
		AtlasMultiFrameLayout<int[]> layout = layoutBuilder.buildLayout(prioritizeAtlasHeight);
		BufferedImage atlasImage = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_4BYTE_ABGR);
		
		System.out.println("Create atlas with size " + layout.width() + " " + layout.height());
		
		for (List<AtlasFrameLayout<int[]>> frameLayout : layout.frameLayouts()) {
			for (AtlasFrameLayout<int[]> imageLayout : frameLayout) {
				
				int pixels[] = framePixels(imageLayout.image(), imageLayout.frame(), imageLayout.frameHeight(), imageLayout.width());
				
				if (imageLayout.interpolate()) {
					
					int[] nextPixels = framePixels(imageLayout.image(), imageLayout.nextFrame(), imageLayout.frameHeight(), imageLayout.width());
					
					pixels = interpolatePixels(pixels, nextPixels, imageLayout.subframe());
					
				}
				
				atlasImage.setRGB(imageLayout.x(), imageLayout.y(), imageLayout.width(), imageLayout.frameHeight(), pixels, 0, imageLayout.width());
				
			}
		}
		
//		File outputFile = new File(textureFolder.getParentFile(), "/atlas.png");
//		System.out.println("Write file to " + outputFile);
//		FileOutputStream s = new FileOutputStream(outputFile);
//		ImageIO.write(atlasImage, "png", s);
//		s.close();
		
		
		
	}

	protected static int[] interpolatePixels(int[] pixels1, int[] pixels2, float interpolation) {
		int[] pixels = new int[pixels1.length];
		for (int i = 0; i < pixels1.length; i++) {
			pixels[i] = (int) (pixels1[i] * (1F - interpolation) + pixels2[i] * interpolation); // TODO: Incorrect interpolation, only for testing!
		}
		return pixels;
	}
	
	protected static int[] framePixels(int[] pixels, int frame, int frameHeight, int width) {
		
		int begin = frame * frameHeight * width;
		int end = begin + frameHeight * width;
		return Arrays.copyOfRange(pixels, begin, end);
		
	}
	
	protected static List<String> listTextureNames(File textureFolder) throws IOException {
		if (!textureFolder.isDirectory()) throw new IOException("The texture folder path '" + textureFolder + "' ist not valid!");
		List<String> textureNames = new ArrayList<>();
		for (String fileName : textureFolder.list()) {
			String[] fileNameParts = fileName.split("\\.");
			if (fileNameParts.length > 1) {
				int formatEndingLength = fileNameParts[fileNameParts.length - 1].length() + 1;
				String textureName = fileName.substring(0, fileName.length() - formatEndingLength);
				if (!textureNames.contains(textureName)) textureNames.add(textureName);
			}
		}
		return textureNames;
	}
	
	public static TexturePack loadTexture(File texturePath) throws IOException {
		File textureMeta = new File(texturePath + "." + TEXTURE_META_DATA_FORMAT);
		TextureMetaData metaData = textureMeta.isFile() ? loadJsonMetaData(new FileInputStream(textureMeta)) : DEFAULT_META_DATA;
		File textureFile = new File(texturePath + "." + metaData.fileFormat());
		if (!textureFile.isFile()) throw new FileNotFoundException("The texture file " + textureFile + " does not exist!");
		BufferedImage texture = loadBufferedTexture(new FileInputStream(textureFile));
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

	public AbstractTextureMap getTexture(R resourceLocation) {
		if (!this.textureCache.containsKey(resourceLocation)) {
			System.err.println("Texture " + resourceLocation + " does not exist!");
			this.textureCache.put(resourceLocation, INVALID_TEXTURE_FALLBACK.get());
		}
		return this.textureCache.get(resourceLocation);
	}
	
}
