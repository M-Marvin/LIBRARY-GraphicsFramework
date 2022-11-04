package de.m_marvin.renderengine.resources.locationtemplates;

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
		throw new UnsupportedOperationException("ResourcePath does not have a namespace!");
	}
	
	@Override
	public ResourcePath locationOfFile(String fileName) {
		return new ResourcePath((this.path.isEmpty() ? this.path : this.path + "/") + fileName);
	}
	
}
