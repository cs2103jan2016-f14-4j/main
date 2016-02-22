package taskey.storage;

import java.io.*;
import java.util.ArrayList;
import taskey.logic.Task;

/**
 * 
 * @author Dylan
 *
 */
public class Storage {

	private static Storage instance = null;
	private File directory;
	private File savefile = new File(DEFAULT_FILENAME);
	private static final String DEFAULT_FILENAME = "TaskList Savefile";
	
	/**
	 * For testing of the Storage class. This is how Logic will interface with Storage.  
	 * @param args
	 */
	public static void main(String args[]) {
		// Get the Storage singleton instance
		Storage storageTest = Storage.getInstance();

		// Can optionally set the savefile name
		if (storageTest.setFilename("TaskList Savefile_test") == true) {
			System.out.println("setFilename() successful.");
		}
		
		// Can optionally set directory (default is the working directory)
		if (storageTest.setDirectory("Storage directory test") == true) {
			System.out.println("setDirectory() successful.");
		}
		
		// Example of an invalid directory
		if (storageTest.setDirectory(":\\?*|\"/<>") == false) {
			System.out.println("Example: setDirectory() failed.");
		}
		
		// Example of an filename
		if (storageTest.setFilename(":\\?*|\"/<>") == false) {
			System.out.println("Example: setFilename() failed.");
		}

		// Create a simulated list of tasks
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(new Task("1. This is a test task"));
		tasks.add(new Task("2. This is a test task"));
		tasks.add(new Task("3. This is a test task"));
		
		// Save the task list to file
		try {
			storageTest.saveTasks(tasks);
		} catch (IOException e) {
			System.err.println("Error saving task list to file.");
			e.printStackTrace();
		}
		
		// Load the task list from file
		try {
			ArrayList<Task> loadedTaskList = storageTest.loadTasks();
			for (Task t : loadedTaskList) {
				System.out.println(t);
			}
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Error loading task list from file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Logic gets Storage singleton.
	 * @return the storage singleton instance
	 */
    public static Storage getInstance() {
    	if (instance == null) {
    		instance = new Storage();
    	}
    	return instance;
    }
    
    /**
     * Logic can set the storage directory.
     * @return true if directory now exists and is indeed a directory
     * @return false if directory name is invalid (previous directory remains unchanged)
     * @param pathname (can be a relative or absolute path)
     */
    public boolean setDirectory(String pathname) {
    	File dir = new File(pathname);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		if (dir.isDirectory()) {
			this.directory = dir;
			// Need to re-set the filename after changing directory.
			// Otherwise, the file will not be saved in the new directory.
			setFilename(savefile.getName());
			return true;
		} else {
			return false;
		}
    }
    
    /**
     * Logic can set the savefile's name.
     * @param filename
     * @return true if the savefile's name and path has been updated
     * @return false if the filename is invalid
     */
    public boolean setFilename(String filename) {
		File file = new File(directory, filename);
		
		try {
			System.out.println("The savefile path is now \"" + file.getCanonicalPath() + "\"");
		} catch (IOException e) {
			return false;
		}
		
		this.savefile = file;
		return true;
    }
    
    /**
     * Logic can get the file path and return it to UI as feedback when user changes directory and/or filename.
     * @return absolute path of the savefile
     */
    public String getFilePath() {
    	return savefile.getAbsolutePath();
    }
    
    /**
     * Logic passes Task list to Storage for saving.
     * @param tasks
     * @throws IOException 
     */
    public void saveTasks(ArrayList<Task> tasks) throws IOException {
		writeToFile(tasks);
    	System.out.println("Saved.");
    }
    
    /**
     * Logic gets Task list from Storage on startup.
     * @return the list of tasks loaded from storage.
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public ArrayList<Task> loadTasks() throws ClassNotFoundException, IOException {
    	ArrayList<Task> tasks = readFromFile();
    	System.out.println("Loaded.");
    	return tasks;
    }
    
    /* =============== *
     * Private methods *
     * =============== */
    
    private void writeToFile(ArrayList<Task> tasks) throws IOException {
    	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile));
    	oos.writeObject(tasks);
    	oos.close();
    }
    
    private ArrayList<Task> readFromFile() throws IOException, ClassNotFoundException {
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
    	@SuppressWarnings("unchecked")
		ArrayList<Task> tasks = (ArrayList<Task>) ois.readObject();
    	ois.close();
    	return tasks;
    }
}
