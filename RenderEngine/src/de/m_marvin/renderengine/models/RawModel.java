package de.m_marvin.renderengine.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;

/**
 * Represents a generic polygon model consisting of triangles with normal and texture properties.
 * The used textures have to be in one texture atlas to be used together for the model rendering.
 * 
 * @author Marvin Köhler
 *
 * @param <R> The used resource location type
 */
public class RawModel<R extends IResourceProvider<R>> {

	protected List<Vec3f> vertecies = new ArrayList<>();
	protected List<Vec3f> normals = new ArrayList<>();
	protected List<Vec2f> textureUVs = new ArrayList<>();
	protected Map<String, List<ModelFace<R>>> modelObjects = new HashMap<>();
	
	@FunctionalInterface
	public static interface VertexPrinter<R extends IResourceProvider<R>> {
		public void print(R texture, Vec3f vertex, Vec3f normal, Vec2f uv);
	}
	
	public static class ModelFace<R extends IResourceProvider<R>> {
		
		public ModelFace(R texture, Vec3i... indexes) {
			this.indexes = indexes;
			this.usedTexture = texture;
		}
		
		protected R usedTexture;
		protected Vec3i[] indexes;
		
	}
	
	/**
	 * Returns a set containing all model part names.
	 * @return A set containing all model part names
	 */
	public Set<String> getPartNames() {
		return this.modelObjects.keySet();
	}
	
	/**
	 * Passes its vertex data to the provided consumer.
	 * @param vertexConsumer The vertex consumer
	 */
	public void drawModelToBuffer(VertexPrinter<R> vertexConsumer) {
		for (String partName : this.modelObjects.keySet()) {
			System.out.println(partName);
			drawPartToBuffer(partName, vertexConsumer);
		}
	}
	
	/**
	 * Passes its vertex data of the named model part to the provided consumer.
	 * @param partName The name of the part
	 * @param vertexConsumer The vertex consumer
	 */
	public void drawPartToBuffer(String partName, VertexPrinter<R> vertexConsumer) {
		for (ModelFace<R> face : this.modelObjects.get(partName)) {
			for (Vec3i indexVec : face.indexes) vertexConsumer.print(face.usedTexture, this.vertecies.get(indexVec.x()), this.normals.get(indexVec.z()), this.textureUVs.get(indexVec.y()));
		}
	}
	
}
