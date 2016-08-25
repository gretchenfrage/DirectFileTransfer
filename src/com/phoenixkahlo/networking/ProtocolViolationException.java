package com.phoenixkahlo.networking;

/**
 * Exception for if a serialization protocol is not followed.
 */
public class ProtocolViolationException extends Exception {

	private static final long serialVersionUID = -1161307127861060278L;

	public ProtocolViolationException() {
	}

	public ProtocolViolationException(String message) {
		super(message);
	}

	public ProtocolViolationException(Throwable cause) {
		super(cause);
	}

}
