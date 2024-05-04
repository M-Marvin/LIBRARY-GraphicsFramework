package de.m_marvin.renderengine.shaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.textures.texture.ITextureSampler;
import de.m_marvin.renderengine.vertices.VertexFormat;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;
import de.m_marvin.univec.impl.Vec4i;

/**
 * Represents a shader program on the GPU.
 * 
 * @author Marvin Koehler
 */
public class ShaderInstance {

	protected Map<UniformType, Integer> typeCount = new HashMap<>();
	
	protected int vertexShader;
	protected int fragmentShader;
	protected int geometryShader;
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

		@SuppressWarnings("unchecked")
		public Class<T> getTypeClass() {
			return (Class<T>) this.defaultValue.getClass();
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
		public void setFloat(float value) {
			this.type.set(location, value);
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec2f(Vec2f value) {
			this.type.set(location, new float[] {value.x, value.y});
		}
		
		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec3f(Vec3f value) {
			this.type.set(location, new float[] {value.x, value.y, value.z});
		}

		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec4f(Vec4f value) {
			this.type.set(location, new float[] {value.x, value.y, value.z, value.w});
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
		public void setVec2i(Vec2i value) {
			this.type.set(location, new int[] {value.x, value.y});
		}

		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec3i(Vec3i value) {
			this.type.set(location, new int[] {value.x, value.y, value.z});
		}

		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec4i(Vec4i value) {
			this.type.set(location, new int[] {value.x, value.y, value.z, value.w});
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
		public void setVec2ui(Vec2i value) {
			this.type.set(location, new int[] {value.x, value.y});
		}

		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec3ui(Vec3i value) {
			this.type.set(location, new int[] {value.x, value.y, value.z});
		}

		/**
		 * Type specific setter for the uniform value.
		 * @param value The new value
		 */
		public void setVec4ui(Vec4i value) {
			this.type.set(location, new int[] {value.x, value.y, value.z, value.w});
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
			if (texture != null) {
				texture.bindSampler(this.index);
				this.type.set(this.location, this.index);
			} else {
				GLStateManager.activeTexture(this.index);
				GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
				this.type.set(location, 0);
			}
		}
		
	}
	
	/**
	 * Creates a new shader on the GPU from the fragment shader and vertex shader GLSL source codes.
	 * 
	 * @param vertexProgram The vertex shader source GLSL
	 * @param fragmentProgram The fragment shader source GLSL
	 * @param arrayFormat The vertex array format
	 */
	public ShaderInstance(String vertexProgram, String fragmentProgram, Optional<String> geometryProgram, VertexFormat arrayFormat) {
		
		GLStateManager.assertOnRenderThread();
		
		this.vertexShader = GLStateManager.createShader(GL33.GL_VERTEX_SHADER);
		this.fragmentShader = GLStateManager.createShader(GL33.GL_FRAGMENT_SHADER);
		if (geometryProgram.isPresent())
			this.geometryShader = GLStateManager.createShader(GL33.GL_GEOMETRY_SHADER);
		this.program =  GLStateManager.createProgram();
		
		GLStateManager.shaderSource(vertexShader, vertexProgram);
		GLStateManager.shaderSource(fragmentShader, fragmentProgram);
		if (geometryProgram.isPresent())
			GLStateManager.shaderSource(geometryShader, geometryProgram.get());
		
		GLStateManager.compileShader(fragmentShader);
		if (!GLStateManager.checkShaderCompile(fragmentShader)) {
			String errorLog = GLStateManager.shaderInfoLog(fragmentShader);
			throw new IllegalArgumentException("Failed to compile fragment shader code:\n" + errorLog);
		}
		
		GLStateManager.compileShader(vertexShader);
		if (!GLStateManager.checkShaderCompile(vertexShader)) {
			String errorLog = GLStateManager.shaderInfoLog(vertexShader);
			throw new IllegalArgumentException("Failed to compile vertex shader code:\n" + errorLog);
		}
		
		if (geometryProgram.isPresent()) {

			GLStateManager.compileShader(geometryShader);
			if (!GLStateManager.checkShaderCompile(geometryShader)) {
				String errorLog = GLStateManager.shaderInfoLog(geometryShader);
				throw new IllegalArgumentException("Failed to compile geometry shader code:\n" + errorLog);
			}
			
		}
		
		GLStateManager.attachShader(program, vertexShader);
		GLStateManager.attachShader(program, fragmentShader);
		if (geometryProgram.isPresent())
			GLStateManager.attachShader(program, geometryShader);
		
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
	 * Deletes the shader from the GPU memory.
	 */
	public void delete() {
		GLStateManager.deleteShader(vertexShader);
		GLStateManager.deleteShader(fragmentShader);
		if (geometryShader != 0) GLStateManager.deleteProgram(geometryShader);
		GLStateManager.deleteProgram(program);
		this.uniforms.clear();
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
		if (!this.uniforms.containsKey(name)) throw new IllegalArgumentException("The uniform '" + name + "' doesn not exist in the shader!");
		return this.uniforms.get(name);
	}
	
	public boolean hasUniform(String name) {
		return this.uniforms.containsKey(name);
	}
	
	public VertexFormat getFormat() {
		return format;
	}

}
