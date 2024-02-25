package de.m_marvin.renderengine.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.m_marvin.renderengine.models.ModelData.ShaderData;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;

public class OBJParser<R extends IResourceProvider<R>> {

	protected static class Face {
		protected String object = "";
		protected String group = "";
		protected String usemtl = "";
		protected List<Integer> vertexIndecies = new ArrayList<>();
		protected List<Integer> texcoordIndecies = new ArrayList<>();
		protected List<Integer> normalIndecies = new ArrayList<>();
	}
	
	protected static class Material {
		protected Vec3f ambientColor;
		protected Vec3f diffuseColor;
		protected Vec3f specularColor;
		protected double specularExponent;
		protected double opticalDensity;
		protected String ambientMap;
		protected String diffuesMap;
		protected String specularMap;
		protected String specularHighlightMap;
		protected String normalMap;
	}
	
	protected List<Vec3f> vertecies = new ArrayList<>();
	protected List<Vec3f> colors = new ArrayList<>();
	protected List<Vec2f> texcoords = new ArrayList<>();
	protected List<Vec3f> normals = new ArrayList<>();
	protected List<Face> faces = new ArrayList<>();
	protected Map<String, Material> materials = new HashMap<>();
	
	public ModelData<R> makeModelData(R modelFilesLocation) {
		
		ModelData<R> modelData = new ModelData<>();
		
		for (String material : this.materials.keySet()) {
			
			ModelData<R>.FragmentData fragment = modelData.new FragmentData();
			
			List<Face> faces = this.faces.stream().filter(f -> f.usemtl.equals(material)).toList();
			
			List<Integer> indecies = new ArrayList<>();
			
			for (Face face : faces) {

				int indecieCount = face.vertexIndecies.size();
				if (indecieCount > 3) indecieCount = 3 + (indecieCount - 3) * 3;
				
				for (int i = 0; i < indecieCount; i++) {
					
					int index = i < 3 ? i : (i % 3) == 0 ? 0 : ((i % 3) == 1) ? 1 + (i / 3) : 2 + (i / 3);
					indecies.add(fragment.vertecies.size() + index);
					
				}
				
				for (int i = 0; i < face.vertexIndecies.size(); i++) {
					
					ModelData<R>.VertexData vertexData = modelData.new VertexData();
					
					vertexData.vertex = face.vertexIndecies.get(i) < 1 ? new Vec3f(0, 0, 0) : this.vertecies.get(face.vertexIndecies.get(i) - 1);
					Vec3f color = face.vertexIndecies.get(i) < 1 ? new Vec3f(1, 1, 1) : this.colors.get(face.vertexIndecies.get(i) - 1);
					vertexData.color = new Vec4f(color.x, color.y, color.z, 1.0F);
					vertexData.texcoord = face.texcoordIndecies.get(i) < 1 ? new Vec2f(0, 0) : this.texcoords.get(face.texcoordIndecies.get(i) - 1);
					vertexData.normal = face.normalIndecies.get(i) < 1 ? new Vec3f(0, 0, 0) : this.normals.get(face.normalIndecies.get(i) - 1);
					
					fragment.vertecies.add(vertexData);
					
				}
				
			}
			
			if (fragment.vertecies.size() == 0) continue;
			
			fragment.indecies = new int[indecies.size()];
			for (int i = 0; i < fragment.indecies.length; i++) fragment.indecies[i] = indecies.get(i);
			
			Material mtrl = this.materials.get(material);
			
			fragment.shaderData = new ShaderData<>();
			fragment.shaderData.ambientColor = 			mtrl.ambientColor;
			fragment.shaderData.diffuseColor = 			mtrl.diffuseColor;
			fragment.shaderData.specularColor = 		mtrl.specularColor;
			fragment.shaderData.specularExponent = 		mtrl.specularExponent;
			fragment.shaderData.opticalDensity = 		mtrl.opticalDensity;
			fragment.shaderData.ambientMap = 			mtrl.ambientMap == null ? null : modelFilesLocation.locationOfFile(mtrl.ambientMap);
			fragment.shaderData.diffuesMap = 			mtrl.diffuesMap == null ? null : modelFilesLocation.locationOfFile(mtrl.diffuesMap);
			fragment.shaderData.specularMap = 			mtrl.specularMap == null ? null : modelFilesLocation.locationOfFile(mtrl.specularMap);
			fragment.shaderData.specularHighlightMap = 	mtrl.specularHighlightMap == null ? null : modelFilesLocation.locationOfFile(mtrl.specularHighlightMap);
			fragment.shaderData.normalMap = 			mtrl.normalMap == null ? null : modelFilesLocation.locationOfFile(mtrl.normalMap);
			
			modelData.fragmentData.add(fragment);
			
		}

		ModelData<R>.FragmentData fragment = modelData.new FragmentData();
		
		List<Face> faces = this.faces.stream().filter(f -> f.usemtl == null || f.usemtl.isEmpty()).toList();
		
		for (Face face : faces) {

			int indecieCount = face.vertexIndecies.size();
			if (indecieCount > 3) indecieCount = 3 + (indecieCount - 3) * 3;
			
			fragment.indecies = new int[indecieCount];
			for (int i = 0; i < indecieCount; i++) {
				fragment.indecies[i] = fragment.vertecies.size() + (i % 3) + (i / 3);
			}
			
			for (int i = 0; i < face.vertexIndecies.size(); i++) {
				
				ModelData<R>.VertexData vertexData = modelData.new VertexData();
				
				vertexData.vertex = face.vertexIndecies.get(i) < 1 ? new Vec3f(0, 0, 0) : this.vertecies.get(face.vertexIndecies.get(i) - 1);
				vertexData.texcoord = face.texcoordIndecies.get(i) < 1 ? new Vec2f(0, 0) : this.texcoords.get(face.texcoordIndecies.get(i) - 1);
				vertexData.normal = face.normalIndecies.get(i) < 1 ? new Vec3f(0, 0, 0) : this.vertecies.get(face.normalIndecies.get(i) - 1);
				vertexData.color = new Vec4f(1, 1, 1, 1);
				
				fragment.vertecies.add(vertexData);
				
			}

		}

		if (fragment.vertecies.size() == 0) return modelData;
		
		fragment.shaderData = new ShaderData<>();
		
		modelData.fragmentData.add(fragment);
		
		return modelData;
		
	}
	
