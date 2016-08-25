package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A union of multiple Encoders, with integer headers.
 */
public class UnionEncoder implements Encoder {

	private Map<Encoder, Integer> encoders = new HashMap<Encoder, Integer>();
	private UnionDecoder toDecoder; // Should be invalidated (nullified) upon
									// modifications

	public void bind(int header, Encoder encoder) {
		encoders.put(encoder, header);
		toDecoder = null;
	}
	
	public void bind(Enum<?> header, Encoder encoder) {
		bind(header.ordinal(), encoder);
	}
	
	public void bind(int header, Function<? super Encoder, ? extends Encoder> function) {
		bind(header, function.apply(this));
	}
	
	public void bind(Enum<?> header, Function<? super Encoder, ? extends Encoder> function) {
		bind(header.ordinal(), function);
	}

	@Override
	public boolean canEncode(Object obj) {
		if (obj == null)
			return true;
		for (Encoder encoder : encoders.keySet()) {
			if (encoder.canEncode(obj))
				return true;
		}
		return false;
	}

	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		if (!canEncode(obj))
			throw new IllegalArgumentException("unencodable object: " + obj);
		SerializationUtils.writeBoolean(obj == null, out);
		if (obj == null)
			return;
		for (Encoder encoder : encoders.keySet()) {
			if (encoder.canEncode(obj)) {
				SerializationUtils.writeInt(encoders.get(encoder), out);
				encoder.encode(obj, out);
				return;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Decoder toDecoder() {
		if (toDecoder == null) {
			toDecoder = new UnionDecoder();
			for (Map.Entry<Encoder, Integer> entry : encoders.entrySet()) {
				toDecoder.registerProtocol(entry.getValue(), entry.getKey().toDecoder());
			}
		}
		return toDecoder;
	}

}
