package com.phoenixkahlo.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.phoenixkahlo.networking.SerializationUtils;

/**
 * Prompts the user to make a connection with another computer. Uses the
 * handshake to differentiate between random connections, LAN scanners, and
 * computers actually trying to connect.
 */
public class SocketMaker {

	// The joiner makes the call
	private static final byte[] HANDSHAKE_CALL = SerializationUtils.stringToBytes("Marco?");
	// The host makes the response
	private static final byte[] HANDSHAKE_RESPONSE = SerializationUtils.stringToBytes("Polo!");
	// The handshake must be completed in HANDSHAKE_PATIENCE ms
	private static final long HANDSHAKE_PATIENCE = 1000;
	/*
	 * After the handshake is completed, the joiner reports if it is only
	 * scanning or if it is trying to connect.
	 */
	private static final int IS_SCANNING = 1;
	private static final int IS_NOT_SCANNING = 2;
	/*
	 * If and after the joiner reports that it is actually trying to connect,
	 * the host reports whether it has accepted it. Any int that is not ACCEPTED
	 * may be treated as rejected.
	 */
	private static final int ACCEPTED = 3;
	private static final int REJECTED = 4;

	public static Socket connectSocket() throws Exception {
		return connectSocket(new Scanner(System.in));
	}

	private static Socket connectSocket(Scanner scanner) throws Exception {
		System.out.println("join [ip] [port]/host [port]/scan [port]");
		String choice = InputUtils.getOption(scanner, "join", "host", "scan");
		Socket connection = null;
		if (choice.equalsIgnoreCase("join"))
			connection = join(scanner);
		else if (choice.equalsIgnoreCase("host"))
			connection = host(scanner);
		else if (choice.equalsIgnoreCase("scan")) {
			localScan(scanner);
			connection = connectSocket(scanner);
		}
		return connection;
	}

	private static void localScan(Scanner scanner) {
		int port = scanner.nextInt();
		System.out.println("scanning LAN on port " + port + "...");
		Object printLock = new Object();
		
		class ScannerThread extends Thread {
			
			String address;
			
			ScannerThread(String address) {
				this.address = address;
			}
			
			@Override
			public void run() {
				try {
					if (InetAddress.getByName(address).isReachable(100)) {
						Socket socket = new Socket(address, port);
						OutputStream out = socket.getOutputStream();
						InputStream in = socket.getInputStream();
						out.write(HANDSHAKE_CALL);
						Optional<byte[]> response = readWithinTime(in, HANDSHAKE_RESPONSE.length, HANDSHAKE_PATIENCE);
						if (response.isPresent() && Arrays.equals(response.get(), HANDSHAKE_RESPONSE)) {
							out.write(IS_SCANNING);
							socket.close();
							synchronized (printLock) {
								System.out.println(address + " hosting");
							}
						} else {
							/*
							synchronized (printLock) {
								System.out.println(address + " failed handshake");
							}
							*/
						}
					} else {
						/*
						synchronized (printLock) {
							System.out.println(address + " unreachable");
						}
						*/
					}
				} catch (IOException e) {
					/*
					synchronized (printLock) {
						System.out.println(address + " threw IOException");
					}
					*/
				}
				
			}
			
		}
		List<Thread> pool = new ArrayList<Thread>();
		for (int i = 1; i < 255; i++) {
			pool.add(new ScannerThread("192.168.0." + i));
			pool.add(new ScannerThread("192.168.1." + i));
		}
		for (Thread thread : pool) {
			thread.start();
		}
		for (Thread thread : pool) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("scan complete");
	}

