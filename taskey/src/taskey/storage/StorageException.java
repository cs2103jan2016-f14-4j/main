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
	private ArrayList<Task> lastModified_Tasklist;

	public StorageException(ArrayList<Task> lastModifiedTasklist) {
		super();
		lastModified_Tasklist = lastModifiedTasklist;
	}

	public StorageException(String message, ArrayList<Task> lastModifiedTasklist) {
		super(message);
		lastModified_Tasklist = lastModifiedTasklist;
	}

	public StorageException(Throwable cause, ArrayList<Task> lastModifiedTasklist) {
		super(cause);
		lastModified_Tasklist = lastModifiedTasklist;
	}

	public StorageException(String message, Throwable cause, ArrayList<Task> lastModifiedTasklist) {
		super(message, cause);
		lastModified_Tasklist = lastModifiedTasklist;
	}

	/**
	 * Returns the last task list that was successfully saved by Storage.
	 * @return the last modified tasklist
	 */
	public ArrayList<Task> getLastModifiedTasklist() {
		return lastModified_Tasklist;
	}
}
