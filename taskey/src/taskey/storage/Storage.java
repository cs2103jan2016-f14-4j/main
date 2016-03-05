package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.Task;

/**
 * @author Dylan
 */
public class Storage {
	static final String DEFAULT_DIRECTORY = "bin" + File.separator + "taskey" + File.separator + "storage";
	static final String CONFIG_FILENAME = "Taskey_Storage_config_file";

	private static Storage instance = null;
	private File directory;
	private File savefile; //pointer to the current file under operation

	/**
	 * For testing of the Storage class. This is how Logic will interface with Storage.
	 * @param args
	 */
	public static void main(String args[]) {
		// Get the Storage singleton instance
		// The default directory was automatically set in the getInstance() method.
		Storage storageTest = Storage.getInstance();

		// Can optionally set the directory again, if requested by user.
		// Examples of invalid paths in Windows
		//System.out.println(storageTest.setDirectory("CON"));
		//System.out.println(storageTest.setDirectory("foo|bar"));
		//storageTest.setDirectory(DEFAULT_DIRECTORY + "\\fubar");

		// Initial load
		ArrayList<Task> loadedTaskList = storageTest.getTaskList("TEST_TASKLIST");
		for (Task t : loadedTaskList) {
			System.out.println(t);
		}

		// Create a simulated list of tasks and save it to file
		ArrayList<Task> testTaskList = new ArrayList<Task>();
		testTaskList.add(new Task("1. This is a test task"));
		testTaskList.add(new Task("2. This is a test task"));
		testTaskList.add(new Task("3. This is a test task"));
		try {
			storageTest.saveTaskList(testTaskList, "TEST_TASKLIST");
		} catch (IOException e) {
			System.err.println("Error saving task list to file.");
			e.printStackTrace();
		}

		// Load after save
		loadedTaskList = storageTest.getTaskList("TEST_TASKLIST");
		for (Task t : loadedTaskList) {
			System.out.println(t);
		}
	}

    /*=============*
     * Constructor *
     *=============*/

	/**
	 * Returns the Storage singleton.
	 * Also sets the default directory after Storage is newly instantiated.
	 * @return The storage singleton.
	 */
    public static Storage getInstance() {
    	if (instance == null) {
    		instance = new Storage();
    		if (instance.loadDirectory() == false) {
        		instance.setDirectory(DEFAULT_DIRECTORY);
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
    	setSavefile(filename);
    	ArrayList<Task> tasks;
		try {
			tasks = readFromFile( new TypeToken<ArrayList<Task>>(){} );
			System.out.println("<Loaded> " + savefile.getPath());
		} catch (FileNotFoundException e) {
			//System.out.println(e.getMessage());
			tasks = new ArrayList<Task>();
		}
    	return tasks;
    }

    /**
     * Private method. Reads tasks from JSON file.
     * @return ArrayList of Task objects generated from the JSON file.
     * @throws FileNotFoundException
     */
    private <T> T readFromFile(TypeToken<T> typeToken) throws FileNotFoundException {
    	Gson gson = new Gson();
    	FileReader reader = new FileReader(savefile);
		T object = gson.fromJson(reader, typeToken.getType());
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
    public void saveTaskList(ArrayList<Task> tasks, String filename) throws IOException {
		setSavefile(filename);
    	writeToFile(tasks);
    	//TODO When exception is encountered during write-after-modified, return/throw the last-modified list to Logic
    }

    /**
     * Private method. Writes an Object to JSON file.
     * @param object to be written as a JSON file.
     * @throws IOException
     */
    private <T> void writeToFile(T object) throws IOException {
    	Gson gson = new Gson();
    	String json = gson.toJson(object);
    	FileWriter writer = new FileWriter(savefile);
    	writer.write(json);
    	writer.close();
    }

    /*=============*
     * Directories *
     *=============*/

    /**
     * Returns the current storage directory.
     * When the user asks to change directory, Logic can return it as feedback.
     * @return Absolute path of the default or user-set directory.
     */
    public String getDirectory() {
    	return directory.getPath();
    }

    /**
     * Sets the storage directory given by the pathname string.
     * This method is invoked by Logic, should the end user request to change it.
     * @return True if the directory already exists or was just created;
     * 		   false if directory name is invalid (directory remains unchanged).
     * @param pathname can be a relative or absolute path
     */
    public boolean setDirectory(String pathname) {
    	File dir = new File(pathname);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (dir.isDirectory()) {
			directory = dir;
			System.out.println("<Storage directory set> " + getDirectory());
	    	if (!pathname.equals(DEFAULT_DIRECTORY)) {
	    		saveDirectory();
	    	}
			return true;
		} else {
			return false;
		}
    }

    /**
     * Private method. Saves the current directory to file.
     * @return true if save was succesful; false otherwise.
     */
    private boolean saveDirectory() {
    	savefile = new File(System.getProperty("user.dir"), CONFIG_FILENAME);
    	try {
    		writeToFile(directory);
    		System.out.println("<Storage directory saved>");
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    private boolean loadDirectory() {
    	savefile = new File(System.getProperty("user.dir"), CONFIG_FILENAME);
    	try {
    		directory = readFromFile( new TypeToken<File>(){} );
    		System.out.println("<Storage directory loaded>");
    		return true;
    	} catch (FileNotFoundException e) {
    		System.out.println(e.getMessage());
    		return false;
    	}
    }

    /**
     * Private helper method invoked by the saveTaskList and getTaskList methods.
     * Before each save/load operation, Storage will set savefile to point to the current tasklist file.
     * @param filename the tasklist file to be saved/loaded
     */
    private void setSavefile(String filename) {
		File file = new File(directory, filename);
		savefile = file;
    }
}