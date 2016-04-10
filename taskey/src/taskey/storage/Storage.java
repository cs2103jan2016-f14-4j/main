package taskey.storage;

import static taskey.storage.Storage.TaskListEnum.savedLists;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.EnumSet;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.storage.TaskVerifier.InvalidTaskException;

/**
 * @@author A0121618M
 * This class exposes public methods for:
 * - Saving/loading task lists
 * - Saving/loading tags
 * - Setting the Storage directory
 */
public class Storage {
	private StorageReader storageReader;
	private StorageWriter storageWriter;
	private DirectoryManager directoryManager;
	private File directory;

	static final File DEFAULT_DIRECTORY = new File("Taskey savefiles");
	public static final String FILENAME_TAGS = "USER_TAG_DB.taskey";
	public static final String FILENAME_DIRCONFIG = "last_used_directory.taskeyconfig";
	public static final String FILENAME_EXTENSION = ".taskey"; //TODO use whole filename rather than just the extension
	public static final int NUM_TASKLISTS_FROM_LOGIC = taskey.logic.LogicMemory.NUM_TASK_LISTS;

	/**
	 * This is an enum of all the task lists that are handled by Storage.
	 * That is, these are the task lists that need to be returned from Storage to Logic during load.
	 * However, not all of these task lists are saved to file.
	 * Only lists in the EnumSet savedLists are actually written to file;
	 * the rest are derived from the PENDING list.
	 */
	public enum TaskListEnum {
		// In the lists from Logic, index 0 (THIS_WEEK list) and 7 (ACTION list) are not handled by Storage
		PENDING		("PENDING.taskey", 1),
		EXPIRED		("EXPIRED.taskey", 2),
		GENERAL		("GENERAL.taskey", 3),	//not saved
		DEADLINE	("DEADLINE.taskey", 4),	//not saved
		EVENT		("EVENT.taskey", 5),	//not saved
		COMPLETED	("COMPLETED.taskey", 6);
		
		static final EnumSet<TaskListEnum> savedLists = EnumSet.of(PENDING, EXPIRED, COMPLETED);
		static final int size = TaskListEnum.values().length;
		
		private final String filename;
		private final int index;

		TaskListEnum(String filename, int index) {
			this.filename = filename;
			this.index = index;
		}
		
		public static int size() {
			return size;
		}

		public String filename() {
			return filename;
		}

		public int index() {
			return index;
		}
		
		/**
		 * Returns the enum type corresponding to the given filename string,
		 * or null if the given filename does not exist in TaskListEnum.
		 */
		public static TaskListEnum enumOf(String fileName) {
			for (TaskListEnum enumType : TaskListEnum.values()) {
				if (enumType.filename.equals(fileName)) {
					return enumType;
				}
			}
			return null;
		}
	}

	/**
	 * Storage constructor and initializer.
	 * Attempts to load and set the last used directory.
	 * If none was found, DEFAULT_DIRECTORY will be set instead.
	 * Post-condition: all the fields of Storage have been instantiated.
	 */
	public Storage() {
		storageReader = new StorageReader();
		storageWriter = new StorageWriter();
		directoryManager = new DirectoryManager();

		File loadedDirectory = storageReader.loadDirectoryConfigFile(FILENAME_DIRCONFIG);
		if (loadedDirectory != null) {
			if (directoryManager.createDirectory(loadedDirectory) == true) { //createDirectory could possibly return null
				directory = loadedDirectory;
				System.out.println("{Storage} Directory loaded | " + directory.getAbsolutePath());
			} else { //loaded directory was invalid or could not be created
				directoryManager.createDirectory(DEFAULT_DIRECTORY);
				directory = DEFAULT_DIRECTORY;
			}
		} else { //directory config file was not found
			directoryManager.createDirectory(DEFAULT_DIRECTORY);
			directory = DEFAULT_DIRECTORY;
		}
	}

	
	/*=====================*
	 * Load/Save tasklists *
	 *=====================*/
	
