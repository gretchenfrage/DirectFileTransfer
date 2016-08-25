package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A union of multiple Decoders, with integer headers.
 */
public class UnionDecoder implements Decoder {

	private Map<Integer, Decoder> decoders = new HashMap<Integer, Decoder>();
	private UnionEncoder toEncoder; // Should be invalidated (nullified) upon
									// modifications

	public void registerProtocol(int header, Decoder decoder) {
		decoders.put(header, decoder);
		toEncoder = null;
	}

	@Override
	public Object decode(InputStream in) throws IOException, ProtocolViolationException {
		if (SerializationUtils.readBoolean(in))
			return null;
		int header = SerializationUtils.readInt(in);
		Decoder decoder = decoders.get(header);
		if (decoder == null)
			throw new ProtocolViolationException(
					"header not found: " + header + " on thread " + Thread.currentThread());
		return decoder.decode(in);
	}

	@Override
	public Encoder toEncoder() {
		if (toEncoder == null) {
			toEncoder = new UnionEncoder();
			for (Map.Entry<Integer, Decoder> entry : decoders.entrySet()) {
				toEncoder.bind(entry.getKey(), entry.getValue().toEncoder());
			}
		}
		return toEncoder;
	}

}
