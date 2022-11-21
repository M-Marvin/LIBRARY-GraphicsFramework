package de.m_marvin.renderengine.textures.utility;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.m_marvin.renderengine.resources.IClearableLoader;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.resources.ResourceLoader;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.textures.AtlasTextureMap;
import de.m_marvin.renderengine.textures.SingleTextureMap;

/**
 * Handles the loading of textures from files.
 * 
 * @author Marvin Koehler
 *
 * @param <R> The type of the resource locations
 * @param <FE> The implementation of the source folder list
 */
public class TextureLoader<R extends IResourceProvider<R>, FE extends ISourceFolder> implements IClearableLoader {
	
	public static record TextureMetaData(int frametime, int[] frames, boolean interpolate, String fileFormat) {}
	public static record TexturePack(TextureMetaData metaData, BufferedImage texture) {}
	
	public static final String TEXTURE_META_DATA_FORMAT = "json";
	public static final String DEFAULT_TEXTURE_FORMAT = "png";
	public static final TextureMetaData DEFAULT_META_DATA = new TextureMetaData(1, new int[] {0}, false, DEFAULT_TEXTURE_FORMAT);
	
	public static final int[] INVALID_TEXTURE_FALLBACK_PIXELS = new int[] {
			new Color(255, 0, 255, 255).getRGB(),
			new Color(0, 0, 0, 255).getRGB(),
			new Color(0, 0, 0, 255).getRGB(),
			new Color(255, 0, 255, 255).getRGB()
	};
	public static final Supplier<BufferedImage> INVALID_TEXTURE_FALLBACK_IMAGE = () -> {
			BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
			image.setRGB(0, 0, 2, 2, INVALID_TEXTURE_FALLBACK_PIXELS, 0, 2);
			return image;
	};
	public static final TexturePack INVALID_TEXTURE_FALLBACK_PACK = new TexturePack(DEFAULT_META_DATA, INVALID_TEXTURE_FALLBACK_IMAGE.get());
	public static final Supplier<SingleTextureMap<?>> INVALID_TEXTURE_FALLBACK = () -> new SingleTextureMap<>(2, 2, new int[] {0}, 1, INVALID_TEXTURE_FALLBACK_PIXELS, false);
	
	protected final FE sourceFolder;
	protected final ResourceLoader<R, FE> resourceLoader;
	
	protected LinkedHashMap<R, AbstractTextureMap<R>> textureCache = new LinkedHashMap<>();
	
	/**
	 * Creates a new texture loader.
	 * @param sourceFolder The source folder
	 * @param resourceLoader The resource loader used for the file access
	 */
	public TextureLoader(FE sourceFolder, ResourceLoader<R, FE> resourceLoader) {
		this.sourceFolder = sourceFolder;
		this.resourceLoader = resourceLoader;
	}
	
	@Override
	public void clearCached() {
		this.textureCache.values().forEach(AbstractTextureMap::delete);
		this.textureCache.clear();
	}
	
