package de.m_marvin.renderengine.textures;

import org.lwjgl.opengl.GL33;

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
