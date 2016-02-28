package taskey.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import taskey.logic.Task;

/**
 * 
 * @author Dylan
 *
 */
public class Storage {

	public static final String DEFAULT_DIRECTORY = "bin\\taskey\\storage";
	
	private static Storage instance = null;
	private static File directory = new File(DEFAULT_DIRECTORY);
	private static File savefile;
	
	/**
	 * For testing of the Storage class. This is how Logic will interface with Storage.  
	 * @param args
	 */
	public static void main(String args[]) {
		// Get the Storage singleton instance
		Storage storageTest = Storage.getInstance();
		
		// The default directory was automatically set in the getInstance() method.
		// Can optionally set the directory again, if requested by user.
		System.out.println("The directory is now \"" + directory.getAbsolutePath() + "\"");

		// Create a simulated list of tasks
		ArrayList<Task> testTaskList = new ArrayList<Task>();
		testTaskList.add(new Task("1. This is a test task"));
		testTaskList.add(new Task("2. This is a test task"));
		testTaskList.add(new Task("3. This is a test task"));
		
		// Save the task list to file, specifying the list's category
		try {
			storageTest.saveTaskList(testTaskList, "TEST_TASKLIST_CATEGORY");
		} catch (IOException e) {
			System.err.println("Error saving task list to file.");
			e.printStackTrace();
		}
		
		// Load the task list from file, specifying the list's category
		try {
			ArrayList<Task> loadedTaskList = storageTest.getTaskList("TEST_TASKLIST_CATEGORY");
			for (Task t : loadedTaskList) {
				System.out.println(t);
			}
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Error loading task list from file.");
			e.printStackTrace();
		}
	}
	
    /*=============*
     * Constructor *
     *=============*/
	
	/**
	 * Logic gets Storage singleton.
	 * @return the storage singleton instance
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
		setFilename(taskCategory);
    	writeToFile(tasks);
    	System.out.println("Saved.");
    }
    
    /**
     * Logic gets a task list from Storage.
     * @param taskCategory The category of tasks that Logic wants to load.
     * @return The task list loaded from file, specified by taskCategory.
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public ArrayList<Task> getTaskList(String taskCategory) throws ClassNotFoundException, IOException {
    	setFilename(taskCategory);
    	ArrayList<Task> tasks = readFromFile();
    	System.out.println("Loaded.");
    	return tasks;
    }
    
    /*===========================*
     * Public accessors/mutators *
     *===========================*/
    
    /**
     * When the user changes directory, Logic can return it as feedback.
     * @return Absolute path of the last saved/loaded file.
     */
    public static String getFilePath() {
    	return savefile.getAbsolutePath();
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
			if (savefile != null) {
				// Need to re-set the file path after changing directory.
				// Otherwise, the file will not be saved in the new directory.
				setFilename(savefile.getName());
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
    private boolean setFilename(String filename) {
		File file = new File(directory, filename);
		
		try {
			file.getCanonicalPath();
		} catch (IOException e) {
			return false;
		}
		
		savefile = file;
		return true;
    }
    
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
