package de.m_marvin.renderengine.resources;

public interface IResourceProvider<R> {
	
	public String getNamespace();
	public String getPath();
	
	public R locationOfFile(String fileName);
	
}
