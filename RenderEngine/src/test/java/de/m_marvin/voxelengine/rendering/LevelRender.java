package de.m_marvin.voxelengine.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class LevelRender {
	
	public static class StructureRender {
		
		protected final Map<RenderType, VertexBuffer> buffer;
		protected boolean dirty;
		
		public StructureRender() {
			this.dirty = true;
			this.buffer = Stream.of(RenderType.voxelRenderLayers()).collect(Collectors.toMap(t -> t, t -> new VertexBuffer()));
		}
		
		public boolean isDirty() {
			return dirty;
		}
		
		public VertexBuffer getBuffer(RenderType renderLayer) {
			return buffer.get(renderLayer);
		}
		
		public void setDirty(boolean dirty) {
			this.dirty = dirty;
		}
		
	}
	
	public Map<VoxelStructure, StructureRender> structureRenders = new HashMap<>();
	
	public StructureRender getOrCreateRender(VoxelStructure structure) {
		StructureRender render = structureRenders.get(structure);
		if (render == null) {
			render = new StructureRender();
			structureRenders.put(structure, render);
		}
		return render;
	}
	
	public void removeRender(VoxelStructure structure) {
		if (!structureRenders.containsKey(structure)) return;
		structureRenders.remove(structure).buffer.forEach((renderLayer, buffer) -> buffer.discard());
	}

	public void discard() {
		this.structureRenders.forEach((structure, render) -> {
			for (RenderType renderLayer : RenderType.voxelRenderLayers()) render.getBuffer(renderLayer).discard();
		});
		this.structureRenders.clear();
	}
	
}
