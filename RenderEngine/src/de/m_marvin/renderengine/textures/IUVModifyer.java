package de.m_marvin.renderengine.textures;

import de.m_marvin.unimat.impl.Matrix3f;

/**
 * Interface implementing all methods required for providing UV modifications to select different frames and textures form atlas maps.
 * 
 * @author Marvin Köhler
 *
 */
public interface IUVModifyer {
	
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
