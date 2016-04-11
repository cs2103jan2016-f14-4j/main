package taskey.storage;

import static taskey.storage.Storage.TasklistEnum.PENDING;
import static taskey.storage.StorageReader.DerivedList.DEADLINE_TASKLIST;
import static taskey.storage.StorageReader.DerivedList.EVENT_TASKLIST;
import static taskey.storage.StorageReader.DerivedList.GENERAL_TASKLIST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.storage.Storage.TasklistEnum;
import taskey.storage.TaskVerifier.InvalidTaskException;

/**
 * @@author A0121618M
 * This class provides methods to Storage for 
 * reading tasklists, taglists and abstract paths from their JSON files.
 * It also uses TaskVerifier to perform input validation on the Task objects read from file.
 * This class is public so that it is visible to taskey.junit.StorageTest
 */
public class StorageReader {
	TaskVerifier taskVerifier = new TaskVerifier();

	/**
	 * These three lists are derived from the PENDING list.
	 * They are not read from, or written to, disk.
	 */
	enum DerivedList {
		GENERAL_TASKLIST (new ArrayList<Task>()),
		DEADLINE_TASKLIST (new ArrayList<Task>()),
		EVENT_TASKLIST (new ArrayList<Task>());

		private ArrayList<Task> tasklist;

		private DerivedList(ArrayList<Task> list) {
			tasklist = list;
		}

		private void add(Task t) {
			tasklist.add(t);
		}

		private ArrayList<Task> get() {
			return tasklist;
		}

		private static void clearAllLists() {
			for (DerivedList list : DerivedList.values()) {
				list.get().clear();
			}
		}
	}

	/**
	 * Generic read method.
	 * Deserializes the JSON file specified by src into an object of the specified type.
	 * @param src JSON file to be read
	 * @param typeToken represents the generic type T of the desired object; 
	 * 		  this is obtained from the Gson TypeToken class
	 * @return An object of type T generated from the JSON file.
	 * @throws FileNotFoundException if FileReader constructor fails due to it being unable 
	 * 		   to open the file for reading. This could be because the file doesn't exist or access was denied.
	 * @throws JsonParseException fromJson throws JsonIOException, JsonSyntaxException
	 */
	private <T> T readFromFile(File src, TypeToken<T> typeToken) throws FileNotFoundException, 
																		JsonParseException {
		FileReader reader = new FileReader(src);
		Gson gson = new Gson();
		T object;
		try {
			object = gson.fromJson(reader, typeToken.getType());
		} catch (JsonParseException e) {
			throw e;
		} finally {
			try {
				reader.close(); //must close the stream to allow deleting/moving of files
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (object == null) {
			throw new JsonParseException("fromJson returned null due to EOF i.e. file was empty");
		}
		return object;
	}

	/*============*
	 * Load tasks *
	 *============*/
	/**
	 * Returns the ArrayList of Task objects read from the File src.
	 * The GENERAL/DEADLINE/EVENT lists are not read from file;
	 * instead, they are derived from the PENDING list.
	 * <p>Pre-condition:
	 * <br>- the PENDING list must be read before the derived lists, 
	 * 		 so that the derived lists in the enum are populated first.
	 * @param src the source file to be read from
	 * @param tasklistType the TasklistEnum constant passed from Storage
	 * @return the tasklist read from file or an empty tasklist if file was not found/is invalid
	 */
	ArrayList<Task> loadTasklist(File src, TasklistEnum tasklistType) {
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
				} catch (InvalidTaskException | JsonParseException e) {
					e.printStackTrace();
					System.err.println("{Storage} Invalid tasklist: " + src.getName());
					renameBadFile(src);
					tasklist = new ArrayList<Task>(); //return empty list
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
	 * Renames the given file, which could be invalid due to a malformed JSON or invalid tasks.
	 * This is done so that if the user makes a mistake while editing the task files,
	 * they can still recover it, instead of losing them when they get overwritten 
	 * the next time the user saves (which happens on program close!).
	 * @param src abstract path of the bad file
	 */
	private void renameBadFile(File src) {
		try {
			Files.move(src.toPath(), src.toPath().resolveSibling("INVALID." + src.getName()), 
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.err.println("{Storage} Could not rename bad file");
			e.printStackTrace();
		}
	}

	/**
	 * Derives the GENERAL, DEADLINE, and EVENT tasklists from the PENDING list.
	 * @param pendingList the PENDING list
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
	 * @return the taglist read from file or an empty taglist if file was not found/is invalid
	 */
	ArrayList<TagCategory> loadTaglist(File src) {
		ArrayList<TagCategory> tags;
		try {
			tags = readFromFile(src, new TypeToken<ArrayList<TagCategory>>() {});
		} catch (JsonParseException e) { //TODO invalid tag exception. JsonParseException not sufficient
			e.printStackTrace();
			System.err.println("{Storage} Invalid taglist: " + src.getName());
			renameBadFile(src);
			tags = new ArrayList<TagCategory>(); //return empty list
		} catch (FileNotFoundException e) {
			tags = new ArrayList<TagCategory>();
		}
		return tags;
	}

	/*================*
	 * Load directory *
	 *================*/
	/**
	 * Tries to read the last-saved directory from the config file located in System.getProperty("user.dir").
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