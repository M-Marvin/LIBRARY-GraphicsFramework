package de.m_marvin.renderengine.buffers.defimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.IBufferSource;
import de.m_marvin.renderengine.resources.IResourceProvider;

public class SimpleBufferSource<R extends IResourceProvider<R>> implements IBufferSource<RenderMode<R>> {
	
	protected final int initialBufferSize;
	protected Map<RenderMode<R>, BufferBuilder> buffers;
	
	public SimpleBufferSource(int initialBufferSize) {
		this.initialBufferSize = initialBufferSize;
		this.buffers = new HashMap<>();
	}
	
	@Override
	public BufferBuilder getBuffer(RenderMode<R> renderLayer) {
		BufferBuilder buffer = buffers.get(renderLayer);
		if (buffer == null) {
			buffer = new BufferBuilder(initialBufferSize);
			buffers.put(renderLayer, buffer);
		}
		return buffer;
	}
	
	@Override
	public BufferBuilder startBuffer(RenderMode<R> renderLayer) {
		BufferBuilder buffer = getBuffer(renderLayer);
		buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
		return buffer;
	}
	
	@Override
	public Set<RenderMode<R>> getBufferTypes() {
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
