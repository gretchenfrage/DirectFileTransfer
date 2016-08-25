package com.phoenixkahlo.filetransfer.test;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class NoDataTester {

	public static void main(String[] args) {
		System.out.println("Send no data to [ip] [port]");
		System.out.print("> ");
		Scanner scanner = new Scanner(System.in);
		String ip = scanner.next();
		int port = scanner.nextInt();
		scanner.close();
		try (Socket socket = new Socket(ip, port)){
			Thread.sleep(Long.MAX_VALUE);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
