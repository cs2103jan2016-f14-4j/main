package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.Task;

/**
 * @author Dylan
 */
public class Storage {
	private static Storage instance = null;
	private File directory;

	static final String DEFAULT_DIRECTORY = "Taskey savefiles";
	static final String FILENAME_CONFIG = "[Taskey_config] last-used directory";
	static final String FILENAME_TAGS = "TAGS";

	/**
	 * For testing of the Storage class. This is how Logic will interface with Storage.
	 * @param args
	 */
	public static void main(String args[]) {
		// Get the Storage singleton instance
		// The default or last-used directory is automatically set in the getInstance() method.
		Storage storageTest = Storage.getInstance();

		// Can optionally set the directory again, if requested by user.
		//storageTest.setDirectory(DEFAULT_DIRECTORY + "\\fubar");

		// Initialize - tasklist
		System.out.println("\nInitial load ======================================");
		ArrayList<Task> loadedTaskList = storageTest.getTaskList("TEST_TASKLIST");
		for (Task t : loadedTaskList) {
			System.out.println(t);
		}

		// Initialize - tags hashmap
		HashMap<String, Integer> loadedTags = storageTest.loadTags();
		System.out.print("" + loadedTags.containsKey("foo") + loadedTags.containsKey("bar") + loadedTags.containsKey("foobar"));

		// Load after save - tasklist
		System.out.println("\n\nLoad after save ===================================");
		ArrayList<Task> testTaskList = new ArrayList<Task>();
		testTaskList.add(new Task("1. This is a test task"));
		testTaskList.add(new Task("2. This is a test task"));
		testTaskList.add(new Task("3. This is a test task"));
		storageTest.saveTaskList(testTaskList, "TEST_TASKLIST");
		loadedTaskList = storageTest.getTaskList("TEST_TASKLIST");
		for (Task t : loadedTaskList) {
			System.out.println(t);
		}

		// Load after save - tags hashmap
		HashMap<String, Integer> tags = new HashMap<String, Integer>();
		tags.put("foo", 1); tags.put("bar", 1); tags.put("foobar", 1);
		storageTest.saveTags(tags);
		loadedTags = storageTest.loadTags();
		System.out.print("" + loadedTags.containsKey("foo") + loadedTags.containsKey("bar") + loadedTags.containsKey("foobar"));
	}

    /*=============*
     * Constructor *
     *=============*/

	/**
	 * Returns the Storage singleton.
	 * Attempts to load the last used directory after Storage is newly instantiated.
	 * If none was found, the DEFAULT_DIRECTORY will be used instead.
	 * @return The Storage singleton.
	 */
    public static Storage getInstance() {
    	if (instance == null) {
    		instance = new Storage();
    		if (instance.loadDirectory() == false) {
        		instance.setDirectory(DEFAULT_DIRECTORY);
    		} else {
    			instance.setDirectory(instance.directory.getPath()); //need to explicitly set directory after loading it
    		}
    	}
    	return instance;
    }

    /*================*
     * Load task list *
     *================*/

    /**
     * Returns an ArrayList of Task objects read from the file specified by the given filename String.
     * An empty ArrayList is returned if the file was not found.
     * This method is invoked by Logic.
     * @param filename name of the file to be read
     * @return The task list loaded from file; or an empty list if the file does not exist.
     */
    public ArrayList<Task> getTaskList(String filename) {
    	File file = new File(directory, filename);
    	ArrayList<Task> tasks;
		try {
			tasks = readFromFile(file, new TypeToken<ArrayList<Task>>() {});
			System.out.println("{Tasklist loaded} " + filename); //debug info
		} catch (FileNotFoundException e) {
			System.out.println("{Tasklist not found} " + filename); //debug info
			tasks = new ArrayList<Task>();
		}
    	return tasks;
    }

    /**
     * Private method. Deserializes the JSON specified by the abstract file into an object of the specified type.
     * @param file JSON file to be read
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @return An object of type T generated from the JSON file.
     * @throws FileNotFoundException
     */
    private <T> T readFromFile(File file, TypeToken<T> typeToken) throws FileNotFoundException {
    	FileReader reader = new FileReader(file);
    	Gson gson = new Gson();
		T object = gson.fromJson(reader, typeToken.getType()); //TODO Handle type safety
		return object;
    }

    /*================*
     * Save task list *
     *================*/

    /**
     * Saves an ArrayList of Task objects to the file specified by the given filename String.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * This method is invoked by Logic.
     * @param tasks list of Task objects for saving
     * @param filename name the file will be saved as
     * @throws IOException
     */
    public void saveTaskList(ArrayList<Task> tasks, String filename) {
    	File file = new File(directory, filename);
    	try {
    		writeToFile(file, tasks, new TypeToken<ArrayList<Task>>() {});
    	} catch (IOException e) {
    		//TODO When exception is encountered during write-after-modified, return/throw the last-modified list to Logic
    	}
    }

    /**
     * Private method. Serializes the specified object of the specified type into its equivalent JSON representation.
     * @param file to be written as JSON
     * @param object of type T to be serialized
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @throws IOException
     */
    private <T> void writeToFile(File file, T object, TypeToken<T> typeToken) throws IOException {
    	FileWriter writer = new FileWriter(file);
    	Gson gson = new Gson();
    	String json = gson.toJson(object, typeToken.getType());
    	writer.write(json);
    	writer.close();
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
	    		saveDirectory();
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

    /*=======================*
     * Save/load directories *
     *=======================*/

    /**
     * Private method. Saves the current directory to file in "user.dir".
     * @return true if save was succesful; false otherwise.
     */
    private boolean saveDirectory() {
    	File config = new File(FILENAME_CONFIG);
    	try {
    		writeToFile(config, directory, new TypeToken<File>() {});
    		System.out.println("{Storage directory saved}"); //debug info
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    /**
     * Private method. Looks for, and loads, the last-saved directory from file in "user.dir".
     * @return true if the directory was successfully loaded; false otherwise.
     */
    private boolean loadDirectory() {
    	File config = new File(FILENAME_CONFIG);
    	try {
    		directory = readFromFile(config, new TypeToken<File>() {});
    		System.out.println("{Storage directory loaded}"); //debug info
    		return true;
    	} catch (FileNotFoundException e) {
    		System.out.println("{Storage config file not found; setting default directory}"); //debug info
    		return false;
    	}
    }

    /*================*
     * Save/load tags *
     *================*/

    /**
     * Saves the given HashMap containing user-defined tags to JSON file.
     * @param tags HashMap that maps the tag strings to their corresponding multiplicities
     * @return true if successful; false otherwise.
     */
    public boolean saveTags(HashMap<String, Integer> tags) {
    	File file = new File(directory, FILENAME_TAGS);
    	try {
    		writeToFile(file, tags, new TypeToken<HashMap<String, Integer>>() {});
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    /**
     * Returns the HashMap containing user-defined tags read from JSON file.
     * An empty HashMap is returned if the tags file was not found.
     * @return the HashMap read from file, or an empty HashMap if the file was not found.
     */
    public HashMap<String, Integer> loadTags() {
    	File file = new File(directory, FILENAME_TAGS);
    	HashMap<String, Integer> tags;
    	try {
    		tags = readFromFile(file, new TypeToken<HashMap<String, Integer>>() {});
    		System.out.println("{UserTagDatabase loaded} " + FILENAME_TAGS); //debug info
    	} catch (FileNotFoundException e) {
    		System.out.println("{UserTagDatabase not found} " + FILENAME_TAGS); //debug info
    		tags = new HashMap<String, Integer>();
    	}
    	return tags;
    }
}