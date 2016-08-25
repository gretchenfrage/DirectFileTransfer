package com.phoenixkahlo.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectionUtils {

	private ReflectionUtils() {
	}

	/**
	 * @return all of clazz's fields, including invisible fields and superclass
	 *         fields.
	 */
	public static Field[] getAllFields(Class<?> clazz) {
		if (clazz == Object.class)
			return clazz.getDeclaredFields();
		else
			return ArrayUtils.concatenate(getAllFields(clazz.getSuperclass()), clazz.getDeclaredFields());
	}

	/**
	 * @return all of clazz's methods, including invisible methods and
	 *         superclass methods.
	 */
	public static Method[] getAllMethods(Class<?> clazz) {
		if (clazz == Object.class)
			return clazz.getDeclaredMethods();
		else
			return ArrayUtils.concatenate(getAllMethods(clazz.getSuperclass()), clazz.getDeclaredMethods());
	}

	/**
	 * @return clazz's field with that name, even if it is invisible or of a
	 *         superclass.
	 */
	public static Field getAnyField(Class<?> clazz, String name) {
		return Arrays.stream(getAllFields(clazz)).filter(field -> field.getName().equals(name)).findAny().get();
	}

	/**
	 * @return clazz's method with that name and argument types, even if it is
	 *         invisible or of a superclass.
	 */
	public static Method getAnyMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		return Arrays.stream(getAllMethods(clazz)).filter(method -> method.getName().equals(name))
				.filter(method -> Arrays.equals(method.getParameterTypes(), argTypes)).findAny().get();
	}

	/**
	 * Sets the value of a static final variable.
	 */
	public static void setConstant(Class<?> clazz, String name, Object value) throws IllegalArgumentException {
		try {
			Field field = clazz.getField(name);
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.setAccessible(true);
			field.set(null, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
