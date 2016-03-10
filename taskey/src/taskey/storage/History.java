package taskey.storage;

import java.util.ArrayList;
import java.util.HashMap;

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
	private static History instance;
	private HashMap<FileType, ArrayList<Task>> lastSavedTasklists;

	public History() {
		lastSavedTasklists = new HashMap<FileType, ArrayList<Task>>();
	}

	/**
	 * Logic can get the History singleton.
	 * @return the history singleton
	 */
	public static History getInstance() {
		if (instance == null) {
			instance = new History();
		}
		return instance;
	}

	/*========*
	 * Setter *
	 *========*/

    /**
     * Overloaded auxiliary method that takes in the FileType enum.
     * The other method that takes in a filename string will be deprecated once we move
     * on to use the FileType enum, so classes should use this method instead.
     * @param category
     * @param tasklist
     */
	public void set(FileType category, ArrayList<Task> tasklist) {
		set(category.getFilename(), tasklist);
	}

	/**
	 * LEGACY METHOD: String filename is expected to be using the OLD FILENAMES.
	 *
	 * Storage will invoke this method for every call to saveTaskList,
	 * so Logic should not need to use this.
	 *
	 * Sets History to map the category specified by filename to tasklist.
	 * If the filename does not correspond to any type (is INVALID),
	 * the tasklist will not be added to History.
	 * @param filename category of the tasklist to be saved
	 * @param tasklist ArrayList of tasks to be saved
	 */
	public void set(String filename, ArrayList<Task> tasklist) {
		FileType tasklistCategory = FileType.getType(filename); //FileType.getType is a legacy method whose argument takes in the OLD FILENAME
		if (tasklistCategory != FileType.INVALID) {
			lastSavedTasklists.put(tasklistCategory, tasklist);
		}
	}

	/*========*
	 * Getter *
	 *========*/

	/**
	 * Logic can use this method for the undo command.
	 *
	 * Overloaded auxiliary method that takes in the FileType enum.
     * The other method that takes in a filename string will be deprecated once we move
     * on to use the FileType enum, so classes should use this method instead.
	 * @param category
	 * @return
	 */
	public ArrayList<Task> get(FileType category) {
		return get(category.getFilename());
	}

	/**
	 * LEGACY METHOD: String filename is expected to be using the OLD FILENAMES.
	 *
	 * Gets the last-saved tasklist specified by filename.
	 * An empty ArrayList is returned if the tasklist specified by filename
	 * has not been added to History yet,
	 * or if the tasklist category specified by filename is invalid.
	 * @param filename category of the last-saved tasklist
	 * @return the last-saved tasklist specified by filename
	 */
	public ArrayList<Task> get(String filename) {
		FileType tasklistCategory = FileType.getType(filename); //FileType.getType is a legacy method whose argument takes in the OLD FILENAME
		ArrayList<Task> ret = lastSavedTasklists.get(tasklistCategory);
		if (ret == null) {
			ret = new ArrayList<Task>();
		}
		return ret;
	}
}