package com.phoenixkahlo.filetransfer;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class InputUtils {

	/**
	 * Reads one of the options (ignoring case) from the scanner. If responded
	 * with a non-option, will continue prompting the user until a valid choice
	 * is given.
	 */
	public static String getOption(Scanner scanner, String... options) {
		while (true) {
			System.out.print("> ");
			String choice = scanner.next();
			if (Arrays.stream(options).anyMatch(str -> str.equalsIgnoreCase(choice)))
				return choice;
			else
				System.out.println("invalid choice: \"" + choice + "\"");
		}
	}
	
	public static String promptFolderPath(Scanner scanner) {
		while (true) {
			System.out.print("> ");
			String path = scanner.next();
			if (new File(path).isDirectory())
				return path;
			else
				System.out.println("not a directory: \"" + path + "\"");
		}
	}

}
