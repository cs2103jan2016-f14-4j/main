package taskey.storage;

import static taskey.storage.Storage.TaskListEnum.savedLists;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.storage.TaskVerifier.InvalidTaskException;

/**
 * @@author A0121618M
 * This class exposes public IO methods for saving tasklists and tags.
 * It also manages the storage directory.
 */
public class Storage {
	private StorageReader storageReader;
	private StorageWriter storageWriter;
	private File directory;
	private HashSet<File> directoriesCreated;

	private static final File DEFAULT_DIRECTORY = new File("Taskey savefiles");
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
		private static final int size = TaskListEnum.values().length;
		
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
	 * For testing
	 * @throws IOException 
	 * @throws NotDirectoryException 
	 * @throws InvalidPathException 
	 * @throws FileAlreadyExistsException 
	 */
	public static void main(String args[]) throws Exception {
		Storage storage = new Storage();
		storage.setDirectory("CON"); //throws invalid path exception
		ArrayList<ArrayList<Task>> loadedLists = storage.loadAllTasklists();
		print(loadedLists);
	}

	/**
	 * For testing
	 */
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


	/*=============*
	 * Constructor *
	 *=============*/
	/**
	 * Storage constructor and initializer.
	 * Attempts to load and set the last used directory.
	 * If none was found, DEFAULT_DIRECTORY will be set instead.
	 * Post-condition: all the fields of Storage have been instantiated.
	 */
	public Storage() {
		storageReader = new StorageReader();
		storageWriter = new StorageWriter();
		directoriesCreated = new HashSet<File>();

		File loadedDirectory = storageReader.loadDirectoryConfigFile(FILENAME_DIRCONFIG);
		if (loadedDirectory != null) {
			if (Boolean.TRUE.equals(createDirectory(loadedDirectory))) { //createDirectory could possibly return null
				directory = loadedDirectory;
				System.out.println("{Storage} Directory loaded | " + directory.getAbsolutePath());
			} else { //loaded directory was invalid or could not be created
				createDirectory(DEFAULT_DIRECTORY);
				directory = DEFAULT_DIRECTORY;
			}
		} else { //directory config file not found
			createDirectory(DEFAULT_DIRECTORY);
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
				createDirectory(directory); //in case user deletes their storage directory during runtime
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


	/*======================*
	 * Directory management * TODO extract into a directory manager class?
	 *======================*/
	/**
	 * Returns the current storage directory.
	 * When the user asks to change directory, Logic can return it as feedback.
	 * @return absolute path of the default or user-set directory
	 */
	public String getDirectory() {
		return directory.getAbsolutePath();
	}

	/**
	 * Has the same effect as calling setDirectory(pathname, true)
	 */
	public void setDirectory(String pathname) throws FileAlreadyExistsException, IOException, 
														InvalidPathException, NotDirectoryException {
		setDirectory(pathname, true);
	}

	/**
	 * Changes the storage directory to the given pathname string after checking that the path is valid.
	 * If the given boolean argument is true, .taskey files in the current directory will be moved to the new one.
	 * This method is invoked by Logic, should the end user request to change it.
	 * 
	 * <p>Post-conditions:
	 * <br>- Creates the directory if it does not exist yet.
	 * <br>- If requested, moves the .taskey storage files from the existing directory to the new one, 
	 * 		 provided the new directory does not contain a full set of pre-existing tasklist files.
	 * <br>- The old folder will be deleted if it was created during runtime and is currently empty.
	 * <br>- Saves the new directory setting to a persistent config file in "user.dir".
	 * <br>- Storage's directory will not be updated if the specified exceptions are thrown.
	 * 
	 * @param pathname can be absolute, or relative to "user.dir"
	 * @param shouldMove [true] if move operation should be performed; 
	 * 					 [false] to set Storage's directory without moving the files (see Throws section for use case).
	 * 
	 * @throws FileAlreadyExistsException if the new directory already contains a full set of pre-existing tasklists.
	 *			This is a signal for Logic to call setDirectory(pathname, false), then loadAllTasklists().
	 *			This will update Storage's directory and load from the new directory, without moving files from the previous one.
	 *
	 * @throws IOException if an I/O error occurs when moving the files.
	 * 			This is not an atomic operation, so it is possible that some files have already been moved. 
	 *			Logic should save everything after this to ensure that the current directory has all the savefiles.
	 *
	 * @throws InvalidPathException when the path string cannot be converted into a Path because it contains invalid characters, 
	 *			or is invalid for other file system specific reasons. On Windows, this could be due to: 
	 *			illegal characters (e.g. *), reserved words (e.g. CON), or nonexistent root drive letters.
	 *
	 * @throws NotDirectoryException when the path points to a normal file and not a directory
	 */
	public void setDirectory(String pathname, boolean shouldMove) throws FileAlreadyExistsException, IOException, 
																			InvalidPathException, NotDirectoryException {
		File newDir = new File(pathname);
		createDirectoryLoudly(newDir);

		if (shouldMove) {
			try {
				moveFiles(directory, newDir);
			} catch (FileAlreadyExistsException e) {
				System.out.println("{Storage} Directory contains existing tasklist files! | " + newDir.getPath());
				throw e; //signal Logic to load the existing task savefiles
			}
		}
		deleteCurrDir(); //delete the old folder if it's currently empty and was created by Taskey during runtime

		if (shouldSaveNewDir(newDir)) {
			storageWriter.saveDirectoryConfigFile(newDir, FILENAME_DIRCONFIG);
		}

		directory = newDir;
		System.out.println("{Storage} Directory set | " + directory.getPath());
	}
	
	/**
	 * This method calls createDirectory and checks for its return value, 
	 * throwing an exception when the return value indicates that it was unsuccessful. 
	 * @param dir
	 * @throws NotDirectoryException
	 */
	private void createDirectoryLoudly(File dir) throws NotDirectoryException, InvalidPathException {
		Boolean isValidDir = createDirectory(dir);
		if (isValidDir == null) {
			throw new NotDirectoryException(dir.getPath()); //the abstract path newDir is a normal file and not a directory/folder
		} else if (isValidDir == false) {
			dir.toPath(); //this line throws InvalidPathException if dir contains illegal characters e.g. *
			throw new InvalidPathException(dir.getPath(), "Illegal name"); //for other reasons not covered in the above line
		}
	}

	/**
	 * Quietly creates the full directory path of the given abstract pathname and also checks that it is valid.
	 * @param dir directory to be created
	 * @return true if the directory was successfully created or already exists;
	 * 		   false if dir is not a valid abstract path;
	 * 		   null if dir exists but is not a directory
	 */
	private Boolean createDirectory(File dir) {
		if (!dir.exists()) {
			if (dir.mkdirs() == true) {
				directoriesCreated.add(dir);
				return true;
			} else { //mkdirs() failed
				return false;
			}
		} else if (dir.isDirectory()) { //dir already exists && is a directory
			return true;
		} else { //dir exists but is not a directory
			return null;
		}
	}

	/**
	 * Moves the ".taskey" savefiles from the given source to destination directory.
	 * @param srcDir the source directory
	 * @param destDir the destination diectory
	 * @returns true if all the files were moved successfully; false if no files were moved
	 * @throws IOException thrown by Files.move method
	 * @throws FileAlreadyExistsException if savefiles exist in destDir
	 */
	private boolean moveFiles(File srcDir, File destDir) throws IOException, FileAlreadyExistsException {
		// Skip the move if srcDir and destDir are the same
		if (srcDir.getAbsolutePath().equalsIgnoreCase(destDir.getAbsolutePath())) {
			return false;
		}

		// If destDir contains a full set of pre-existing tasklist files
		// we do not want to inadvertently overwrite them, so we skip the move
		if (containsExistingTaskFilesIn(destDir)) {
			throw new FileAlreadyExistsException(null);
		}

		boolean wasMoved = false;
		for (File srcFile : srcDir.listFiles()) {
			if (srcFile.getName().endsWith(FILENAME_EXTENSION)) {
				Path srcPath = srcFile.toPath();
				Path destPath = destDir.toPath().resolve(srcFile.getName());

				try {
					Files.move(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
					wasMoved = true;
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}

		if (wasMoved) {
			System.out.println("{Storage} Files moved");
		}
		return wasMoved;
	}

	/**
	 * Checks if the given directory contains the full set of tasklist savefiles.
	 * @param dir directory to check for pre-existing tasklist files
	 * @return true if and only if dir contains the full set of tasklist files; false otherwise
	 */
	private boolean containsExistingTaskFilesIn(File dir) {
		EnumSet<TaskListEnum> set = EnumSet.noneOf(TaskListEnum.class);
		for (String filename : dir.list()) {
			TaskListEnum listType = TaskListEnum.enumOf(filename);
			if (savedLists.contains(listType)) { //does return false when listType == null
				set.add(listType);
			}
		}

		if (set.equals(savedLists)) {
			return true; //all tasklist files are present in dir
		} else {
			return false;//at least one tasklist file is missing
		}
	}

	/**
	 * Deletes the current Storage directory if it was created during runtime and is currently empty.
	 * This method is meant to be used only in the setDirectory method.
	 */
	private void deleteCurrDir() {
		if (directoriesCreated.contains(directory) && (directory.list().length == 0)) {
			try {
				Files.delete(directory.toPath());
				directoriesCreated.remove(directory);
				System.out.println("{Storage} Old directory deleted | " + directory.getPath());
			} catch (Exception e) {
				System.err.println("{Storage} Could not delete old directory | " + directory.getPath());
			}
		}
	}

	/**
	 * Checks whether the abstract pathname given by dir should be saved to the directory config file.
	 * @param dir the candidate directory
	 * @return true if dir is different from the current directory;
	 * 		   false if dir is the default directory or is the same as the current directory
	 */
	private boolean shouldSaveNewDir(File dir) {
		// If dir is equal to the default directory, we can delete the config file
		// since Storage does not need it to remember it.
		if (dir.getAbsolutePath().equalsIgnoreCase(DEFAULT_DIRECTORY.getAbsolutePath())) {
			File configFile = new File(FILENAME_DIRCONFIG);
			try {
				Files.deleteIfExists(configFile.toPath());
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return true; //since delete failed, return true to save it
			}
		}

		// Check that dir is different from the current directory
		if (! dir.getAbsolutePath().equalsIgnoreCase(directory.getAbsolutePath()) ) {
			return true;
		} else {
			return false;
		}
	}
}