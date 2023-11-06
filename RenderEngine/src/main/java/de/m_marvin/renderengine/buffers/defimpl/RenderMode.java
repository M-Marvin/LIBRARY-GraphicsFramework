package de.m_marvin.renderengine.buffers.defimpl;

import java.util.function.BiConsumer;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public record RenderMode<R extends IResourceProvider<R>> (
		RenderPrimitive primitive, 
		VertexFormat vertexFormat, 
		R shader,
		BiConsumer<ShaderInstance, TextureLoader<R, ? extends ISourceFolder>> setupRenderMode
) implements IRenderMode {
	public void setupRenderMode(ShaderInstance shader, TextureLoader<R, ? extends ISourceFolder> textureLoader) {
		this.setupRenderMode().accept(shader, textureLoader);
	}
}
