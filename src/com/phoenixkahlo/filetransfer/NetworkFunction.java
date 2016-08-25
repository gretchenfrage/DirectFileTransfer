package com.phoenixkahlo.filetransfer;

public enum NetworkFunction {

	/**
	 * <p>
	 * Invoke <code>respondTransferRequest(message)</code> in the sender.
	 * </p>
	 * <code>params = (String message)</code>
	 */
	REQUEST_TRANSFER,

	/**
	 * <p>
	 * Invoke <code>receiveTransferRequestResponse(response)</code> in the
	 * sender.
	 * </p>
	 * <code>params = (RequestResponse response)</code>
	 */
	RESPOND_TO_REQUEST,
	
	/**
	 * <p>
	 * Invoke <code>receiveSetupFilesAndFolders(files, folders)</code> in the sender.
	 * </p>
	 * <code>params = (List<String[]> files, List<String[]> folders)</code>
	 */
	SETUP_FILES_AND_FOLDERS,
	
	/**
	 * <p>
	 * Invoke <code>receiveAppendToFile(path, data</code> in the sender.
	 * </p>
	 * <code>params = (String[] path, CompoundedByteArray data)</code>
	 */
	APPEND_TO_FILE,
	
	/**
	 * <p>
	 * Invoked <code>finishTransfer()</code> in the sender.
	 * </p>
	 * <code>params = ()</code>
	 */
	FINISH_TRANSFER,

}
