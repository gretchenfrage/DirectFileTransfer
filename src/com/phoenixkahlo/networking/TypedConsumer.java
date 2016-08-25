package com.phoenixkahlo.networking;

/**
 * Represents a consumer of a certain combination of types of arguments.
 */
public interface TypedConsumer {

	void invoke(Object... args) throws IllegalArgumentException;

	Class<?>[] getArgTypes();

	/**
	 * @throws IllegalArgumentException
	 *             if args aren't instances of getArgTypes();
	 */
	default void checkTypes(Object... args) throws IllegalArgumentException {
		checkTypes(getArgTypes(), args);
	}
	
	/**
	 * @throws IllegalArgumentException
	 *             if args aren't instances of argTypes;
	 */
	static void checkTypes(Class<?>[] argTypes, Object... args) throws IllegalArgumentException {
		if (args.length != argTypes.length)
			throw new IllegalArgumentException("Different argument lengths");
		for (int i = 0; i < args.length; i++) {
			if (!argTypes[i].isInstance(args[i]))
				throw new IllegalArgumentException(args[i] + " not instance of " + argTypes[i]);
		}		
	}

}
