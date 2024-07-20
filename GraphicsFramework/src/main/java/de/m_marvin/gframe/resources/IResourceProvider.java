package de.m_marvin.gframe.resources;

public interface IResourceProvider<R> {
	
	/**
	 * Returns the namespace part of the resource location.
	 * @return The namespace as string
	 * @throws UnsupportedOperationException if the implementation has no namespace
	 */
	public String getNamespace();

	/**
	 * Returns the path part of the resource location.
	 * @return The path as string
	 */
	public String getPath();
	
	/**
	 * Appends the given filename or path to this resource and returns the resulting location as new instance.
	 * @param fileName The filename or path to append to this location
	 * @return The resulting location or null if the fileName is null
	 */
	public R locationOfFile(String fileName);

	/**
	 * Returns this resource location but with the new namespace
	 * @param namespace The new namespace
	 * @return A new resource location the new namespace
	 */
	public R withNamespace(String namespace);

	/**
	 * Appends the given string to this resource and returns the resulting location as new instance.
	 * @param string The string to append to this location
	 * @return The resulting location or null if the string is null
	 */
	public R append(String string);
	
	/**
	 * Returns the parent location of this resource, the parent of folder1/folder2/file for example would be folder1/folder2
	 * @return the parent location of this resource
	 */
	public R getParent();
	
	public String nameString();

}
