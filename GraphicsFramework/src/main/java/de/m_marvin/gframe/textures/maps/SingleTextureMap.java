package de.m_marvin.gframe.textures.maps;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.gframe.textures.utility.TextureDataFormat;
import de.m_marvin.gframe.textures.utility.TextureFormat;
import de.m_marvin.univec.impl.Vec4f;

/**
 * The single texture implementation of the {@link AbstractTextureMap}.
 * Contains only one texture and should not be changed to often to reduce GPU uploads.
 * 
 * @author Marivn Köhler
 *
 * @param <R> The type of the resource locations
 */
public class SingleTextureMap<R extends IResourceProvider<R>> extends AbstractTextureMap<R> {
	
	/**
	 * Creates a new single texture map and fully initializes it with the given texture data.
	 * 
	 * @param format The format of the texture on the GPU
	 * @param width The width of the textures in pixels
	 * @param height The complete height of the texture in pixels (not the height of one frame)
	 * @param frames The frames id array describing the order of animation frames
	 * @param frametime The number of ticks a frame lasts
	 * @param pixelFormat The pixel format of the image data
	 * @param pixels The pixel data array
	 * @param interpolate True if the texture has to be interpolated
	 */
	public SingleTextureMap(TextureFormat format, int width, int height, int[] frames, int frametime, TextureDataFormat pixelFormat, int[] pixels, boolean interpolate) {
		super(format);
		this.frames = frames;
		this.frameHeight = this.height / (IntStream.of(this.frames).max().getAsInt() + 1);
		this.frametime = frametime;
		this.interpolate = interpolate;
		upload(width, height, pixelFormat, pixels);
		updateMatrix();
	}
	
	/**
	 * Creates a new single texture map and fully initializes it with the given texture data.
	 * 
	 * @param imageSource The texture as {@link BufferedImage}
	 * @param frames The frames id array describing the order of animation frames
	 * @param frametime The number of ticks a frame lasts
	 * @param interpolate True if the texture has to be interpolated
	 */
	public SingleTextureMap(TextureFormat format, BufferedImage imageSource, int[] frames, int frametime, boolean interpolate) {
		this(	format,
				imageSource.getWidth(), 
				imageSource.getHeight(), 
				frames, 
				frametime, 
				TextureDataFormat.INT_RGBA_8_8_8_8,
				imageSource.getRGB(0, 0, imageSource.getWidth(), imageSource.getHeight(), null, 0, imageSource.getWidth()), 
				interpolate);
	}
	
	@Override
	public void activateTexture(R textureLoc) {}
	
	@Override
	public float mapU(float u) {
		return u;
	}
	@Override
	public float mapV(float v) {
		return v;
	}
	
	@Override
	public Vec4f getUV() {
		return new Vec4f(0F, 0F, 1F, 1F);
	}

	@Override
	public int getImageWidth() {
		return width;
	}

	@Override
	public int getImageHeight() {
		return frameHeight;
	}
	
	@Override
	public int getMapWidth() {
		return getImageWidth();
	}

	@Override
	public int getMapHeight() {
		return getImageHeight();
	}
	
}
