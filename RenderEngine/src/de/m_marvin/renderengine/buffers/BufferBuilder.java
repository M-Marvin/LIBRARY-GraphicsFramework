package de.m_marvin.renderengine.buffers;

import com.google.common.collect.Queues;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.lwjgl.system.MemoryUtil;

import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.renderengine.vertecies.VertexFormat.VertexElement;

public class BufferBuilder implements IVertexConsumer {
	
	protected ByteBuffer buffer;
	protected Queue<DrawState> drawStates;
	protected VertexFormat format;
	protected RenderPrimitive type;
	protected boolean building;
	protected boolean buildingIndecies;
	protected int vertexCount;
	protected int indexCount;
	protected int currentElementIndex;
	protected VertexElement currentElement;
	protected int uploadedBytes;
	
	public static record DrawState(int vertecies, int indecies, VertexFormat format, RenderPrimitive type) {}
	public static record BufferPair(ByteBuffer buffer, DrawState drawState) {}
	
	public BufferBuilder(int bufferSize) {
		this.buffer = MemoryUtil.memAlloc(bufferSize);
		this.drawStates = Queues.newArrayDeque();
	}
	
	public BufferPair popNext() {
		if (this.drawStates.isEmpty()) throw new IllegalStateException("Nothing has ben drawn to the buffer!");
		DrawState drawState = this.drawStates.poll();
		this.buffer.position(uploadedBytes);
		this.uploadedBytes += drawState.format().getSize() * drawState.vertecies + drawState.indecies * Integer.BYTES;
		this.buffer.limit(uploadedBytes);
		ByteBuffer drawBuffer = this.buffer.slice();
		this.buffer.clear();
		return new BufferPair(drawBuffer, drawState);	
	}
	
	public void clear() {
		this.buffer.clear();
		this.drawStates.clear();
		this.vertexCount = 0;
		this.indexCount = 0;
		this.building = false;
		this.currentElementIndex = -1;
	}

	public void freeMemory() {
		MemoryUtil.memFree(buffer);
	}
	
	public void begin(RenderPrimitive type, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("BufferBuilder already building!");
		} else {
			this.building = true;
			this.format = format;
			this.type = type;
			this.vertexCount = 0;
			this.indexCount = 0;
			this.currentElementIndex = -1;
		}
	}

	public void end() {
		if (this.building) {
			if (!this.buildingIndecies) {
				this.type.buildDefaultIndecies(this.vertexCount, this::putInt);
			}
			this.drawStates.add(new DrawState(this.vertexCount, this.indexCount, this.format, this.type));
			this.vertexCount = 0;
			this.indexCount = 0;
			this.building = false;
			this.buildingIndecies = false;
		}
	}

	public VertexElement getCurrentElement() {
		return currentElement;
	}
	
	public IVertexConsumer nextElement() {
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		this.currentElementIndex++;
		if (this.currentElementIndex == this.format.getElementCount()) throw new IllegalStateException("The current VertexFormat does not have more than " + this.format.getElementCount() + " elements!");
		this.currentElement = this.format.getElements().get(currentElementIndex);
		return this;
	}
	
	public IVertexConsumer putFloat(float f) {
		this.buffer.putFloat(f);
		return this;
	}
	public IVertexConsumer putInt(int i) {
		this.buffer.putInt(i);
		return this;
	}
	public IVertexConsumer putShort(short s) {
		this.buffer.putShort(s);
		return this;
	}
	public IVertexConsumer putByte(byte b) {
		this.buffer.put(b);
		return this;
	}
	public IVertexConsumer putIntArr(int... intArr) {
		for (int i : intArr) this.buffer.putInt(i);
		return this;
	}
	public IVertexConsumer putFloatArr(float... floatArr) {
		for (float f : floatArr) this.buffer.putFloat(f);
		return this;
	}
	public IVertexConsumer putShortArr(short... shortArr) {
		for (short s : shortArr) this.buffer.putShort(s);
		return this;
	}
	public IVertexConsumer putByteArr(byte... floatArr) {
		for (byte b : floatArr) this.buffer.put(b);
		return this;
	}
	
	@Override
	public IVertexConsumer vertex(float x, float y, float z) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(x);
		putFloat(y);
		putFloat(z);
		return this;
	}

	@Override
	public IVertexConsumer normal(float x, float y, float z) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(x);
		putFloat(y);
		putFloat(z);
		return this;
	}

	@Override
	public IVertexConsumer color(float r, float g, float b, float a) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(r);
		putFloat(g);
		putFloat(b);
		putFloat(a);
		return this;
	}

	@Override
	public IVertexConsumer uv(float u, float v) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(u);
		putFloat(v);
		return this;
	}
	
	@Override
	public void endVertex() {
		if (this.currentElementIndex != this.format.getElementCount() - 1) {
			throw new IllegalStateException("Not all elements filled!");
		} else if (this.buildingIndecies) {
			throw new IllegalStateException("Cant put vertecies after indecies!");
		} else {
			this.currentElementIndex = -1;
			this.vertexCount++;
		}
	}

	@Override
	public IVertexConsumer index(int i) {
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		if (this.currentElementIndex != -1) {
			throw new IllegalStateException("Not all elements filled!");
		} else {
			this.buildingIndecies = true;
			putInt(i);
			this.indexCount++;
		}
		return this;
	}

	@Override
	public IVertexConsumer indecies(int... i) {
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		if (this.currentElementIndex != -1) {
			throw new IllegalStateException("Not all elements filled!");
		} else {
			this.buildingIndecies = true;
			putIntArr(i);
			this.indexCount += i.length;
		}
		return this;
	}
	
}
