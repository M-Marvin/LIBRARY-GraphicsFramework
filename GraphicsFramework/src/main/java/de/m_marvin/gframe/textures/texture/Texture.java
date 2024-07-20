package de.m_marvin.gframe.textures.texture;

import org.lwjgl.opengl.GL33;

import de.m_marvin.gframe.GLStateManager;
import de.m_marvin.gframe.textures.utility.TextureDataFormat;
import de.m_marvin.gframe.textures.utility.TextureFilter;
import de.m_marvin.gframe.textures.utility.TextureFormat;

/**
 * Base class of the different textures.
 * Contains basic functionality to upload the texture data to the GPU.
 * 
 * @author Marvin Köhler
 */
public class Texture implements ITextureSampler {
	
	protected int width;
	protected int height;
	protected TextureFormat format;
	protected TextureFilter minFilter = TextureFilter.NEAREST;
	protected TextureFilter magFilter = TextureFilter.NEAREST;
	protected int textureId = -1;
	
	public Texture(TextureFormat format) {
		this.format = format;
	}
	
	/**
	 * Sets the internal format of the texture data on the GPU
	 * @param format
	 */
	public void setFormat(TextureFormat format) {
		this.format = format;
	}
	
	public void setMinFilter(TextureFilter minFilter) {
		this.minFilter = minFilter;
	}
	
	public void setMagFilter(TextureFilter magFilter) {
		this.magFilter = magFilter;
	}

	public int getTexWidth() {
		return width;
	}
	
	public int getTexHeight() {
		return height;
	}
	
	/**
	 * Uploads the given texture data to the GPU.<br>
	 * <b>NOTE:</b> The texture is not actually created on the GPU before this function is called.
	 * @param width The width of the texture image
	 * @param height The height of the texture image
	 * @param format The format of the pixel data in the pixel array
	 * @param pixels The pixel data or null if initialization with 0 is required
	 */
	public void upload(int width, int height, TextureDataFormat format, int[] pixels) {
		GLStateManager.assertOnRenderThread();
		this.width = width;
		this.height = height;
		if (this.textureId == -1) {
			this.textureId = GLStateManager.genTexture();
		}
		bind();
		GLStateManager.uploadTexture(GL33.GL_TEXTURE_2D, 0, this.format.glType(), format.glPixelFormat(), this.width, this.height, 0, format.glFormat(), pixels);
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, this.minFilter.glType());
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, this.magFilter.glType());
	}
	
	/**
	 * Downloads the current texture data from the GPU<br>
	 * <b>NOTE:</b> The texture must have been initialized (uploaded) before this can be used.
	 * @param format The format if the pixel data in the pixel array
	 * @return The pixel data of the texture on the GPU
	 */
	public int[] download(TextureDataFormat format) {
		GLStateManager.assertOnRenderThread();
		assert this.textureId > -1 : "this texture has not yet been initialized on the GPU!";
		bind();
		int[] pixels = new int[width * height];
		GLStateManager.downloadTexture(GL33.GL_TEXTURE_2D, 0, format.glPixelFormat(), format.glFormat(), pixels);
		return pixels;
	}
	
	/**
	 * The gl texture id
	 * @return The texture id on the GPU
	 */
	public int getTextureId() {
		return textureId;
	}
	
	/**
	 * Binds the texture for usage
	 */
	public void bind() {
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, this.textureId);
	}
	
	/**
	 * Unbinds the texture
	 */
	public void unbind() {
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
	}
	
	@Override
	public void bindSampler(int samplerId) {
		GLStateManager.assertOnRenderThread();
		GLStateManager.activeTexture(samplerId);
		bind();
	}
	
	@Override
	public void unbindSampler() {
		GLStateManager.assertOnRenderThread();
		unbind();
	}
	
	/**
	 * Deletes the texture map from the GPU memory.
	 */
	public void discard() {
		GLStateManager.deleteTexture(this.textureId);
	}
	
}
