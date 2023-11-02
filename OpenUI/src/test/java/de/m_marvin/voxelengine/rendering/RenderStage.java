package de.m_marvin.voxelengine.rendering;

import java.util.LinkedList;
import java.util.Queue;

public enum RenderStage {
	
	UTIL("renderstage.util"),UI("renderstage.ui"),LEVEL("renderstage.level");
	
	private RenderStage(String name) {
		this.name = name;
		this.preExecQueue = new LinkedList<>();
		this.postExecQueue = new LinkedList<>();
	}
	
	public String getName() {
		return name;
	}
	
	protected String name;
	protected Queue<Runnable> preExecQueue;
	protected Queue<Runnable> postExecQueue;
	
}
