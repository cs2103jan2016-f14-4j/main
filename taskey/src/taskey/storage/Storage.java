package taskey.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.Task;

/**
 * This is a facade class.
 * @author Dylan
 */
public class Storage {
	private static Storage instance; //DEPRECATED - DO NOT USE
	private StorageLoader loader;
	private StorageSaver saver;
	private History history;
	private File directory;

	private static final String DEFAULT_DIRECTORY = "Taskey savefiles";
	private static final String FILENAME_CONFIG = "last_used_directory.taskeyconfig";
	private static final String FILENAME_TAGS = "USER_TAG_DB.taskey";

	public enum TasklistEnum {
		// Index 0 is to  be ignored
	    PENDING		("PENDING.taskey", 1),
	    EXPIRED		("EXPIRED.taskey", 2),
	    GENERAL		("GENERAL.taskey", 3),
	    DEADLINE	("DEADLINE.taskey", 4),
	    EVENT		("EVENT.taskey", 5),
	    COMPLETED	("COMPLETED.taskey", 6);

	    private final String filename;
	    private final int index;

	    TasklistEnum(String filename, int index) {
	        this.filename = filename;
	        this.index = index;
	    }

	    public String filename() {
	    	return filename;
	    }
	    public int index() {
	    	return index;
	    }
	}

	/**
	 * For testing of the Storage class. This is how Logic will interface with Storage.
	 * @param args
	 */
	public static void main(String args[]) {
		// Get the Storage singleton instance
		// The default or last-used directory is automatically set in the constructor method.
		Storage storage = new Storage();

		// Can optionally set the directory again, if requested by user.
		//storage.setDirectory(DEFAULT_DIRECTORY + "\\fubar");

		// Initialize - tasklist
		System.out.println("\nInitial load ======================================");
		ArrayList<ArrayList<Task>> loadedLists = storage.loadAllTasklists();
		print(loadedLists);

		// Initialize - tags hashmap
		HashMap<String, Integer> loadedTags = storage.loadTags();
		System.out.print("" + loadedTags.containsKey("foo") + loadedTags.containsKey("bar") + loadedTags.containsKey("foobar"));

		// Load after save - tasklist
		System.out.println("\n\nLoad after save ===================================");
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();
		ArrayList<Task> ignoredList = new ArrayList<Task>();
		ArrayList<Task> pendingList = new ArrayList<Task>();
		pendingList.add(new Task("1. This is a test task"));
		pendingList.add(new Task("2. This is a test task"));
		pendingList.add(new Task("3. This is a test task"));
		superlist.add(ignoredList);
		superlist.add(pendingList);
		try {
			storage.saveAllTasklists(superlist);
		} catch (StorageException e) {
			System.err.println(e.getMessage());
			print(e.getLastModifiedTasklists());
		}
		loadedLists = storage.loadAllTasklists();
		print(loadedLists);

		// Load after save - tagsmap
		HashMap<String, Integer> tags = new HashMap<String, Integer>();
		tags.put("foo", 1); tags.put("bar", 1); tags.put("foobar", 1);
		storage.saveTags(tags);
		loadedTags = storage.loadTags();
		System.out.print("" + loadedTags.containsKey("foo") + loadedTags.containsKey("bar") + loadedTags.containsKey("foobar"));
	}

	private static void print(ArrayList<ArrayList<Task>> lists) {
		for (ArrayList<Task> list : lists) {
			for (Task t : list)
				System.out.println(t);
		}
	}

    /*=============*
     * Constructor *
     *=============*/

	/**
	 * Storage constructor and initializer.
	 * Attempts to load the last used directory after Storage is newly instantiated.
	 * If none was found, the DEFAULT_DIRECTORY will be used instead.
	 */
	public Storage() {
    		loader = new StorageLoader();
    		saver = new StorageSaver();
    		history = History.getInstance();
    		directory = loader.loadDirectory(FILENAME_CONFIG);

    		if (directory == null) {
        		System.out.println("{Setting default directory}"); //log info
        		this.setDirectory(DEFAULT_DIRECTORY);
    		} else {
        		System.out.println("{Storage directory loaded}"); //log info
    			this.setDirectory(directory.getPath()); //need to explicitly set directory after loading it
    		}
    }

