/*package taskey.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import taskey.messenger.Task;

*//**
 * @@author A0121618M-unused
 * This exception class has fields that hold the last successfully saved tasklist/tagmap by Storage.
 * If Storage encounters an error during write, it will throw an instance of this class to Logic,
 * so that Logic can get the last-saved tasklist/tagmap.
 *//*
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

	*//**
	 * Returns the ArrayList of tasklists that were last successfully saved by Storage.
	 * @return the last modified superlist
	 *//*
	public ArrayList<ArrayList<Task>> getLastModifiedTasklists() {
		return lastModifiedSuperlist;
	}

	*//**
	 * Returns the HashMap of tags that were last successfully saved by Storage.
	 * @return the last modified tagmap
	 *//*
	public HashMap<String, Integer> getLastModifiedTagMap() {
		return lastModifiedTagMap;
	}
}

*//**
 * This class was to allow Storage to easily retrieve the last successfully saved tasks/tags from memory
 * so that Storage can throw it to Logic when an error is encountered during saving.
 * This will in turn allow Logic to undo the last operation so that its data remains in sync with Storage.
 *
 * This class was only meant to be used by Storage for the above purpose.
 * But, after more methods were added, it was moved to Logic for its the undo command.
 *//*
class History {
}*/