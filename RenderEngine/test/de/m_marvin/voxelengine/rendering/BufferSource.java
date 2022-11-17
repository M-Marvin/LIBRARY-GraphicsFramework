package de.m_marvin.voxelengine.rendering;

import java.util.HashMap;
import java.util.Map;

import de.m_marvin.renderengine.buffers.BufferBuilder;

public class BufferSource {
	
	protected final int initialBufferSize;
	protected Map<RenderType, BufferBuilder> buffers;
	
	public BufferSource(int initialBufferSize) {
		this.initialBufferSize = initialBufferSize;
		this.buffers = new HashMap<>();
	}
	
	public BufferBuilder getBuffer(RenderType renderLayer) {
		BufferBuilder buffer = buffers.get(renderLayer);
		if (buffer == null) {
			buffer = new BufferBuilder(initialBufferSize);
			buffers.put(renderLayer, buffer);
		}
		return buffer;
	}
	
	public void freeMemory() {
		this.buffers.forEach((renderLayer, buffer) -> buffer.freeMemory());
	}
	
	public void discard() {
		this.buffers.forEach((renderLayer, buffer) -> buffer.discardStored());
	}
	
}
