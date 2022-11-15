package de.m_marvin.renderengine.translation;

import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;

/**
 * Represents a view-point on the rendered geometry.
 * Can be moved with the provided methods and returns a translation view matrix that has to be applied in the shader.
 * 
 * @author Marvin Kähler
 *
 */
public class Camera {
	
	protected Matrix4f viewMatrix = new Matrix4f();
	protected Vec3f position;
	protected Quaternion rotation;
	
	/**
	 * Creates a new camera on the given position.
	 * @param position The position of the camera
	 * @param rotation The rotation as {@link Quaternion}
	 */
	public Camera(Vec3f position, Quaternion rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	/**
	 * Creates a new camera on th given position with the given rotation in euler angles.
	 * @param position The position of the camera
	 * @param eulerRotation The rotation as euler-angles
	 */
	public Camera(Vec3f position, Vec3f eulerRotation) {
		this(position, Quaternion.fromXYZDegrees(eulerRotation));
	}
	
	/**
	 * Creates a new camera on the default position 0 0 0 with orientation 0 0 0.
	 */
	public Camera() {
		this(new Vec3f(0F, 0F, 0F), new Quaternion(new Vec3i(0, 0, 0), 0F));
	}
	/**
	 * Moves the camera by the specified xyz coordinates.
	 * @param linearMotion The xyz offset to move the camera
	 */
	public void offset(Vec3f linearMotion) {
		this.position.addI(linearMotion);
	}
	
	/**
	 * Moves the camera by the specified xyz coordinates from the view of the camera.
	 * @param relativeMovement The xyz offset from the view of the camera
	 */
	public void move(Vec3f relativeMovement) {
		Vec3f offsetMovement = relativeMovement.transform(rotation);
		offset(offsetMovement);
	}
	
	/**
	 * Rotates the camera by the specified angle around the angle specified by the axis-vector (aligned on the camera view).
	 * @param axisVec The axis to rotate the camera around from the view of the camera
	 * @param ammount The angle to rotate in degrees
	 */
	public void rotate(Vec3i axisVec, float ammount) {
		this.rotation.mulI(new Quaternion(axisVec, (float) Math.toRadians(ammount)));
	}
	
	/**
	 * Rotates the camera by the specified angle around the angle specified by the axis-vector (aligned on the coordinate axes).
	 * @param axisVec The axis to rotate the camera around
	 * @param ammount The angle to rotate in degrees
	 */
	public void orientate(Vec3i axisVec, float ammount) {
		this.rotation = new Quaternion(axisVec, (float) Math.toRadians(ammount)).mul(this.rotation);
	}
	
	/**
	 * Updates the view matrix storen in the camera instance.
	 * Must be called to let the last translation calls take effect.
	 */
	public void upadteViewMatrix() {
		this.viewMatrix.identity();
		this.viewMatrix.mulI(rotation.conj());
		this.viewMatrix.mulI(Matrix4f.translateMatrix(-this.position.x(), -this.position.y(), -this.position.z()));
	}
	
	/**
	 * Returns the view matrix containg all translations of the camera.
	 * @return A matrix that has to be applied to all vertices in the shader
	 */
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
	/**
	 * Returns the position of the camera.
	 * @return The xyz position of the camera
	 */
	public Vec3f getPosition() {
		return position;
	}
	
	/**
	 * Returns the rotation quaternion of the camera.
	 * @return The rotation of the camera as quaternion
	 */
	public Quaternion getRotation() {
		return rotation;
	}
	
	@Override
	public String toString() {
		return "Camera[position=" + this.position + ",rotation=" + this.rotation + "]";
	}
	
}
