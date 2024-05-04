package de.m_marvin.renderengine.textures.texture;

/**
 * Interface implementing all methods required for binding a texture to a shader for rendering.
 * 
 * @author Marvin Köhler
 *
 */
public interface ITextureSampler {
	
	/**
	 * Binds the texture to the given sampler index.
	 * @param samplerId The sampler index to bind the texture to
	 */
	public void bindSampler(int samplerId);
	
	/**
	 * Unbinds the texture.
	 */
	public void unbindSampler();
		
}
