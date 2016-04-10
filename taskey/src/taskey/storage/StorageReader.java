package taskey.storage;

import static taskey.storage.Storage.TasklistEnum.PENDING;
import static taskey.storage.StorageReader.DerivedList.DEADLINE_TASKLIST;
import static taskey.storage.StorageReader.DerivedList.EVENT_TASKLIST;
import static taskey.storage.StorageReader.DerivedList.GENERAL_TASKLIST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.storage.Storage.TasklistEnum;
import taskey.storage.TaskVerifier.InvalidTaskException;

/**
 * @@author A0121618M
 */
public class StorageReader {
	TaskVerifier taskVerifier = new TaskVerifier();

	/**
	 * These three lists are derived from the PENDING list.
	 * Hence, they do not need to be read/written to disk.
	 */
	enum DerivedList {
		GENERAL_TASKLIST (new ArrayList<Task>()),
		DEADLINE_TASKLIST (new ArrayList<Task>()),
		EVENT_TASKLIST (new ArrayList<Task>());

		ArrayList<Task> tasklist;

		DerivedList(ArrayList<Task> list) {
			tasklist = list;
		}

		void add(Task t) {
			tasklist.add(t);
		}

		ArrayList<Task> get() {
			return tasklist;
		}

		static void clearAllLists() {
			for (DerivedList list : DerivedList.values()) {
				list.get().clear();
			}
		}
	}

	/**
	 * Generic read method. Deserializes the JSON specified by src into an object of the specified type.
	 * @param src JSON file to be read
	 * @param typeToken represents the generic type T of the desired object; 
	 * 		  this is obtained from the Gson TypeToken class
	 * @return An object of type T generated from the JSON file.
	 * @throws FileNotFoundException if FileReader constructor fails
	 */
	private <T> T readFromFile(File src, TypeToken<T> typeToken) throws FileNotFoundException {
		FileReader reader = new FileReader(src);
		Gson gson = new Gson();
		T object = gson.fromJson(reader, typeToken.getType()); //TODO Handle type safety
		try {
			reader.close(); //must close the stream to allow deletion of files by StorageTest
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}

	/*============*
	 * Load tasks *
	 *============*/
	/**
	 * Returns the ArrayList of Task objects read from the File src.
	 * The GENERAL/DEADLINE/EVENT lists are not read from file; instead, they are derived from the PENDING list.
	 * Pre-condition: the PENDING list must be read before the derived lists.
	 * @param src the source file to be read from
	 * @param tasklistType
	 * @return the tasklist read from file
	 * @throws FileNotFoundException 
	 * @throws InvalidTaskException 
	 */
	ArrayList<Task> loadTasklist(File src, TasklistEnum tasklistType) throws FileNotFoundException, InvalidTaskException {
		ArrayList<Task> tasklist;
		switch (tasklistType) {
			default: //default case is just to avoid compilation error
			case PENDING:
			case EXPIRED:
			case COMPLETED:
				try {
					tasklist = readFromFile(src, new TypeToken<ArrayList<Task>>() {});
					taskVerifier.verify(tasklist);
					taskVerifier.checkDates(tasklist);
				} catch (InvalidTaskException e) {
					System.err.println("{Storage} Invalid tasklist | " + src.getName());
					throw e; //TODO dump file into invalid subfolder so tt its not overwritten
				} catch (FileNotFoundException e) {
					tasklist = new ArrayList<Task>();
				}
	
				if (tasklistType == PENDING) {
					 // Generate the GENERAL, DEADLINE and EVENT lists from the PENDING list
					getDerivedLists(tasklist);
				}
				break;

			case GENERAL:
				return GENERAL_TASKLIST.get();
			case DEADLINE:
				return DEADLINE_TASKLIST.get();
			case EVENT:
				return EVENT_TASKLIST.get();
		}
		return tasklist;
	}

	/**
	 * Derives the GENERAL, DEADLINE, and EVENT tasklists from the PENDING list.
	 * @param pendingList
	 */
	private void getDerivedLists(ArrayList<Task> pendingList) {
		DerivedList.clearAllLists(); //erase the previous data, if any
		for (Task task : pendingList) {
			switch (task.getTaskType().toUpperCase()) {
				case "FLOATING":
					GENERAL_TASKLIST.add(task);
					break;
				case "DEADLINE":
					DEADLINE_TASKLIST.add(task);
					break;
				case "EVENT":
					EVENT_TASKLIST.add(task);
					break;
			}
		}
	}

	/*===========*
	 * Load tags *
	 *===========*/
	/**
	 * Returns an ArrayList of Tags read from the File src.
	 * An empty ArrayList is returned if src was not found.
	 * @param src source file to be read
	 * @return ArrayList containing the user-defined tags, or an empty ArrayList if src was not found
	 */
	ArrayList<TagCategory> loadTaglist(File src) {
		ArrayList<TagCategory> tags;
		try {
			tags = readFromFile(src, new TypeToken<ArrayList<TagCategory>>() {});
		} catch (FileNotFoundException e) {
			tags = new ArrayList<TagCategory>();
		}
		return tags;
	}
	
	/*================*
	 * Load directory *
	 *================*/
	/**
	 * Tries to read the last-saved directory from a config file located in System.getProperty("user.dir").
	 * @param filename name of the config file to be read
	 * @return the File representing the last-saved directory, or null if it was not found
	 */
	public File loadDirectoryConfigFile(String filename) {
		File src = new File(filename);
		try {
			//FIXME: buggyPath will somehow always have user.dir prepended to its absolute path
			File buggyPath = readFromFile(src, new TypeToken<File>() {});
			File fixedPath = new File(buggyPath.getPath()); //kludge solution
			return fixedPath;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}