	/**
	 * DEPRECATED -- DO NOT USE.
	 * This is here in case Parser needs to call Storage directly to save the user tags database.
	 * @return The Storage singleton.
	 */
    public static Storage getInstance() {
    	if (instance == null) {
    		instance = new Storage();
    		instance.loader = new StorageLoader();
    		instance.saver = new StorageSaver();
    		instance.history = History.getInstance();

    		if ((instance.directory = instance.loader.loadDirectory(FILENAME_CONFIG)) == null) {
        		System.out.println("{Setting default directory}"); //log info
        		instance.setDirectory(DEFAULT_DIRECTORY);
    		} else {
        		System.out.println("{Storage directory loaded}"); //log info
        		instance.setDirectory(instance.directory.getPath()); //need to explicitly set directory after loading it
    		}
    	}
    	return instance;
    }

    /*======================*
     * Load/Save task lists *
     *======================*/

    /**
	 * Returns the superlist of tasklists.
	 * Logic calls this on program startup.
	 * Post-cond: the lists in superlist are in the same order as the enum constants in TasklistEnum.
	 *            These lists are read from disk and hence do not include the THIS_WEEK list.
	 * @return the list of tasklists
	 */
	public ArrayList<ArrayList<Task>> loadAllTasklists() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (TasklistEnum e : TasklistEnum.values()) {
			File src = new File(directory, e.filename());
			ArrayList<Task> loadedList = loader.loadTasklist(src);
			superlist.add(loadedList);
		}

		return superlist;
	}

	/**
	 * Saves the superlist of tasklists to disk.
	 * Pre-cond: Starting from index 1, the lists in superlist are in the same order as the enum constants in TasklistEnum.
	 * 			 Index 0 is reserved for the THIS_WEEK list and is not saved to disk because it is time-dependent.
	 * @param superlist the taskLists field passed directly from Logic
	 * @throws StorageException
	 */
	public void saveAllTasklists(ArrayList<ArrayList<Task>> superlist) throws StorageException {
		assert (superlist.size() == 7); //TODO assert(false) doesn't work

		for (TasklistEnum e : TasklistEnum.values()) {
			if (e.index() >= superlist.size()) { //check for when testing in main method
				break;
			}
			ArrayList<Task> listToSave = superlist.get(e.index());
			File dest = new File(directory, e.filename());
			saver.saveTasklist(listToSave, dest);
		}

		history.add(superlist); //only if all the lists were successfully saved
	}

    /*================*
     * Save/load tags *
     *================*/

	/**
     * Returns the HashMap containing the user-defined tags read from file.
     * An empty HashMap is returned if the tags file was not found.
     * @return the HashMap read from file, or an empty HashMap if the file was not found.
     */
    public HashMap<String, Integer> loadTags() {
    	File src = new File(directory, FILENAME_TAGS);
    	return loader.loadTags(src);
    }

    /**
     * Saves the given HashMap containing user-defined tags to JSON file.
     * @param tags HashMap that maps the tag strings to their corresponding multiplicities
     * @return true if successful; false otherwise.
     */
    public boolean saveTags(HashMap<String, Integer> tags) {
    	File dest = new File(directory, FILENAME_TAGS);
    	return saver.saveTags(tags, dest);
    }

    /*=====================*
     * Get/set directories *
     *=====================*/

    /**
     * Returns the current storage directory.
     * When the user asks to change directory, Logic can return it as feedback.
     * @return Absolute path of the default or user-set directory.
     */
    public String getDirectory() {
    	return directory.getAbsolutePath();
    }

    /**
     * Sets the storage directory to the given pathname string after checking that the path is valid.
     * This method is invoked by Logic, should the end user request to change it.
     * @return True if the directory already exists or was just created;
     * 		   false if the path is invalid due to illegal characters (e.g. *) or reserved words (e.g. CON) in Windows.
     * @param pathname can be a relative or absolute path
     */
    public boolean setDirectory(String pathname) {
    	File dir = new File(pathname);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (dir.isDirectory()) {
			boolean shouldSave = isNewDirectory(dir);
			directory = dir;
			System.out.println("{Storage directory set} " + getDirectory()); //debug info
			if (shouldSave) {
	    		saver.saveDirectory(directory, FILENAME_CONFIG);
	    	}
			return true;
		} else {
			return false;
		}
    }

    /**
     * Private helper method. Checks whether dir is a new/different directory that should be saved.
     * This check is done to avoid unnecessary calls to saveDirectory().
     * @param dir the new candidate directory
     * @return true if dir is a new directory; false otherwise.
     */
    private boolean isNewDirectory(File dir) {
    	// Disregard dir if it's the default directory
    	if (!dir.getAbsolutePath().equals( new File(DEFAULT_DIRECTORY).getAbsolutePath() )) {
    		// If directory hasn't been set
    		if (directory == null) {
    			return true;
    		}
    		// If the new dir is not the same as the old directory
    		else if (!dir.getAbsolutePath().equals(directory.getAbsolutePath())) {
    			return true;
    		}
    	}
    	return false;
    }

}