	protected String usemtl = "";
	protected String object = "";
	protected String group = "";
	protected List<String> mtllib = new ArrayList<>();
	
	public List<String> parseOBJFile(InputStream input) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		String line;
		while ((line = reader.readLine()) != null) {
			
			String[] lineSegments = line.split(" ");
			
			if (lineSegments.length == 0) continue;
			if (lineSegments[0].startsWith("#")) continue;
			
			switch (lineSegments[0]) {
			case "mtllib":
				if (lineSegments.length > 1) this.mtllib.add(lineSegments[1]);
				continue;
			case "usemtl":
				this.usemtl = lineSegments.length > 1 ? lineSegments[1] : "";
				continue;
			case "g":
				this.group = lineSegments.length > 1 ? lineSegments[1] : "";
				continue;
			case "o":
				this.object = lineSegments.length > 1 ? lineSegments[1] : "";
				continue;
			case "v":
				this.vertecies.add(parseVec3f(lineSegments, 0));
				if (lineSegments.length > 4) 
					this.colors.add(parseVec3f(lineSegments, 3));
				else
					this.colors.add(new Vec3f(1, 1, 1));
				continue;
			case "vt":
				this.texcoords.add(parseVec2f(lineSegments, 0));
				continue;
			case "vn":
				this.normals.add(parseVec3f(lineSegments, 0).normalize());
				continue;
			case "f":
				this.faces.add(parseFace(lineSegments));
				continue;
			}
			
		}
		
		reader.close();
		
