package com.phoenixkahlo.filetransfer.test;

import java.util.Arrays;

import com.phoenixkahlo.filetransfer.FileSystem;

public class FileSystemTester {

	public static void main(String[] args) {
		FileSystem system = new FileSystem("/Users/Phoenix/Desktop/Java/DirectFileTransfer/bin/com/phoenixkahlo");
		system.getFilePaths().forEach(arr -> System.out.println(Arrays.toString(arr)));
		System.out.println("~~~~~~~~~~~~~");
		system.getFolderPaths().forEach(arr -> System.out.println(Arrays.toString(arr)));
		System.out.println("=============");
		system.getFilePaths().map(system::getFile).forEach(System.out::println);
		System.out.println("-------------");
		system.getFolderPaths().map(system::getFile).forEach(System.out::println);
	}

}
