package com.phoenixkahlo.filetransfer.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class BadDataTester {

	public static void main(String[] args) throws IOException {
		System.out.println("send bad data to [ip] [port] [amount]");
		System.out.print("> ");
		Scanner scanner = new Scanner(System.in);
		String ip = scanner.next();
		int port = scanner.nextInt();
		int amount = scanner.nextInt();
		scanner.close();
		Socket socket = new Socket(ip, port);
		OutputStream out = socket.getOutputStream();
		Random random = new Random();
		for (int i = 0; i < amount; i++) {
			out.write(random.nextInt());
		}
		socket.close();
		System.out.println("done");
	}
	
}
