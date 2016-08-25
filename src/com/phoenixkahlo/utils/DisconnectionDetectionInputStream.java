package com.phoenixkahlo.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an InputStream and throws an IOException on reading if stream has ended.
 */
public class DisconnectionDetectionInputStream extends InputStream {

	private InputStream in;
	
	public DisconnectionDetectionInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		int read = in.read();
		if (read == -1)
			throw new IOException(in + " disconnected");
		else
			return read;
	}
	
}
