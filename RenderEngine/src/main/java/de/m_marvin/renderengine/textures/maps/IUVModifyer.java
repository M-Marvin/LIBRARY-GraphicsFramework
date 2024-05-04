package de.m_marvin.renderengine.textures.maps;

import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.univec.impl.Vec4f;

/**
 * Interface implementing all methods required for providing UV modifications to select different frames and textures form atlas maps.
 * 
 * @author Marvin Köhler
 *
 */
public interface IUVModifyer {

	/**
	 * Returns the UV position and size of the texture in the atlas.
	 * The XY values of the Vec4f represent the XY position of the low corner.
	 * The ZW values of the Vec4f represent the width and height.
	 * 
	 * @return The UV position and size of the texture in the atlas packed as Vec4f
	 */
	public Vec4f getUV();

	/**
	 * Modifies the provided texture parameter.
	 * Used to point to the correct location on texture atlas maps.
	 * 
	 * @param u The texture position parameter
	 * @return The modified texture position parameter
	 */
	public float mapU(float u);

	/**
	 * Modifies the provided texture parameter.
	 * Used to point to the correct location on texture atlas maps.
	 * 
	 * @param v The texture position parameter
	 * @return The modified texture position parameter
	 */
	public float mapV(float v);
	
	/**
	 * Returns a matrix for the current animation frame.
	 * @return A {@link Matrix3f} that can be applied to the UV parameters in the shader to select the correct frame
	 */	
	public Matrix3f frameMatrix();
	
	/**
	 * Returns a matrix for the last animation frame for interpolated textures and a matrix to the current frame for not interpolated textures.
	 * @return A {@link Matrix3f} that can be applied to the UV parameters in the shader to select the correct frame
	 */	
	public Matrix3f lastFrameMatrix();
	
	/**
	 * Returns true if this texture map has to be interpolated between frames.
	 * @return True if this texture map has to be interpolated
	 */
	public boolean doFrameInterpolation();

}
