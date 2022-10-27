package de.m_marvin.renderengine.textures;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.textures.utility.TextureFilter;

public abstract class AbstractTextureMap implements ITextureSampler, IUVModifyer {
	
	protected int width;
	protected int height;
	protected int[] pixels;
	protected int textureId;
	
	protected void init() {
		GLStateManager.assertOnRenderThread();
		this.textureId = GLStateManager.genTexture();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
		setTextureFilter(TextureFilter.NEAREST, TextureFilter.NEAREST);
		GLStateManager.loadTexture(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8, GL33.GL_BGRA, width, height, 0, GL33.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
	}
	
	public void setTextureFilter(TextureFilter minificationFilter, TextureFilter magnificationFilter) {
		GLStateManager.assertOnRenderThread();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, minificationFilter.glType());
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, magnificationFilter.glType());
	}
	
	@Override
	public void bindTexture(int samplerId) {
		GLStateManager.assertOnRenderThread();
		GLStateManager.activeTexture(samplerId);
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
	}
	
	@Override
	public void unbindTexture() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
	}
	
}
