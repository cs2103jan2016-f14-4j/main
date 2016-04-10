package taskey.storage;

import static taskey.storage.Storage.TaskListEnum.savedLists;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
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
		} else { //directory config file was not found
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
	 * Changes the storage directory to the given pathname string after checking that the path is valid.
	 * If the given boolean argument is true, .taskey files in the current directory will be moved to the new one.
	 * This method is invoked by Logic, should the end user request to change it.
	 * 
	 * <p>Post-conditions:
	 * <br>- Creates the directory if it does not exist yet.
	 * <br>- If requested, moves the .taskey savefiles from the existing directory to the new one, 
	 * 		 provided the new directory does not contain a full set of pre-existing tasklist files.
	 * <br>- The old folder will be deleted if it was created during runtime and is currently empty.
	 * <br>- Saves the new directory setting to a persistent config file in "user.dir".
	 * <br>- Storage's directory will not be updated if the specified exceptions are thrown.
	 * 
	 * @param pathname can be absolute, or relative to "user.dir"
	 * @param shouldMove [true] if move operation should be performed; 
	 * 					 [false] to set Storage's directory without moving the files (see Throws section for use case).
	 * 
	 * @throws InvalidPathException when the path string cannot be converted into a Path because it contains invalid characters.
	 *
	 * @throws NotDirectoryException when the path points to a normal file and not a directory.
	 * 
	 * @throws AccessDeniedException if creating a new directory or writing to an existing one was denied by the Windows file system.
	 * 
	 * @throws FileSystemException if the path contains a reserved/illegal filename or the root directory doesn't exist.
	 * 
	 * @throws FileAlreadyExistsException if the new directory already contains a full set of pre-existing tasklists.
	 *			This is a signal for Logic to call setDirectory(pathname, false), then loadAllTasklists().
	 *			This will update Storage's directory (bypassing the FileAlreadyExistsException) and load from the new directory.
	 * 
	 * @throws IOException any other IO error, when creating a new directory or moving files, that is not covered above.
	 * 			Note that moving is not an atomic operation, so it is possible that some files have already been moved.
	 */
	public void setDirectory(String pathname, boolean shouldMove) throws InvalidPathException, NotDirectoryException,
																		 AccessDeniedException, FileSystemException, 
																		 FileAlreadyExistsException, IOException {
		File newDir = new File(pathname);
		createDirectoryLoudly(newDir);
		checkCanWriteToDirectory(newDir);

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
	 * This method attempts to create the full directory path of the given abstract pathname and throws exceptions when it fails.
	 * All known exceptions are enumerated in the throws clause of this method just for documentation purposes;
	 * except for InvalidPathException, the other exceptions are actually subclasses of IOException.
	 * @param dir directory to be created
	 * @throws InvalidPathException if dir contains illegal characters, e.g. * or ?
	 * @throws NotDirectoryException if dir points to a normal file and not a folder
	 * @throws AccessDeniedException if creation of dir was denied, e.g. trying to create a folder in c:\\program files
	 * @throws FileSystemException if dir is an illegal filename in Windows, e.g. reserved words such as CON or PRN
	 * @throws IOException for any other reason not covered by the above exceptions
	 */
	private void createDirectoryLoudly(File dir) throws InvalidPathException, NotDirectoryException, 
														AccessDeniedException, FileSystemException,
														IOException {
		Path path = dir.toPath(); //throws InvalidPathException for illegal characters
		try {
			if (!dir.exists()) {
				Files.createDirectories(path);
				directoriesCreated.add(dir);
			} else {
				Files.createDirectories(path);
			}
		} catch (FileAlreadyExistsException e) { //dir exists but is not a directory
			throw new NotDirectoryException(dir.getPath());
		} catch (AccessDeniedException e) { //WindowsFileSystemProvider denied creation of dir
			throw e;
		} catch (FileSystemException e) { //illegal or reserved filename
			throw e;
		} catch (IOException e) { //whatever else
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Checks whether a file can be written to the given directory.
	 * @param dir directory in which the test file will be created
	 * @throws AccessDeniedException if the test file cannot be created
	 * @throws IOException test file already exists or some other reason
	 */
	private void checkCanWriteToDirectory(File dir) throws AccessDeniedException, IOException {
		try {
			File testFile = new File(dir, "DeleteMe.Taskey");
			Files.createFile(testFile.toPath());
			testFile.delete();
		} catch (AccessDeniedException e) {
			throw e;
		} catch (FileSystemException e) { //test file already exists
			System.err.println(e.getMessage());
		} catch (IOException e) { //whatever else
			e.printStackTrace();
			throw e;
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
				} catch (AccessDeniedException e) {
					throw e;
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