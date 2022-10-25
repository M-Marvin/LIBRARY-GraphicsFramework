package de.m_marvin.renderengine.shaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.textures.ITextureSampler;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;

public class ShaderInstance {

	protected Map<UniformType, Integer> typeCount = new HashMap<>();
	
	protected int vertexShader;
	protected int fragmentShader;
	protected int program;
	protected VertexFormat format;
	protected Map<String, Uniform<?>> uniforms = new HashMap<>();
	
	public class Uniform<T> {
		
		protected int index;
		protected int location;
		protected UniformType type;
		protected T defaultValue;
		
		public Uniform(int location, UniformType type, T defaultValue, int index) {
			this.location = location;
			this.type = type;
			this.defaultValue = defaultValue;
			this.index = index;
		}

		public void setDefault() {
			set(this.defaultValue);
		}
		
		public void set(T value) {
			this.type.set(location, value);
		}
		
		public int getIndex() {
			return index;
		}
		
		/* Type specific setter methods to avoid the instanceof check */
		
		public void setFloatArr(float[] value) {
			this.type.set(location, value);
		}

		public void setIntArr(int[] value) {
			this.type.set(location, value);
		}
		
		public void setFloat(float value) {
			this.type.set(location, value);
		}

		public void setInt(int value) {
			this.type.set(location, value);
		}

		public void setUnsignedInt(int value) {
			this.type.set(location, value);
		}
		
		public void setMatrix4f(Matrix4f value) {
			this.type.set(location, value.toFloatArr());
		}

		public void setMatrix3f(Matrix3f value) {
			this.type.set(location, value.toFloatArr());
		}

		public void setTextureSampler(ITextureSampler texture) {
			texture.bindTexture(index);
			this.type.set(location, this.index);
		}
		
	}
	
	public ShaderInstance(String vertexProgram, String fragmentProgram, VertexFormat arrayFormat) throws IOException {

		GLStateManager.assertOnRenderThread();
		
		this.vertexShader = GLStateManager.createShader(GL33.GL_VERTEX_SHADER);
		this.fragmentShader = GLStateManager.createShader(GL33.GL_FRAGMENT_SHADER);
		this.program =  GLStateManager.createProgram();
		
		GLStateManager.shaderSource(vertexShader, vertexProgram);
		GLStateManager.shaderSource(fragmentShader, fragmentProgram);
		
		GLStateManager.compileShader(fragmentShader);
		if (!GLStateManager.checkShaderCompile(fragmentShader)) {
			String errorLog = GLStateManager.shaderInfoLog(fragmentShader);
			throw new IllegalArgumentException("Failed to compile the provoided fragment shader code:\n" + errorLog);
		}
		
		GLStateManager.compileShader(vertexShader);
		if (!GLStateManager.checkShaderCompile(vertexShader)) {
			String errorLog = GLStateManager.shaderInfoLog(vertexShader);
			throw new IllegalArgumentException("Failed to compile the provoided vertex shader code:\n" + errorLog);
		}
		
		GLStateManager.attachShader(program, vertexShader);
		GLStateManager.attachShader(program, fragmentShader);
		
		this.format = arrayFormat;
		this.format.getElements().forEach((element) -> GLStateManager.bindVertexAttributeLocation(program, element.index(), element.name()));
		
		GLStateManager.linkProgram(program);
		if (!GLStateManager.checkProgramLink(program)) {
			String errorLog = GLStateManager.programInfoLog(program);
			throw new IllegalArgumentException("Failed to link shader program: " + errorLog);
		}
		
		GLStateManager.validateProgram(program);
		if (!GLStateManager.checkProgramValidation(program)) {
			String errorLog = GLStateManager.programInfoLog(program);
			throw new IllegalArgumentException("Failed to validate shader program: " + errorLog);
		}
		
	}
	
	public void useShader() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.useShader(this.program);
		this.uniforms.values().forEach(Uniform::setDefault);
	}
	
	public void unbindShader() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.useShader(0);
	}
		
	public <T> void createUniform(String name, UniformType type, T defaultValue) {
		GLStateManager.assertOnRenderThread();
		int id = this.typeCount.getOrDefault(type, 0);
		this.typeCount.put(type, 1 + id);
		this.uniforms.put(name, new Uniform<T>(GLStateManager.getUniformLocation(program, name), type, defaultValue, id));
	}
	
	public Uniform<?> getUniform(String name) {
		return this.uniforms.get(name);
	}

}
