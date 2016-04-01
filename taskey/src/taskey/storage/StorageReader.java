package taskey.storage;

import static taskey.storage.Storage.TasklistEnum.PENDING;
import static taskey.storage.StorageReader.DerivedList.DEADLINE_TASKLIST;
import static taskey.storage.StorageReader.DerivedList.EVENT_TASKLIST;
import static taskey.storage.StorageReader.DerivedList.GENERAL_TASKLIST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.TimeConverter;
import taskey.storage.Storage.TasklistEnum;

/**
 * @@author A0121618M
 */
public class StorageReader {
	TimeConverter timeConverter = new TimeConverter();
	//EnumSet<TasklistEnum> derivedLists = EnumSet.of(GENERAL, DEADLINE, EVENT);

	/**
	 * These three lists are derived from the PENDING list.
	 * Hence, they do not need to be read/written to disk.
	 * Instead, they are derived from the PENDING list.
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
			//FIXME: buggyPath will somehow always have user.dir prefixed in its absolute path
			File buggyPath = readFromFile(src, new TypeToken<File>() {});
			File fixedPath = new File(buggyPath.getPath()); //kludge solution
			return fixedPath;
		} catch (FileNotFoundException e) {
			return null;
		}
	}


	/*============*
	 * Load tasks *
	 *============*/
	@SuppressWarnings("serial")
	class InvalidTaskException extends Exception {
	}

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
					verifyTasks(tasklist);
					checkTaskDates(tasklist);
				} catch (InvalidTaskException e) {
					System.err.println("{Storage} Invalid tasklist | " + src.getName());
					throw e;
				}
	
				if (tasklistType == PENDING) {
					getDerivedLists(tasklist); //generate the derived lists from the PENDING list
				}
				break;
	
			case GENERAL:
				jsonShouldBeEqual(src, GENERAL_TASKLIST.get()); //temporary check
				return GENERAL_TASKLIST.get();
			case DEADLINE:
				jsonShouldBeEqual(src, DEADLINE_TASKLIST.get()); //temporary check
				return DEADLINE_TASKLIST.get();
			case EVENT:
				jsonShouldBeEqual(src, EVENT_TASKLIST.get()); //temporary check
				return EVENT_TASKLIST.get();
		}
		return tasklist;
	}

	/**
	 * Derives the GENERAL, DEADLINE, and EVENT tasklists from the PENDING list.
	 * @param pendingList
	 */
	private void getDerivedLists(ArrayList<Task> pendingList) {
		DerivedList.clearAllLists(); //erase the previous data
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
	
	/**
	 * Temporary method to check that the generated GENERAL/DEADLINE/EVENT tasklist is equal to the savefile.
	 * This method will be deprecated once the reduced number of savefiles are deemed stable.
	 * @param src
	 * @param derivedTasklist
	 */
	private void jsonShouldBeEqual(File src, ArrayList<Task> derivedTasklist) {
		ArrayList<Task> tasklistFromFile;
		try {
			tasklistFromFile = readFromFile(src, new TypeToken<ArrayList<Task>>(){});
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}
		Gson gson = new Gson();
		String json1 = gson.toJson(derivedTasklist, new TypeToken<ArrayList<Task>>(){}.getType());
		String json2 = gson.toJson(tasklistFromFile, new TypeToken<ArrayList<Task>>(){}.getType());
		System.out.println(json1.equals(json2)); //in case assertions aren't enabled
		//assert (json1.equals(json2));
	}

	/**
	 * Sanity check for when reading a tasklist. Checks that all Task objects in a given list are valid;
	 * i.e. that they follow the contract laid out in the Task class.
	 * TODO: add more checks
	 * @param tasklist
	 * @throws InvalidTaskException
	 */
	private void verifyTasks(ArrayList<Task> tasklist) throws InvalidTaskException {
		for (Task t : tasklist) {
			if (t.getTaskType() == null) {
				throw new InvalidTaskException();
			} else if (! (t.getTaskType().equalsIgnoreCase("FLOATING")
					|| t.getTaskType().equalsIgnoreCase("DEADLINE") 
					|| t.getTaskType().equalsIgnoreCase("EVENT")) ) {
				throw new InvalidTaskException();
			}
		}
	}

	/**
	 * Checks whether any Task in the given tasklist has had their dates/times changed 
	 * by the user editing the JSON files. If so, then the Task's epoch date(s) will be modified
	 * to fit the human-readable time.
	 * @param tasklist the list of tasks to be checked
	 */
	private void checkTaskDates(ArrayList<Task> tasklist) {
		String humanTime;
		long epochTime;

		for (Task task : tasklist) {
			String taskType = task.getTaskType();

			switch (taskType.toUpperCase()) {
				case "DEADLINE":
					humanTime = task.getDeadlineFull(); //HH:mm by default in Taskey (or up to HH:mm:ss if user-edited)
					epochTime = task.getDeadlineEpoch(); //the original time with precision in seconds

					if (timeWasEdited(humanTime, epochTime)) {
						epochTime = getEpochTime(humanTime, epochTime);
						task.setDeadline(epochTime);
					}
					break;

				case "EVENT":
					// Check start time of event
					humanTime = task.getStartDateFull();
					epochTime = task.getStartDateEpoch();
					if (timeWasEdited(humanTime, epochTime)) {
						epochTime = getEpochTime(humanTime, epochTime);
						task.setStartDate(epochTime);
					}

					// Check end time of event
					humanTime = task.getEndDateFull();
					epochTime = task.getEndDateEpoch(); 
					if (timeWasEdited(humanTime, epochTime)) {
						epochTime = getEpochTime(humanTime, epochTime);
						task.setEndDate(epochTime);
					}
					break;
			}
		}
	}

	/**
	 * Returns true if the given human and epoch times are NOT equivalent.
	 * I.e. this means that one of them has been edited by the user.
	 * Assume that the one edited by the user is the human time.
	 * @param humanTime user-editable human time in HH:mm (or HH:mm:ss)
	 * @param epochTime in seconds. Hence the precision will be >= that of human time.
	 * @return true if not equal (edited); false if equal (unedited)
	 */
	private boolean timeWasEdited(String humanTime, long epochTime) {
		if (! humanTime.equals(timeConverter.toHumanTime(epochTime)) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Attempts to convert the given human time to its epoch equivalent.
	 * If failed, the given epochTime will be returned instead.
	 * @param humanTime user-editable human time in HH:mm (or HH:mm:ss)
	 * @param epochTime this will always have a precision in seconds 
	 * @return the epoch time converted from the given human time;
	 * 		   otherwise, if there is a parse error, the given epochTime will be returned instead
	 */
	private long getEpochTime(String humanTime, long epochTime) {
		System.out.println();
		System.out.println("{Storage} A date was edited. Converting:");
		System.out.println("[BEFORE] " + new Date(epochTime * 1000));

		long convertedEpoch;
		try {
			String[] tokens = humanTime.split(":"); 
			if (tokens.length == 2) {
				// Time was given in HH:mm format, but HH:mm:ss is needed
				convertedEpoch = timeConverter.toEpochTime(humanTime + ":00");
			} else if (tokens.length == 3) {
				// Time in HH:mm:ss format
				convertedEpoch = timeConverter.toEpochTime(humanTime);
			} else {
				throw new ParseException("", 0);
			}
		} catch (ParseException e) {
			System.err.println("{Storage} Invalid user-edited date");
			return epochTime;
		}

		System.out.println("[AFTER]  " + new Date(convertedEpoch * 1000));
		return convertedEpoch;
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
}