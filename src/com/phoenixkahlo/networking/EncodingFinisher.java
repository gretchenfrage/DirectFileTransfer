package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An Encoder may invoke finishEncoding after encoding a EncodingFinisher.
 */
public interface EncodingFinisher {

	void finishEncoding(OutputStream out) throws IOException;

}
