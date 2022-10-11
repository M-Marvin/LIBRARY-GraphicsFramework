package de.m_marvin.renderengine;

import java.io.File;
import java.io.IOException;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.buffers.BufferBuilder.BufferPair;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.renderengine.vertecies.VertexFormat.Format;

public class RenderEngine {
	
	public static void main(String... args) {
		try {
			new RenderEngine().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void start() throws IOException {
		
		// TODO GL-Context
		
		VertexFormat format = new VertexFormat().appand("vertex", Format.FLOAT, 3, false).appand("normal", Format.FLOAT, 3, true).appand("color", Format.FLOAT, 4, true).appand("uv", Format.FLOAT, 2, true);
		
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
		
		VertexBuffer vertexBuffer = new VertexBuffer();
		vertexBuffer.upload(buffer);
		
		File shaderFile = new File(this.getClass().getClassLoader().getResource("").getPath(), "shaders/testShader.json");
		ShaderInstance shader = ShaderLoader.load(shaderFile, format);
		
		vertexBuffer.discard();
		
	}
	
}
