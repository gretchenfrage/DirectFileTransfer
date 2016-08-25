package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Decodes all ArrayList types.
 */
public class ArrayListEncoder implements Encoder {

	private ArrayEncoder arrayEncoder;

	public ArrayListEncoder(Encoder itemEncoder) {
		arrayEncoder = new ArrayEncoder(Object.class, itemEncoder);
	}

	@Override
	public boolean canEncode(Object obj) {
		if (obj == null)
			return true;
		return obj instanceof ArrayList<?>;
	}

	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		if (!canEncode(obj))
			throw new IllegalArgumentException();
		SerializationUtils.writeBoolean(obj == null, out);
		if (obj == null)
			return;
		ArrayList<?> list = (ArrayList<?>) obj;
		arrayEncoder.encode(list.toArray(), out);
	}

	@Override
	public Decoder toDecoder() {
		return new ArrayListDecoder(arrayEncoder.getItemEncoder().toDecoder());
	}

}
