package de.m_marvin.renderengine.resources.defimpl;

import java.io.File;

import de.m_marvin.renderengine.resources.IResourceProvider;

/**
 * One of the two default implementations of {@link IResourceProvider}.
 * Consists only of a path.
 * 
 * @author Marvin Köhler
 *
 */
public class ResourcePath implements IResourceProvider<ResourcePath> {
	
	private final String path;
	
	/**
	 * Creates a new ResourcePath from the given path string
	 * @param path The path string
	 */
	public ResourcePath(String path) {
		this.path = path;
	}
	
	/**
	 * Returns the path.
	 * @return The path as string
	 */
	public String getPath() {
		return path;
	}
	
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResourcePath loc) {
			return loc.path.equals(path);
 		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Resource{" + this.path + "}";
	}

	@Override
	public String getNamespace() {
		return null;
	}
	
	@Override
	public ResourcePath locationOfFile(String fileName) {
		if (fileName == null) return null;
		return new ResourcePath((this.path.isEmpty() ? this.path : this.path + "/") + fileName);
	}
	
	@Override
	public ResourcePath append(String string) {
		if (string == null) return null;
		return new ResourcePath(this.path + string);
	}
	
	@Override
	public ResourcePath getParent() {
		String parent = new File(this.path).getParent();
		return new ResourcePath(parent == null ? "" : parent);
	}
	
	@Override
	public String nameString() {
		return this.path;
	}
	
}
