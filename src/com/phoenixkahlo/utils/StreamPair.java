package com.phoenixkahlo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A connected pair of input and output streams.
 */
public class StreamPair {

	// Add to end, remove from start.
	private List<Integer> buffer = new ArrayList<Integer>();
	private boolean closed = false;
	
	public final OutputStream out = new OutputStream() {

		@Override
		public void write(int b) throws IOException {
			synchronized (buffer) {
				buffer.add(b);
				buffer.notify();
			}
		}
		
		@Override
		public void close() throws IOException {
			synchronized (buffer) {
				closed = true;
			}
		}
		
	};
	
	public final InputStream in = new InputStream() {

		@Override
		public int read() throws IOException {
			synchronized (buffer) {
				if (closed) {
					return -1;
				} else {
					while (buffer.isEmpty()) {
						try {
							buffer.wait();
						} catch (InterruptedException e) {
							throw new IOException(e);
						}
					}
					return buffer.remove(0);
				}
			}
		}
		
		@Override
		public int available() {
			synchronized (buffer) {
				return buffer.size();
			}
		}
		
	};
	
}
