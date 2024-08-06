package de.m_marvin.stonegenerator;

import de.m_marvin.gframe.buffers.defimpl.RenderMode;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.gframe.utility.NumberFormat;
import de.m_marvin.gframe.vertices.RenderPrimitive;
import de.m_marvin.gframe.vertices.VertexFormat;

public class RenderTypes {
	
	private static final RenderMode<ResourceLocation> stone = new RenderMode<ResourceLocation>(
			RenderPrimitive.TRIANGLES, 
			new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("color", NumberFormat.FLOAT, 4, false), 
			new ResourceLocation("stonegen:draw_stone"), 
			(shader, texloader) -> {});
	public static RenderMode<ResourceLocation> stone() {
		return stone;
	}
	
}
