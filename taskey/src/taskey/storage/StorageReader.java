package taskey.storage;

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

/**
 * @@author A0121618M
 */
public class StorageReader {
	TimeConverter timeConverter = new TimeConverter();

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
			reader.close(); //close stream to allow deletion of files by StorageTest
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
			//TODO: buggyPath will somehow always have user.dir prefixed in its absolute path
			File buggyPath = readFromFile(src, new TypeToken<File>() {});
			File fixedPath = new File(buggyPath.getPath()); //kludge solution
			return fixedPath;
		} catch (FileNotFoundException e) {
			return null;
		}
	}


	/*================*
	 * Load task list *
	 *================*/
	@SuppressWarnings("serial")
	class InvalidTaskException extends Exception {
	}

	/**
	 * Returns an ArrayList of Task objects read from the File src.
	 * An empty ArrayList is returned if src was not found.
	 * @param src the source file to be read from
	 * @return the tasklist read from file; or an empty list if the file was not found
	 * @throws FileNotFoundException 
	 * @throws InvalidTaskException 
	 */
	ArrayList<Task> loadTasklist(File src) throws FileNotFoundException, InvalidTaskException {
		ArrayList<Task> tasklist;
		try {
			tasklist = readFromFile(src, new TypeToken<ArrayList<Task>>() {});
			verifyTasklist(tasklist);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (InvalidTaskException e) {
			System.err.println("{Storage} Invalid tasklist | " + src.getName());
			throw e;
		}
		checkTaskDates(tasklist);
		return tasklist;
	}

	/**
	 * Sanity check for when reading a tasklist. Checks that all Task objects in a given list are valid;
	 * i.e. that they follow the contract laid out in the Task class.
	 * TODO: add more checks
	 * @param tasklist
	 * @throws StorageException
	 */
	private void verifyTasklist(ArrayList<Task> tasklist) throws InvalidTaskException {
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
	 * TODO: no need to check general list. Consolidate files (i.e. dun save the task type lists)? 
	 * 										Or move loadAllTaskLists into this class?
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
					humanTime = task.getStartDateFull();
					epochTime = task.getStartDateEpoch();
					if (timeWasEdited(humanTime, epochTime)) {
						epochTime = getEpochTime(humanTime, epochTime);
						task.setStartDate(epochTime);
					}

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
	 * Attempts to converts the given human time to its epoch equivalent.
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