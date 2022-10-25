package de.m_marvin.renderengine.shaders;

import java.util.function.BiConsumer;
import java.util.function.Function;

import de.m_marvin.renderengine.GLStateManager;

public enum UniformType {
	
	SAMPLER_2D("sampler2D", (array) -> int.class, (location, value) -> {
		GLStateManager.setUniformInt1(location, (int) value);
	}),
	MATRIX_3x3("matrix3x3", (array) -> float[].class, (location, value) -> {
		GLStateManager.setUniformMatrix3(location, false, (float[]) value);
	}),
	MATRIX_4x4("matrix4x4", (array) -> float[].class, (location, value) -> {
		GLStateManager.setUniformMatrix4(location, false, (float[]) value);
	}),
	INT("int", (array) -> array ? int[].class : int.class, (location, value) -> {
		if (value instanceof int[]) {
			GLStateManager.setUniformIntN(location, (int[]) value);
		} else {
			GLStateManager.setUniformInt1(location, (int) value);
		}
	}),
	UINT("uint", (array) -> array ? int[].class : int.class, (location, value) -> {
		if (value instanceof int[]) {
			GLStateManager.setUniformUnsignedIntN(location, (int[]) value);
		} else {
			GLStateManager.setUniformUnsignedInt1(location, (int) value);
		}
	}),
	FLOAT("float", (array) -> array ? float[].class : float.class, (location, value) -> {
		if (value instanceof float[]) {
			GLStateManager.setUniformFloatN(location, (float[]) value);
		} else {
			GLStateManager.setUniformFloat1(location, (float) value);
		}
	});
		
	private final String codeName;
	private final BiConsumer<Integer, Object> glSetter;
	private final Function<Boolean, Class<?>> valueTypeSupplier;
	
	private UniformType(String codeName, Function<Boolean, Class<?>> valueTypeSupplier, BiConsumer<Integer, Object> glSetter) {
		this.codeName = codeName;
		this.glSetter = glSetter;
		this.valueTypeSupplier = valueTypeSupplier;
	}
	
	public Class<?> getValueType(boolean definedAsArray) {
		return valueTypeSupplier.apply(definedAsArray);
	}
	
	public void set(int location, Object value) {
		this.glSetter.accept(location, value);
	}
	
	public String getCodeName() {
		return codeName;
	}
	
	public static UniformType byName(String name) {
		for (UniformType type : UniformType.values()) if (type.getCodeName().equals(name)) return type;
		return FLOAT;
	}
	
}
