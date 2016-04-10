package taskey.storage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import taskey.messenger.Task;
import taskey.parser.TimeConverter;

/**
 * @@author A0121618M
 * This class is used by StorageReader to check the validity of the task lists read from file,
 * in case the user makes mistakes when editing them.
 * It also checks whether the date(s) in the files have been edited and sets them accordingly.
 */
class TaskVerifier {
	TimeConverter timeConverter = new TimeConverter();
	
	TaskVerifier() {
	}
	
	@SuppressWarnings("serial")
	class InvalidTaskException extends Exception {
	}
	
	/**
	 * Temporary method to check that the generated GENERAL/DEADLINE/EVENT tasklist is equal to the savefile.
	 * This method will be deprecated once the reduced number of savefiles are deemed stable.
	 * @param src
	 * @param derivedTasklist
	 */
//	private void jsonShouldBeEqual(File src, ArrayList<Task> derivedTasklist) {
//		ArrayList<Task> tasklistFromFile;
//		try {
//			tasklistFromFile = readFromFile(src, new TypeToken<ArrayList<Task>>(){});
//		} catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//			return;
//		}
//		Gson gson = new Gson();
//		String json1 = gson.toJson(derivedTasklist, new TypeToken<ArrayList<Task>>(){}.getType());
//		String json2 = gson.toJson(tasklistFromFile, new TypeToken<ArrayList<Task>>(){}.getType());
//		System.out.println(json1.equals(json2)); //in case assertions aren't enabled
//		//assert (json1.equals(json2)); //disabled cuz manually editing savefiles will cause assertion to trigger
//	}

	/**
	 * Sanity check for when reading a tasklist. Checks that all Task objects in a given list are valid;
	 * i.e. that they follow the contract laid out in the Task class.
	 * TODO: add more checks
	 * @param tasklist
	 * @throws InvalidTaskException
	 */
	void verify(ArrayList<Task> tasklist) throws InvalidTaskException {
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
	void checkDates(ArrayList<Task> tasklist) {
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

}
