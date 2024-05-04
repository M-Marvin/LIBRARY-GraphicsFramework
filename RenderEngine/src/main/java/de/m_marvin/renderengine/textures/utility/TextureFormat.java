package de.m_marvin.renderengine.textures.utility;

import org.lwjgl.opengl.GL33;

public enum TextureFormat {
	
	DEPTH(GL33.GL_DEPTH_COMPONENT),
	DEPTH_STENCIL(GL33.GL_DEPTH_STENCIL),
	RED(GL33.GL_RED),
	RED_GREEN(GL33.GL_RG),
	RED_GREEN_BLUE(GL33.GL_RGB),
	RED_GREEN_BLUE_ALPHA(GL33.GL_RGBA),
	RED_GREEN_BLUE_ALPHA_GAMMACORRECT(GL33.GL_SRGB_ALPHA);
	
	// TODO all formats from https://docs.gl/gl4/glTexImage2D
	
	private final int glType;
	
	private TextureFormat(int glType) {
		this.glType = glType;
	}
	
	public int glType() {
		return glType;
	}
}
