package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import taskey.logic.TagCategory;
import taskey.logic.Task;
import taskey.storage.Storage;
import taskey.storage.Storage.TasklistEnum;
import taskey.storage.StorageReader;

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
		storage.setDirectory(testfolder.getAbsolutePath());
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {		
		// Delete savefiles
		for (File file : testfolder.listFiles()) {
			if (file.getName().endsWith(Storage.FILENAME_EXTENSION)) {
				File testTasklist = new File(testfolder, file.getName());
				Files.delete(testTasklist.toPath());
				System.out.println("[StorageTest] " + file.getName() + " deleted");
			}
		}
		
		// Delete config file
		if (originalDirConfigFile != null) {
			System.out.println("[StorageTest] Setting back to original directory");
			storage.setDirectory(originalDirConfigFile.getAbsolutePath());
		} else {
			// Else if there was no pre-existing config file, delete the one created in the test
			File testDirConfigFile = new File(Storage.FILENAME_DIRCONFIG);
			Files.delete(testDirConfigFile.toPath());
			System.out.println("[StorageTest] Config file deleted");
		}
		
		// Delete folder
		Files.delete(testfolder.toPath());
		System.out.println("[StorageTest] Test folder deleted");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
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
	 * Test tasklists *
	 *================*/
	@Test
	public void saveAndLoadTasklists() throws IOException {
		ArrayList<ArrayList<Task>> savedList = createSuperlist();
		storage.saveAllTasklists(savedList); //TODO use temp folder
		List<ArrayList<Task>> sublist = savedList.subList(1, 7); //storage only saves from index 1 to 6 (both inclusive)

		ArrayList<ArrayList<Task>> loadedList = storage.loadAllTasklists();
		assertEquals(TasklistEnum.size(), loadedList.size()); //loaded list must be size 6

		//System.out.println(toString( sublist ));
		//System.out.println(toString(loadedList));

		assertEquals(toString(sublist), toString(loadedList));
	}

	private ArrayList<ArrayList<Task>> createSuperlist() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (int i=0; i < 8; i++) {
			ArrayList<Task> list = new ArrayList<Task>();
			for (int j=1; j<=3; j++) {
				Task t = new Task("Tasklist" + i +" Task" + j);
				t.setTaskType("floating");
				list.add(t);
			}
			superlist.add(list);
		}

		return superlist;
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
