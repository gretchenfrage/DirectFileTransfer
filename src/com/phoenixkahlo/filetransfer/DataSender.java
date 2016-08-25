package com.phoenixkahlo.filetransfer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.phoenixkahlo.networking.CallBroadcaster;
import com.phoenixkahlo.networking.Encoder;

public class DataSender extends Thread {

	private Scanner scanner = new Scanner(System.in);
	private DataReceiver receiver;
	private CallBroadcaster broadcaster;
	/*
	 * If inReceiveMode, the sender thread can be considered dead, and the
	 * receiver thread has permission to access the standard input.
	 */
	private volatile boolean inReceiveMode = false;
	/*
	 * When waiting for a transfer request response, the sender thread should
	 * nullify requestResponse, set awaitingRequestResponse to true, and then
	 * wait on requestResponseNotary while requestResponse is null.
	 * 
	 * When receiving a transfer request response, the receiver thread should
	 * set requestResponse and then notify requestResponseNotary, but only if
	 * awaitingRequestResponse is true. After it does so, it should set
	 * awaitingRequestResponse to false.
	 */
	private volatile boolean awaitingRequestResponse = false;
	private RequestResponse requestResponse;
	private Object requestResponseNotary = new Object();
	/*
	 * If during an invocation of respondTransferRequest the user accepts the
	 * transfer, receiving should be set to true and a receivingSystem should be
	 * set up. Any received methods involving the receivingSystem invoked while
	 * receiving is false should be ignored, and the occurence should be printed
	 * or logged. receivingSystem is required to be valid and not null whenever
	 * receiving is true.
	 */
	private FileSystem receivingSystem;
	private boolean receiving = false;

	public DataSender(Socket socket, Encoder encoder) throws IOException {
		super("sending thread");
		broadcaster = new CallBroadcaster(socket.getOutputStream(), encoder);
		broadcaster.bindEnum(NetworkFunction.class);
	}

