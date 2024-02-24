package de.m_marvin.renderengine.utility;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

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

	@SuppressWarnings("unchecked")
	public static <T> T[] concatArr(T[] a, T[] b, Class<?> clazz) {
		return concatArr(a, b, i -> (T[]) Array.newInstance(clazz, i));
	}

	public static <T> T[] concatArr(T[] a, T[] b, IntFunction<T[]> arrSup) {
		T[] newArr = arrSup.apply(a.length + b.length);
		for (int i = 0; i < newArr.length; i++) {
			newArr[i] = i < a.length ? a[i] : b[i - a.length];
		}
		return newArr;
	}

	public static <T> Object[] concatArr(T[] a, T[] b) {
		Object[] newArr = new Object[a.length + b.length];
		for (int i = 0; i < newArr.length; i++) {
			newArr[i] = i < a.length ? a[i] : b[i - a.length];
		}
		return newArr;
	}
	
}
