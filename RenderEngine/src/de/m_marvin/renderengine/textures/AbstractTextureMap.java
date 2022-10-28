package de.m_marvin.renderengine.textures;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.textures.utility.TextureFilter;
import de.m_marvin.unimat.impl.Matrix3f;

public abstract class AbstractTextureMap implements ITextureSampler, IUVModifyer {

	protected Matrix3f animationMatrixLast;
	protected Matrix3f animationMatrix;
	protected int frameHeight;
	protected int[] frames;
	protected int frametime;
	protected int activeFrame;
	protected boolean interpolate;
	
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

	public int getFrametime() {
		return frametime;
	}
	
	public void nextFrame() {
		this.activeFrame++;
		if (this.activeFrame >= this.frames.length) this.activeFrame = 0;
		updateMatrix();
	}
	
	public void setFrame(int frame) {
		if (frame < 0) throw new IndexOutOfBoundsException("The frame id can not be smaler than 0!");
		if (this.frames.length <= frame) throw new IndexOutOfBoundsException("The texture has only " + this.frames.length + " frames!");
		this.activeFrame = frame;
		updateMatrix();
	}

	public void updateMatrix() {
		this.animationMatrixLast = this.animationMatrix;
		this.animationMatrix = Matrix3f.createScaleMatrix(1, (float) frameHeight / height).mul(Matrix3f.createTranslationMatrix(0, frames[activeFrame]));
		if (this.animationMatrixLast == null) this.animationMatrixLast = this.animationMatrix;
	}
	
	@Override
	public Matrix3f frameMatrix() {
		return this.animationMatrix;
	}
	@Override
	public Matrix3f lastFrameMatrix() {
		return this.interpolate ? this.animationMatrixLast : this.animationMatrix;
	}
	@Override
	public boolean doFrameInterpolation() {
		return this.interpolate;
	}
	
}
