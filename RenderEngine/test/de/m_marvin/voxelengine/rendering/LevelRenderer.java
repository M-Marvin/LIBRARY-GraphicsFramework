package de.m_marvin.voxelengine.rendering;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.LevelRender.StructureRender;
import de.m_marvin.voxelengine.world.ClientLevel;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class LevelRenderer {

	public static final ResourceLocation VOXEL_SHADER = new ResourceLocation("example:world/voxelShader");
	
	public float fov;
	protected Matrix4f projectionMatrix;
	
	protected LevelRenderer levelRender;
	
	public void updatePerspective() {
		int[] windowSize = VoxelEngine.getInstance().getMainWindow().getSize();
		this.projectionMatrix = Matrix4f.perspective(this.fov, windowSize[0] / (float) windowSize[1], 1, 1000);
	}
	
	public void resetRenderCache() {
		// TODO
		this.levelRender = new LevelRenderer();
	}
	
	public void drawLevel(ClientLevel level) {
		
		ShaderInstance shader = VoxelEngine.getInstance().getShaderLoader().getShader(VOXEL_SHADER);
		
		Matrix4f viewMatrix = VoxelEngine.getInstance().getMainCamera().getViewMatrix();
		
		shader.useShader();
		shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
		shader.getUniform("ViewMat").setMatrix4f(viewMatrix);
		
		GLStateManager.enable(GL33.GL_DEPTH_TEST);
		GLStateManager.enable(GL33.GL_BLEND);
		GLStateManager.enable(GL33.GL_CULL_FACE);
		
		level.getStructures().forEach((structure) -> {
			
			
			
		});
		
	}
	
}
