package de.m_marvin.renderengine;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferBuilder.BufferPair;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.renderengine.vertecies.VertexFormat.Format;

public class RenderEngine {
	
	public static void main(String... args) {
		new RenderEngine().start();		
	}
	
	public void start() {
		
		VertexFormat format = new VertexFormat().appand("vertex", Format.FLOAT, 3).appand("normal", Format.FLOAT, 3).appand("color", Format.FLOAT, 4).appand("uv", Format.FLOAT, 2);
		
		BufferBuilder buffer = new BufferBuilder(3200);
		
		buffer.begin(RenderPrimitive.QUADS, format);
		
		buffer.vertex(-1, -1, 0).normal(0, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
		buffer.vertex(1, -1, 0).normal(0, 0, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		buffer.vertex(-1, 1, 0).normal(0, 0, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(1, 1, 0).normal(0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
				
		buffer.end();
		

		buffer.begin(RenderPrimitive.QUADS, format);
		
		buffer.vertex(-1, -1, 0).normal(0, 0, 1).color(1, 0, 0, 1).uv(0, 0).endVertex();
		buffer.vertex(1, -1, 0).normal(0, 0, 1).color(0, 1, 0, 1).uv(1, 0).endVertex();
		buffer.vertex(-1, 1, 0).normal(0, 0, 1).color(0, 0, 1, 1).uv(0, 1).endVertex();
		buffer.vertex(1, 1, 0).normal(0, 0, 1).color(1, 1, 1, 1).uv(1, 1).endVertex();
		
		buffer.index(0).index(1).index(2).index(3);
		buffer.index(3).index(2).index(1).index(0);
		
		buffer.end();
		
		BufferPair pair1 = buffer.popNext();
		BufferPair pair2 = buffer.popNext();
		
		
	}
	
}
