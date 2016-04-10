package taskey.storage;

import static taskey.storage.Storage.TasklistEnum.savedLists;

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
	public static final String FILENAME_TAGS = "TAGS.taskey";
	public static final String FILENAME_EXTENSION = ".taskey"; //TODO use whole filename rather than just the extension
	public static final String FILENAME_DIRCONFIG = "last_used_directory.taskeyconfig";
	public static final int NUM_TASKLISTS_FROM_LOGIC = taskey.logic.LogicMemory.NUM_TASK_LISTS;

	/**
	 * This is an enum of all the task lists that are handled by Storage, together with their filenames.
	 * That is, these are the task lists that need to be returned from Storage to Logic during load.
	 * However, not all of these task lists are saved to file:
	 * Only lists in the EnumSet savedLists are actually written to file; the rest are derived from the PENDING list.
	 * The index refers to the index of each list in the list of lists (the "superlist") from Logic.
	 * In the superlist from Logic, index 0 (THIS_WEEK list) and 7 (ACTION list) are not handled by Storage
	 */
	public enum TasklistEnum {
		PENDING		("PENDING.taskey", 1),
		EXPIRED		("EXPIRED.taskey", 2),
		GENERAL		("GENERAL.taskey", 3),	//not saved
		DEADLINE	("DEADLINE.taskey", 4),	//not saved
		EVENT		("EVENT.taskey", 5),	//not saved
		COMPLETED	("ARCHIVE.taskey", 6);

		static final EnumSet<TasklistEnum> savedLists = EnumSet.of(PENDING, EXPIRED, COMPLETED);
		static final int size = TasklistEnum.values().length;

		private final String filename;
		private final int index;

		TasklistEnum(String filename, int index) {
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
		 * Returns the enum type corresponding to the given index i,
		 * or null if the given index does not appear in TaskListEnum.
		 */
		public static TasklistEnum enumOf(int i) {
			for (TasklistEnum enumType : TasklistEnum.values()) {
				if (enumType.index == i) {
					return enumType;
				}
			}
			return null;
		}

		/**
		 * Returns the enum type corresponding to the given filename string,
		 * or null if the given filename does not exist in TaskListEnum.
		 */
		public static TasklistEnum enumOf(String fileName) {
			for (TasklistEnum enumType : TasklistEnum.values()) {
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
			if (directoryManager.createDirectory(loadedDirectory) == true) {
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
	 * Returns the list of task lists loaded from Storage.
	 * Logic calls this on program startup, or when the user loads from a directory using the setdir command.
	 * <p>Post-conditions:
	 * <br>- The lists in the returned superlist are in the same order as the enum constants in TasklistEnum.
	 * <br>- These lists do not include the THIS_WEEK and ACTION list.
	 * <br>- If any single list was not found, or is invalid, an empty list is added in its place.
	 * @return the list of tasklists read from disk, some or all of which may be empty
	 */
	public ArrayList<ArrayList<Task>> loadAllTasklists() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (TasklistEnum listType : TasklistEnum.values()) {
			File src = new File(directory, listType.filename());
			ArrayList<Task> loadedList = storageReader.loadTasklist(src, listType);
			superlist.add(loadedList);
		}
		return superlist;
	}

	/**
	 * Saves the PENDING, EXPIRED and COMPLETED lists of the given superlist to file.
	 * If any of the above lists are empty, instead of saving an empty list, the file will be deleted instead.
	 * These savefiles are mutually exclusive hence any one of them can be safely deleted.
	 * <p>Pre-conditions:
	 * <br>- Starting from index 1, the lists in the given superlist
	 * 		 must be in the same order as the enum constants in TasklistEnum.
	 * <br>- Index 0 is reserved for the THIS_WEEK list and is not saved to disk because it is time-dependent.
	 * <br>- Index 7 is reserved for the ACTION list and is not saved to disk because it is session-dependent.
	 * <br>- The GENERAL, DEADLINE and EVENT lists are not saved because they can be derived from the PENDING list.
	 * @param superlist the list of tasklists to be saved
	 * @throws IOException when FileWriter fails to write any single list to file
	 */
	public void saveAllTasklists(ArrayList<ArrayList<Task>> superlist) throws IOException {
		assert (superlist.size() == NUM_TASKLISTS_FROM_LOGIC);

		for (TasklistEnum listType : savedLists) {
			File dest = new File(directory, listType.filename());
			ArrayList<Task> listToSave = superlist.get(listType.index());
			try {
				storageWriter.saveTasklist(listToSave, dest);
			} catch (FileNotFoundException e) {
				directoryManager.createDirectory(directory);  //in case user deletes the directory during runtime
				storageWriter.saveTasklist(listToSave, dest); //recreate the directory and try again
			}
		}
	}


	/*================*
	 * Load/Save tags *
	 *================*/
	/**
	 * Returns the ArrayList of Tags loaded from Storage.
	 * An empty ArrayList is returned if the tags file was not found.
	 * @return the list of user-defined tags
	 */
	public ArrayList<TagCategory> loadTaglist() {
		File src = new File(directory, FILENAME_TAGS);
		return storageReader.loadTaglist(src);
	}

	/**
	 * Saves the given ArrayList of Tags to Storage.
	 * If the list is empty, instead of saving an empty list, the file will be deleted instead.
	 * @param tags the ArrayList containing the user-defined tags
	 * @throws IOException FileWriter failed
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
	 * Has the same effect as calling {@link #setDirectory(File, boolean) setDirectory(pathname, true)}
	 */
	public void setDirectory(String pathname) throws InvalidPathException, IOException {
		setDirectory(pathname, true);
	}


	/**
	 * See {@link DirectoryManager#changeDirectory(File, boolean, File)}
	 * @param pathname path of the new directory
	 * @param shouldMove whether Storage should move files from the current directory to the new one.
	 */
	public void setDirectory(String pathname, boolean shouldMove) throws InvalidPathException, NotDirectoryException, 
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
		storage.setDirectory("con"); //invalid
		ArrayList<ArrayList<Task>> loadedLists = storage.loadAllTasklists();
		print(loadedLists);
	}

	private static void print(ArrayList<ArrayList<Task>> lists) {
		int i=1;
		for (ArrayList<Task> list : lists) {
			System.out.println(TasklistEnum.enumOf(i++) + " LIST");
			for (Task t : list) {
				System.out.println(t);
			}
		}
		System.out.println("end print");
	}
}