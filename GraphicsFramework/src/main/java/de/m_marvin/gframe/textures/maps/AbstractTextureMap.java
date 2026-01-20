package de.m_marvin.gframe.textures.maps;

import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.gframe.textures.texture.Texture;
import de.m_marvin.gframe.textures.utility.TextureDataFormat;
import de.m_marvin.gframe.textures.utility.TextureFormat;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.univec.impl.Vec2f;

/**
 * Base class of the different texture maps.
 * Contains basic functionality to upload the texture data to the GPU and declares animation and texture map features.
 * 
 * @author Marvin Köhler
 *
 * @param <R> The resource location type used
 */
public abstract class AbstractTextureMap<R extends IResourceProvider<R>> extends Texture implements IUVModifyer {
	
	protected Matrix3f animationMatrixLast;
	protected Matrix3f animationMatrix;
	protected int frameHeight;
	protected int[] frames;
	protected int frametime;
	protected int activeFrame;
	protected boolean interpolate;

	public AbstractTextureMap(TextureFormat format) {
		super(format);
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
		float scale = (float) frameHeight / height;
		float offset = frames[activeFrame];
		this.animationMatrix = Matrix3f.scale(new Vec2f(1, scale)).mul(Matrix3f.translation(new Vec2f(0, offset)));
		if (this.animationMatrixLast == null) this.animationMatrixLast = this.animationMatrix;
	}
	
	@Override
	public void upload(int width, int height, TextureDataFormat format, int[] pixels) {
		super.upload(width, height, format, pixels);
		updateMatrix();
	}
	
	/**
	 * Gets the width of the currently activated texture of this map.
	 * @return The width in pixels
	 */
	public abstract int getImageWidth();
	/**
	 * Gets the height of the currently activated texture of this map.
	 * @return The height in pixels
	 */
	public abstract int getImageHeight();

	/**
	 * Gets the width of the entire texture map.
	 * @return The width in pixels
	 */
	public abstract int getMapWidth();
	/**
	 * Gets the height of the entire texture map.
	 * @return The height in pixels
	 */
	public abstract int getMapHeight();
	
	/**
	 * A matrix that can be applied to the uv positions in the shader to apply the animation.
	 * @return The matrix for the current frame
	 */
	@Override
	public Matrix3f frameMatrix() {
		return this.animationMatrix;
	}
	
	/**
	 * A matrix that can be applied to the uv positions in the shader to apply the animation.
	 * @return The matrix for the last frame, used for interpolation
	 */
	@Override
	public Matrix3f lastFrameMatrix() {
		return doFrameInterpolation() ? this.animationMatrixLast : this.animationMatrix;
	}
	
	/**
	 * If the texture was marked for interpolation.
	 * @return true if interpolation should be applied
	 */
	@Override
	public boolean doFrameInterpolation() {
		return this.interpolate;
	}
	
}
