package com.phoenixkahlo.utils;

/**
 * An event that can be attempted repeatedly, that may fail, and that can be
 * interrupted from another thread.
 */
public interface Attemptable {

	/**
	 * Should return with exception if thread is interrupted.
	 */
	void attempt() throws Exception;

}
