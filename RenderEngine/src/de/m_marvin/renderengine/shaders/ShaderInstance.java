package de.m_marvin.renderengine.shaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;

public class ShaderInstance {
	
	protected int vertexShader;
	protected int fragmentShader;
	protected int program;
	protected VertexFormat format;
	protected Map<String, Uniform<?>> uniforms = new HashMap<>();
	
	public class Uniform<T> {
		
		protected int location;
		protected UniformType type;
		
		public Uniform(int location, UniformType type, T defaultValue) {
			this.location = location;
			this.type = type;
			set(defaultValue);
		}
		
		public void set(T value) {
			int count = 1;
			if (value instanceof int[] arr) {
				count = arr.length;
			} else if (value instanceof float[] arr) {
				count = arr.length;
			}
			this.type.set(location, count, value);
		}
		
		public void setFloatArr(float[] value) {
			this.type.set(location, value.length, value);
		}

		public void setIntArr(int[] value) {
			this.type.set(location, value.length, value);
		}
		
		public void setFloat(float value) {
			this.type.set(location, 1, value);
		}

		public void setInt(int value) {
			this.type.set(location, 1, value);
		}
		
		public void setMatrix4f(Matrix4f value) {
			this.type.set(location, 1, value.toFloatArr());
		}

		public void setMatrix3f(Matrix3f value) {
			this.type.set(location, 1, value.toFloatArr());
		}
		
	}
	
	public ShaderInstance(String vertexProgram, String fragmentProgram, VertexFormat arrayFormat) throws IOException {
		
		GLStateManager.createShader(GL33.GL_VERTEX_SHADER, (id) -> this.vertexShader = id);
		GLStateManager.createShader(GL33.GL_FRAGMENT_SHADER, (id) -> this.fragmentShader = id);
		GLStateManager.createProgram((id) -> this.program = id);
		
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
		
		GLStateManager.createProgram((id) -> this.program = id);
		GLStateManager.attachShader(program, vertexShader);
		GLStateManager.attachShader(program, fragmentShader);
		
		this.format = arrayFormat;
		this.format.getElements().forEach((element) -> GLStateManager.bindAttributeLocation(program, element.index(), element.name()));
		
		GLStateManager.linkProgram(program);
		if (!GLStateManager.checkProgramLink(program)) {
			String errorLog = GLStateManager.programInfoLog(program);
			throw new IllegalArgumentException("Failed to link shader program: " + errorLog);
		}
		
		GLStateManager.bindAttributeLocation(program, 1, "normal");
		GLStateManager.linkProgram(program);
		
	}
	
	public void useShader() {
		GLStateManager.bindShader(this.program);
	}
	
	public void useShaderAndFormat() {
		useShader();
		this.format.setupAttributes();
	}

	public void unbindShader() {
		GLStateManager.bindShader(0);
	}
	
	public <T> void createUniform(String name, UniformType type, T defaultValue) {
		this.uniforms.put(name, new Uniform<T>(GLStateManager.getUniformLocation(program, name), type, defaultValue));
	}
	
	public Uniform<?> getUniform(String name) {
		return this.uniforms.get(name);
	}

}
