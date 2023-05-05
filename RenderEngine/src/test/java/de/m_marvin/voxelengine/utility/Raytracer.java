package de.m_marvin.voxelengine.utility;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4d;
import de.m_marvin.univec.impl.Vec4f;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.world.VoxelComponent;

public class Raytracer {
	
	public static Vec3f getCameraRay(Vec2d coursorPosition, float clipPlaneZ, Vec2f windowSize, Matrix4f viewMatrix, Matrix4f projectionMatrix) {

		Matrix4f transformMatrix = projectionMatrix.mul(viewMatrix);
		transformMatrix.invert();
		
		Vec4f clip = new Vec4f((float) coursorPosition.x, (float) coursorPosition.y, clipPlaneZ, 1);
		
		clip.x = clip.x / windowSize.x;
		clip.y = clip.y / windowSize.y;
		
		clip.x = clip.x * 2 - 1;
		clip.y = clip.y * -2 + 1;
		clip.z = clip.z * 2 - 1;
		
		clip = (Vec4f) transformMatrix.translate(clip);
		
		clip.x /= clip.w;
		clip.y /= clip.w;
		clip.z /= clip.w;
		
		return new Vec3f(clip.x, clip.y, clip.z);
		
//		Vec2d normalizedCoords = coursorPosition.mul(2.0, -2.0).div(windowSize).sub(1.0, -1.0);
//		Vec4f clipCoords = new Vec4f((float) normalizedCoords.x, (float) normalizedCoords.y, -1F, 1F);
//		System.out.println(clipCoords);
//		Matrix4f invertedProjectionMatrix = projectionMatrix.copy();
//		invertedProjectionMatrix.invert();
//		Vec4f eyeCoords = (Vec4f) invertedProjectionMatrix.translate(clipCoords);
//		eyeCoords = new Vec4f(eyeCoords.x, eyeCoords.y, -1F, 0F); // Z = 1 ?
//		System.out.println(eyeCoords);
//		Matrix4f invertedViewMatrix = viewMatrix.copy();
//		invertedViewMatrix.invert();
//		Vec4f rayWorld = (Vec4f) invertedViewMatrix.translate(eyeCoords);
//		System.out.println(rayWorld);
//		Vec3f mouseRay = new Vec3f(rayWorld.x, rayWorld.y, rayWorld.z);
//		return mouseRay;
	}
//	gluUnProject(GLdouble winx, GLdouble winy, GLdouble winz,
//			  const GLdouble modelMatrix[16],
//			  const GLdouble projMatrix[16],
//			const GLint viewport[4],
//			  GLdouble *objx, GLdouble *objy, GLdouble *objz)
//			{
//			double finalMatrix[16];
//			double in[4];
//			double out[4];
//
//			__gluMultMatricesd(modelMatrix, projMatrix, finalMatrix);
//			if (!__gluInvertMatrixd(finalMatrix, finalMatrix)) return(GL_FALSE);
//
//			in[0]=winx;
//			in[1]=winy;
//			in[2]=winz;
//			in[3]=1.0;
//
//			/* Map x and y from window coordinates */
//			in[0] = (in[0] - viewport[0]) / viewport[2];
//			in[1] = (in[1] - viewport[1]) / viewport[3];
//
//			/* Map to range -1 to 1 */
//			in[0] = in[0] * 2 - 1;
//			in[1] = in[1] * 2 - 1;
//			in[2] = in[2] * 2 - 1;
//
//			__gluMultMatrixVecd(finalMatrix, in, out);
//			if (out[3] == 0.0) return(GL_FALSE);
//			out[0] /= out[3];
//			out[1] /= out[3];
//			out[2] /= out[3];
//			*objx = out[0];
//			*objy = out[1];
//			*objz = out[2];
//			return(GL_TRUE);
//			}
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
