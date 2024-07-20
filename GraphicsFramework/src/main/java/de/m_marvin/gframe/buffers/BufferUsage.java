package de.m_marvin.gframe.buffers;

import org.lwjgl.opengl.GL33;

/**
 * Enum representing the OpenGL vertex buffer usage constants.
 * Only the render engine used combinations are implemented.
 * 
 * @author Marvin Koehler
 */
public enum BufferUsage {
	
	STATIC(GL33.GL_STATIC_DRAW),DYNAMIC(GL33.GL_DYNAMIC_DRAW),STREAM(GL33.GL_STREAM_DRAW);
	
	private final int gltype;
	
	BufferUsage(int gltype) {
		this.gltype = gltype;
	}
	
	public int gltype() {
		return this.gltype;
	}
	
}
