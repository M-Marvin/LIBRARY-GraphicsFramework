package de.m_marvin.renderengine.buffers.defimpl;

import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public interface IRenderMode {
	
	RenderPrimitive primitive(); 
	VertexFormat vertexFormat();
	
}
