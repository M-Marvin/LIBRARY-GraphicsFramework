package de.m_marvin.renderengine;

import java.nio.ByteBuffer;
import java.util.function.IntConsumer;

import org.lwjgl.opengl.GL33;

public class GLStateManager {
	
	public static boolean isOnRenderThread() {
		return true; // TODO
	}
	
	public static void assertOnRenderThread() {
		if (!isOnRenderThread()) throw new IllegalStateException("GL operations have to be performed on the render thread!");
	}
	
	public static void genVertexArray(IntConsumer idConsumer) {
		idConsumer.accept(GL33.glGenVertexArrays());
	}
	
	public static void genBufferObject(IntConsumer idConsumer) {
		idConsumer.accept(GL33.glGenBuffers());
	}

	public static void deleteVertexArray(int vertexArrayId) {
		GL33.glDeleteVertexArrays(vertexArrayId);
	}
	
	public static void deleteBufferObject(int bufferObjectId) {
		GL33.glDeleteBuffers(bufferObjectId);
	}

	public static void bufferData(int target, ByteBuffer data, int usage) {
		GL33.glBufferData(target, data, usage);
	}

	public static void bindBufferObject(int target, int vertexBufferId) {
		GL33.glBindBuffer(target, vertexBufferId);
	}

	public static void bindVertexArray(int arrayObjectId) {
		GL33.glBindVertexArray(arrayObjectId);
	}
	
	public static void attributePointer(int attributeId, int size, int position, int format, boolean normalize, long bufferOffset) {
		GL33.glVertexAttribPointer(attributeId, size, format, normalize, position, bufferOffset);
	}
	
	public static void createShader(int type, IntConsumer idConsumer) {
		idConsumer.accept(GL33.glCreateShader(type));
	}
	
	public static void shaderSource(int shader, String shaderCode) {
		GL33.glShaderSource(shader, shaderCode);
	}
	
	public static void compileShader(int shader) {
		GL33.glCompileShader(shader);
	}
	
	public static boolean checkShaderCompile(int shader) {
		int[] intBuffer = new int[1];
		GL33.glGetShaderiv(shader, GL33.GL_COMPILE_STATUS, intBuffer);
		return intBuffer[0] == GL33.GL_TRUE;
	}
	
	public static String shaderInfoLog(int shader) {
		return GL33.glGetShaderInfoLog(shader);
	}
	
	public static void createProgram(IntConsumer idConsumer) {
		idConsumer.accept(GL33.glCreateProgram());
	}
	
	public static void attachShader(int program, int shader) {
		GL33.glAttachShader(program, shader);
	}
	
	public static void linkProgram(int program) {
		GL33.glLinkProgram(program);
	}
	
	public static boolean checkProgramLink(int program) {
		int[] intBuffer = new int[1];
		GL33.glGetProgramiv(program, GL33.GL_LINK_STATUS, intBuffer);
		return intBuffer[0] == GL33.GL_TRUE;
	}
	
	public static String programInfoLog(int program) {
		return GL33.glGetProgramInfoLog(program);
	}
	
	public static void bindAttributeLocation(int program, int index, String name) {
		GL33.glBindAttribLocation(program, index, name);
	}
	
//	public static void setUniformMatrix2x2(int location, ) {
//		GL33.glUniformMatrix2fv(location, false, null);
//	}
	
	public static int getUniformLocation(int program, String name) {
		return GL33.glGetUniformLocation(program, name);
	}

	public static void setUniformInt1(int location, int value) {
		GL33.glUniform1i(location, value);
	}
	public static void setUniformIntN(int location, int... arr) {
		GL33.glUniform1iv(location, arr);
	}

	public static void setUniformFloat1(int location, float value) {
		GL33.glUniform1f(location, value);
	}
	public static void setUniformFloatN(int location, float... arr) {
		GL33.glUniform1fv(location, arr);
	}

	public static void bindShader(int program) {
		GL33.glUseProgram(program);
	}
	
	public static void drawElements(int mode, int count, int indecieFormat) {
		GL33.glDrawElements(mode, count, indecieFormat, 0);
	}
	
}
