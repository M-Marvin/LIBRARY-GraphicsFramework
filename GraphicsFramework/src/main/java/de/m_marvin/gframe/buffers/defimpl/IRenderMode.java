package de.m_marvin.gframe.buffers.defimpl;

import de.m_marvin.gframe.vertices.RenderPrimitive;
import de.m_marvin.gframe.vertices.VertexFormat;

public interface IRenderMode {
	
	RenderPrimitive primitive(); 
	VertexFormat vertexFormat();
	
}
