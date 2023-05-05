package de.m_marvin.voxelengine.rendering;

import java.util.Queue;

import com.google.common.collect.Queues;

public enum RenderStage {
	
	UTIL("renderstage.util"),UI("renderstage.ui"),LEVEL("renderstage.level");
	
	private RenderStage(String name) {
		this.name = name;
		this.preExecQueue = Queues.newArrayDeque();
		this.postExecQueue = Queues.newArrayDeque();
	}
	
	public String getName() {
		return name;
	}
	
	protected String name;
	protected Queue<Runnable> preExecQueue;
	protected Queue<Runnable> postExecQueue;
	
}
