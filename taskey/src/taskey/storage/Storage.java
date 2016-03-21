package taskey.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.TagCategory;
import taskey.logic.Task;

/**
 * This class exposes public IO methods for saving tasklists and tags.
 * It also manages the storage directory.
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
	private static final String FILENAME_EXTENSION = ".taskey";

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
	    
	    public static boolean contains(String filename) {
	    	for (TasklistEnum e : TasklistEnum.values()) {
	    		if (e.filename.equals(filename)) {
	    			return true;
	    		}
	    	}
	    	return false;
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
		//System.out.println(storage.setDirectory(DEFAULT_DIRECTORY + "\\fubar"));

		// Initialize - tasklist
		System.out.println("\nInitial load ======================================");
		ArrayList<ArrayList<Task>> loadedLists = storage.loadAllTasklists();
		print(loadedLists);

		// Initialize - tags hashmap
		HashMap<String, Integer> loadedTags = storage.loadTags();
		System.out.print("" + loadedTags.containsKey("foo") + loadedTags.containsKey("bar") + loadedTags.containsKey("foobar"));

		// Load after save - tasklists
		System.out.println("\n\nLoad after save ===================================");
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();
		ArrayList<Task> ignoredList = new ArrayList<Task>();
		ArrayList<Task> pendingList = new ArrayList<Task>();
		pendingList.add(new Task("1. This is a test task"));
		pendingList.add(new Task("2. This is a test task"));
		pendingList.add(new Task("3. This is a test task"));
		superlist.add(ignoredList);
		superlist.add(pendingList);
		for (int i=0; i<5; i++) {
			superlist.add(new ArrayList<Task>());
		}
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
	 * Attempts to load and set the last used directory.
	 * If none was found, DEFAULT_DIRECTORY will be set instead.
	 * Post-condition: all the member fields of Storage have been instantiated.
	 */
	public Storage() {
		storageReader = new StorageReader();
		storageWriter = new StorageWriter();
		history = new History();
		directory = storageReader.loadDirectory(FILENAME_CONFIG);

		if (directory == null) {
    		System.out.println("{Setting default directory}");
    		setDirectory(DEFAULT_DIRECTORY);
		} else {
    		System.out.println("{Storage directory loaded}");
			setDirectory(directory.getPath()); //must call setDirectory to create the folder path
		}
    }

    public History getHistory() {
    	return history;
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
	 * 		 must be in the same order as the enum constants in TasklistEnum.
	 * <br>- Index 0 is reserved for the THIS_WEEK list and is not saved to disk because it is time-dependent.
	 * @param superlist the list of tasklists to be saved
	 * @throws StorageException contains the last saved tasklist
	 */
	public void saveAllTasklists(ArrayList<ArrayList<Task>> superlist) throws StorageException {
		assert (superlist.size() == 7);

		for (TasklistEnum e : TasklistEnum.values()) {
			ArrayList<Task> listToSave = superlist.get(e.index());
			File dest = new File(directory, e.filename());
			try {
				storageWriter.saveTasklist(listToSave, dest);
			} catch (IOException ioe) {
				// When exception is encountered during write-after-modified, throw the last-modified superlist to Logic.
				assert (!history.isEmpty());
				throw new StorageException(ioe, history.peek());
			}
		}
		history.add(superlist); //to Hubert: moved the check to History's add method
	}


    /*==============================*
     * Load/Save tags - new methods *
     *==============================*/
	/**
     * Returns the ArrayList of Tags loaded from Storage.
     * An empty ArrayList is returned if the tags file was not found.
     * @return the ArrayList of user-defined tags, or an empty ArrayList if the file was not found
	 */
	public ArrayList<TagCategory> loadTaglist() {
		File src = new File(directory, FILENAME_TAGS);
		return storageReader.loadTaglist(src);
	}
	
    /**
     * Saves the given ArrayList of Tags to Storage.
     * @param tags the ArrayList containing the user-defined tags
     * @throws IOException in case Logic wants to handle the exception
     */
    public void saveTaglist(ArrayList<TagCategory> tags) throws IOException {
    	assert (tags != null);
    	File dest = new File(directory, FILENAME_TAGS);
		storageWriter.saveTaglist(tags, dest);
    	history.addTagList(tags);
		//return true; //unnecessary?
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
     * This method is invoked by Logic, should the end user request to change it.
     * <p>Post-conditions:
     * <br>- Creates the directory if it does not exist yet.
     * <br>- Saves the new directory setting to a .taskeyconfig file in "user.dir".
     * <br>- Moves the .taskey storage files from the existing directory to the new one.
     * @return True if the new directory was successfully created and/or set;
     * 		   <br>False if the path is invalid due to illegal characters (e.g. *), 
     * 		   reserved words (e.g. CON in Windows), or nonexistent root drive letters.
     * @param pathname can be a relative or absolute path
     */
    public boolean setDirectory(String pathname) {
    	File dir = new File(pathname);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (dir.isDirectory()) {
			// Compare the new directory with the old to see if it should be saved and moved
			if (isNewDirectory(dir)) {
	    		storageWriter.saveDirectory(dir, FILENAME_CONFIG);
				moveFiles(directory.getAbsoluteFile(), dir.getAbsoluteFile());
	    	}
			
			directory = dir;
			System.out.println("{Storage directory set} " + getDirectory());
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

	/**
	 * Moves the .taskey savefiles in the existing directory to the new one.
	 * @param srcDir the source directory
	 * @param destDir the destination diectory
	 * @returns TODO: error handling?
	 */
    private boolean moveFiles(File srcDir, File destDir) {
    	boolean isSuccessful = true;
    	    	
    	for (File srcFile : srcDir.listFiles()) {
        	if ( srcFile.getName().endsWith(FILENAME_EXTENSION) ) {
        		Path srcPath = srcFile.toPath();
        		Path destPath = destDir.toPath().resolve(srcFile.getName());

            	try {
        			Files.move(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
        		} catch (IOException e) {
        			isSuccessful = false;
        			e.printStackTrace();
        		}
        	}
    	}
    	
		if (isSuccessful) {
			System.out.println("{Storage files moved}");
		} else {
			System.out.println("Error moving directory");
		}
    	return isSuccessful;
    }
}