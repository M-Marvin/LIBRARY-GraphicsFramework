package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;

import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;

public interface IBufferBuilder {
	
	public static record DrawState(int vertecies, int indecies, VertexFormat format, RenderPrimitive type) {}
	public static record BufferPair(ByteBuffer buffer, DrawState drawState) {}
	
	public void begin(RenderPrimitive type, VertexFormat format);
	public void end();
	
	public int completedBuffers();
	public BufferPair popNext();
	public void discardStored();
	
}
