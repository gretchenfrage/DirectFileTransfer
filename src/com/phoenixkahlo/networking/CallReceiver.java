package com.phoenixkahlo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Binds integer headers to typed consumers and can attempt to receive calls to
 * those consumers, which it will invoke.
 */
public class CallReceiver {

	private Map<Integer, TypedConsumer> consumers = new HashMap<Integer, TypedConsumer>();
	private InputStream in;
	private Decoder decoder; // Nullable.

	public CallReceiver(InputStream in, Decoder decoder) {
		this.in = in;
		this.decoder = decoder;
	}

	public CallReceiver(InputStream in) {
		this(in, null);
	}

	public void bind(int header, TypedConsumer consumer) {
		consumers.put(header, consumer);
	}
	
	public void bind(Enum<?> header, TypedConsumer consumer) {
		bind(header.ordinal(), consumer);
	}

	public void receive() throws IOException, ProtocolViolationException {
		TypedConsumer consumer = consumers.get(SerializationUtils.readInt(in));
		Class<?>[] argTypes = consumer.getArgTypes();
		Object[] args = new Object[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = SerializationUtils.readType(argTypes[i], in, decoder);
		}
		consumer.invoke(args);
	}

}
