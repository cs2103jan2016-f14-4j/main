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
 *
 * @author Dylan
 *
 */
public class Storage {
	public static final String DEFAULT_DIRECTORY = "bin" + File.separator + "taskey" + File.separator + "storage";

	private static Storage instance = null;
	private File directory = new File(DEFAULT_DIRECTORY);
	private File savefile;

	/**
	 * For testing of the Storage class. This is how Logic will interface with Storage.
	 * @param args
	 */
	public static void main(String args[]) {
		// Get the Storage singleton instance
		Storage storageTest = Storage.getInstance();

		// The default directory was automatically set in the getInstance() method.
		// Can optionally set the directory again, if requested by user.
		System.out.println(storageTest.getDirectory());

		// Create a simulated list of tasks
		ArrayList<Task> testTaskList = new ArrayList<Task>();
		testTaskList.add(new Task("1. This is a test task"));
		testTaskList.add(new Task("2. This is a test task"));
		testTaskList.add(new Task("3. This is a test task"));

		// Save the task list to file, specifying the list's name
		try {
			storageTest.saveTaskList(testTaskList, "TEST_TASKLIST");
		} catch (IOException e) {
			System.err.println("Error saving task list to file.");
			e.printStackTrace();
		}

		// Attempt to load from an empty directory (file not found)
		storageTest.setDirectory(DEFAULT_DIRECTORY + "/foo");
		ArrayList<Task> loadedTaskList = storageTest.getTaskList("TEST_TASKLIST");
		for (Task t : loadedTaskList) {
			System.out.println(t);
		}

		// Change back to the default directory
		storageTest.setDirectory(DEFAULT_DIRECTORY);
		loadedTaskList = storageTest.getTaskList("TEST_TASKLIST");
		for (Task t : loadedTaskList) {
			System.out.println(t);
		}
	}

    /*=============*
     * Constructor *
     *=============*/

	/**
	 * Returns the single instance of storage.
	 * Also sets the default directory after creating the storage singleton.
	 * @return storage singleton
	 */
    public static Storage getInstance() {
    	if (instance == null) {
    		instance = new Storage();
    		instance.setDirectory(DEFAULT_DIRECTORY);
    	}
    	return instance;
    }

    /*=====================*
     * Save/load task list *
     *=====================*/

    /**
     * Logic passes a task list to Storage for saving.
     * @param tasks The list of task objects for saving.
     * @param taskCategory The category that the task list belongs to.
     * 					   i.e. ALL, FLOATING, DEADLINE, EVENT, DONE, EXPIRED
     * @throws IOException
     */
    public void saveTaskList(ArrayList<Task> tasks, String taskCategory) throws IOException {
		setSavefile(taskCategory);
    	writeToFile(tasks);
    	//TODO When exception is encountered during write, return/throw the last-modified list to Logic
    }

    /**
     * Logic gets a task list from Storage.
     * @param taskCategory The category of tasks that Logic wants to load.
     * @return The task list loaded from file, specified by taskCategory;
     * 			an empty list is returned if file is not found.
     */
    public ArrayList<Task> getTaskList(String taskCategory) {
    	setSavefile(taskCategory);
    	ArrayList<Task> tasks;
		try {
			tasks = readFromFile();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			tasks = new ArrayList<Task>();
		}
    	return tasks;
    }

    /*===========================*
     * Public accessors/mutators *
     *===========================*/

    /**
     * When the user changes directory, Logic can return it as feedback.
     * @return Absolute path of the last saved/loaded file.
     */
    public String getDirectory() {
    	return directory.getAbsolutePath();
    }

    /**
     * Logic can set the storage directory, if the user requests to change it.
     * @return True if directory now exists and is indeed a directory;
     * 		   false if directory name is invalid (previous directory remains unchanged).
     * @param pathname (can be a relative or absolute path)
     */
    public boolean setDirectory(String pathname) {
    	File dir = new File(pathname);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (dir.isDirectory()) {
			directory = dir;
			if (savefile != null) { //check for when setDirectory is called before setSavefile
				setSavefile(savefile.getName()); //need to update savefile after changing directory
			}
			return true;
		} else {
			return false;
		}
    }

    /*=================*
     * Private methods *
     *=================*/

    /**
     * Storage will set the filename according to which tasklist is being updated, before each read/write operation.
     * @param filename
     * @return true if the savefile's name and path has been updated; false if the filename is invalid
     */
    private boolean setSavefile(String filename) {
		File file = new File(directory, filename);

		try {
			file.getCanonicalPath();
		} catch (IOException e) {
			return false;
		}

		savefile = file;
		return true;
    }

    /**
     * Writes tasks to JSON file.
     * @param ArrayList<Task> tasks
     * @throws IOException
     */
    private void writeToFile(ArrayList<Task> tasks) throws IOException {
    	Gson gson = new Gson();
    	String json = gson.toJson(tasks);
    	FileWriter writer = new FileWriter(savefile);
    	writer.write(json);
    	writer.close();
    	/*
    	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile));
    	oos.writeObject(tasks);
    	oos.close();
    	*/
    }

    /**
     * Reads tasks from JSON file.
     * @return ArrayList<Task> tasks
     * @throws FileNotFoundException
     */
    private ArrayList<Task> readFromFile() throws FileNotFoundException {
    	Gson gson = new Gson();
    	FileReader reader = new FileReader(savefile);
		ArrayList<Task> tasks = gson.fromJson(reader, new TypeToken<ArrayList<Task>>(){}.getType());
    	return tasks;
    	/*
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
    	@SuppressWarnings("unchecked")
		ArrayList<Task> tasks = (ArrayList<Task>) ois.readObject();
    	ois.close();
    	*/
    }
}
