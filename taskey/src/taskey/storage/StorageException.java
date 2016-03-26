package taskey.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.Task;

/**
 * @@author A0121618M
 * This exception class has fields that hold the last successfully saved tasklist/tagmap by Storage.
 * If Storage encounters an error during write, it will throw an instance of this class to Logic,
 * so that Logic can get the last modified tasklist/tagmap.
 */
@SuppressWarnings("serial")
public class StorageException extends IOException {
	ArrayList<ArrayList<Task>> lastModifiedSuperlist;
	HashMap<String, Integer> lastModifiedTagMap; //not in use

	public StorageException(Throwable cause, ArrayList<ArrayList<Task>> superlist) {
		super(cause.getMessage(), cause);
		lastModifiedSuperlist = superlist;
	}

	public StorageException(Throwable cause, HashMap<String, Integer> tagmap) {
		super(cause.getMessage(), cause);
		lastModifiedTagMap = tagmap;
	}

	public StorageException() {
	}

	/**
	 * Returns the ArrayList of tasklists that were last successfully saved by Storage.
	 * @return the last modified superlist
	 */
	public ArrayList<ArrayList<Task>> getLastModifiedTasklists() {
		return lastModifiedSuperlist;
	}

	/**
	 * Returns the HashMap of tags that were last successfully saved by Storage.
	 * @return the last modified tagmap
	 */
	public HashMap<String, Integer> getLastModifiedTagMap() {
		return lastModifiedTagMap;
	}
}