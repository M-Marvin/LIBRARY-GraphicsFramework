package de.m_marvin.gframe.textures.utility;

import org.lwjgl.opengl.GL33;

/**
 * Represents the texture filters that can be applied to textures by OpenGL.
 * 
 * @author Marvin Köhler
 *
 */
public enum TextureFilter {
	
	LINEAR(GL33.GL_LINEAR),NEAREST(GL33.GL_NEAREST);
	
	private final int glType;
	
	private TextureFilter(int glType) {
		this.glType = glType;
	}
	
	public int glType() {
		return this.glType;
	}
	
}
