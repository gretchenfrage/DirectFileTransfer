package com.phoenixkahlo.filetransfer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents a file or collection of files with universal string array paths.
 */
public class FileSystem {

	/*
	 * The path of the file or folder that will be represented by an empty array
	 * path.
	 */
	private Path root;

	public FileSystem(Path root) {
		this.root = root;
	}

	public FileSystem(File root) {
		this(root.toPath());
	}

	public FileSystem(String root) {
		this(new File(root));
	}

	private List<File> getAllContents() {
		List<File> contents = new ArrayList<File>();
		// Recursion without recursion.
		Stack<File> stack = new Stack<File>();
		stack.push(root.toFile());
		while (!stack.isEmpty()) {
			File file = stack.pop();
			contents.add(file);
			if (file.isDirectory()) {
				for (File subFile : file.listFiles()) {
					stack.push(subFile);
				}
			}
		}
		return contents;
	}

	private String[] fileToArrayPath(File file) {
		return root.relativize(file.toPath()).toString().split(Pattern.quote(File.separator));
	}

	private Stream<String[]> filteredPathStream(Predicate<? super File> filter) {
		return getAllContents().stream().filter(filter).map(this::fileToArrayPath);
	}
	
	/**
	 * @return the array paths of all non-folder files within this system.
	 */
	public Stream<String[]> getFilePaths() {
		return filteredPathStream(File::isFile);
	}

	/**
	 * @return the array paths of all folders within this system.
	 */
	public Stream<String[]> getFolderPaths() {
		return filteredPathStream(File::isDirectory);
	}

	private String arrToSystemPath(String[] arrPath) {
		return root.toString() + File.separator + String.join(File.separator, arrPath);
	}
	
	/**
	 * @return the file or folder represented by that array path.
	 */
	public File getFile(String[] path) {
		return new File(arrToSystemPath(path));
	}
	
	public void makeFile(String[] path) throws IOException {
		getFile(path).createNewFile();
	}
	
	public void makeFolder(String[] path) {
		getFile(path).mkdirs();
	}

}