	public void injectReceiver(DataReceiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * Broadcast method.
	 */
	private void requestTransfer(String message) throws IOException {
		broadcaster.broadcast(NetworkFunction.REQUEST_TRANSFER, message);
	}

	/**
	 * Broadcast method.
	 */
	private void respondToRequest(RequestResponse response) throws IOException {
		broadcaster.broadcast(NetworkFunction.RESPOND_TO_REQUEST, response);
	}

	/**
	 * Broadcast method.
	 */
	private void setupFilesAndFolders(List<String[]> files, List<String[]> folders) throws IOException {
		broadcaster.broadcast(NetworkFunction.SETUP_FILES_AND_FOLDERS, files, folders);
	}

	/**
	 * Broadcast method.
	 */
	private void broadcastAppendToFile(String[] path, byte[] data) throws IOException {
		broadcaster.broadcast(NetworkFunction.APPEND_TO_FILE, path, data);
	}

	/**
	 * Broadcast method.
	 */
	private void broadcastFinishTransfer() throws IOException {
		broadcaster.broadcast(NetworkFunction.FINISH_TRANSFER);
	}

	/**
	 * Called by the receiver thread upon receiving <code>requestTransfer</code>
	 * . If the sender is in receive mode, prompt the user for if they want to
	 * accept or reject the transfer, and then <code>respondToRequest</code>
	 * with the appropriate enum constant (<code>ACCEPT</code> or
	 * <code>REJECT</code>). If the users accepts the transfer, follow by
	 * prompting them for a location, and prepare the <code>FileSystem</code> to
	 * receive. If the sender is in send mode, <code>respondToRequest</code>
	 * with <code>NOT_RECEIVING</code>.
	 */
	public void respondTransferRequest(String message) throws IOException {
		if (inReceiveMode) {
			System.out.println("transfer request: \"" + message + "\"");
			System.out.println("accept/reject");
			String choice = InputUtils.getOption(scanner, "accept", "reject");
			if (choice.equalsIgnoreCase("accept")) {
				System.out.println("what folder to save to");
				String path = InputUtils.promptFolderPath(scanner);
				receivingSystem = new FileSystem(path);
				receiving = true;
				respondToRequest(RequestResponse.ACCEPT);
			} else {
				respondToRequest(RequestResponse.REJECT);
			}
		} else {
			respondToRequest(RequestResponse.NOT_RECEIVING);
		}
	}

	/**
	 * Called by the receiver thread upon receiving
	 * <code>respondToRequest</code>. If the sending thread is waiting on a
	 * request response, provide it with that response and then notify it.
	 */
	public void receiveTransferRequestResponse(RequestResponse response) {
		if (awaitingRequestResponse) {
			awaitingRequestResponse = false;
			requestResponse = response;
			synchronized (requestResponseNotary) {
				requestResponseNotary.notify();
			}
		} else {
			System.err.println("Warning: receiveTransferRequestResponse invoked while not awaitingRequestResponse");
		}
	}

	/**
	 * Called by the receive thread. If receiving, create the files and folders
	 * in the receiving system.
	 */
	public void receiveSetupFilesAndFolders(List<String[]> files, List<String[]> folders) {
		if (receiving) {
			try {
				for (String[] path : folders) {
					receivingSystem.makeFolder(path);
				}
				for (String[] path : files) {
					receivingSystem.makeFile(path);
				}
			} catch (IOException e) {
				e.printStackTrace(); // TODO: handle this better
			}
		} else {
			System.err.println("Warning: receiveSetupFilesAndFolders invoked while not receiving");
		}
	}

	/**
	 * Called by the receive thread. If receiving, append the bytes to the file
	 * in the receiving system.
	 */
	public void receiveAppendToFile(String[] path, byte[] data) {
		if (receiving) {
			try (OutputStream out = new FileOutputStream(receivingSystem.getFile(path))) {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace(); // TODO: handle this better
			}
		} else {
			System.err.println("Warning: receiveAppendToFile invoked while not receiving");
		}
	}

	/**
	 * Called by the receive thread. If receiving, stop receiving, and cleanup
	 * any cached streams.
	 */
	public void receiveFinishTransfer() {
		if (receiving) {
			receiving = false;
			receivingSystem = null;
		} else {
			System.err.println("Warning: receiveFinishTransfer invoked while not receiving");
		}
	}

	/**
	 * Called by the sender thread. Broadcast
	 * <code>requestTransfer(message)</code>, sleep until a response is
	 * received, and then return that response.
	 */
	private RequestResponse requestTransferAwaitResponse(String message) throws IOException {
		requestResponse = null; // So it will know when to wake up
		awaitingRequestResponse = true; // So the response won't be rejected
		requestTransfer(message);
		while (requestResponse == null) {
			try {
				synchronized (requestResponseNotary) {
					requestResponseNotary.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return requestResponse;
	}

	@Override
	public void run() {
		if (receiver == null)
			throw new IllegalStateException("DataReceiver not injected");

		try {
			System.out.println("send/receive");
			String option = InputUtils.getOption(scanner, "send", "receive");
			if (option.equalsIgnoreCase("send")) {
				System.out.println("enter file path (in one line)");
				String path = InputUtils.promptFolderPath(scanner);
				System.out.println("give message (in one line)");
				System.out.print("> ");
				String message = InputUtils.nextNonEmptyLine(scanner);
				RequestResponse response = requestTransferAwaitResponse(message);
				if (response == RequestResponse.REJECT) {
					System.out.println("other user rejected transfer");
				} else if (response == RequestResponse.NOT_RECEIVING) {
					System.out.println("other user not in receive mode");
				} else if (response == RequestResponse.ACCEPT) {
					System.out.println("beginning transfer...");
					// Time to transfer
					FileSystem sendingSystem = new FileSystem(path);
					List<String[]> files = sendingSystem.getFilePaths().collect(Collectors.toList());
					List<String[]> folders = sendingSystem.getFolderPaths().collect(Collectors.toList());
					setupFilesAndFolders(files, folders);
					for (String[] sendingPath : files) {
						InputStream in = new FileInputStream(sendingSystem.getFile(sendingPath));
						byte[] buffer = new byte[1_000_000]; // 1 MB buffer
						int read;
						do {
							read = in.read(buffer);
							byte[] data = Arrays.copyOf(buffer, read);
							if (read != 0) {
								broadcastAppendToFile(sendingPath, data);
								System.out.println("sent " + read + "B of " + sendingPath[sendingPath.length - 1]);
							}
						} while (in.available() > 0);
						in.close();
					}
					System.out.println("transfer complete.");
					broadcastFinishTransfer();
				}
			} else if (option.equalsIgnoreCase("receive")) {
				System.out.println("in receive mode...");
				inReceiveMode = true;
			}
		} catch (IOException e) {
			e.printStackTrace(); // TODO: handle better
		}
	}

}
