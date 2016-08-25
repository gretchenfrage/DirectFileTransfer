package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

/**
 * Decodes a class of arrays.
 */
public class ArrayDecoder implements Decoder {

	private Class<?> clazz; // If this encodes int[]s, clazz == int.class
	private Decoder itemDecoder; // Nullable

	public ArrayDecoder(Class<?> clazz, Decoder itemDecoder) {
		this.clazz = clazz;
		this.itemDecoder = itemDecoder;
	}

	public ArrayDecoder(Class<?> clazz) throws IllegalArgumentException {
		this(clazz, null);
	}

	@Override
	public Object decode(InputStream in) throws IOException, ProtocolViolationException {
		boolean isNull = SerializationUtils.readBoolean(in);
		if (isNull)
			return null;
		int length = SerializationUtils.readInt(in);
		Object arr = Array.newInstance(clazz, length);
		for (int i = 0; i < length; i++) {
			Array.set(arr, i, SerializationUtils.readType(clazz, in, itemDecoder));
		}
		return arr;
	}

	@Override
	public ArrayEncoder toEncoder() {
		if (itemDecoder == null)
			return new ArrayEncoder(clazz);
		else
			return new ArrayEncoder(clazz, itemDecoder.toEncoder());
	}

	public Decoder getItemDecoder() {
		return itemDecoder;
	}

}
