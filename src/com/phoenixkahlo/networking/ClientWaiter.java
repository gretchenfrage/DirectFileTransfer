package com.phoenixkahlo.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Thread that waits on a port to accept connections, then invokes a callback to
 * initialize them.
 */
public class ClientWaiter extends Thread {

	private Consumer<Socket> creator;
	private int port;
	private Consumer<IOException> exceptionEvent; // Nullable
	private ServerSocket ss;

	public ClientWaiter(Consumer<Socket> creator, int port, Consumer<IOException> exceptionEvent) {
		this.creator = creator;
		this.port = port;
		this.exceptionEvent = exceptionEvent;
	}

	public ClientWaiter(Consumer<Socket> creator, int port) {
		this(creator, port, null);
	}

	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
			while (true) {
				creator.accept(ss.accept());
			}
		} catch (IOException e) {
			if (exceptionEvent != null)
				exceptionEvent.accept(e);
		}
	}

	public void terminate() {
		try {
			ss.close();
		} catch (IOException e) {
			exceptionEvent.accept(e);
		}
	}

}
