package com.phoenixkahlo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.phoenixkahlo.networking.Encoder;
import com.phoenixkahlo.networking.FieldEncoder;

/**
 * A compounded, 256B checksum of an InputStream
 */
public class CheckSum {

	/**
	 * Dependencies: int[]
	 */
	public static Encoder makeEncoder(Encoder subEncoder) {
		return new FieldEncoder(CheckSum.class, CheckSum::new, subEncoder);
	}
	
	private int[] compound;
	
	private CheckSum() {}
	
	public CheckSum(InputStream in) throws IOException {
		byte[] sums = new byte[256];
		while (in.available() > 0)
			sums[in.read()]++;
		compound = new int[64];
		for (int i = 0; i < 256; i++) {
			compound[i / 4] |= sums[i] << ((i % 4) * 8);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof CheckSum)
			return Arrays.equals(compound, ((CheckSum) obj).compound);
		else
			return super.equals(obj);
	}
	
}
