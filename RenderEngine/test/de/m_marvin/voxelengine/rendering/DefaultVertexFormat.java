package de.m_marvin.voxelengine.rendering;

import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class DefaultVertexFormat {
	
	public static final VertexFormat VOXELS = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("orientation", NumberFormat.FLOAT, 4, true).appand("voxel", NumberFormat.INT, 3, false).appand("sides", NumberFormat.UINT, 1, false).appand("color", NumberFormat.FLOAT, 4, false).appand("texuv", NumberFormat.FLOAT, 4, false).appand("texsize", NumberFormat.INT, 2, false);
	public static final VertexFormat SOLID = new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("normal", NumberFormat.FLOAT, 3, true).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
	public static final VertexFormat SCREEN = new VertexFormat().appand("position", NumberFormat.FLOAT, 2, false).appand("color", NumberFormat.FLOAT, 4, false).appand("uv", NumberFormat.FLOAT, 2, false);
	
}
