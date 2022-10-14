package de.m_marvin.renderengine.buffers;

import de.m_marvin.renderengine.vertecies.PoseStack;

public interface IVertexConsumer {
	
	public default IVertexConsumer vertex(PoseStack poseStack, float x, float y, float z) {
		// TODO Requires Vec4f
	}
	public IVertexConsumer vertex(float x, float y, float z);
	public default IVertexConsumer normal(PoseStack poseStack, float x, float y, float z) {
		// TODO Requires Vec4f
	}
	public IVertexConsumer normal(float x, float y, float z);
	public IVertexConsumer color(float r, float g, float b, float a);
	public IVertexConsumer uv(float u, float v);
	public IVertexConsumer index(int i);
	public IVertexConsumer indecies(int... i);
	public void endVertex();
	
}
