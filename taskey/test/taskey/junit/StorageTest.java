package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.TimeConverter;
import taskey.storage.Storage;
import taskey.storage.Storage.TasklistEnum;
import taskey.storage.StorageReader;
import static taskey.junit.StorageTest.TaskList.*;

/**
 * @@author A0121618M
 */
public class StorageTest {
	private static Storage storage = new Storage();
	private static File testfolder = new File("Taskey savefiles\\temp_test");
	private static File originalDirConfigFile; //to hold the existing directory config file until end of tests

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		originalDirConfigFile = new StorageReader().loadDirectoryConfigFile(Storage.FILENAME_DIRCONFIG);
		if (originalDirConfigFile == null) {
			System.out.println("[StorageTest] No existing directory config file");
		}
		storage.setDirectory(testfolder.getAbsolutePath(), false); //don't move savefiles to test folder
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		// Delete savefiles
		for (File file : testfolder.listFiles()) {
			if (file.getName().endsWith(Storage.FILENAME_EXTENSION)) {
				File testTasklist = new File(testfolder, file.getName());
				Files.delete(testTasklist.toPath());
			}
		}
		System.out.println("[StorageTest] All " + Storage.FILENAME_EXTENSION + " test files deleted");
		
		// Delete folder
		Files.delete(testfolder.toPath());
		System.out.println("[StorageTest] Test folder deleted");
		
		// Revert back to previous config file
		if (originalDirConfigFile != null) {
			System.out.println("[StorageTest] Reverting back to original directory");
			storage.setDirectory(originalDirConfigFile.getAbsolutePath(), false); //don't move test files to original folder
		} else {
			// Else if there was no pre-existing config file, delete the one created in the test
			File testDirConfigFile = new File(Storage.FILENAME_DIRCONFIG);
			Files.delete(testDirConfigFile.toPath());
			System.out.println("[StorageTest] Test config file deleted");
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*================*
	 * Test tasklists *
	 *================*/
	/**
	 * Enum of all task lists from Logic.
	 */
	enum TaskList {
		THIS_WEEK (new ArrayList<Task>()), //ignored by Storage
		PENDING (new ArrayList<Task>()),   //saved to disk
		EXPIRED (new ArrayList<Task>()),   //saved to disk
		FLOATING (new ArrayList<Task>()),  //derived from pending list
		DEADLINE (new ArrayList<Task>()),  //derived from pending list
		EVENT (new ArrayList<Task>()),     //derived from pending list
		COMPLETED (new ArrayList<Task>()), //saved to disk
		ACTION(new ArrayList<Task>());	   //ignored by Storage
		
		static EnumSet<TaskList> derivedLists = EnumSet.of(EVENT, DEADLINE, FLOATING);
		static EnumSet<TaskList> taskListsFromStorage = EnumSet.range(PENDING, COMPLETED);
		static TimeConverter tc = new TimeConverter();
		
		ArrayList<Task> tasklist;
		TaskList(ArrayList<Task> list) {
			tasklist = list;
		}

		void add(Task t) {
			tasklist.add(t);
		}

		ArrayList<Task> get() {
			return tasklist;
		}

		static void clearAllLists() {
			for (TaskList list : TaskList.values()) {
				list.get().clear();
			}
		}
		
		static void populateLists() {
			for (TaskList list : TaskList.values()) {
				Task task = new Task();
				
				switch (list) {
					case PENDING:
						task.setTaskName("General");
						task.setTaskType("FLOATING");
						FLOATING.add(task);
						PENDING.add(task);
						
						task = new Task();
						task.setTaskName("Deadline");
						task.setTaskType("DEADLINE");
						task.setDeadline(tc.getCurrTime());
						DEADLINE.add(task);
						PENDING.add(task);
						
						task = new Task();
						task.setTaskName("Event");
						task.setTaskType("EVENT");
						task.setStartDate(tc.getCurrTime());
						task.setEndDate(tc.getCurrTime());
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
						
					case THIS_WEEK:
						THIS_WEEK.add(task);
						break;
						
					case ACTION:
						ACTION.add(task);
						break;
						
					case DEADLINE:
					case EVENT:
					case FLOATING:
						break;
				}
			}
		}
		
		static ArrayList<ArrayList<Task>> getSuperlist() {
			ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();
			for (TaskList tasklist : TaskList.values()) {
				superlist.add(tasklist.get());
			}
			return superlist;
		}
	}
	
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
		assertEquals(TasklistEnum.size(), loadedList.size()); //loaded list must be size 6
		assertEquals(toString(expectedList), toString(loadedList));
		
		System.out.println("\n" + toString(expectedList));
		System.out.println(toString(loadedList));
	}

	
	/*===========*
	 * Test tags *
	 *===========*/
	@Test
	public void saveAndLoadTags() throws IOException {
		ArrayList<TagCategory> savedTags = createTaglist();
		storage.saveTaglist(savedTags);

		ArrayList<TagCategory> loadedTags = storage.loadTaglist();
		assertNotEquals(0, loadedTags.size()); //taglist must not be empty

		//System.out.println(toString(savedTags));
		//System.out.println(toString(loadedTags));

		assertEquals(toString(savedTags), toString(loadedTags));
	}

	private static ArrayList<TagCategory> createTaglist() {
		ArrayList<TagCategory> tags = new ArrayList<TagCategory>();
		for (int i=0; i<3; i++) {
			tags.add(new TagCategory("TestTag_" + i + " "));
		}
		return tags;
	}

	
	/*================*
	 * Helper methods *
	 *================*/
	private static String toString(List<ArrayList<Task>> superlist) {
		String str = "";
		for (ArrayList<Task> list : superlist) {
			for (Task t : list) {
				str += t.toString();
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

	@SuppressWarnings("unused")
	private static <E> void print(ArrayList<ArrayList<E>> lists) {
		int i = 0;
		for (ArrayList<E> list : lists) {
			System.out.println(i++);
			for (E t : list) {
				System.out.println(t);
			}
		}
	}
}
