package com.phoenixkahlo.networking;

/**
 * Wraps a function and allows it to be disabled from another thread, after
 * which all calls to the function will be ignored.
 */
public class DisableableTypedConsumer implements TypedConsumer {

	private TypedConsumer subFunction;
	private boolean enabled = true;

	public DisableableTypedConsumer(TypedConsumer subFunction) {
		this.subFunction = subFunction;
	}

	public synchronized void disable() {
		enabled = false;
	}

	@Override
	public synchronized void invoke(Object... args) throws IllegalArgumentException {
		if (enabled)
			subFunction.invoke(args);
	}

	@Override
	public Class<?>[] getArgTypes() {
		return subFunction.getArgTypes();
	}

}
