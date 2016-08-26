package com.phoenixkahlo.filetransfer;

import java.net.Socket;

import com.phoenixkahlo.networking.ArrayEncoder;
import com.phoenixkahlo.networking.ArrayListEncoder;
import com.phoenixkahlo.networking.Decoder;
import com.phoenixkahlo.networking.UnionEncoder;

/**
 * The main class.
 */
public class FileTransfer {
	
	public static void main(String[] args) {
		UnionEncoder encoder = new UnionEncoder();
		
		encoder.bind(0, new ArrayEncoder(String.class));
		encoder.bind(1, new ArrayEncoder(byte.class));
		encoder.bind(2, ArrayListEncoder::new);
		
		Decoder decoder = encoder.toDecoder();
		
		DataSender sender = null;
		DataReceiver receiver = null;
		while (sender == null || receiver == null) {
			try {
				Socket socket = SocketMaker.connectSocket();
				sender = new DataSender(socket, encoder);
				receiver = new DataReceiver(socket, decoder);
			} catch (Exception e) {
				System.out.println();
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
		sender.injectReceiver(receiver);
		receiver.injectSender(sender);
		sender.start();
		receiver.start();
	}
}
