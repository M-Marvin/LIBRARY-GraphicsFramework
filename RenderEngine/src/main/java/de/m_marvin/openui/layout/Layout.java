package de.m_marvin.openui.layout;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.m_marvin.openui.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;

public abstract class Layout<T extends Layout.LayoutData> {
	
	public static class LayoutData {}
	
	public abstract <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents);
	
	public abstract Class<T> getDataClass();
	
	@SuppressWarnings("unchecked")
	public T getData(Compound<?> component) {
		if (this.getDataClass().isInstance(component.getLayoutData())) return (T) component.getLayoutData();
		try {
			T defaultData = (T) getDataClass().getConstructor().newInstance();
			component.setLayoutData(defaultData);
			return defaultData;
		} catch (InstantiationException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.err.println("Failed to instanziate the default layout-data of the " + getDataClass().getName() + " class!");
			e.printStackTrace();
			return null;
		}
	}
	
}
