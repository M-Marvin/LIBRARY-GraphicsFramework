package de.m_marvin.gframe;

import java.io.PrintStream;
import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

/**
 * This class contains all used OpenGL methods.
 * Contains the {@link GLStateManager#initialize(PrintStream)} method that must be called before any OpenGL or GLFW methods can be used.
 * 
 * @author Marvin Koehler
 */
public class GLStateManager {

	/**
	 * Initializes GLFW, must be called before any OpenGL or GLFW related methods can be used.
	 * This function should be called on the thread used to process user input and game logic!
	 * 
	 * @param errorStream Error stream to print GLFW and OpenGL errors.
	 * @return True if GLFW could be initialized, false if an error occurred
	 */
	public static boolean initialize(PrintStream errorStream) {
		if (!GLFW.glfwInit()) return false;
		GLFWErrorCallback.createPrint(errorStream).set();
		return true;
	}
	
	/**
	 * Calls the GLFW terminate method.
	 * Should be called to cleanup everything.
	 * This function must be called on the same thread on which {@link #initialize(PrintStream)} was!
	 */
	public static void terminate() {
		GLFW.glfwTerminate();
	}
	
	/**
	 * Returns true if the calling thread has a OpenGL context bound to it and can perform rendering operations.
	 * @return True if a OpenGL context is bound
	 */
	public static boolean isOnRenderThread() {
		try {
			GL.getCapabilities();
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}
	
	public static void assertOnRenderThread() {
		assert isOnRenderThread() : "GL operations have to be performed on the render thread!";
	}
	
	public static void clear(int bufferBitMask) {
		GL33.glClear(bufferBitMask);
	}
	
	public static void clear() {
		clear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
	}
	
	public static void flush() {
		GL33.glFlush();
	}
	
	public static void drawElements(int mode, int count, int indecieFormat) {
		GL33.glDrawElements(mode, count, indecieFormat, 0);
	}

	public static void enable(int target) {
		GL33.glEnable(target);
	}

	public static void disable(int target) {
		GL33.glDisable(target);
	}
	
	public static void lineWidth(float width) {
		GL33.glLineWidth(width);
	}

	public static void pointSize(int size) {
		GL33.glPointSize(size);
	}
	
	public static void polygonOffset(float factor, float units) {
		GL33.glPolygonOffset(factor, units);
	}
	
	public static void resizeViewport(int x, int y, int w, int h) {
		GL33.glViewport(x, y, w, h);
	}

	public static void clearColor(float r, float g, float b, float a) {
		GL33.glClearColor(r, g, b, a);
	}
	
	public static void clearDepth(double depth) {
		GL33.glClearDepth(depth);
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
	
	public static void deleteTexture(int textureId) {
		GL33.glDeleteTextures(textureId);
	}

	public static void bindTexture(int target, int textureId) {
		GL33.glBindTexture(target, textureId);
	}

	public static void textureParameter(int target, int parameter, int value) {
		GL33.glTexParameteri(target, parameter, value);
	}

	public static void uploadTexture(int target, int level, int internalformat, int format, int width, int height, int border, int type, int[] pixels) {
		GL33.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}
	
	public static void downloadTexture(int target, int level, int format, int type, int[] pixelBuffer) {
		GL33.glGetTexImage(target, level, format, type, pixelBuffer);
	}

	public static void activeTexture(int textureId) {
		GL33.glActiveTexture(GL33.GL_TEXTURE0 + textureId);
	}

	public static int createShader(int type) {
		return GL33.glCreateShader(type);
	}
	
	public static void deleteShader(int shader) {
		GL33.glDeleteShader(shader);
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
	
	public static void deleteProgram(int program) {
		GL33.glDeleteProgram(program);
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

	public static void setUniformInt(int location, int value) {
		GL33.glUniform1i(location, value);
	}
	public static void setUniformIntVec2(int location, float valueX, float valueY) {
		GL33.glUniform2f(location, valueX, valueY);
	}
	public static void setUniformIntVec3(int location, float valueX, float valueY, float valueZ) {
		GL33.glUniform3f(location, valueX, valueY, valueZ);
	}
	public static void setUniformIntVec4(int location, float valueX, float valueY, float valueZ, float valueW) {
		GL33.glUniform4f(location, valueX, valueY, valueZ, valueW);
	}

	public static void setUniformUnsignedInt(int location, int value) {
		GL33.glUniform1ui(location, value);
	}
	public static void setUniformUnsignedIntVec2(int location, int valueX, int valueY) {
		GL33.glUniform2ui(location, valueX, valueY);
	}
	public static void setUniformUnsignedIntVec3(int location, int valueX, int valueY, int valueZ) {
		GL33.glUniform3ui(location, valueX, valueY, valueZ);
	}
	public static void setUniformUnsignedIntVec4(int location, int valueX, int valueY, int valueZ, int valueW) {
		GL33.glUniform4ui(location, valueX, valueY, valueZ, valueW);
	}

	public static void setUniformFloat(int location, float value) {
		GL33.glUniform1f(location, value);
	}
	public static void setUniformFloatVec2(int location, float valueX, float valueY) {
		GL33.glUniform2f(location, valueX, valueY);
	}
	public static void setUniformFloatVec3(int location, float valueX, float valueY, float valueZ) {
		GL33.glUniform3f(location, valueX, valueY, valueZ);
	}
	public static void setUniformFloatVec4(int location, float valueX, float valueY, float valueZ, float valueW) {
		GL33.glUniform4f(location, valueX, valueY, valueZ, valueW);
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
	
	public static void bindFramebuffer(int target, int framebuffer) {
		GL33.glBindFramebuffer(target, framebuffer);
	}
	
	public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
		GL33.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
	}
	
	public static int checkFramebufferStatus(int target) {
		return GL33.glCheckFramebufferStatus(target);
	}
	
	public static void deleteFramebuffer(int framebuffer) {
		GL33.glDeleteFramebuffers(framebuffer);
	}
	
	public static int genFramebuffer() {
		return GL33.glGenFramebuffers();
	}
	
}
