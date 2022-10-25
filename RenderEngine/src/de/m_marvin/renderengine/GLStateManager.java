package de.m_marvin.renderengine;

import java.io.PrintStream;
import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL33;

public class GLStateManager {

	public static boolean initialisate(PrintStream errorStream) {
		if (!GLFW.glfwInit()) return false;
		GLFWErrorCallback.createPrint(errorStream).set();
		return true;
	}
	
	public static void terminate() {
		GLFW.glfwTerminate();
	}
	
	public static boolean isOnRenderThread() {
		return GLFW.glfwGetCurrentContext() > 0;
	}
	
	public static void assertOnRenderThread() {
		if (!isOnRenderThread()) throw new IllegalStateException("GL operations have to be performed on the render thread!");
	}
	
	public static void drawElements(int mode, int count, int indecieFormat) {
		GL33.glDrawElements(mode, count, indecieFormat, 0);
	}

	public static void enable(int target) {
		GL33.glEnable(target);
	}

	public static void resizeViewport(int lx, int ly, int hx, int hy) {
		GL33.glViewport(lx, ly, hx, hy);
	}

	public static void clearColor(float r, float g, float b, float a) {
		GL33.glClearColor(r, g, b, a);
	}

	public static int genVertexArray() {
		return GL33.glGenVertexArrays();
	}
	
	public static int genBufferObject() {
		return GL33.glGenBuffers();
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
	
	public static int genTexture() {
		return GL33.glGenTextures();
	}

	public static void bindTexture(int target, int textureId) {
		GL33.glBindTexture(target, textureId);
	}

	public static void textureParameter(int target, int parameter, int value) {
		GL33.glTexParameteri(target, parameter, value);
	}

	public static void loadTexture(int target, int level, int internalformat, int format, int width, int height, int border, int type, int[] pixels) {
		GL33.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	public static void activeTexture(int textureId) {
		GL33.glActiveTexture(GL33.GL_TEXTURE0 + textureId);
	}

	public static int createShader(int type) {
		return GL33.glCreateShader(type);
	}
	
	public static void shaderSource(int shader, String shaderCode) {
		GL33.glShaderSource(shader, shaderCode);
	}
	
	public static void attributePointer(int attributeId, int size, int format, boolean normalize, int stride, long bufferOffset) {
		if (format != GL33.GL_INT && format != GL33.GL_UNSIGNED_INT) {
			GL33.glVertexAttribPointer(attributeId, size, format, normalize, stride, bufferOffset);
		} else {
			GL33.glVertexAttribIPointer(attributeId, size, format, stride, bufferOffset);
		}		
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
	
	public static int createProgram() {
	 return GL33.glCreateProgram();
	}
	
	public static void attachShader(int program, int shader) {
		GL33.glAttachShader(program, shader);
	}
	
	public static void linkProgram(int program) {
		GL33.glLinkProgram(program);
	}
	
	public static void validateProgram(int program) {
		GL33.glValidateProgram(program);
	}
	
	public static boolean checkProgramValidation(int program) {
		int[] intBuffer = new int[1];
		GL33.glGetProgramiv(program, GL33.GL_VALIDATE_STATUS, intBuffer);
		return intBuffer[0] == GL33.GL_TRUE;
	}

	public static boolean checkProgramLink(int program) {
		int[] intBuffer = new int[1];
		GL33.glGetProgramiv(program, GL33.GL_LINK_STATUS, intBuffer);
		return intBuffer[0] == GL33.GL_TRUE;
	}
	
	public static String programInfoLog(int program) {
		return GL33.glGetProgramInfoLog(program);
	}
	
	public static void bindVertexAttributeLocation(int program, int index, String name) {
		GL33.glBindAttribLocation(program, index, name);
	}
	
	public static int getVertexAttributeLocation(int programm, String name) {
		return GL33.glGetAttribLocation(programm, name);
	}
	
	public static void useShader(int program) {
		GL33.glUseProgram(program);
	}

	public static void enableClientState(int state) {
		GL33.glEnableClientState(state);
	}

	public static void disableClientState(int state) {
		GL33.glDisableClientState(state);
	}
	
	public static void enableAttributeArray(int index) {
		GL33.glEnableVertexAttribArray(index);
	}

	public static void disableAttributeArray(int index) {
		GL33.glDisableVertexAttribArray(index);
	}
	
	public static int getUniformLocation(int program, String name) {
		return GL33.glGetUniformLocation(program, name);
	}

	public static void setUniformInt1(int location, int value) {
		GL33.glUniform1i(location, value);
	}
	public static void setUniformIntN(int location, int... arr) {
		GL33.glUniform1iv(location, arr);
	}

	public static void setUniformUnsignedInt1(int location, int value) {
		GL33.glUniform1ui(location, value);
	}
	public static void setUniformUnsignedIntN(int location, int... arr) {
		GL33.glUniform1uiv(location, arr);
	}

	public static void setUniformFloat1(int location, float value) {
		GL33.glUniform1f(location, value);
	}
	public static void setUniformFloatN(int location, float... arr) {
		GL33.glUniform1fv(location, arr);
	}

	public static void setUniformMatrix3(int location, boolean transpose, float[] value) {
		GL33.glUniformMatrix3fv(location, transpose, value);
	}

	public static void setUniformMatrix4(int location, boolean transpose, float[] value) {
		GL33.glUniformMatrix4fv(location, transpose, value);
	}

	public static void blendFunc(int sfactor, int dfactor) {
		GL33.glBlendFunc(sfactor, dfactor);
	}
	
}