		return this.mtllib;
		
	}
	
	protected String newmtl;
	protected Vec3f ambientColor;
	protected Vec3f diffuseColor;
	protected Vec3f specularColor;
	protected double specularExponent;
	protected double opticalDensity;
	protected String ambientMap;
	protected String diffuesMap;
	protected String specularMap;
	protected String specularHighlightMap;
	protected String normalMap;
	
	public void parseMTLFile(InputStream input) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		setDefaultMaterial();
		
		String line;
		while ((line = reader.readLine()) != null) {
			
			String[] lineSegments = line.split(" ");
			
			if (lineSegments.length == 0) continue;
			if (lineSegments[0].startsWith("#")) continue;
			
			switch (lineSegments[0]) {
			case "newmtl":
				if (this.newmtl != null) {
					this.materials.put(this.newmtl, makeMaterial());
					setDefaultMaterial();
				}
				this.newmtl = lineSegments.length >= 2 ? lineSegments[1] : "";
				continue;
			case "Ka":
				this.ambientColor = parseVec3f(lineSegments, 0);
				continue;
			case "Kd":
				this.diffuseColor = parseVec3f(lineSegments, 0);
				continue;
			case "Ks":
				this.specularColor = parseVec3f(lineSegments, 0);
				continue;
			case "Ns":
				this.specularExponent = lineSegments.length >= 2 ? Double.parseDouble(lineSegments[1]) : 0;
				continue;
			case "Ni":
				this.opticalDensity = lineSegments.length >= 2 ? Double.parseDouble(lineSegments[1]) : 0;
				continue;
			case "map_Ka":
				this.ambientMap = lineSegments.length >= 2 ? lineSegments[1] : "";
				continue;
			case "map_Kd":
				this.diffuesMap = lineSegments.length >= 2 ? lineSegments[1] : "";
				continue;
			case "map_Ks":
				this.specularMap = lineSegments.length >= 2 ? lineSegments[1] : "";
				continue;
			case "map_Ns":
				this.specularHighlightMap = lineSegments.length >= 2 ? lineSegments[1] : "";
				continue;
			case "map_bump":
				this.normalMap = lineSegments.length >= 2 ? lineSegments[1] : "";
				continue;
			}
			
		}

		if (this.newmtl != null) this.materials.put(this.newmtl, makeMaterial());
		
		reader.close();
		
	}
	
	protected void setDefaultMaterial() {
		this.newmtl = null;
		this.ambientColor = new Vec3f(1, 1, 1);
		this.diffuseColor = new Vec3f(1, 1, 1);
		this.specularColor = new Vec3f(0, 0, 0);
		this.specularExponent = 10.0;
		this.opticalDensity = 1.0;
		this.ambientMap = null;
		this.diffuesMap = null;
		this.specularMap = null;
		this.specularHighlightMap = null;
		this.normalMap = null;
	}
	
	protected Material makeMaterial() {
		Material material = new Material();
		material.ambientColor = this.ambientColor;
		material.diffuseColor = this.diffuseColor;
		material.specularColor = this.specularColor;
		material.specularExponent = this.specularExponent;
		material.opticalDensity = this.opticalDensity;
		material.ambientMap = this.ambientMap;
		material.diffuesMap = this.diffuesMap;
		material.specularMap = this.specularMap;
		material.specularHighlightMap = this.specularHighlightMap;
		material.normalMap = this.normalMap;
		return material;
	}
	
	protected Face parseFace(String[] segments) {
		Face face = new Face();
		for (int i = 1; i < segments.length; i++) {
			String[] indecies = segments[i].split("/");
			face.vertexIndecies.add((indecies.length >= 1) ? safeParseInt(indecies[0]) : 0);
			face.texcoordIndecies.add((indecies.length >= 2) ? safeParseInt(indecies[1]) : 0);
			face.normalIndecies.add((indecies.length >= 3) ? safeParseInt(indecies[2]) : 0);
		}
		face.object = this.object;
		face.group = this.group;
		face.usemtl = this.usemtl;
		return face;
	}

	protected Vec2f parseVec2f(String[] segments, int offset) {
		if (segments.length >= 3 + offset) {
			return new Vec2f(
					(segments.length >= 1) ? safeParseFloat(segments[offset + 1]) : 0,
					(segments.length >= 2) ? safeParseFloat(segments[offset + 2]) : 0
					);
		}
		return new Vec2f();
	}
	
	protected Vec3f parseVec3f(String[] segments, int offset) {
		if (segments.length >= 4 + offset) {
			return new Vec3f(
					(segments.length >= 1) ? safeParseFloat(segments[offset + 1]) : 0,
					(segments.length >= 2) ? safeParseFloat(segments[offset + 2]) : 0,
					(segments.length >= 3) ? safeParseFloat(segments[offset + 3]) : 0
					);
		}
		return new Vec3f();
	}
	
	protected int safeParseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected float safeParseFloat(String s) {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
}
