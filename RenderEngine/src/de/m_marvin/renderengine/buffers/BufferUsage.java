package de.m_marvin.renderengine.buffers;

import org.lwjgl.opengl.GL33;

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
