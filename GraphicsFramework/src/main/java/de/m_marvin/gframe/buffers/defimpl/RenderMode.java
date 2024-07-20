package de.m_marvin.gframe.buffers.defimpl;

import java.util.function.BiConsumer;

import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.gframe.resources.ISourceFolder;
import de.m_marvin.gframe.shaders.ShaderInstance;
import de.m_marvin.gframe.textures.TextureLoader;
import de.m_marvin.gframe.vertices.RenderPrimitive;
import de.m_marvin.gframe.vertices.VertexFormat;

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
