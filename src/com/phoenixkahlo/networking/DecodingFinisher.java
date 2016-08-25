package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;

/**
 * A Decoder may invoke finishDecoding after decoding a DecodingFinisher.
 */
public interface DecodingFinisher {

	void finishDecoding(InputStream in) throws IOException, ProtocolViolationException;

}
