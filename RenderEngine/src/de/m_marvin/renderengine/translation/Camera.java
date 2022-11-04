package de.m_marvin.renderengine.translation;

import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;

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
	protected Vec3f rotation;
	
	/**
	 * Creates a new camera on the given position.
	 * @param position
	 * @param rotation
	 */
	public Camera(Vec3f position, Vec3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	/**
	 * Creates a new camera on the default position 0 0 0 with orientation 0 0 0.
	 */
	public Camera() {
		this(new Vec3f(0F, 0F, 0F), new Vec3f(0F, 0F, 0F));
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
		Quaternion orientation = Quaternion.fromXYZDegrees(this.rotation);
		Vec3f offsetMovement = relativeMovement.transform(orientation);
		offset(offsetMovement);
	}
	
	/**
	 * Rotates the camera by the given xyz euler angles.
	 * @param axialMotion
	 */
	public void rotate(Vec3f axialMotion) {
		this.rotation.addI(axialMotion);
		this.rotation.x %= 360;
		this.rotation.y %= 360;
		this.rotation.z %= 360;
	}
	
	/**
	 * Updates the view matrix storen in the camera instance.
	 * Must be called to let the last translation calls take effect.
	 */
	public void upadteViewMatrix() {
		this.viewMatrix.identity();
		this.viewMatrix.mulI(Quaternion.fromXYZDegrees(rotation).conj());
		this.viewMatrix.mulI(Matrix4f.translateMatrix(-this.position.x(), -this.position.y(), -this.position.z()));
	}
	
	/**
	 * Returns the view matrix containg all translations of the camera.
	 * @return A matrix that has to be applied to all vertecies in the shader
	 */
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
	@Override
	public String toString() {
		return "Camera[position=" + this.position + ",rotation=" + this.rotation + "]";
	}
	
}
