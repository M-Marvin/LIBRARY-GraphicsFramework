package de.m_marvin.renderengine.resources.locationtemplates;

import java.util.Objects;

import de.m_marvin.renderengine.resources.IResourceProvider;

public class ResourceLocation implements IResourceProvider<ResourceLocation> {
	
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

	@Override
	public ResourceLocation locationOfFile(String fileName) {
		return new ResourceLocation(this.namespace, (this.path.isEmpty() ? this.path : this.path + "/") + fileName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(namespace, path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceLocation other = (ResourceLocation) obj;
		return Objects.equals(namespace, other.namespace) && Objects.equals(path, other.path);
	}
	
	@Override
	public String toString() {
		return "Resource{" + this.namespace + ":" + this.path + "}";
	}
		
}
