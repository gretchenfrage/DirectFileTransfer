package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.phoenixkahlo.utils.ReflectionUtils;

/**
 * Encodes an object by its fields in order of declaration. Begins with boolean
 * to signify if null.
 */
public class FieldEncoder implements Encoder {

	private final Class<?> clazz;
	private final Encoder subEncoder; // Nullable
	private final Predicate<Field> condition;
	private Supplier<?> supplier;

	public <E> FieldEncoder(Class<E> clazz, Supplier<E> supplier, Encoder subEncoder,
			Predicate<Field> condition) {
		this.clazz = clazz;
		this.supplier = supplier;
		this.subEncoder = subEncoder;
		this.condition = condition;
	}

	public <E> FieldEncoder(Class<E> clazz, Supplier<E> supplier, Encoder subEncoder) {
		this(clazz, supplier, subEncoder, (Field field) -> !Modifier.isTransient(field.getModifiers())
				&& !Modifier.isStatic(field.getModifiers()));
	}

	public <E> FieldEncoder(Class<E> clazz, Supplier<E> supplier) {
		this(clazz, supplier, null);
	}

	/**
	 * Exists only for, and should only be used for, FieldDecoder.toDecoder()
	 */
	FieldEncoder(Class<?> clazz, Supplier<?> supplier, Encoder subEncoder, Predicate<Field> condition,
			Void differentiator) {
		this.clazz = clazz;
		this.supplier = supplier;
		this.subEncoder = subEncoder;
		this.condition = condition;
	}

	@Override
	public boolean canEncode(Object obj) {
		if (obj == null)
			return true;
		return obj.getClass() == clazz;
	}

	// Protects against FieldEncoders getting into infinite loops with circular
	// references
	private static Map<Thread, List<Object>> encoded = new HashMap<Thread, List<Object>>();

	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		if (!canEncode(obj))
			throw new IllegalArgumentException();
		// Handle null scenario
		SerializationUtils.writeBoolean(obj == null, out);
		if (obj == null)
			return;
		// Handle circular reference scenario
		Thread thread = Thread.currentThread();
		boolean head = false;
		synchronized (encoded) {
			if (!encoded.containsKey(thread)) {
				encoded.put(thread, new ArrayList<Object>());
				head = true;
			}
			if (!head && identityContains(encoded.get(thread), obj))
				throw new IllegalArgumentException("Circular references: " + obj + " of class " + obj.getClass());
			encoded.get(thread).add(obj);
		}
		// Encode fields
		for (Field field : ReflectionUtils.getAllFields(clazz)) {
			field.setAccessible(true);
			try {
				if (condition.test(field)) {
					SerializationUtils.writeAny(field.get(obj), out, subEncoder);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		// Let object do its own encoding
		if (obj instanceof EncodingFinisher) {
			((EncodingFinisher) obj).finishEncoding(out);
		}
		// Cleanup circular reference handling
		if (head)
			synchronized (encoded) {
				encoded.remove(thread);
			}
	}

	private static boolean identityContains(List<?> list, Object obj) {
		for (Object item : list)
			if (item == obj)
				return true;
		return false;
	}

	@Override
	public Decoder toDecoder() {
		if (subEncoder == null)
			return new FieldDecoder(clazz, supplier, null, condition, null);
		else
			return new FieldDecoder(clazz, supplier, subEncoder.toDecoder(), condition, null);
	}

}
