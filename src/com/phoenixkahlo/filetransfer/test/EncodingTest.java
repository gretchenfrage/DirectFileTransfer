package com.phoenixkahlo.filetransfer.test;

import com.phoenixkahlo.networking.SerializationUtils;

public class EncodingTest {

	public static void main(String[] args) {
		for (byte b : SerializationUtils.stringToBytes("Marco?")) {
			System.out.println(b);
		}
	}
	
}
