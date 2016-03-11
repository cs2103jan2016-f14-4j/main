package taskey.storage;

import java.util.ArrayDeque;
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
	private ArrayDeque<ArrayList<ArrayList<Task>>> stack;
	private static History instance;

	public History() {
		stack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
	}

	/**
	 * Logic can get the History singleton, if it uses this class in the future.
	 * @return the history singleton
	 */
	public static History getInstance() {
		if (instance == null) {
			instance = new History();
		}
		return instance;
	}

	/**
	 * Adds a superlist to History.
	 * @param superlist
	 */
	public void add(ArrayList<ArrayList<Task>> superlist) {
		stack.push(superlist);
	}

	/**
	 * Pops the last saved superlist from History.
	 * @return
	 */
	public ArrayList<ArrayList<Task>>get() {
		return stack.pop();
	}


	// Old implementation - ignore
	//private HashMap<TasklistEnum, ArrayList<Task>> lastSavedTasklists = new HashMap<FileType, ArrayList<Task>>();
	/**
	 * Sets History to map the category specified by filename to tasklist.
	 * If the filename does not correspond to any type (is INVALID),
	 * the tasklist will not be added to History.
	 * Storage will invoke this method for every call to saveTaskList,
	 * so Logic should not need to use this.
	 * @param filename category of the tasklist to be saved
	 * @param tasklist ArrayList of tasks to be saved

	public void set(TasklistEnum tasklistType, ArrayList<Task> tasklist) {
		lastSavedTasklists.put(tasklistType, tasklist);
	}

	/**
	 * Gets the last-saved tasklist specified by filename.
	 * An empty ArrayList is returned if the tasklist specified by filename
	 * has not been added to History yet,
	 * or if the tasklist category specified by filename is invalid.
	 * @param filename category of the last-saved tasklist
	 * @return the last-saved tasklist specified by filename

	public ArrayList<Task> get(TasklistEnum tasklistType) {
		ArrayList<Task> ret = lastSavedTasklists.get(tasklistType);
		if (ret == null) {
			ret = new ArrayList<Task>();
		}
		return ret;
	} */
}