	/**
	 * Reads an ip and then a port from the scanner, then connects to it. If
	 * connection fails, will recurse to the connectSocket method, beginning the
	 * prompt again.
	 */
	private static Socket join(Scanner scanner) throws Exception {
		String ip = scanner.next();
		int port = scanner.nextInt();
		System.out.print("joining... ");
		Socket connection = new Socket(ip, port);
		System.out.print("handshaking... ");
		connection.getOutputStream().write(HANDSHAKE_CALL);
		Optional<byte[]> response = readWithinTime(connection.getInputStream(), HANDSHAKE_RESPONSE.length,
				HANDSHAKE_PATIENCE);
		if (response.isPresent()) {
			if (Arrays.equals(response.get(), HANDSHAKE_RESPONSE)) {
				connection.getOutputStream().write(IS_NOT_SCANNING);
				System.out.print("handshaken... ");
				int confirmation = connection.getInputStream().read();
				if (confirmation == ACCEPTED) {
					System.out.println("accepted.");
					return connection;
				} else {
					System.out.println("rejected.");
				}
			} else {
				System.out.println("handshake failed.");
			}
		} else {
			System.out.println("handshake timed out.");
		}
		return connectSocket(scanner);
	}

	/**
	 * Reads a port from a scanner, and then connects to a client on it.
	 */
	private static Socket host(Scanner scanner) throws IOException {
		int port = scanner.nextInt();
		ServerSocket ss = new ServerSocket(port);
		while (true) {
			try {
				System.out.print("waiting... ");
				Socket connection = ss.accept();
				System.out.print("handshaking... ");
				Optional<byte[]> initiation = readWithinTime(connection.getInputStream(), HANDSHAKE_CALL.length,
						HANDSHAKE_PATIENCE);
				if (initiation.isPresent()) {
					if (Arrays.equals(initiation.get(), HANDSHAKE_CALL)) {
						connection.getOutputStream().write(HANDSHAKE_RESPONSE);
						Optional<byte[]> scanning = readWithinTime(connection.getInputStream(), 1, HANDSHAKE_PATIENCE);
						if (scanning.isPresent()) {
							if (scanning.get()[0] == IS_SCANNING) {
								System.out.println("was only scanning.");
							} else if (scanning.get()[0] == IS_NOT_SCANNING) {
								System.out.println();
								System.out.println("joined by \"" + connection + "\".");
								System.out.println("accept/reject");
								String choice = InputUtils.getOption(scanner, "accept", "reject");
								if (choice.equalsIgnoreCase("accept")) {
									ss.close();
									connection.getOutputStream().write(ACCEPTED);
									System.out.println("accepted.");
									return connection;
								} else if (choice.equalsIgnoreCase("reject")) {
									connection.getOutputStream().write(REJECTED);
									connection.close();
									System.out.println("rejected.");
								}
							} else {
								System.out.println("scanning boolean is invalid.");
							}
						} else {
							System.out.println("scanning boolean timed out.");
						}
					} else {
						System.out.println("handshake failed.");
					}
				} else {
					System.out.println("handshake timed out.");
				}
			} catch (IOException e) {
				System.out.println("IOException.");
			}
		}
	}

	/**
	 * Try to read length bytes from in within patience ms, or return empty if
	 * that takes too long. If the socket takes too long, it may be closed, as
	 * Thread.interrupt() does not reliably work for I/O blocking. >:(
	 */
	private static Optional<byte[]> readWithinTime(InputStream in, int length, long patience) {
		try {
			Thread closer = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(patience);
						/*
						 * If the closer thread wakes before the reader thread
						 * finishes reading, the control flow will reach here.
						 * The closer thread will close the socket, jumping the
						 * reader thread's control flow to the catch clause,
						 * causing it to return empty.
						 */
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						/*
						 * If the reader thread finishes reading before the
						 * closer thread wakes, the reader thread will interrupt
						 * the closer thread, causing its control flow to jump
						 * to this catch clause, preventing it from reaching its
						 * close statement statement.
						 */
					}
				}

			});
			byte[] buffer = new byte[length];
			closer.start();
			in.read(buffer);
			closer.interrupt();
			return Optional.of(buffer);
		} catch (IOException e) {
			return Optional.empty();
		}
	}

}
