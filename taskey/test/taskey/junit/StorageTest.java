package taskey.junit;

import static org.junit.Assert.*;

import java.io.IOException;
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

/**
 * @@author A0121618M
 */
public class StorageTest {

	Storage storage;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		storage = new Storage();
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
		
		System.out.println(toString(savedTags));
		System.out.println(toString(loadedTags));
		
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
		
		System.out.println(toString( sublist ));
		System.out.println(toString(loadedList));
		
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
