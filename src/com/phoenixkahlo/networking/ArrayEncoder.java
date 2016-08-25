package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;

/**
 * Encodes a class of arrays.
 */
public class ArrayEncoder implements Encoder {

	private Class<?> itemClass; // If this encodes int[]s, itemClass ==
								// int.class
	private Encoder itemEncoder; // Nullable

	public ArrayEncoder(Class<?> itemClass, Encoder itemEncoder) {
		this.itemClass = itemClass;
		this.itemEncoder = itemEncoder;
	}

	public ArrayEncoder(Class<?> itemClass) throws IllegalArgumentException {
		this(itemClass, null);
	}

	@Override
	public boolean canEncode(Object obj) {
		if (obj == null)
			return true;
		if (!obj.getClass().isArray())
			return false;
		Class<?> objComponentType = obj.getClass().getComponentType();
		if (itemClass.isAssignableFrom(objComponentType))
			return true;
		// Check again because Object.class.isAssignableFrom(int.class) == false
		if (objComponentType == short.class)
			objComponentType = Short.class;
		else if (objComponentType == int.class)
			objComponentType = Integer.class;
		else if (objComponentType == long.class)
			objComponentType = Long.class;
		else if (objComponentType == char.class)
			objComponentType = Character.class;
		else if (objComponentType == float.class)
			objComponentType = Float.class;
		else if (objComponentType == double.class)
			objComponentType = Double.class;
		else if (objComponentType == byte.class)
			objComponentType = Byte.class;
		else if (objComponentType == boolean.class)
			objComponentType = Boolean.class;
		return itemClass.isAssignableFrom(objComponentType);
	}

	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		SerializationUtils.writeBoolean(obj == null, out);
		if (obj == null)
			return;
		if (!canEncode(obj))
			throw new IllegalArgumentException();
		int length = Array.getLength(obj);
		SerializationUtils.writeInt(length, out);
		for (int i = 0; i < length; i++) {
			SerializationUtils.writeAny(Array.get(obj, i), out, itemEncoder);
		}
	}

	@Override
	public ArrayDecoder toDecoder() {
		if (itemEncoder == null)
			return new ArrayDecoder(itemClass);
		else
			return new ArrayDecoder(itemClass, itemEncoder.toDecoder());
	}

	public Encoder getItemEncoder() {
		return itemEncoder;
	}

}
