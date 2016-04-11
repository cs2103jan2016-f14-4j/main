package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static taskey.junit.StorageTest.TaskList.taskListsFromStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.TimeConverter;
import taskey.storage.Storage;
import taskey.storage.Storage.TasklistEnum;
import taskey.storage.StorageReader;
import taskey.storage.StorageWriter;

/**
 * @@author A0121618M
 * Unit test for the Storage package/component.
 */
public class StorageTest {
	private static Storage storage = new Storage();
	private static File testDir = new File(Storage.DEFAULT_DIRECTORY, "temp_test");
	private static File originalDirConfigFile; //to hold the existing directory config file until end of tests

	/**
	 * Enum of all the task lists that are passed from Logic to Storage.
	 * taskListsFromStorage is the set of lists that are returned from Storage to Logic on load.
	 * derivedLists is the set of lists that are derived from the pending list instead of being loaded from disk.
	 */
	enum TaskList {
		THIS_WEEK	(new ArrayList<Task>()), //ignored by Storage
		PENDING 	(new ArrayList<Task>()), //saved to disk
		EXPIRED 	(new ArrayList<Task>()), //saved to disk
		FLOATING 	(new ArrayList<Task>()), //derived from pending list
		DEADLINE 	(new ArrayList<Task>()), //derived from pending list
		EVENT 		(new ArrayList<Task>()), //derived from pending list
		COMPLETED 	(new ArrayList<Task>()), //saved to disk
		ACTION		(new ArrayList<Task>()); //ignored by Storage

		//static EnumSet<TaskList> derivedLists = EnumSet.of(EVENT, DEADLINE, FLOATING);
		static EnumSet<TaskList> taskListsFromStorage = EnumSet.range(PENDING, COMPLETED);
		static TimeConverter timeConverter = new TimeConverter();

		private ArrayList<Task> tasklist;
		TaskList(ArrayList<Task> list) {
			tasklist = list;
		}

		void add(Task task) {
			tasklist.add(task);
		}

		ArrayList<Task> get() {
			return tasklist;
		}

		static void clearAllLists() {
			for (TaskList listType : TaskList.values()) {
				listType.tasklist.clear();
			}
		}
		
		/**
		 * Returns the list of tasklists currently in this enum.
		 */
		static ArrayList<ArrayList<Task>> getSuperlist() {
			ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();
			for (TaskList tasklist : TaskList.values()) {
				superlist.add(tasklist.get());
			}
			return superlist;
		}

		/**
		 * Populate the lists in this enum with dummy task lists.
		 */
		static void populateLists() {
			for (TaskList list : TaskList.values()) {
				Task task = new Task();

				switch (list) {
					case PENDING:
						task.setTaskName("Float");
						task.setTaskType("FLOATING");
						task.setTaskTags(new ArrayList<String>(Arrays.asList(("tag"))));
						FLOATING.add(task);
						PENDING.add(task);
						
						task = new Task();
						task.setTaskName("Deadline");
						task.setTaskType("DEADLINE");
						task.setDeadline(timeConverter.getCurrTime());
						DEADLINE.add(task);
						PENDING.add(task);
						
						task = new Task();
						task.setTaskName("Event");
						task.setTaskType("EVENT");
						task.setStartDate(timeConverter.getCurrTime());
						task.setEndDate(timeConverter.getCurrTime());
						EVENT.add(task);
						PENDING.add(task);
						break;

					case EXPIRED:
						task.setTaskName("Expired task");
						task.setTaskType("FLOATING");
						EXPIRED.add(task);
						break;
						
					case COMPLETED:
						task.setTaskName("Completed task");
						task.setTaskType("FLOATING");
						COMPLETED.add(task);
						break;
						
					case THIS_WEEK: //ignored by Storage
						THIS_WEEK.add(task);
						break;
						
					case ACTION: //ignored by Storage
						ACTION.add(task);
						break;
						
					case DEADLINE: //these lists have already been added
					case EVENT:
					case FLOATING:
						break;
				}
			}
		}
	}
	
	/**
	 * Load the original directory config file (if present) into memory before the running the test.
	 * Otherwise, the file will be lost when overwritten during testing.
	 * @throws IOException
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		originalDirConfigFile = new StorageReader().loadDirectoryConfigFile(Storage.FILENAME_DIRCONFIG);
		if (originalDirConfigFile == null) {
			System.out.println("[StorageTest] No existing directory config file");
		} else {
			System.out.println("[StorageTest] Existing directory config file found");
		}
		storage.setDirectory(testDir.getAbsolutePath(), false); //don't move savefiles to the test folder
	}

	/**
	 * Deletes files created during the test, the test directory, 
	 * and reverts the directory config file back to the original one (if present).
	 * @throws IOException
	 */
	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		// Delete savefiles
		for (File file : testDir.listFiles()) {
			if (Arrays.asList(Storage.FILENAMES).contains(file.getName())) {
				File testfile = new File(testDir, file.getName());
				Files.delete(testfile.toPath());
			}
		}
		System.out.println("[StorageTest] All taskey test savefiles deleted");
		
		// Delete the test directory
		Files.deleteIfExists(testDir.toPath());
		System.out.println("[StorageTest] Test folder deleted");

