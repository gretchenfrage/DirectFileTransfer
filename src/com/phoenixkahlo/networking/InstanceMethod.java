package com.phoenixkahlo.networking;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A TypedConsumer that calls either a method of an object or a static method.
 */
public class InstanceMethod implements TypedConsumer {

	private Object object; // Nullable to represent static methods
	private Method method;

	public InstanceMethod(Object object, Method method) {
		this.object = object;
		this.method = method;
	}

	public InstanceMethod(Object object, String name, Class<?>... argTypes) {
		this.object = object;
		try {
			method = object.getClass().getMethod(name, argTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(e);
		}
		if (!Modifier.isPublic(method.getModifiers()))
			throw new IllegalArgumentException(method + " is inaccessible");
		if (method.getExceptionTypes().length > 0)
			throw new IllegalArgumentException(method + " throws exceptions");
	}

	public InstanceMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		try {
			method = clazz.getMethod(name, argTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Class<?>[] getArgTypes() {
		return method.getParameterTypes();
	}

	@Override
	public void invoke(Object... args) throws IllegalArgumentException {
		try {
			method.invoke(object, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

}
