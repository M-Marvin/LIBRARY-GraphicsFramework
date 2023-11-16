package de.m_marvin.renderengine.textures.utility;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
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
import de.m_marvin.simplelogging.printing.LogType;
import de.m_marvin.simplelogging.printing.Logger;

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
	protected Set<R> textureMapNames = new HashSet<>();
	
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
		this.textureMapNames.clear();
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
			Logger.defaultLogger().logWarn("Failed to read some of the textures in " + textureFolderLocation);
			Logger.defaultLogger().printException(LogType.WARN, e);
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
	 * @param prioritizeAtlasHeight Decides if the alignment of the images in the atlas are oriented on the x or y axis
	 * @param selectInterpolatedTextures If true, only interpolated textures are loaded into the atlas, if false only non interpolated textures are loaded, mixing is not allowed
	 */
	public void buildAtlasMapFromTextures(R textureFolderLocation, R atlasName, boolean prioritizeAtlasHeight, boolean selectInterpolatedTextures) {
		try {
			buildAtlasMapFromTexutes0(textureFolderLocation, atlasName, prioritizeAtlasHeight, selectInterpolatedTextures);
		} catch (IOException e) {
			Logger.defaultLogger().logWarn("Failed to read some of the textures in " + textureFolderLocation);
			Logger.defaultLogger().printException(LogType.WARN, e);
		}
	}
	
	/**
	 * Non try-catch version of {@link #buildSingleMapsFromTextures(IResourceProvider)}.
	 * 
	 * @param textureFolderLocation The texture folder location of the textures to load
	 * @throws IOException If an error occurs accessing the texture files
	 */
	public void buildSingleMapsFromTextures0(R textureFolderLocation) throws IOException {
		
		for (String textureName : listTextureNames(textureFolderLocation)) {
			
			try {
				
				TexturePack textureData = loadTexture(textureFolderLocation.locationOfFile(textureName));
				R locationName = textureFolderLocation.locationOfFile(textureName);
				
				SingleTextureMap<R> map = new SingleTextureMap<R>(textureData.texture(), textureData.metaData().frames(), textureData.metaData().frametime(), textureData.metaData().interpolate());
				this.textureCache.put(locationName, map);
				this.textureMapNames.add(locationName);
				
			} catch (FileNotFoundException e) {
				Logger.defaultLogger().logWarn("Warning: A texture could not be loaded!");
				Logger.defaultLogger().printException(LogType.WARN, e);	
			}
			
		}
		
	}
	
	/**
	 * Non try-catch version of {@link #buildAtlasMapFromTextures(IResourceProvider, IResourceProvider, boolean, boolean)}.
	 * 
	 * @param textureFolderLocation The texture folder location of the textures to load
	 * @param atlasName The additional custom name for the atlas
	 * @param prioritizeAtlasHeight Decides if the alignment of the images in the atlas are oriented on the x or y axis
	 * @param selectInterpolatedTextures If true, only interpolated textures are loaded into the atlas, if false only non interpolated textures are loaded
	 * @throws IOException If an error occurs accessing the texture files
	 */
	public void buildAtlasMapFromTexutes0(R textureFolderLocation, R atlasName, boolean prioritizeAtlasHeight, boolean selectInterpolatedTextures) throws IOException {
		
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
		for (String textureName : listTextureNames(textureFolderLocation)) {
			
			try {
				
				TexturePack textureData = loadTexture(textureFolderLocation.locationOfFile(textureName));
				
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
				Logger.defaultLogger().logWarn("Warning: A texture could not be loaded!");
				Logger.defaultLogger().printException(LogType.WARN, e);	
			}
			
		}
		
		if (addedImages) {

			map.buildAtlas(prioritizeAtlasHeight, selectInterpolatedTextures);
			this.textureCache.put(atlasName, map);
			this.textureMapNames.add(atlasName);
			for (R location : locationsToLink) this.textureCache.put(location, map);
			
		}
		
	}

	/**
	 * Manually adds an texture map to the cached textures.
	 * This is mostly used by external texture loaders, like the font manager.
	 * 
	 * @param atlasName The name under which the atlas should be cached
	 * @param textureMap The atlas's texture map
	 */
	public void cacheTextureMap(R atlasName, AbstractTextureMap<R> textureMap) {
		this.textureMapNames.add(atlasName);
		this.textureCache.put(atlasName, textureMap);
	}

	/**
	 * Manually links an texture name with the given map.
	 * This is mostly used by external texture loaders, like the font manager.
	 * 
	 * @param textureName The name under which the texture should be cached
	 * @param textureMap The textures's texture map
	 */
	public void cacheTexture(R textureName, AbstractTextureMap<R> textureMap) {
		this.textureCache.put(textureName, textureMap);
	}
	
	/**
	 * Lists all texture names found in the given folder.
	 * Does not search in sub-folders.
	 * 
	 * @param textureFolder The folder location to search for textures
	 * @return A list containing all found texture names
	 * @throws IOException If an error occurs accessing the files
	 */
	protected List<String> listTextureNames(R textureFolder) throws IOException {
		List<String> textureNames = new ArrayList<>();
		for (String fileName : resourceLoader.listFilesIn(sourceFolder, textureFolder)) {
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
	public TexturePack loadTexture(R textureLocation) throws IOException {
		R textureMeta = textureLocation.append("." + TEXTURE_META_DATA_FORMAT);
		TextureMetaData metaData = DEFAULT_META_DATA;
		try {
			metaData = loadJsonMetaData(resourceLoader.getAsStream(sourceFolder, textureMeta));
		} catch (FileNotFoundException e) {}
		R textureFile = textureLocation.append("." + metaData.fileFormat());
		try {
			BufferedImage texture = loadBufferedTexture(resourceLoader.getAsStream(sourceFolder, textureFile));
			return new TexturePack(metaData, texture);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("The texture file " + textureFile + " does not exist!");
		}
	}
	
	/**
	 * Loads a texture from an input stream.
	 * @param inputStream The {@link InputStream} to read from
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
	 * @param inputStream The {@link InputStream} to read from
	 * @return The metadata as {@link TextureMetaData}
	 * @throws IOException If an error occurs accessing the input stream
	 */
	public static TextureMetaData loadJsonMetaData(InputStream inputStream) throws IOException {
		
		Gson gson = new Gson();
		JsonObject metaJson = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);
		inputStream.close();
		
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
			Logger.defaultLogger().logWarn("Texture " + resourceLocation + " does not exist!");
			this.textureCache.put(resourceLocation, (AbstractTextureMap<R>) INVALID_TEXTURE_FALLBACK.get());
		}
		AbstractTextureMap<R> texture = this.textureCache.get(resourceLocation);
		texture.activateTexture(null);
		return texture;
	}
	
	/**
	 * Returns all textures currently cached in the texture loader.
	 * 
	 * @return A Collection of all textures currently cached
	 */
	public Collection<AbstractTextureMap<R>> getTextureMaps() {
		return this.textureMapNames.stream().map(this::getTextureMap).toList();
	}

	/**
	 * Returns the names of all texture-maps currently cached in the texture loader.
	 * A texture map can consist of multiple textures packed into an atlas.
	 * 
	 * @return A Collection of the names of all texture-maps currently cached
	 */
	public Collection<R> getTextureMapNames() {
		return this.textureMapNames;
	}
	
}
