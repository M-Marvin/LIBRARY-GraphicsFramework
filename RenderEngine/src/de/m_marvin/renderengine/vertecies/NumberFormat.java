package de.m_marvin.renderengine.vertecies;

import org.lwjgl.opengl.GL33;

public enum NumberFormat {
	UBYTE(Byte.BYTES, GL33.GL_UNSIGNED_BYTE),USHORT(Short.BYTES, GL33.GL_UNSIGNED_SHORT),UINT(Integer.BYTES, GL33.GL_UNSIGNED_INT),INT(Integer.BYTES, GL33.GL_INT),SHORT(Short.BYTES, GL33.GL_SHORT),FLOAT(Float.BYTES, GL33.GL_FLOAT),BYTE(Byte.BYTES, GL33.GL_BYTE);
	private final int bytes;
	private final int glType;
	NumberFormat(int bytes, int glType) {
		this.bytes = bytes;
		this.glType = glType;
	}
	public int size() {
		return this.bytes;
	}
	public int gltype() {
		return this.glType;
	}
}

