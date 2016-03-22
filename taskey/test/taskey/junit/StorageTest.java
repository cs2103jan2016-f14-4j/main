package taskey.junit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import taskey.logic.TagCategory;
import taskey.logic.Task;
import taskey.storage.Storage;
import taskey.storage.Storage.TasklistEnum;

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
		ArrayList<TagCategory> tags = createTaglist();
		storage.saveTaglist(tags);
		
		ArrayList<TagCategory> loadedList = storage.loadTaglist();
		assertNotEquals(0, loadedList.size()); //taglist must not be empty
		assertEquals(taglistToString(tags), taglistToString(loadedList));
	}
	
	private static ArrayList<TagCategory> createTaglist() {
		ArrayList<TagCategory> tags = new ArrayList<TagCategory>();
		for (int i=0; i<3; i++) {
			tags.add(new TagCategory("tag_" + i));
		}
		return tags;
	}
	
    /*================*
     * Test tasklists *
     *================*/
	@Test
	public void saveAndLoadTasklists() throws IOException {
		ArrayList<ArrayList<Task>> superlist = createSuperlist();
		storage.saveAllTasklists(superlist); //TODO use temp folder
		
		ArrayList<ArrayList<Task>> loadedlist = storage.loadAllTasklists();
		assertEquals(TasklistEnum.values().length, loadedlist.size()); //loaded list must be size 7
		assertEquals(superlistToString(superlist), superlistToString(loadedlist));
	}

	private ArrayList<ArrayList<Task>> createSuperlist() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (int i=0; i <= TasklistEnum.values().length; i++) {
			ArrayList<Task> list = new ArrayList<Task>();
			for (int j=0; j<3; j++) {
				list.add(new Task("Tasklist" + i + ": Task" + j));
			}
			superlist.add(list);
		}

		return superlist;
	}


    /*================*
     * Helper methods *
     *================*/
	private static String superlistToString(ArrayList<ArrayList<Task>> superlist) {
		String str = "";
		for (ArrayList<Task> list : superlist) {
			for (Task t : list)
				str.concat(t.toString());
		}
		return str;
	}
	
	private static String taglistToString(ArrayList<TagCategory> tags) {
		String str = "";
		for (TagCategory tag : tags) {
			str.concat(tag.getTagName());
		}
		return str;
	}
	

}
