package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Decodes objects according to a certain type of protocol.
 */
public interface Encoder {

	boolean canEncode(Object obj);

	void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException;

	/**
	 * @return a symmetric decoder.
	 */
	Decoder toDecoder();

}
