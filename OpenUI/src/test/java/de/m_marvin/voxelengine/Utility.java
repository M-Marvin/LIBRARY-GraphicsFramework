package de.m_marvin.voxelengine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Utility {
	
	public static void executeForEachFolder(File folderTreeStart, Consumer<String> task) {
		executeForEachFolder0(folderTreeStart, "", task);
	}
	
	protected static void executeForEachFolder0(File folderTreeStart, String path, Consumer<String> task) {
		task.accept(path);
		for (File entry : folderTreeStart.listFiles()) {
			if (entry.isDirectory()) executeForEachFolder0(entry, (path.isEmpty() ? path : path + "/") + entry.getName(), task);
		}
	}
	
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
