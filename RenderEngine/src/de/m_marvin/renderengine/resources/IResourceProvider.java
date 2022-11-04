package de.m_marvin.renderengine.resources;

public interface IResourceProvider<R> {
	
	/**
	 * Returns the path part of the resource location.
	 * @return The path as string
	 * @throws UnsupportedOperationException if the implementation has no namespace
	 */
	public String getNamespace();

	/**
	 * Returns the namespace part of the resource location.
	 * @return The namespace as string
	 */
	public String getPath();
	
	/**
	 * Appends the given filename or path to this resource and returns the resulting location as new instance.
	 * @param fileName The filename or path to append to this location
	 * @return The resulting location
	 */
	public R locationOfFile(String fileName);
	
}
