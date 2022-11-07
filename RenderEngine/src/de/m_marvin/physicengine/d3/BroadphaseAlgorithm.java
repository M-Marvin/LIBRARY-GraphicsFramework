package de.m_marvin.physicengine.d3;

import java.util.function.BiFunction;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.AxisSweep3_32;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.SimpleBroadphase;

import de.m_marvin.univec.impl.Vec3f;

public enum BroadphaseAlgorithm {
	SIMPLE((worldMin, worldMax) -> new SimpleBroadphase()),
	DBVT((worldMin, worldMax) -> new DbvtBroadphase()),
	AXIS_SWEEP_3((worldMin, worldMax) -> new AxisSweep3(new Vector3f(worldMin.x, worldMin.y, worldMin.z), new Vector3f(worldMax.x, worldMax.y, worldMax.z))),
	AXIS_SWEEP_3_32((worldMin, worldMax) -> new AxisSweep3_32(new Vector3f(worldMin.x, worldMin.y, worldMin.z), new Vector3f(worldMax.x, worldMax.y, worldMax.z)));
	
	private BroadphaseAlgorithm(BiFunction<Vec3f, Vec3f, BroadphaseInterface> broadphaseInterface) {
		this.broadphaseInterface = broadphaseInterface;
	}
	
	private final BiFunction<Vec3f, Vec3f, BroadphaseInterface> broadphaseInterface;
	
	public BroadphaseInterface getInterface(Vec3f worldMin, Vec3f worldMax) {
		return this.broadphaseInterface.apply(worldMin, worldMax);
	}
	
}
