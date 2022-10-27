package de.m_marvin.renderengine.resources.locationtemplates;

public class ResourceLocation {
	
	private final String namespace;
	private final String path;
	
	public ResourceLocation(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
	}
	
	public ResourceLocation(String namespaceAndPath) {
		String[] parts = namespaceAndPath.split(":");
		if (parts.length != 2) throw new IllegalArgumentException("The namespace and path must be seperated by ONE ':' to be a valid format!");
		this.namespace = parts[0];
		this.path = parts[1];
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getPath() {
		return path;
	}
	
}
