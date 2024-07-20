package de.m_marvin.gframe.textures.utility;

import org.lwjgl.opengl.GL33;

public enum TextureDataFormat {
	
	INT_RGBA_8_8_8_8(GL33.GL_RGBA, GL33.GL_UNSIGNED_INT_8_8_8_8),
	INT_RGB_8_8_8(GL33.GL_RGB, GL33.GL_UNSIGNED_INT_8_8_8_8),
	INT_BGR_8_8_8(GL33.GL_BGR, GL33.GL_UNSIGNED_INT_8_8_8_8),
	INT_BGRA_8_8_8_8(GL33.GL_BGRA, GL33.GL_UNSIGNED_INT_8_8_8_8),
	INT_DEPTH(GL33.GL_DEPTH_COMPONENT, GL33.GL_UNSIGNED_INT),
	FLOAT_DEPTH(GL33.GL_DEPTH_COMPONENT, GL33.GL_FLOAT),
	INT_STENCIL(GL33.GL_STENCIL_INDEX, GL33.GL_UNSIGNED_BYTE),
	FLOAT_STENCIL(GL33.GL_STENCIL_INDEX, GL33.GL_FLOAT),
	INT_DEPTH_STENCIL(GL33.GL_DEPTH_STENCIL, GL33.GL_UNSIGNED_INT),
	FLOAT_DEPTH_STENCIL(GL33.GL_DEPTH_STENCIL, GL33.GL_FLOAT);
	
	private final int glPixelFormat;
	private final int glFormat;
	
	private TextureDataFormat(int pixelFormat, int format) {
		this.glPixelFormat = pixelFormat;
		this.glFormat = format;
	}
	
	public int glFormat() {
		return glFormat;
	}
	
	public int glPixelFormat() {
		return glPixelFormat;
	}
	
}
