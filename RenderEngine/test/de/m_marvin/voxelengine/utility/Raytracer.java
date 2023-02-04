package de.m_marvin.voxelengine.utility;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.world.VoxelComponent;

public class Raytracer {
	
	public static Vec3f getCameraRay(Vec2d coursorPosition, Vec2i windowSize, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
		Vec2d normalizedCoords = coursorPosition.mul(2.0).div(windowSize).sub(1.0, 1.0);
		Vec4f clipCoords = new Vec4f((float) normalizedCoords.x, (float) normalizedCoords.y, -1F, 1F);
		Matrix4f invertedProjectionMatrix = projectionMatrix.copy();
		invertedProjectionMatrix.invert();
		Vec4f eyeCoords = (Vec4f) invertedProjectionMatrix.translate(clipCoords);
		eyeCoords = new Vec4f(eyeCoords.x, eyeCoords.y, -1F, 0F);
		Matrix4f invertedViewMatrix = viewMatrix.copy();
		invertedViewMatrix.invert();
		Vec4f rayWorld = (Vec4f) invertedViewMatrix.translate(eyeCoords);
		Vec3f mouseRay = new Vec3f(rayWorld.x, rayWorld.y, rayWorld.z).normalize();
		return mouseRay;
	}
	
	protected final Vec3f rayOrigin;
	protected final Vec3f rayVector;
	
	public Raytracer(Vec3f origin, Vec3f vector) {
		this.rayOrigin = origin;
		this.rayVector = vector;
	}
	
	public Optional<Vec3i> raytraceComponent(VoxelComponent component, float stepLength, float rayLength) {
		
		for (float rayPos = 0F; rayPos <= rayLength; rayPos += stepLength) {
			Vec3f point = this.rayOrigin.add(this.rayVector.mul(rayPos));
			Vec3i voxelPos = new Vec3i((int) Math.floor(point.x), (int) Math.floor(point.y), (int) Math.floor(point.z));
			
			if (GLFW.glfwGetKey(VoxelEngine.getInstance().getMainWindow().windowId(), GLFW.GLFW_KEY_T) == GLFW.GLFW_PRESS) {

				System.out.println(this.rayOrigin + " " + this.rayVector + " " + voxelPos);
				
			}
			
			for (int[][][] voxel : component.getVoxels()) {
				
				if (voxelPos.x >= 0 && voxelPos.x < voxel.length &&
					voxelPos.y >= 0 && voxelPos.y < voxel[0].length &&
					voxelPos.z >= 0 && voxelPos.z < voxel[0][0].length)  {
					
					int voxelMaterialId = voxel[voxelPos.x][voxelPos.y][voxelPos.z];
					
					if (voxelMaterialId > 0) {
						
						return Optional.of(voxelPos);
						
					}
					
				}
			}
			
		}
		
		return Optional.empty();
		
	}
	
}
