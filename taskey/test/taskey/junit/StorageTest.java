package taskey.junit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

	@Test
	public final void saveAndLoadTasklists() throws IOException {
		ArrayList<ArrayList<Task>> superlist = createSuperlist();
		storage.saveAllTasklists(superlist);
		
		ArrayList<ArrayList<Task>> loadedlist = storage.loadAllTasklists();
		assertEquals(toString(superlist), toString(loadedlist));
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

	
	private static String toString(ArrayList<ArrayList<Task>> superlist) {
		String str = "";
		for (ArrayList<Task> list : superlist) {
			for (Task t : list)
				str.concat(t.toString());
		}
		return str;
	}

}
