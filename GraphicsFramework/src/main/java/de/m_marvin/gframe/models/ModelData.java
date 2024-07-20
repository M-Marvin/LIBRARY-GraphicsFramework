package de.m_marvin.gframe.models;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;

public class ModelData<R extends IResourceProvider<R>> {
	
	public class VertexData {
		protected Vec3f vertex;
		protected Vec3f normal;
		protected Vec3f tangent;
		protected Vec3f bitangent;
		protected Vec2f texcoord;
		protected Vec4f color;
	}
	
	public class FragmentData {
		
		protected List<VertexData> vertecies = new ArrayList<>();
		protected int[] indecies;
		
		protected ShaderData<R> shaderData;
		
	}
	
	public static class ShaderData<R extends IResourceProvider<R>> {
		
		public ShaderData() {
			this.ambientColor = 			new Vec3f(1, 1, 1);
			this.diffuseColor = 			new Vec3f(1, 1, 1);
			this.specularColor = 			new Vec3f(0, 0, 0);
			this.specularExponent = 		0;
			this.opticalDensity = 			1;
			this.ambientMap = 				null;
			this.diffuesMap = 				null;
			this.specularMap = 				null;
			this.specularHighlightMap = 	null;
			this.normalMap = 				null;
			this.displacementMap =			null;
		}
		
		protected Vec3f ambientColor;
		protected Vec3f diffuseColor;
		protected Vec3f specularColor;
		protected float specularExponent;
		protected float opticalDensity;
		protected float dissolve;
		protected R ambientMap;
		protected R diffuesMap;
		protected R specularMap;
		protected R dissolveMap;
		protected R specularHighlightMap;
		protected R normalMap;
		protected R displacementMap;
		
		public Vec3f getAmbientColor() {
			return ambientColor;
		}
		public Vec3f getDiffuseColor() {
			return diffuseColor;
		}
		public Vec3f getSpecularColor() {
			return specularColor;
		}
		public float getSpecularExponent() {
			return specularExponent;
		}
		public float getOpticalDensity() {
			return opticalDensity;
		}
		public float getDissolve() {
			return dissolve;
		}
		public R getAmbientMap() {
			return ambientMap;
		}
		public R getDiffuesMap() {
			return diffuesMap;
		}
		public R getSpecularMap() {
			return specularMap;
		}
		public R getDissolveMap() {
			return dissolveMap;
		}
		public R getSpecularHighlightMap() {
			return specularHighlightMap;
		}
		public R getNormalMap() {
			return normalMap;
		}
		public R getDisplacementMap() {
			return displacementMap;
		}
		
	}
	
	
	protected List<FragmentData> fragmentData = new ArrayList<>();
	
	public void writeToBuffer(FragmentWriter<R> fragmentWriter) {
		
		for (FragmentData fragment : this.fragmentData) {
			fragmentWriter.startFragment(fragment.shaderData);
			for (VertexData vertex : fragment.vertecies) {
				fragmentWriter.writeVertex(vertex.vertex, vertex.color, vertex.normal, vertex.tangent, vertex.bitangent, vertex.texcoord);
			}
			fragmentWriter.endFragment(fragment.indecies);
		}
		
	}
	
	public static interface FragmentWriter<R extends IResourceProvider<R>> {
		public void startFragment(ShaderData<R> shaderData);
		public void writeVertex(Vec3f vertex, Vec4f color, Vec3f normal, Vec3f tangent, Vec3f bitangent, Vec2f uv);
		public void endFragment(int[] indecies);
	}
	
}
