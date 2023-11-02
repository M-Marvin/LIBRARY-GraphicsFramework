package de.m_marvin.openui.layout;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import de.m_marvin.openui.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2i;

public abstract class Layout<T extends Layout.LayoutData> {
	
	public static class LayoutData {}

	public abstract Vec2i getMinSizeRequired();
	public abstract <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents);

	public abstract Class<T> getDataClass();
	
	@Deprecated
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
	
	// TODO Rework: Let small objects reach max size first (scale all objects with same step size)
	public static int[] fitSizes(int totalSize, int[] ... sizeMinAndMax)  {
		int min = 0, max = 0;
		for (int i = 0; i < sizeMinAndMax.length; i++) {
			min += sizeMinAndMax[i][0];
			max += sizeMinAndMax[i][1];
		}
		if (min == max) return Stream.of(sizeMinAndMax).mapToInt(s -> s[0]).toArray();
		float f = max == min ? 1 : (totalSize - min) / (float) (max - min);
		int[] sizes = new int[sizeMinAndMax.length];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = (int) (sizeMinAndMax[i][0] + (sizeMinAndMax[i][1] - sizeMinAndMax[i][0]) * f);
		}
		return sizes;
	}
	
	public static int[] widthMinMax(Compound<?> component) {
		return component == null ? new int[] {0, 0} : new int[] {component.getSizeMinMargin().x, component.getSizeMaxMargin().x};
	}

	public static int[] heightMinMax(Compound<?> component) {
		return component == null ? new int[] {0, 0} : new int[] {component.getSizeMinMargin().y, component.getSizeMaxMargin().y};
	}
	
	public static int[] totalMinAndMax(int[] ... sizeMinAndMax) {
		if (sizeMinAndMax.length == 0) return new int[] {0, 0};
		int min = sizeMinAndMax[0][0];
		int max = sizeMinAndMax[0][1];
		for (int i = 0; i < sizeMinAndMax.length; i++) {
			if (sizeMinAndMax[i][0] > min) min = sizeMinAndMax[i][0];
			if ((sizeMinAndMax[i][1] < max || max == 0) && sizeMinAndMax[i][1] != 0) max = sizeMinAndMax[i][1];
		}
		if (min > max) return new int[] {min, min};
		return new int[] {min, max};
	}
	
	public static int minSizeRequired(int[]... sizeMinAndMax) {
		if (sizeMinAndMax.length == 0) return 0;
		int min = 0;
		for (int[] minAndMax : sizeMinAndMax) min += minAndMax[0];
		return min;
	}
	
}
