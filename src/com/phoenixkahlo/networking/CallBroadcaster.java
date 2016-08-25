package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Binds tokens to integer headers, and can be invoked with a token and
 * arguments to broadcast the associated header, and then the arguments.
 */
public class CallBroadcaster {

	private Map<Object, Integer> headers = new HashMap<Object, Integer>();
	private OutputStream out;
	private Encoder encoder; // Nullable
	
	public CallBroadcaster(OutputStream out, Encoder encoder) {
		this.out = out;
		this.encoder = encoder;
	}
	
	public CallBroadcaster(OutputStream out) {
		this(out, null);
	}
	
	public void bind(int header, Object token) {
		headers.put(token, header);
	}
	
	public void bind(Enum<?> token) {
		bind(token.ordinal(), token);
	}
	
	public void bindEnum(Class<? extends Enum<?>> clazz) {
		for (Enum<?> token : clazz.getEnumConstants()) {
			bind(token);
		}
	}
	
	public synchronized void broadcast(Object token, Object... args) throws IOException {
		SerializationUtils.writeInt(headers.get(token), out);
		for (Object arg : args) {
			SerializationUtils.writeAny(arg, out, encoder);
		}
	}
	
}
