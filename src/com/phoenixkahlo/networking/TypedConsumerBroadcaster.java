package com.phoenixkahlo.networking;

import java.io.IOException;

/**
 * Registers itself with a broadcaster and may be used to invoke the
 * broadcaster, but first check for illegal arguments.
 */
public class TypedConsumerBroadcaster {

	private CallBroadcaster broadcaster;
	private Class<?>[] argTypes;

	public TypedConsumerBroadcaster(Class<?>[] argTypes, CallBroadcaster broadcaster, int header) {
		this.argTypes = argTypes;
		this.broadcaster = broadcaster;
		broadcaster.bind(header, this);
	}

	public void broadcast(Object... args) throws IllegalArgumentException, IOException {
		TypedConsumer.checkTypes(argTypes, args);
		broadcaster.broadcast(this, args);
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

}
