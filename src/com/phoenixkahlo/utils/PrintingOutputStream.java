package com.phoenixkahlo.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * For debugging, wraps an OutputStream and prints every byte that goes through
 * it.
 */
public class PrintingOutputStream extends OutputStream {

	private OutputStream out;

	public PrintingOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		System.out.println("writing: " + b);
		out.write(b);
	}

}
