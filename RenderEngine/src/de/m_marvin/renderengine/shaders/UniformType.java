package de.m_marvin.renderengine.shaders;

import de.m_marvin.renderengine.GLStateManager;

public enum UniformType {
	
	SAMPLER_2D("sampler2D", int.class, (location, count, value) -> {
		GLStateManager.setUniformInt1(location, (int) value);
	}),
	MATRIX_3x3("matrix3x3", float[].class, (location, count, value) -> {
		GLStateManager.setUniformMatrix3(location, false, (float[]) value);
	}),
	MATRIX_4x4("matrix4x4", float[].class, (location, count, value) -> {
		GLStateManager.setUniformMatrix4(location, false, (float[]) value);
	}),
	INT("int", int[].class, (location, count, value) -> {
		if (count == 1) {
			GLStateManager.setUniformInt1(location, (int) value);
		} else {
			GLStateManager.setUniformIntN(location, (int[]) value);
		}
	}),
	FLOAT("float", float[].class, (location, count, value) -> {
		if (count == 1) {
			GLStateManager.setUniformFloat1(location, (float) value);
		} else {
			GLStateManager.setUniformFloatN(location, (float[]) value);
		}
	});
	
	private final String codeName;
	private final UniformSetter glSetter;
	private final Class<?> valueType;
	
	private UniformType(String codeName, Class<?> valueType, UniformSetter glSetter) {
		this.codeName = codeName;
		this.glSetter = glSetter;
		this.valueType = valueType;
	}
	
	public Class<?> getValueType() {
		return valueType;
	}
	
	public void set(int location, int count, Object value) {
		this.glSetter.set(location, count, value);
	}
	
	public String getCodeName() {
		return codeName;
	}
	
	@FunctionalInterface
	public static interface UniformSetter {
		public void set(int location, int count, Object value);
	}

	public static UniformType byName(String name) {
		for (UniformType type : UniformType.values()) if (type.getCodeName().equals(name)) return type;
		return FLOAT;
	}
	
}
