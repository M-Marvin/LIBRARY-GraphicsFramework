package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.lwjgl.system.MemoryUtil;

import com.google.common.collect.Queues;

import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.IVertexConsumer;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.renderengine.vertecies.VertexFormat.VertexElement;

public class ParalelBufferBuilder implements IBufferBuilder, IVertexConsumer {

	protected ByteBuffer[] buffer;
	protected Queue<DrawState> drawStates;
	protected int[] uploadedBytes;

	protected VertexFormat format;
	protected RenderPrimitive type;
	protected boolean building;
	protected boolean buildingIndecies;
	protected int vertexCount;
	protected int indexCount;
	protected int currentElementIndex;
	protected VertexElement currentElement;
	
	public ParalelBufferBuilder(int bufferSize, int paralelBuffers) {
		this.buffer = new ByteBuffer[paralelBuffers];
		for (int i = 0; i < this.buffer.length; i++)
			this.buffer[i] = MemoryUtil.memAlloc(bufferSize);
		this.uploadedBytes = new int[this.buffer.length];
		this.drawStates = Queues.newArrayDeque();
	}
	
	@Override
	public int paralelDataVAOs() {
		return this.buffer.length;
	}
	
	@Override
	public int completedBuffers() {
		return this.drawStates.size();
	}
	
	public BufferPair popNext() {
		if (this.drawStates.isEmpty()) throw new IllegalStateException("Nothing has ben drawn to the buffer!");
		DrawState drawState = this.drawStates.poll();
		ByteBuffer[] drawBuffer = new ByteBuffer[paralelDataVAOs()];
		
		this.buffer[0].position(uploadedBytes[0]);
		this.uploadedBytes[0] += NumberFormat.UINT.size() * drawState.indecies();
		this.buffer[0].limit(uploadedBytes[0]);
		drawBuffer[0] = this.buffer[0].slice();
		drawBuffer[0].order(this.buffer[0].order());
		
		for (int i = 1; i < drawState.format().getElementCount() + 1; i++) {
			VertexFormat.VertexElement element = drawState.format().elementWithIndex(i - 1);
			this.buffer[i].position(uploadedBytes[i]);
			this.uploadedBytes[i] += element.format().size() * element.count() * drawState.vertecies();
			this.buffer[i].limit(uploadedBytes[i]);
			drawBuffer[i] = this.buffer[i].slice();
			drawBuffer[i].order(this.buffer[i].order());
		}
		
		return new BufferPair(drawBuffer, drawState);
	}
	
	@Override
	public void discardStored() {
		for (int i = 0; i < paralelDataVAOs(); i++) {
			this.buffer[i].clear();
			this.uploadedBytes[i] = 0;
		}
		this.drawStates.clear();
		this.vertexCount = 0;
		this.indexCount = 0;
		this.building = false;
		this.currentElementIndex = -1;
	}

	public void freeMemory() {
		discardStored();
		for (int i = 0; i < paralelDataVAOs(); i++) {
			MemoryUtil.memFree(buffer[i]);
		}
	}
	
	protected int targetBuffer() {
		return this.currentElementIndex == -1 ? 0 : this.currentElement.index() + 1;
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
				this.type.buildDefaultIndecies(this.vertexCount, this::index);
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

	@Override
	public IVertexConsumer nextElement(int targetIndex) {
		if (targetIndex > paralelDataVAOs()) throw new IllegalStateException("Buffer does not support a vao index grater than " + paralelDataVAOs() + "!");
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		this.currentElementIndex++;
		if (this.currentElementIndex == this.format.getElementCount()) throw new IllegalStateException("The current VertexFormat does not have more than " + this.format.getElementCount() + " elements!");
		this.currentElement = this.format.getElements().get(currentElementIndex);
		return this;
	}

	@Override
	public IVertexConsumer putFloat(float f) {
		this.buffer[targetBuffer()].putFloat(f);
		return this;
	}
	@Override
	public IVertexConsumer putInt(int i) {
		this.buffer[targetBuffer()].putInt(i);
		return this;
	}
	@Override
	public IVertexConsumer putShort(short s) {
		this.buffer[targetBuffer()].putShort(s);
		return this;
	}
	@Override
	public IVertexConsumer putByte(byte b) {
		this.buffer[targetBuffer()].put(b);
		return this;
	}
	@Override
	public IVertexConsumer putIntArr(int... intArr) {
		for (int i : intArr) this.buffer[targetBuffer()].putInt(i);
		return this;
	}
	@Override
	public IVertexConsumer putFloatArr(float... floatArr) {
		for (float f : floatArr) this.buffer[targetBuffer()].putFloat(f);
		return this;
	}
	@Override
	public IVertexConsumer putShortArr(short... shortArr) {
		for (short s : shortArr) this.buffer[targetBuffer()].putShort(s);
		return this;
	}
	@Override
	public IVertexConsumer putByteArr(byte... floatArr) {
		for (byte b : floatArr) this.buffer[targetBuffer()].put(b);
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
