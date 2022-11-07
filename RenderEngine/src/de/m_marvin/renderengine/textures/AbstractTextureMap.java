package de.m_marvin.renderengine.textures;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.textures.utility.TextureFilter;
import de.m_marvin.unimat.impl.Matrix3f;

/**
 * Base class of the different texture maps.
 * Contains basic functionality to upload the texture data to the GPU.
 * 
 * @author Marvin Köhler
 *
 * @param <R> The resource location type used
 */
public abstract class AbstractTextureMap<R extends IResourceProvider<R>> implements ITextureSampler, IUVModifyer {

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
	
	/**
	 * Initializes the texture on the GPU.
	 * All parameters have to be set before this method can be called.
	 * After this method is called, the texture is ready for use.
	 */
	protected void init() {
		GLStateManager.assertOnRenderThread();
		this.textureId = GLStateManager.genTexture();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
		setTextureFilter(TextureFilter.NEAREST, TextureFilter.NEAREST);
		GLStateManager.loadTexture(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8, GL33.GL_BGRA, width, height, 0, GL33.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
	}
	
	/**
	 * Sets the used texture filter on the GPU.
	 * @param minificationFilter
	 * @param magnificationFilter
	 */
	public void setTextureFilter(TextureFilter minificationFilter, TextureFilter magnificationFilter) {
		GLStateManager.assertOnRenderThread();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, minificationFilter.glType());
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, magnificationFilter.glType());
	}
	
	/**
	 * Binds the texture on the GPU for rendering.
	 */
	@Override
	public void bindTexture(int samplerId) {
		GLStateManager.assertOnRenderThread();
		GLStateManager.activeTexture(samplerId);
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
	}
	
	/**
	 * Binds texture 0 on the GPU.
	 * Effectively unbinds this texture.
	 */
	@Override
	public void unbindTexture() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
	}
	
	/**
	 * Deletes the texture map from the GPU memory.
	 */
	public void delete() {
		GLStateManager.deleteTexture(this.textureId);
	}
	
	/**
	 * If this texture map implementation supports multiple textures, this method is used to select one for other operations.
	 * @param textureLoc The texture name of the texture to select
	 */
	public abstract void activateTexture(R textureLoc);
	
	/**
	 * Returns the number of ticks each frame of the animation lasts.
	 * @return The number of ticks per frame
	 */
	public int getFrametime() {
		return frametime;
	}
	
	/**
	 * Activates the next frame of the animation.
	 */
	public void nextFrame() {
		this.activeFrame++;
		if (this.activeFrame >= this.frames.length) this.activeFrame = 0;
		updateMatrix();
	}
	
	/**
	 * Jumps to the given frame of the animation.
	 * @param frame The frame to jump to
	 * @throws IndexOutOfBoundsException If the frame index out of bounds
	 */
	public void setFrame(int frame) {
		if (frame < 0) throw new IndexOutOfBoundsException("The frame id can not be smaler than 0!");
		if (this.frames.length <= frame) throw new IndexOutOfBoundsException("The texture has only " + this.frames.length + " frames!");
		this.activeFrame = frame;
		updateMatrix();
	}

	/**
	 * Updates the frame matrices for the animation.
	 * This method is automatically called after {@link #nextFrame()} and {@link #setFrame(int)}.
	 * The matrices are passed to the shader to modify the UV parameters and select to correct frame in the texture.
	 */
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
		return doFrameInterpolation() ? this.animationMatrixLast : this.animationMatrix;
	}
	@Override
	public boolean doFrameInterpolation() {
		return this.interpolate;
	}
	
}
