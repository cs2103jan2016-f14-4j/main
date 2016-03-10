package taskey.storage;

/**
 * This enum standardizes all the possible filenames and their types.
 * Feel free to edit this as the number or types of lists change.

 * To loop over all the task lists only (and skip the tags), you can do this:
	for (FileType category : FileType.values()) {
		if (category == TAGS || category == INVALID) {
			break;
		}
		ArrayList<Task> list = getTaskList(category);
		// etc
	}

 * @author Dylan
 */
public enum FileType {
	// Task lists
    PENDING		("PENDING.taskey"),
    EXPIRED		("EXPIRED.taskey"),
    GENERAL		("GENERAL.taskey"),
    DEADLINE	("DEADLINE.taskey"),
    EVENT		("EVENT.taskey"),
    COMPLETED	("COMPLETED.taskey"),
    // Tag map
    TAGS		("USER_TAG_DB.taskey"),
    INVALID		(null);

    private final String filename;

    FileType(String filename) {
        this.filename = filename;
    }

    /**
     * Legacy method.
     * Converts our old filenames to the corresponding new enum types.
     * @param filename string
     * @return the corresponding enum type
     */
    public static FileType getType(String filename) {
    	switch (filename) {
    		case "PENDING.taskey":
    			return PENDING;
    		case "EXPIRED.taskey":
    			return EXPIRED;
    		case "GENERAL.taskey":
    			return GENERAL;
    		case "DEADLINE.taskey":
    			return DEADLINE;
    		case "EVENT.taskey":
    			return EVENT;
    		case "COMPLETED.taskey":
    			return COMPLETED;
    		case "TAGS.taskey":
    		case "USER_TAG_DB.taskey":
    			return TAGS;
    		default:
    			return INVALID;
    	}
    }

    /**
     * Returns the filename corresponding to this enum type.
     * @return the filename string or an empty string if this enum type == INVALID
     */
    public String getFilename() {
    	if (this == INVALID || filename == null) {
    		return "";
    	}
    	return filename;
    }
}
