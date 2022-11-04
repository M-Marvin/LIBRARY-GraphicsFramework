package de.m_marvin.renderengine.shaders;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.textures.ITextureSampler;
import de.m_marvin.renderengine.vertecies.VertexFormat;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;

/**
 * Represents a shader program on the GPU.
 * 
 * @author Marvin Koehler
 */
public class ShaderInstance {

	protected Map<UniformType, Integer> typeCount = new HashMap<>();
	
	protected int vertexShader;
	protected int fragmentShader;
	protected int program;
	protected VertexFormat format;
	protected Map<String, Uniform<?>> uniforms = new HashMap<>();
	
	 /**
	  * Represents a uniform of the shader program.
	  * Provides methods to upload data to the shader.
	  * 
	  * @author Marvin Köhler
	  *
	  * @param <T> Type of the uniform
	  */
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

		/**
		 * Sets the uniform to the default value.
		 */
		public void setDefault() {
			set(this.defaultValue);
		}
		
		/**
		 * Sets the value of the uniform.
		 * Uploads the new value to the GPU.
		 * @param value The new value
		 */
		public void set(T value) {
			this.type.set(location, value);
		}
		
		/**
		 * Returns the index of the uniform in the shader.
		 * @return The uniform index
		 */
		public int getIndex() {
			return index;
		}
		
		/* Type specific setter methods to avoid the instanceof check */
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setFloatArr(float[] value) {
			this.type.set(location, value);
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setIntArr(int[] value) {
			this.type.set(location, value);
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setFloat(float value) {
			this.type.set(location, value);
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setInt(int value) {
			this.type.set(location, value);
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setUnsignedInt(int value) {
			this.type.set(location, value);
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setMatrix4f(Matrix4f value) {
			this.type.set(location, value.toFloatArr());
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setMatrix3f(Matrix3f value) {
			this.type.set(location, value.toFloatArr());
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setTextureSampler(ITextureSampler texture) {
			texture.bindTexture(index);
			this.type.set(location, this.index);
		}
		
	}
	
	/**
	 * Creates a new shader on the GPU from the fragment shader and vertex shader GLSL source codes.
	 * 
	 * @param vertexProgram The vertex shader source GLSL
	 * @param fragmentProgram The fragment shader source GLSL
	 * @param arrayFormat The vertex array format
	 */
	public ShaderInstance(String vertexProgram, String fragmentProgram, VertexFormat arrayFormat) {

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
	
	/**
	 * Binds the shader ready for drawing.
	 */
	public void useShader() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.useShader(this.program);
		this.uniforms.values().forEach(Uniform::setDefault);
	}
	
	/**
	 * Unbinds the shader.
	 */
	public void unbindShader() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.useShader(0);
	}
	
	/**
	 * Creates a new uniform in the shader.
	 * This method is used to register the used uniforms used by the shader.
	 * Normally this is done by the {@link ShaderLoader}.
	 * 
	 * @param <T> The the uniform value type
	 * @param name The name of the uniform
	 * @param type The type of the uniform value
	 * @param defaultValue The default value of the uniform
	 */
	public <T> void createUniform(String name, UniformType type, T defaultValue) {
		GLStateManager.assertOnRenderThread();
		int id = this.typeCount.getOrDefault(type, 0);
		this.typeCount.put(type, 1 + id);
		this.uniforms.put(name, new Uniform<T>(GLStateManager.getUniformLocation(program, name), type, defaultValue, id));
	}
	
	/**
	 * Get the uniform with the given name.
	 * 
	 * @param name The name of the uniform
	 * @return The uniform with the given name
	 */
	public Uniform<?> getUniform(String name) {
		return this.uniforms.get(name);
	}

}
