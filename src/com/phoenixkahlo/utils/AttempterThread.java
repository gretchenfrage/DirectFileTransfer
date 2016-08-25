package com.phoenixkahlo.utils;

import java.util.function.Consumer;

/**
 * A thread that attempts an attemptable until it fails.
 */
public class AttempterThread extends Thread {

	private Attemptable attemptable;
	private Consumer<Exception> exceptionHandler; // Nullable
	
	public AttempterThread(Attemptable attemptable, Consumer<Exception> exceptionHandler) {
		this.attemptable = attemptable;
		this.exceptionHandler = exceptionHandler;
	}

	public AttempterThread(Attemptable attemptable) {
		this(attemptable, null);
	}

	@Override
	public void run() {
		// They say catchable exceptions are a glorified goto statement...
		try {
			while (true) {
				attemptable.attempt();
			}
		} catch (Exception e) { // ...they're correct.
			exceptionHandler.accept(e);
		}
	}

}
