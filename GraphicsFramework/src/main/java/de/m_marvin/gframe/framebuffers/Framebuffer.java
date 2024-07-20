package de.m_marvin.gframe.framebuffers;

import org.lwjgl.opengl.GL33;

import de.m_marvin.gframe.GLStateManager;
import de.m_marvin.gframe.textures.texture.Texture;
import de.m_marvin.gframe.textures.utility.TextureDataFormat;
import de.m_marvin.gframe.textures.utility.TextureFormat;

/**
 * Class used to create custom framebuffers.
 * 
 * @author Marvin Koehler
 *
 */
public class Framebuffer {
	
	protected int width;
	protected int height;
	protected int framebufferId;
	protected Texture colorTexture;
	protected Texture depthTexture;
	
	/**
	 * Creates a framebuffer with the initial size of width x height
	 * @param width The width in pixels
	 * @param height The height in pixels
	 */
	public Framebuffer(int width, int height) {
		resize(width, height);
	}
	
	/**
	 * Changes the size of the framebuffer.<br>
	 * <b>NOTE:</b> This reinitializes the framebuffer and its textures
	 * @param width The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		GLStateManager.assertOnRenderThread();
		if (this.framebufferId > -1) {
			discard();
		}
		this.width = width;
		this.height = height;
		createBuffers();
	}
	
	protected void createBuffers() {
		GLStateManager.assertOnRenderThread();
		if (this.width > 0 && this.height > 0) {
			
			this.framebufferId = GLStateManager.genFramebuffer();
			
			if (this.colorTexture == null) this.colorTexture = new Texture(TextureFormat.RED_GREEN_BLUE_ALPHA);
			this.colorTexture.upload(this.width, this.height, TextureDataFormat.INT_RGBA_8_8_8_8, null);
			
			if (this.depthTexture == null) this.depthTexture = new Texture(TextureFormat.DEPTH);
			this.depthTexture.upload(this.width, this.height, TextureDataFormat.FLOAT_DEPTH, null);
			
			GLStateManager.bindFramebuffer(GL33.GL_FRAMEBUFFER, this.framebufferId);
			GLStateManager.framebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, this.colorTexture.getTextureId(), 0);
			GLStateManager.framebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_TEXTURE_2D, this.depthTexture.getTextureId(), 0);
			
			checkStatus();
		}
	}
	
	/**
	 * Checks if the framebuffer is ready to be used, throws an RuntimeException if not.
	 */
	public void checkStatus() {
		GLStateManager.assertOnRenderThread();
		int i = GLStateManager.checkFramebufferStatus(GL33.GL_FRAMEBUFFER);
		if (i != 36053) {
			if (i == 36054) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == 36055) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == 36059) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == 36060) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			} else if (i == 36061) {
				throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
			} else if (i == 1285) {
				throw new RuntimeException("GL_OUT_OF_MEMORY");
			} else {
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
			}
		}
	}
	
	/**
	 * Binds the framebuffer to draw onto it.
	 */
	public void bind() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.bindFramebuffer(GL33.GL_FRAMEBUFFER, framebufferId);
	}
	
	/**
	 * Unbinds the framebuffer
	 */
	public void unbind() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.bindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * Returns the color texture
	 * @return The color texture
	 */
	public Texture getColorTexture() {
		return colorTexture;
	}
	
	/**
	 * Returns the depth texture
	 * @return The depth texture
	 */
	public Texture getDepthTexture() {
		return depthTexture;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Discards the buffer and deletes all its textures on the GPU
	 */
	public void discard() {
		if (this.colorTexture != null) {
			this.colorTexture.discard();
		}
		if (this.depthTexture != null) {
			this.depthTexture.discard();
		}
		if (this.framebufferId > -1) {
			GLStateManager.bindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
			GLStateManager.deleteFramebuffer(this.framebufferId);
			this.framebufferId = -1;
		}
	}
	
}
