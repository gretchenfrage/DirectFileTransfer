package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes objects according to a certain type of protocol.
 */
public interface Decoder {

	Object decode(InputStream in) throws IOException, ProtocolViolationException;

	/**
	 * @return the symmetric encoder.
	 */
	Encoder toEncoder();

}
