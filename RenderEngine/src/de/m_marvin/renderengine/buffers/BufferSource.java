package de.m_marvin.renderengine.buffers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.m_marvin.voxelengine.rendering.RenderType;

public class BufferSource implements IBufferSource<RenderType> {
	
	protected final int initialBufferSize;
	protected Map<RenderType, BufferBuilder> buffers;
	
	public BufferSource(int initialBufferSize) {
		this.initialBufferSize = initialBufferSize;
		this.buffers = new HashMap<>();
	}
	
	@Override
	public BufferBuilder getBuffer(RenderType renderLayer) {
		BufferBuilder buffer = buffers.get(renderLayer);
		if (buffer == null) {
			buffer = new BufferBuilder(initialBufferSize);
			buffers.put(renderLayer, buffer);
		}
		return buffer;
	}
	
	@Override
	public BufferBuilder startBuffer(RenderType renderLayer) {
		BufferBuilder buffer = getBuffer(renderLayer);
		buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
		return buffer;
	}
	
	@Override
	public Set<RenderType> getBufferTypes() {
		return this.buffers.keySet();
	}
	
	@Override
	public void freeAllMemory() {
		this.buffers.forEach((renderLayer, buffer) -> buffer.freeMemory());
	}

	@Override
	public void discardAll() {
		this.buffers.forEach((renderLayer, buffer) -> buffer.discardStored());
	}
	
}
