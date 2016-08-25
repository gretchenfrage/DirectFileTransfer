package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Encodes all HashMap types.
 */
public class HashMapEncoder implements Encoder {

	private ArrayEncoder keyArrayEncoder;
	private ArrayEncoder valueArrayEncoder;

	public HashMapEncoder(Encoder keyItemEncoder, Encoder valueItemEncoder) {
		keyArrayEncoder = new ArrayEncoder(Object.class, keyItemEncoder);
		valueArrayEncoder = new ArrayEncoder(Object.class, valueItemEncoder);
	}

	public HashMapEncoder(Encoder contentsEncoder) {
		this(contentsEncoder, contentsEncoder);
	}
	
	@Override
	public boolean canEncode(Object obj) {
		if (obj == null)
			return true;
		return obj instanceof HashMap<?, ?>;
	}

	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		if (!canEncode(obj))
			throw new IllegalArgumentException();
		SerializationUtils.writeBoolean(obj == null, out);
		if (obj == null)
			return;
		HashMap<?, ?> map = (HashMap<?, ?>) obj;
		Object[] keys = map.keySet().toArray();
		Object[] values = map.values().toArray();
		keyArrayEncoder.encode(keys, out);
		valueArrayEncoder.encode(values, out);
	}

	@Override
	public Decoder toDecoder() {
		return new HashMapDecoder(keyArrayEncoder.getItemEncoder().toDecoder(),
				valueArrayEncoder.getItemEncoder().toDecoder());
	}

}
