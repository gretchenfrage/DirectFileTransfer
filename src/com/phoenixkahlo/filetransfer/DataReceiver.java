package com.phoenixkahlo.filetransfer;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.phoenixkahlo.networking.CallReceiver;
import com.phoenixkahlo.networking.Decoder;
import com.phoenixkahlo.networking.InstanceMethod;
import com.phoenixkahlo.networking.ProtocolViolationException;

public class DataReceiver extends Thread {

	private DataSender sender;
	private CallReceiver receiver;

	public DataReceiver(Socket socket, Decoder decoder) throws IOException {
		super("receiving thread");
		
		receiver = new CallReceiver(socket.getInputStream(), decoder);
		receiver.bind(NetworkFunction.REQUEST_TRANSFER, new InstanceMethod(this, "requestTransfer", String.class));
		receiver.bind(NetworkFunction.RESPOND_TO_REQUEST,
				new InstanceMethod(this, "respondToRequest", RequestResponse.class));
		receiver.bind(NetworkFunction.SETUP_FILES_AND_FOLDERS,
				new InstanceMethod(this, "setupFilesAndFolders", List.class, List.class));
		receiver.bind(NetworkFunction.APPEND_TO_FILE,
				new InstanceMethod(this, "appendToFile", String[].class, byte[].class));
		receiver.bind(NetworkFunction.FINISH_TRANSFER, new InstanceMethod(this, "finishTransfer"));
	}

	public void injectSender(DataSender sender) {
		this.sender = sender;
	}

	/**
	 * Receive method.
	 */
	public void requestTransfer(String message) {
		try {
			sender.respondTransferRequest(message);
		} catch (IOException e) {
			e.printStackTrace(); // TODO: handle better
		}
	}

	/**
	 * Receive method.
	 */
	public void respondToRequest(RequestResponse response) {
		sender.receiveTransferRequestResponse(response);
	}

	/**
	 * Receive method.
	 */
	public void setupFilesAndFolders(List<String[]> files, List<String[]> folders) {
		sender.receiveSetupFilesAndFolders(files, folders);
	}
	
	/**
	 * Receive method.
	 */
	public void appendToFile(String[] path, byte[] data) {
		sender.receiveAppendToFile(path, data);
	}
	
	/**
	 * Receive method.
	 */
	public void finishTransfer() {
		sender.receiveFinishTransfer();
	}
	
	@Override
	public void run() {
		if (sender == null)
			throw new IllegalStateException("DataSender not injected");
		try {
			while (true) {
				receiver.receive();
			}
		} catch (IOException | ProtocolViolationException e) {
			e.printStackTrace();
		}
	}

}
