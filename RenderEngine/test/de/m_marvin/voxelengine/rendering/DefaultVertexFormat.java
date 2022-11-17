package de.m_marvin.voxelengine.rendering;

import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class DefaultVertexFormat {
	
	public static final VertexFormat VOXELS = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 4, false);
	
}
