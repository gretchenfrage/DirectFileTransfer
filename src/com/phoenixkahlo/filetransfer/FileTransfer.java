package com.phoenixkahlo.filetransfer;

import java.io.IOException;
import java.net.Socket;

import com.phoenixkahlo.networking.ArrayEncoder;
import com.phoenixkahlo.networking.ArrayListEncoder;
import com.phoenixkahlo.networking.Decoder;
import com.phoenixkahlo.networking.UnionEncoder;

/**
 * The main class.
 */
public class FileTransfer {
	
	public static void main(String[] args) throws IOException {
		UnionEncoder encoder = new UnionEncoder();
		
		encoder.bind(0, new ArrayEncoder(String.class));
		encoder.bind(1, new ArrayEncoder(byte.class));
		encoder.bind(2, ArrayListEncoder::new);
		
		Decoder decoder = encoder.toDecoder();
		
		Socket socket = SocketMaker.connectSocket();
		DataSender sender = new DataSender(socket, encoder);
		DataReceiver receiver = new DataReceiver(socket, decoder);
		sender.injectReceiver(receiver);
		receiver.injectSender(sender);
		sender.start();
		receiver.start();
	}
}
