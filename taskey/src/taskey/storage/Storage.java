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
	private StorageReader storageReader;
	private StorageWriter storageWriter;
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
		try {
			storage.saveTags(tags);
		} catch (StorageException e) {
			System.err.println(e.getMessage());
			System.out.println(e.getLastModifiedTagMap());
		}
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
	 * Attempts to load and set the last used directory.
	 * If none was found, DEFAULT_DIRECTORY will be set instead.
	 * Post-condition: all the member fields of Storage have been instantiated.
	 */
	public Storage() {
    		storageReader = new StorageReader();
    		storageWriter = new StorageWriter();
    		history = History.getInstance();
    		directory = storageReader.loadDirectory(FILENAME_CONFIG);

    		if (directory == null) {
        		System.out.println("{Setting default directory}"); //debug info
        		setDirectory(DEFAULT_DIRECTORY);
    		} else {
        		System.out.println("{Storage directory loaded}"); //debug info
    			setDirectory(directory.getPath()); //must call setDirectory to create the folder path
    		}
    }

    /*=====================*
     * Load/Save tasklists *
     *=====================*/
    /**
	 * Returns the superlist of tasklists loaded from Storage.
	 * Logic calls this on program startup.
	 * <p>Post-conditions:
	 * <br>- The lists in the returned superlist are in the same order as the enum constants in TasklistEnum.
	 * <br>- These lists are read from disk and hence do not include the THIS_WEEK list.
	 * @return the list of tasklists read from disk
	 */
	public ArrayList<ArrayList<Task>> loadAllTasklists() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (TasklistEnum e : TasklistEnum.values()) {
			File src = new File(directory, e.filename());
			ArrayList<Task> loadedList = storageReader.loadTasklist(src);
			superlist.add(loadedList);
		}

		//history.add(superlist); //Logic will add the initial loaded list to History
		return superlist;
	}

	/**
	 * Saves the superlist of tasklists to Storage.
	 * Logic calls this after every operation.
	 * <p>Pre-conditions:
	 * <br>- Starting from index 1, the lists in the given superlist
	 * 		 are in the same order as the enum constants in TasklistEnum.
	 * <br>- Index 0 is reserved for the THIS_WEEK list and is not saved to disk because it is time-dependent.
	 * @param superlist the list of tasklists to be saved
	 * @throws StorageException contains the last saved tasklist
	 */
	public void saveAllTasklists(ArrayList<ArrayList<Task>> superlist) throws StorageException {
		assert (superlist.size() == 7); //TODO assert(false) doesn't work

		for (TasklistEnum e : TasklistEnum.values()) {
			if (e.index() == superlist.size()) { //check for when testing in main method
				break;
			}
			ArrayList<Task> listToSave = superlist.get(e.index());
			File dest = new File(directory, e.filename());
			storageWriter.saveTasklist(listToSave, dest);
		}

		history.add(superlist); //only if all the lists were successfully saved
	}


    /*================*
     * Load/Save tags *
     *================*/
	/**
     * Returns a HashMap the containing user-defined tags loaded from Storage.
     * An empty HashMap is returned if the tags file was not found.
     * @return the HashMap read from file, or an empty HashMap if the file was not found
     */
    public HashMap<String, Integer> loadTags() {
    	File src = new File(directory, FILENAME_TAGS);
    	return storageReader.loadTags(src);
    }

    /**
     * Saves the given HashMap containing user-defined tags to Storage.
     * @param tags the HashMap that maps tag strings to their corresponding multiplicities
     * @throws StorageException contains the last saved tagmap
     */
    public void saveTags(HashMap<String, Integer> tags) throws StorageException {
    	File dest = new File(directory, FILENAME_TAGS);
    	storageWriter.saveTags(tags, dest);
    	history.add(tags);
    }

    /*=====================*
     * Set/get directories *
     *=====================*/
    /**
     * Returns the current storage directory.
     * When the user asks to change directory, Logic can return it as feedback.
     * @return absolute path of the default or user-set directory
     */
    public String getDirectory() {
    	return directory.getAbsolutePath();
    }

    /**
     * Sets the storage directory to the given pathname string after checking that the path is valid.
     * Creates the directory if it does not exist yet.
     * Saves the new directory if it
     * This method is invoked by Logic, should the end user request to change it.
     * @return true if successful;
     * 		   false if the path is invalid due to illegal characters (e.g. *) or reserved words (e.g. CON in Windows)
     * @param pathname can be a relative or absolute path
     */
    public boolean setDirectory(String pathname) {
    	File dir = new File(pathname);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (dir.isDirectory()) {
			boolean shouldSave = isNewDirectory(dir); //compares the new directory with the old to see if it should be saved
			directory = dir;
			System.out.println("{Storage directory set} " + getDirectory()); //debug info
			if (shouldSave) {
	    		storageWriter.saveDirectory(directory, FILENAME_CONFIG);
	    		System.out.println("{Storage directory saved}"); //debug info
	    	}
			//TODO method to move files
			return true;
		} else {
			return false;
		}
    }

    /**
     * Private helper method. Checks whether dir is a new/different directory that should be saved.
     * This check is done to avoid unnecessary calls to saveDirectory().
     * @param dir the new candidate directory
     * @return true if dir is a new directory; false otherwise
     */
    private boolean isNewDirectory(File dir) {
    	// If directory == null, then dir must be the default directory, which we do not want to save
    	if (directory != null) {
			// If the new dir is different from the old directory
    		if (! dir.getAbsolutePath().equals(directory.getAbsolutePath()) ) {
    			return true;
    		}
    	}
    	return false;
    }

}