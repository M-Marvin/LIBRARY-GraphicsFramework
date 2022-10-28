package de.m_marvin.renderengine.resources.locationtemplates;

public class ResourcePath {
	
	private final String path;
	
	public ResourcePath(String namespace, String path) {
		this.path = path;
	}
	
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
	
}
