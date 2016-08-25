package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Decodes all HashMap types.
 */
public class HashMapDecoder implements Decoder {

	private ArrayDecoder keyArrayDecoder;
	private ArrayDecoder valueArrayDecoder;

	public HashMapDecoder(Decoder keyItemDecoder, Decoder valueItemDecoder) {
		keyArrayDecoder = new ArrayDecoder(Object.class, keyItemDecoder);
		valueArrayDecoder = new ArrayDecoder(Object.class, valueItemDecoder);
	}

	@Override
	public Object decode(InputStream in) throws IOException, ProtocolViolationException {
		if (SerializationUtils.readBoolean(in))
			return null;
		Object[] keys = (Object[]) keyArrayDecoder.decode(in);
		Object[] values = (Object[]) valueArrayDecoder.decode(in);
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}

	@Override
	public Encoder toEncoder() {
		return new HashMapEncoder(keyArrayDecoder.getItemDecoder().toEncoder(),
				valueArrayDecoder.getItemDecoder().toEncoder());
	}

}