	/**
	 * Returns the superlist of tasklists loaded from Storage.
	 * Logic calls this on program startup.
	 * <p>Post-conditions:
	 * <br>- The lists in the returned superlist are in the same order as the enum constants in StorageTaskList.
	 * <br>- These lists do not include the THIS_WEEK and ACTION list.
	 * <br>- If any one list was not found, or is invalid, an empty superlist is returned.
	 * @return the superlist of tasklists read from disk, or an empty superlist
	 */
	public ArrayList<ArrayList<Task>> loadAllTasklists() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (TaskListEnum listType : TaskListEnum.values()) {
			File src = new File(directory, listType.filename());
			try {
				ArrayList<Task> loadedList = storageReader.loadTasklist(src, listType);
				superlist.add(loadedList);
			} catch (FileNotFoundException | InvalidTaskException e) { //TODO handle invalid tasks
				superlist.clear();
				while (superlist.size() < TaskListEnum.size()) {
					superlist.add(new ArrayList<Task>());
				}
				return superlist; //return an empty superlist if any tasklist was not found or is invalid
			}
		}
		return superlist;
	}

	/**
	 * Saves the PENDING, EXPIRED and COMPLETED lists of the given superlist to file.
	 * <p>Pre-conditions:
	 * <br>- Starting from index 1, the lists in the given superlist
	 * 		 must be in the same order as the enum constants in StorageTaskList.
	 * <br>- Index 0 is reserved for the THIS_WEEK list and is not saved to disk because it is time-dependent.
	 * <br>- Index 7 is reserved for the ACTION list and is not saved to disk because it is session-dependent.
	 * <br>- GENERAL, DEADLINE and EVENT lists are not saved to disk because they can be derived from the PENDING list.
	 * @param superlist the list of tasklists to be saved
	 * @throws IOException
	 */
	public void saveAllTasklists(ArrayList<ArrayList<Task>> superlist) throws IOException {
		assert (superlist.size() == NUM_TASKLISTS_FROM_LOGIC);

		for (TaskListEnum listType : savedLists) {
			File dest = new File(directory, listType.filename());
			ArrayList<Task> listToSave = superlist.get(listType.index());
			try {
				storageWriter.saveTasklist(listToSave, dest);
			} catch (FileNotFoundException e) {
				directoryManager.createDirectory(directory); //in case user deletes their storage directory during runtime
				storageWriter.saveTasklist(listToSave, dest); //try again
			}
		}
	}

	
	/*================*
	 * Load/Save tags *
	 *================*/
	
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
	}

	
	/*===============*
	 * Set Directory *
	 *===============*/
	
	/**
	 * Has the same effect as calling setDirectory(pathname, true)
	 */
	public void setDirectory(String pathname) throws InvalidPathException, IOException {
		setDirectory(pathname, true);
	}
	
	
	public void setDirectory(String pathname, boolean shouldMove)  throws InvalidPathException, NotDirectoryException, 
	  																	  AccessDeniedException, FileSystemException, 
	  																	  FileAlreadyExistsException, IOException {
		File newDir = new File(pathname);
		directory = directoryManager.changeDirectory(directory, shouldMove, newDir);
		System.out.println("{Storage} Directory set | " + directory.getPath());
	}

	
	/*=============*
	 * For testing *
	 *=============*/
	
	public static void main(String args[]) throws Exception {
		Storage storage = new Storage();
		storage.setDirectory("c:\\program files\\taskey"); //invalid
		ArrayList<ArrayList<Task>> loadedLists = storage.loadAllTasklists();
		print(loadedLists);
	}

	private static void print(ArrayList<ArrayList<Task>> lists) {
		int i=1;
		for (ArrayList<Task> list : lists) {
			System.out.println(i++);
			for (Task t : list) {
				System.out.println(t);
			}
		}
		System.out.println("end print");
	}
}