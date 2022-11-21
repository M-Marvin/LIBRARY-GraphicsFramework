package de.m_marvin.renderengine.shaders;

import java.util.function.BiConsumer;

import de.m_marvin.renderengine.GLStateManager;

/**
 * Represents the different uniform types supported by OpenGL and the render engine.
 * Provides the different type specific setter methods for uploading data to the GPU.
 * 
 * @author Marvin Köhler
 *
 */
public enum UniformType {
	
	SAMPLER_2D("sampler2D", int.class, (location, value) -> {
		GLStateManager.setUniformInt(location, (int) value);
	}),
	MATRIX_3x3("matrix3x3", float[].class, (location, value) -> {
		GLStateManager.setUniformMatrix3(location, false, (float[]) value);
	}),
	MATRIX_4x4("matrix4x4", float[].class, (location, value) -> {
		GLStateManager.setUniformMatrix4(location, false, (float[]) value);
	}),
	INT("int", int.class, (location, value) -> {
		GLStateManager.setUniformInt(location, (int) value);
	}),
	VEC_2I("vec2i", int[].class, (location, value) -> {
		GLStateManager.setUniformIntVec2(location, ((int[]) value)[0], ((int[]) value)[1]);
	}),
	VEC_3I("vec3i", int[].class, (location, value) -> {
		GLStateManager.setUniformIntVec3(location, ((int[]) value)[0], ((int[]) value)[1], ((int[]) value)[2]);
	}),
	VEC_4I("vec4i", int[].class, (location, value) -> {
		GLStateManager.setUniformIntVec4(location, ((int[]) value)[0], ((int[]) value)[1], ((int[]) value)[2], ((int[]) value)[3]);
	}),
	
	UINT("uint", int.class, (location, value) -> {
		GLStateManager.setUniformUnsignedInt(location, (int) value);
	}),
	VEC_2UI("vec2ui", int[].class, (location, value) -> {
		GLStateManager.setUniformUnsignedIntVec2(location, ((int[]) value)[0], ((int[]) value)[1]);
	}),
	VEC_3UI("vec3ui", int[].class, (location, value) -> {
		GLStateManager.setUniformUnsignedIntVec3(location, ((int[]) value)[0], ((int[]) value)[1], ((int[]) value)[2]);
	}),
	VEC_4UI("vec4ui", int[].class, (location, value) -> {
		GLStateManager.setUniformUnsignedIntVec4(location, ((int[]) value)[0], ((int[]) value)[1], ((int[]) value)[2], ((int[]) value)[3]);
	}),
	
	FLOAT("float", float.class, (location, value) -> {
		GLStateManager.setUniformFloat(location, (float) value);
	}),
	VEC_2F("vec2f", float[].class, (location, value) -> {
		GLStateManager.setUniformFloatVec2(location, ((float[]) value)[0], ((float[]) value)[1]);
	}),
	VEC_3F("vec3f", float[].class, (location, value) -> {
		GLStateManager.setUniformFloatVec3(location, ((float[]) value)[0], ((float[]) value)[1], ((float[]) value)[2]);
	}),
	VEC_4F("vec4f", float[].class, (location, value) -> {
		GLStateManager.setUniformFloatVec4(location, ((float[]) value)[0], ((float[]) value)[1], ((float[]) value)[2], ((float[]) value)[3]);
	});
	
	private final String codeName;
	private final BiConsumer<Integer, Object> glSetter;
	private final Class<?> valueType;
	
	private UniformType(String codeName, Class<?> valueType, BiConsumer<Integer, Object> glSetter) {
		this.codeName = codeName;
		this.glSetter = glSetter;
		this.valueType = valueType;
	}
	
	/**
	 * Returns the value type of the uniform type.
	 * 
	 * @return The value type class
	 */
	public Class<?> getValueType() {
		return valueType;
	}
	
	/**
	 * Type unspecific setter method to upload new values to the GPU.
	 * 
	 * @param location The index of the target uniform on the GPU
	 * @param value The new value to upload to the GPU
	 */
	public void set(int location, Object value) {
		this.glSetter.accept(location, value);
	}
	
	/**
	 * Returns the name of the uniform type used in the shader JSON
	 * @return The name of the uniform type
	 */
	public String getCodeName() {
		return codeName;
	}
	
	/**
	 * Tries to find the uniform type with the given name
	 * @param name The name of the uniform type
	 * @return The uniform type or null if no type if found
	 */
	public static UniformType byName(String name) {
		for (UniformType type : UniformType.values()) if (type.getCodeName().equals(name)) return type;
		return FLOAT;
	}
	
}