	/**
	 * Loads all textures in the given folder and sores them as multiple textures on the GPU and caches them as {@link SingleTextureMap}s in the cache.
	 * The textures have to be in the default {@link #DEFAULT_TEXTURE_FORMAT} format or require a metadata-json with the same file containing the name format used.
	 * Textures without such a file and without the default format are not loaded.
	 * The metadata JSON can also hold informations about animation.
	 * 
	 * @param textureFolderLocation The texture folder location of the textures to load
	 */
	public void buildSingleMapsFromTextures(R textureFolderLocation) {
		try {
			buildSingleMapsFromTextures0(textureFolderLocation);
		} catch (IOException e) {
			System.err.println("Failed to read some of the textures in " + textureFolderLocation);
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all textures in the given folder and sores them as one texture-atlas on the GPU and caches this texture as {@link AtlasTextureMap}s in the cache.
	 * The atlas gets cached under the name of all textures that are placed in the atlas and an additional custom name, so it can be queried by the name of any texture from the atlas or its custom name.
	 * The atlas does not allow to mix interpolated and non interpolated animated textures, therefore a parameter to specify which textures should be loaded is required for this method.
	 * The textures have to be in the default {@link #DEFAULT_TEXTURE_FORMAT} format or require a metadata-json with the same file containing the name format used.
	 * Textures without such a file and without the default format are not loaded.
	 * The metadata JSON can also hold informations about animation.
	 * 
	 * @param textureFolderLocation The texture folder location of the textures to load
	 * @param atlasName The additional custom name for the atlas
	 * @param prioritizeAtlasHeight Decides if the aligment of the images in the atlas are oriented on the x or y axis
	 * @param selectInterpolatedTextures If true, only interpolated textures are loaded into the atlas, if false only non interpolated textures are loaded, mixing is not allowed
	 */
	public void buildAtlasMapFromTextures(R textureFolderLocation, R atlasName, boolean prioritizeAtlasHeight, boolean selectInterpolatedTextures) {
		try {
			buildAtlasMapFromTexutes0(textureFolderLocation, atlasName, prioritizeAtlasHeight, selectInterpolatedTextures);
		} catch (IOException e) {
			System.err.println("Failed to read some of the textures in " + textureFolderLocation);
			e.printStackTrace();
		}
	}
	
	/**
	 * Non try-catch version of {@link #buildSingleMapsFromTextures(IResourceProvider)}.
	 * 
	 * @param textureFolderLocation The texture folder location of the textures to load
	 * @throws IOException If an error occurs accessing the texture files
	 */
	public void buildSingleMapsFromTextures0(R textureFolderLocation) throws IOException {
		
		File path = resourceLoader.resolveLocation(sourceFolder, textureFolderLocation);
		for (String textureName : listTextureNames(path)) {
			
			try {

				File texturePath = new File(path, textureName);
				TexturePack textureData = loadTexture(texturePath);
				R locationName = textureFolderLocation.locationOfFile(textureName);
				
				SingleTextureMap<R> map = new SingleTextureMap<R>(textureData.texture(), textureData.metaData().frames(), textureData.metaData().frametime(), textureData.metaData().interpolate());
				this.textureCache.put(locationName, map);
				
			} catch (FileNotFoundException e) {
				System.err.println("Warning: A texture could not be loaded!");
				e.printStackTrace();	
			}
			
		}
		
	}
	
	/**
	 * Non try-catch version of {@link #buildAtlasMapFromTextures(IResourceProvider, IResourceProvider, boolean, boolean)}.
	 * 
	 * @param textureFolderLocation The texture folder location of the textures to load
	 * @param atlasName The additional custom name for the atlas
	 * @param prioritizeAtlasHeight Decides if the aligment of the images in the atlas are oriented on the x or y axis
	 * @param selectInterpolatedTextures If true, only interpolated textures are loaded into the atlas, if false only non interpolated textures are loaded
	 * @throws IOException If an error occurs accessing the texture files
	 */
	public void buildAtlasMapFromTexutes0(R textureFolderLocation, R atlasName, boolean prioritizeAtlasHeight, boolean selectInterpolatedTextures) throws IOException {

		File path = resourceLoader.resolveLocation(sourceFolder, textureFolderLocation);
		
		AtlasTextureMap<R> map = new AtlasTextureMap<>();
		List<R> locationsToLink = new ArrayList<>();
		
		// Put fallback texture as with location "null" as default into the atlas
		TexturePack fallbackData = INVALID_TEXTURE_FALLBACK_PACK;
		map.addTexture(
				null,
				fallbackData.texture().getWidth(),
				fallbackData.texture().getHeight(),
				fallbackData.metaData.frames,
				fallbackData.metaData.frametime,
				fallbackData.texture().getRGB(0, 0, fallbackData.texture().getWidth(), fallbackData.texture.getHeight(), null, 0, fallbackData.texture().getWidth())
		);
		
		boolean addedImages = false;
		for (String textureName : listTextureNames(path)) {
			
			try {

				File texturePath = new File(path, textureName);
				TexturePack textureData = loadTexture(texturePath);
				
				if (textureData.metaData().interpolate() == selectInterpolatedTextures) {

					R locationName = textureFolderLocation.locationOfFile(textureName);
					
					BufferedImage image = textureData.texture();
					int width = image.getWidth();
					int height= image.getHeight();
					int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
					
					addedImages = true;
					map.addTexture(
							locationName,
							width,
							height,
							textureData.metaData.frames,
							textureData.metaData.frametime,
							pixels
					);
					locationsToLink.add(locationName);
					
				}
					
			} catch (FileNotFoundException e) {
				System.err.println("Warning: A texture could not be loaded!");
				e.printStackTrace();	
			}
			
		}
		
		if (addedImages) {

			map.buildAtlas(prioritizeAtlasHeight, selectInterpolatedTextures);
			this.textureCache.put(atlasName, map);
			for (R location : locationsToLink) this.textureCache.put(location, map);
			
		}
		
	}

	/**
	 * Lists all texture names found in the fiven folder.
	 * Does not search in sub-folders.
	 * 
	 * @param textureFolder The folder to search for textures
	 * @return A list containing all found texture names
	 * @throws IOException If an error occurs accessing the files
	 */
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
	
	/**
	 * Loads a given textures meta-json and texture and returns it as {@link TexturePack}.
	 * The returned texture pack is used to construct the implementations of {@link AbstractMethodError}.
	 * 
	 * @param texturePath The path to the texture to load
	 * @return A {@link TexturePack} containing all informations about the texture
	 * @throws IOException If an error occurs accessing the files
	 */
	public static TexturePack loadTexture(File texturePath) throws IOException {
		File textureMeta = new File(texturePath + "." + TEXTURE_META_DATA_FORMAT);
		TextureMetaData metaData = textureMeta.isFile() ? loadJsonMetaData(new FileInputStream(textureMeta)) : DEFAULT_META_DATA;
		File textureFile = new File(texturePath + "." + metaData.fileFormat());
		if (!textureFile.isFile()) throw new FileNotFoundException("The texture file " + textureFile + " does not exist!");
		BufferedImage texture = loadBufferedTexture(new FileInputStream(textureFile));
		return new TexturePack(metaData, texture);
	}
	
	/**
	 * Loads a texture from an input stream.
	 * @param inputStream The input stream to read from
	 * @return The texture as {@link BufferedImage}
	 * @throws IOException If an error occurs accessing the input stream
	 */
	public static BufferedImage loadBufferedTexture(InputStream inputStream) throws IOException {
		BufferedImage image = ImageIO.read(inputStream);
		final AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight());
        final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);
        return image;
	}
	
	/**
	 * Loads the metadata of a texture from an input stream.
	 * @param inputSteam The input stream to read from
	 * @return The metadata as {@link TextureMetaData}
	 * @throws IOException If an error occurs accessing the input stream
	 */
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
	
	/**
	 * Returns the texture map cached under the given name.
	 * Also if the map contains multiple texture, selects the required one by calling {@link AbstractTextureMap#activateTexture(IResourceProvider)}.
	 * If no texture is cached under the name, a default texture is created with {@link #INVALID_TEXTURE_FALLBACK}.
	 * The default texture is then cached under that name.
	 * 
	 * @param resourceLocation The location/name of the texture
	 * @return The texture map cached under that name or the invalid-texture if no texture was found
	 */
	public AbstractTextureMap<R> getTexture(R resourceLocation) {
		AbstractTextureMap<R> texture = getTextureMap(resourceLocation);
		texture.activateTexture(resourceLocation);
		return texture;
	}

	/**
	 * Returns the texture map cached under the given name.
	 * If no texture is cached under the name, a default texture is created with {@link #INVALID_TEXTURE_FALLBACK}.
	 * The default texture is then cached under that name.
	 * 
	 * @param resourceLocation The location/name of the texture
	 * @return The texture map cached under that name or the invalid-texture if no texture was found
	 */
	@SuppressWarnings("unchecked")
	public AbstractTextureMap<R> getTextureMap(R resourceLocation) {
		if (!this.textureCache.containsKey(resourceLocation)) {
			System.err.println("Texture " + resourceLocation + " does not exist!");
			this.textureCache.put(resourceLocation, (AbstractTextureMap<R>) INVALID_TEXTURE_FALLBACK.get());
		}
		AbstractTextureMap<R> texture = this.textureCache.get(resourceLocation);
		texture.activateTexture(null);
		return texture;
	}
	
}
