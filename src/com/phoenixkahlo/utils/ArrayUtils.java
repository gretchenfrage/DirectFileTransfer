package com.phoenixkahlo.utils;

import java.lang.reflect.Array;

/**
 * I figured out this trick where you can use reflection to get the class of the
 * items of an array, and then use the Arrays.newInstance native method to
 * effectively instantiate a generic type of array (if you can obtain an
 * instance of that type of array). Since the default java packages don't appear
 * to have any utilities that take advantage of that, I have this static array
 * utilities class. Expect bad performance as it uses both reflection and the
 * java native interface.
 */
public class ArrayUtils {

	private ArrayUtils() {
	}

	public static <E> E[] concatenate(E[] arr1, E[] arr2) {
		@SuppressWarnings("unchecked")
		E[] out = (E[]) Array.newInstance(arr1.getClass().getComponentType(), arr1.length + arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			out[i] = arr1[i];
		}
		for (int i = 0; i < arr2.length; i++) {
			out[arr1.length + i] = arr2[i];
		}
		return out;
	}

	public static <E> E[] append(E[] arr1, E obj) {
		@SuppressWarnings("unchecked")
		E[] out = (E[]) Array.newInstance(arr1.getClass().getComponentType(), arr1.length + 1);
		for (int i = 0; i < arr1.length; i++) {
			out[i] = arr1[i];
		}
		out[out.length - 1] = obj;
		return out;
	}
	
	public static <E> E[] prepend(E obj, E[] arr) {
		@SuppressWarnings("unchecked")
		E[] out = (E[]) Array.newInstance(arr.getClass().getComponentType(), arr.length + 1);
		out[0] = obj;
		for (int i = 0; i < arr.length; i++) {
			out[i + 1] = arr[i];
		}
		return out;
	}
	
	public static <E> E[] snippet(E[] arr, int start, int end) {
		@SuppressWarnings("unchecked")
		E[] out = (E[]) Array.newInstance(arr.getClass().getComponentType(), end - start);
		for (int i = 0; i < end - start; i++) {
			out[i + start] = arr[i];
		}
		return out;
	}

	/**
	 * @return a shallow copy of arr.
	 */
	public static <E> E[] copy(E[] arr) {
		@SuppressWarnings("unchecked")
		E[] copy = (E[]) Array.newInstance(arr.getClass().getComponentType(), arr.length);
		for (int i = 0; i < arr.length; i++) {
			copy[i] = arr[i];
		}
		return copy;
	}
}
