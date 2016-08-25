package com.phoenixkahlo.utils;

import com.phoenixkahlo.networking.Encoder;
import com.phoenixkahlo.networking.FieldEncoder;

/**
 * Like a boolean[], but 32 times as memory efficient.
 */
public class CompoundedBooleanArray {

	/**
	 * Dependencies: int[]
	 */
	public static Encoder makeEncoder(Encoder subEncoder) {
		return new FieldEncoder(CompoundedBooleanArray.class, CompoundedBooleanArray::new, subEncoder);
	}

	private int[] arr;
	private int length;

	private CompoundedBooleanArray() {}

	public CompoundedBooleanArray(int length) {
		if (length % 32 == 0)
			arr = new int[length / 32];
		else
			arr = new int[length / 32 + 1];
		this.length = length;
	}

	public boolean get(int index) throws ArrayIndexOutOfBoundsException {
		if (index >= length)
			throw new ArrayIndexOutOfBoundsException();
		return (arr[index / 32] & (1 << (index % 32))) != 0;
	}

	public void set(int index, boolean value) throws ArrayIndexOutOfBoundsException {
		if (index >= length)
			throw new ArrayIndexOutOfBoundsException();
		if (value)
			arr[index / 32] |= 1 << (index % 32);
		else
			arr[index / 32] &= ~(1 << (index % 32));
	}

}