		// Revert back to previous config file
		if (originalDirConfigFile != null) {
			System.out.println("[StorageTest] Reverting back to original directory");
			new StorageWriter().saveDirectoryConfigFile(originalDirConfigFile, Storage.FILENAME_DIRCONFIG);
		} else {
			// Else if there was no pre-existing config file, delete the one created in the test
			File testDirConfigFile = new File(Storage.FILENAME_DIRCONFIG);
			Files.delete(testDirConfigFile.toPath());
			System.out.println("[StorageTest] Test config file deleted");
		}
	}


	/*===========================*
	 * Test directory management *
	 *===========================*/
	/**
	 * Tests the exceptions thrown by DirectoryManager's changeDirectory method.
	 * @throws IOException
	 */
	@Test
	public void testSetDirectoryExceptions() throws IOException {
		try {
			storage.setDirectory("\\/:*?\"<>|");
			fail("InvalidPathException not thrown for invalid characters *");
		} catch (InvalidPathException e) {
		}
		
		try {
			storage.setDirectory(Storage.FILENAME_DIRCONFIG); //path to a file, not a directory
			fail("NotDirectoryException not thrown for paths that point to a file");
		} catch (NotDirectoryException e) {
		}
		
		try {
			storage.setDirectory(System.getenv("ProgramFiles") + "\\taskey_fubar"); //e.g. c:\program files\taskey_fubar
			fail("AccessDeniedException not thrown when creating folder in restricted directory");
		} catch (AccessDeniedException e) {
		}
		
		try {
			storage.setDirectory(Paths.get(System.getenv("ProgramFiles")).getRoot().toString()); //e.g. c:\
			fail("AccessDeniedException not thrown when creating files in restricted directory");
		} catch (AccessDeniedException e) {
		}
		
		try {
			storage.setDirectory("con");
			fail("FileSystemException not thrown for reserved filename");
		} catch (FileSystemException e) {
		}
		
		try {
			// Generate and save all task lists to the test directory
			TaskList.clearAllLists();
			TaskList.populateLists();
			storage.saveAllTasklists(TaskList.getSuperlist());
			
			// Change the storage directory (without moving files from the test directory)
			storage.setDirectory(Storage.DEFAULT_DIRECTORY.getAbsolutePath(), false);
			
			// Change back to the test directory and invoke DirectoryManager's moveFiles method, 
			// which will throw FileAlreadyExistsException
			storage.setDirectory(testDir.getAbsolutePath(), true);
			
			fail("FileAlreadyExistsException not thrown");
		} catch (FileAlreadyExistsException e) {
			storage.setDirectory(testDir.getAbsolutePath(), false); //revert back to the test directory to continue testing
		}
	}
	
	/**
	 * Tests the moving of files by calling setDirectory(dest, true)
	 * for an empty directory dest.
	 * @throws IOException
	 */
	@Test
	public void testMoveFiles() throws IOException {
		File dest = new File(testDir, "moved");
		// Move the files
		storage.setDirectory(dest.getAbsolutePath(), true);
		for (String filename : dest.list()) {
			assertTrue(Arrays.asList(Storage.FILENAMES).contains(filename));
		}
		// Move them back
		storage.setDirectory(testDir.getAbsolutePath(), true);
	}

	/*================*
	 * Test tasklists *
	 *================*/
	/**
	 * Tests the saving and subsequent loading of task lists
	 * @throws IOException
	 */
	@Test
	public void saveAndLoadTasklists() throws IOException {
		TaskList.clearAllLists();
		TaskList.populateLists();

		ArrayList<ArrayList<Task>> expectedList = new ArrayList<ArrayList<Task>>();	 
		for (TaskList list : taskListsFromStorage) {
			expectedList.add(list.get());
		}

		storage.saveAllTasklists(TaskList.getSuperlist());
		ArrayList<ArrayList<Task>> loadedList = storage.loadAllTasklists();

		assertEquals("Loaded tasklist must be size 6", TasklistEnum.size(), loadedList.size());
		assertTrue(expectedList.equals(loadedList)); 				//test Task's equals method
		assertEquals(toString(expectedList), toString(loadedList)); //test Task's toString method

		//System.out.println("\n" + toString(expectedList));
		//System.out.println(toString(loadedList));
	}


	/*===========*
	 * Test tags *
	 *===========*/
	/**
	 * Tests the saving and subsequent loading of tags
	 * @throws IOException
	 */
	@Test
	public void saveAndLoadTags() throws IOException {
		ArrayList<TagCategory> expectedList = createTaglist();
		storage.saveTaglist(expectedList);
		ArrayList<TagCategory> loadedList = storage.loadTaglist();

		assertNotEquals("taglist should not be empty", 0, loadedList.size());
		assertTrue(expectedList.equals(loadedList));
		assertEquals(toString(expectedList), toString(loadedList));
	}

	private static ArrayList<TagCategory> createTaglist() {
		ArrayList<TagCategory> tags = new ArrayList<TagCategory>();
		for (int i=0; i<3; i++) {
			tags.add(new TagCategory("TestTag_" + i + " "));
		}
		return tags;
	}


	/*==================*
	 * toString methods *
	 *==================*/
	private static String toString(List<ArrayList<Task>> superlist) { //note: type erasure
		String str = "";
		for (ArrayList<Task> list : superlist) {
			for (Task task : list) {
				str += task.toString();
			}
		}
		return str;
	}

	private static String toString(ArrayList<TagCategory> tags) {
		String str = "";
		for (TagCategory tag : tags) {
			str += tag.getTagName();
		}
		return str;
	}
}
