package taskey.storage;

import java.io.IOException;
import java.util.ArrayList;

import taskey.logic.Task;

/**
 * This exception class has a field that holds the last successfully saved tasklist by Storage.
 * If Storage encounters an error during write, it will throw an instance of this class to Logic,
 * so that Logic can get the last modified task list.
 * @author Dylan
 */
@SuppressWarnings("serial")
public class StorageException extends IOException {
	ArrayList<ArrayList<Task>> lastModifiedSuperlist;

	public StorageException(ArrayList<ArrayList<Task>> superlist) {
		super();
		lastModifiedSuperlist = superlist;
	}

	public StorageException(String message, ArrayList<ArrayList<Task>> superlist) {
		super(message);
		lastModifiedSuperlist = superlist;
	}

	public StorageException(Throwable cause, ArrayList<ArrayList<Task>> superlist) {
		super(cause);
		lastModifiedSuperlist = superlist;
	}

	public StorageException(String message, Throwable cause, ArrayList<ArrayList<Task>> superlist) {
		super(message, cause);
		lastModifiedSuperlist = superlist;
	}

	/**
	 * Returns the list of tasklists that were last successfully saved by Storage.
	 * @return the last modified superlist
	 */
	public ArrayList<ArrayList<Task>> getLastModifiedTasklists() {
		return lastModifiedSuperlist;
	}
}
