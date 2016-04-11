package taskey.storage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.TimeConverter;

/**
 * @@author A0121618M
 * This class is used by StorageReader to check the validity of the task and tag lists read from file,
 * in case the user makes mistakes while editing them.
 * It also checks whether the date(s) in the files have been edited and sets them accordingly.
 */
class StorageVerifier {
	TimeConverter timeConverter = new TimeConverter();
	
	StorageVerifier() {
	}
	
	@SuppressWarnings("serial")
	class InvalidTaskException extends Exception {
	}
	@SuppressWarnings("serial")
	class InvalidTagException extends Exception {
	}

	/**
	 * Sanity check for when reading a tasklist. Checks that all Task objects in a given list are valid.
	 * The only field that needs to be checked to prevent program from crashing is the task type.
	 * @param tasklist the tasklist to be checked
	 * @throws InvalidTaskException when the taskType is null or invalid
	 */
	void verifyTasks(ArrayList<Task> tasklist) throws InvalidTaskException {
		for (Task task : tasklist) {
			if (task.getTaskType() == null) {
				throw new InvalidTaskException();
			} else if (! (task.getTaskType().equalsIgnoreCase("FLOATING")
					|| task.getTaskType().equalsIgnoreCase("DEADLINE") 
					|| task.getTaskType().equalsIgnoreCase("EVENT")) ) {
				throw new InvalidTaskException();
			}
		}
	}
	
	/**
	 * Sanity check for when reading a taglist. Checks that all tag objects in the given list are valid.
	 * The tag name cannot be null or it will cause UI to crash due to NullPointerException.
	 * @param taglist the tag list to be checked
	 * @throws InvalidTagException if any tagName field is null
	 */
	void verifyTags(ArrayList<TagCategory> taglist) throws InvalidTagException {
		for (TagCategory tag : taglist) {
			if (tag.getTagName() == null) {
				throw new InvalidTagException();
			}
		}
	}

	/**
	 * Checks whether any Task in the given tasklist has had their dates/times changed 
	 * by the user editing the JSON files. If so, then the Task's epoch date(s) will be modified
	 * to match the human-readable time. This assumes that any user editing the files would be
	 * changing the human-readable dates, and not the epoch dates.
	 * The reason this check needs to be done is that Taskey only uses epoch time in its
	 * internal implementation; without this check, human readable dates would be ignored.
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
				// Time was given in HH:mm format, but HH:mm:ss is needed for TimeConverter
				convertedEpoch = timeConverter.toEpochTime(humanTime + ":00");
			} else if (tokens.length == 3) {
				// Time is in HH:mm:ss format
				convertedEpoch = timeConverter.toEpochTime(humanTime);
			} else {
				throw new ParseException("Wrong human date format", 0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("{Storage} Invalid user-edited date");
			return epochTime;
		}

		System.out.println("[AFTER]  " + new Date(convertedEpoch * 1000));
		return convertedEpoch;
	}

}
