package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.phoenixkahlo.utils.ReflectionUtils;

/**
 * Decodes an object by its fields in order of declaration. Begins with boolean
 * to signify if null.
 */
public class FieldDecoder implements Decoder {

	private final Supplier<?> supplier;
	private final Field[] fields;
	private final Decoder subDecoder; // Nullable
	private final Predicate<Field> condition;
	private Class<?> clazz;

	public <E> FieldDecoder(Class<E> clazz, Supplier<E> supplier, Decoder subDecoder,
			Predicate<Field> condition) {
		this.clazz = clazz;
		this.supplier = supplier;
		this.subDecoder = subDecoder;
		this.condition = condition;
		fields = ReflectionUtils.getAllFields(clazz);
		for (Field field : fields)
			field.setAccessible(true);
	}

	public <E> FieldDecoder(Class<E> clazz, Supplier<E> supplier, Decoder subDecoder) {
		this(clazz, supplier, subDecoder, (Field field) -> !Modifier.isTransient(field.getModifiers())
				&& !Modifier.isStatic(field.getModifiers()));
	}

	public <E> FieldDecoder(Class<E> clazz, Supplier<E> supplier) {
		this(clazz, supplier, null);
	}

	/**
	 * Exists only for, and should only be used for, FieldEncoder.toDecoder()
	 */
	FieldDecoder(Class<?> clazz, Supplier<?> supplier, Decoder subDecoder, Predicate<Field> condition,
			Void differentiator) {
		this.clazz = clazz;
		this.supplier = supplier;
		this.subDecoder = subDecoder;
		this.condition = condition;
		fields = ReflectionUtils.getAllFields(clazz);
		for (Field field : fields)
			field.setAccessible(true);
	}

	@Override
	public Object decode(InputStream in) throws IOException, ProtocolViolationException {
		if (SerializationUtils.readBoolean(in))
			return null;
		else {
			Object obj = supplier.get();
			// Decode fields
			for (Field field : fields) {
				try {
					if (condition.test(field)) {
						field.set(obj, SerializationUtils.readType(field.getType(), in, subDecoder));
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			// Allow DecodingFinishers to finish
			if (obj instanceof DecodingFinisher) {
				((DecodingFinisher) obj).finishDecoding(in);
			}
			return obj;
		}
	}

	@Override
	public Encoder toEncoder() {
		if (subDecoder == null)
			return new FieldEncoder(clazz, supplier, null, condition, null);
		else
			return new FieldEncoder(clazz, supplier, subDecoder.toEncoder(), condition, null);
	}

}
