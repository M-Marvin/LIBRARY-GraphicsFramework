package de.m_marvin.voxelengine.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.voxelengine.rendering.LevelRender.StructureRender;
import de.m_marvin.voxelengine.world.ClientLevel;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class LevelRender {
	
	public static record StructureRender(AtomicReference<VertexBuffer> renderBuffer, boolean dirty) {}
	
	public Map<VoxelStructure, StructureRender> structureRenders = new HashMap<>();
	
	public StructureRender getOrCreateRender(VoxelStructure structure) {
		StructureRender render = structureRenders.get(structure);
		if (render == null) {
			render = new StructureRender(new AtomicReference<>(), true);
			structureRenders.put(structure, render);
		}
		return render;
	}
	
	public void removeRender(VoxelStructure structure) {
		if (!structureRenders.containsKey(structure)) return;
		structureRenders.remove(structure).renderBuffer.get().discard();
	}
	
}
