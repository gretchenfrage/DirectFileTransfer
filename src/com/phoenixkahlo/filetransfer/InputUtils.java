package com.phoenixkahlo.filetransfer;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;

public class InputUtils {

	private InputUtils() {
	}

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

	public static String promptConditionPath(Scanner scanner, Predicate<File> condition, String failMessage) {
		while (true) {
			System.out.print("> ");
			String path = nextNonEmptyLine(scanner);
			if (path.toCharArray()[0] == '"' && path.toCharArray()[path.length() - 1] == '"')
				path = path.substring(1, path.length() - 1);
			if (condition.test(new File(path)))
				return path;
			else
				System.out.println(failMessage + ": \"" + path + "\"");
		}
	}
	
	public static String promptFolderPath(Scanner scanner) {
		return promptConditionPath(scanner, File::isDirectory, "not a directory");
	}
	
	public static String promptValidPath(Scanner scanner) {
		return promptConditionPath(scanner, File::exists, "path not found");
	}

	public static String nextNonEmptyLine(Scanner scanner) {
		String line;
		do {
			line = scanner.nextLine();
		} while (line.isEmpty());
		return line;
	}

}
