package taskey.storage;

import static taskey.storage.Storage.DEFAULT_DIRECTORY;
import static taskey.storage.Storage.FILENAME_DIRCONFIG;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;

import taskey.storage.Storage.TasklistEnum;

/**
 * @@author A0121618M
 * This class provides two methods to Storage for managing its directory:
 *  - createDirectory(File)
 *  - changeDirectory(File, boolean, File)
 */
class DirectoryManager {
	// For remembering the directories created during runtime,
	// so that DirectoryManager can delete them when they are no longer needed
	private HashSet<File> directoriesCreated = new HashSet<File>();
	private StorageWriter storageWriter = new StorageWriter();

	DirectoryManager() {
	}

	/**
	 * Quietly creates the full directory path of the given abstract pathname and also checks that it is valid.
	 * @param dir directory to be created
	 * @return true if the directory was successfully created or already exists;
	 * 		   false if mkdirs() failed or dir exists but is not a directory
	 */
	boolean createDirectory(File dir) {
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
			return false;
		}
	}

	/**
	 * <p> This method does the following in sequence:
	 * <br>1. Checks the validity of the new directory
	 * <br>2. If the given boolean argument is true, taskey files in the current directory will be moved to the new one.
	 *		  However, if the new directory contains existing task file(s), this method will signal Logic to load them.
	 * <br>3. Delete the old directory, if it is empty and was created by Taskey during runtime.
	 * <br>4. Saves the new directory to the directory config file.
	 * 
	 * <p>Post-conditions:
	 * <br>- Creates the directory if it does not exist yet.
	 * <br>- If requested, taskey savefiles from the current directory will be moved to the new one, 
	 * 		 provided that the new directory does not contain pre-existing task file(s).
	 * <br>- The old folder will be deleted if it was created during runtime and is currently empty.
	 * <br>- The new directory setting will be saved to a persistent config file in "user.dir".
	 * <br>- Storage's directory will not be updated if any exceptions are thrown.
	 * 
	 * @param pathname can be absolute, or relative to "user.dir"
	 * @param shouldMove <code>true</code> if the move operation should be performed; 
	 * 					 <code>false</code> to update Storage's directory without moving the files (see FileAlreadyExistsException).
	 * 
	 * @throws InvalidPathException when the path string cannot be converted to a Path because it contains invalid characters.
	 *
	 * @throws NotDirectoryException when the path points to a normal file and not a directory.
	 * 
	 * @throws AccessDeniedException if creating a new directory or writing to an existing one was denied by the Windows file system.
	 * 
	 * @throws FileSystemException if the path contains a reserved/illegal filename or the root directory doesn't exist.
	 * 
	 * @throws FileAlreadyExistsException if the new directory already contains at least one tasklist file.
	 *		   This is a signal for Logic to call Storage.setDirectory(pathname, false) and then load from the new directory.
	 *		   (Passing in false will update Storage's directory and bypass FileAlreadyExistsException.)
	 * 
	 * @throws IOException any other IO error not covered above, when creating a new directory, writing to it, or moving files.
	 * 		   Note that moving is not an atomic operation, so it is possible that some files will have been moved halfway.
	 */
	File changeDirectory(File currDir, boolean shouldMove, File newDir) throws InvalidPathException, NotDirectoryException, 
																			   AccessDeniedException, FileSystemException, 
																			   FileAlreadyExistsException, IOException {
		createDirectoryLoudly(newDir);
		checkCanWriteToDirectory(newDir);

		if (shouldMove) {
			try {
				moveFiles(currDir, newDir);
			} catch (FileAlreadyExistsException e) {
				System.out.println("{Storage} Load from directory | " + newDir.getPath());
				throw e; //signal Logic to load the existing task files
			}
		}
		deleteCurrDir(currDir); //delete the old folder if it's currently empty and was created by Taskey during runtime

		if (shouldSaveNewDir(newDir, currDir)) {
			storageWriter.saveDirectoryConfigFile(newDir, FILENAME_DIRCONFIG);
		}

		return newDir;
	}

	/**
	 * This method attempts to create the full directory path of the given abstract pathname and throws exceptions when it fails.
	 * All known exceptions are enumerated in the throws clause of this method just for documentation purposes;
	 * except for InvalidPathException, the other exceptions are actually subclasses of IOException.
	 * @param dir directory to be created
	 * @throws InvalidPathException if dir contains illegal characters, e.g. * or ?
	 * @throws NotDirectoryException if dir points to a normal file and not a folder
	 * @throws AccessDeniedException if creation of dir was denied, e.g. trying to create a folder in c:\\program files
	 * @throws FileSystemException if dir contains an illegal filename in Windows, e.g. reserved words such as CON or PRN
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
			testFile.delete();
			Files.createFile(testFile.toPath());
			testFile.delete();
		} catch (AccessDeniedException e) {
			throw e;
		} catch (FileAlreadyExistsException e) { //test file already exists
			System.err.println(e.getMessage());
		} catch (IOException e) { //whatever else
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Moves the taskey savefiles from the given source to destination directory.
	 * This method has no effect if srcDir and destDir are the same.
	 * Files are moved only if destDir does not contain any tasklist savefiles.
	 * @param srcDir the source directory
	 * @param destDir the destination diectory
	 * @returns true if all the files were moved successfully; false if no files were moved
	 * @throws IOException thrown by the Files.move method
	 * @throws FileAlreadyExistsException if tasklist files already exist in destDir
	 */
	private boolean moveFiles(File srcDir, File destDir) throws IOException, FileAlreadyExistsException {
		// Skip the move if srcDir and destDir are the same
		if (srcDir.getAbsolutePath().equalsIgnoreCase(destDir.getAbsolutePath())) {
			return false;
		}

		// If destDir contains pre-existing task file(s)
		// we do not want to inadvertently overwrite them, so we skip the move
		if (containsExistingTaskFilesIn(destDir)) {
			throw new FileAlreadyExistsException("Load from directory");
		}

		boolean wasMoved = false;
		for (File srcFile : srcDir.listFiles()) {
			if (Arrays.asList(Storage.FILENAMES).contains(srcFile.getName())) {
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
	 * Checks if the given directory contains any tasklist savefile(s).
	 * @param dir directory to check
	 * @return true if any tasklist file was found; false otherwise
	 */
	private boolean containsExistingTaskFilesIn(File dir) {
		//EnumSet<TasklistEnum> set = EnumSet.noneOf(TasklistEnum.class);
		for (String filename : dir.list()) {
			TasklistEnum listType = TasklistEnum.enumOf(filename);
			if (TasklistEnum.savedLists.contains(listType)) {
				return true;
				//set.add(listType);
			}
		}
		return false;

		/*//Not used - For checking that dir contains ALL tasklists
		if (set.equals(TasklistEnum.savedLists)) {
			return true; //all tasklist files are present in dir
		} else {
			return false;//at least one tasklist file is missing
		}
		*/
	}

	/**
	 * Deletes the current Storage directory if it was created during runtime and is currently empty.
	 * This method should only be used in the changeDirectory method.
	 * @param currDir the old directory to be deleted before setting the new one
	 */
	private void deleteCurrDir(File currDir) {
		if (directoriesCreated.contains(currDir) && (currDir.list().length == 0)) {
			try {
				Files.delete(currDir.toPath());
				directoriesCreated.remove(currDir);
				System.out.println("{Storage} Old directory deleted | " + currDir.getPath());
			} catch (Exception e) {
				System.err.println("{Storage} Could not delete old directory | " + currDir.getPath());
			}
		}
	}

	/**
	 * Checks whether the abstract pathname given by dir should be saved to the directory config file.
	 * @param newDir the candidate directory to be saved
	 * @param currDir the old directory that is used for comparison
	 * @return true if dir is different from the current directory;
	 * 		   false if dir is the default directory or is the same as the current directory
	 */
	private boolean shouldSaveNewDir(File newDir, File currDir) {
		// If dir is equal to the default directory, we can delete the config file
		// since Storage does not need it to remember it.
		if (newDir.getAbsolutePath().equalsIgnoreCase(DEFAULT_DIRECTORY.getAbsolutePath())) {
			File configFile = new File(FILENAME_DIRCONFIG);
			try {
				Files.deleteIfExists(configFile.toPath());
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
				//return true; //(?)since delete failed, return true to save it(?)
			}
		}

		// Check that dir is different from the current directory
		if (! newDir.getAbsolutePath().equalsIgnoreCase(currDir.getAbsolutePath()) ) {
			return true;
		} else {
			return false;
		}
	}
}
