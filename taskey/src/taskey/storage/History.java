package taskey.storage;

import java.util.ArrayList;

import taskey.logic.Task;

/**
 * This class is to allow Storage to easily retrieve the last successfully saved tasklist from memory
 * so that Storage can throw it to Logic when an error is encountered during saving.
 * This will in turn allow Logic to easily undo the last operation so that its data remains in sync with Storage.
 *
 * For now, this class is only meant to be used by Storage for the above purpose.
 * But, in the future, if more methods are added to this class, Logic could also use this for the undo command.
 * TODO: multiple undos/redos using stacks?
 *
 * @author Dylan
 */
public class History {
	private ArrayList<Task> lastModifiedTasklist_PENDING;
	private ArrayList<Task> lastModifiedTasklist_EXPIRED;
	private ArrayList<Task> lastModifiedTasklist_GENERAL;
	private ArrayList<Task> lastModifiedTasklist_DEADLINE;
	private ArrayList<Task> lastModifiedTasklist_EVENT;
	private ArrayList<Task> lastModifiedTasklist_COMPLETED;

	public History() {
	}

	void set(String filename, ArrayList<Task> tasklist) {
		FilenameEnum tasklistCategory = FilenameEnum.getType(filename);
    	switch (tasklistCategory) {
			case PENDING:
				lastModifiedTasklist_PENDING = tasklist;
				break;
			case EXPIRED:
				lastModifiedTasklist_EXPIRED = tasklist;
				break;
			case GENERAL:
				lastModifiedTasklist_GENERAL = tasklist;
				break;
			case DEADLINE:
				lastModifiedTasklist_DEADLINE = tasklist;
				break;
			case EVENT:
				lastModifiedTasklist_EVENT = tasklist;
				break;
			case COMPLETED:
				lastModifiedTasklist_COMPLETED = tasklist;
				break;
			default:
    	}
	}

	ArrayList<Task> get(String filename) {
		FilenameEnum tasklistCategory = FilenameEnum.getType(filename);
    	switch (tasklistCategory) {
			case PENDING:
				return (lastModifiedTasklist_PENDING == null) ? new ArrayList<Task>() : lastModifiedTasklist_PENDING;
			case EXPIRED:
				return (lastModifiedTasklist_EXPIRED == null) ? new ArrayList<Task>() :  lastModifiedTasklist_EXPIRED;
			case GENERAL:
				return (lastModifiedTasklist_GENERAL == null) ? new ArrayList<Task>() :  lastModifiedTasklist_GENERAL;
			case DEADLINE:
				return (lastModifiedTasklist_DEADLINE == null) ? new ArrayList<Task>() :  lastModifiedTasklist_DEADLINE;
			case EVENT:
				return (lastModifiedTasklist_EVENT == null) ? new ArrayList<Task>() :  lastModifiedTasklist_EVENT;
			case COMPLETED:
				return (lastModifiedTasklist_COMPLETED == null) ? new ArrayList<Task>() :  lastModifiedTasklist_COMPLETED;
			default:
				return null;
    	}
	}
}