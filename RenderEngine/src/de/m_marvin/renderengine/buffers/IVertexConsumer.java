package de.m_marvin.renderengine.buffers;

public interface IVertexConsumer {
	
	public IVertexConsumer vertex(float x, float y, float z);
	public IVertexConsumer normal(float x, float y, float z);
	public IVertexConsumer color(float r, float g, float b, float a);
	public IVertexConsumer uv(float u, float v);
	public IVertexConsumer index(int i);
	public IVertexConsumer indecies(int... i);
	public void endVertex();
	
}
