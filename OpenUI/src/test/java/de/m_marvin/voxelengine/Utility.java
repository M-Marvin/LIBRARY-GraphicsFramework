package de.m_marvin.voxelengine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Utility {
	
	protected static Map<Function<?, ?>, Map<Object, Object>> memorizeMap = new HashMap<>();
	@SuppressWarnings("unchecked")
	public static <R, T> Function<R, T> memorize(Function<R, T> func) {
		memorizeMap.put(func, new HashMap<>());
		return (input) -> {
			Map<Object, Object> map = Utility.memorizeMap.get(func);
			if (!map.containsKey(input)) {
				map.put(input, func.apply(input));
			}
			return (T) map.get(input);
		};
	}
	
}
