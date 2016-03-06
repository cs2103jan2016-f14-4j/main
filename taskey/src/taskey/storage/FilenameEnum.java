package taskey.storage;

/**
 * This enum standardizes all the possible filenames and their types.
 * Currently only used in History.java
 * @author Dylan
 */
public enum FilenameEnum {
	// Task lists
    PENDING		("PENDING.taskey"),
    EXPIRED		("EXPIRED.taskey"),
    GENERAL		("GENERAL.taskey"),
    DEADLINE	("DEADLINE.taskey"),
    EVENT		("EVENT.taskey"),
    COMPLETED	("COMPLETED.taskey"),
    // Tag map
    TAGS		("TAGS.taskey"),
    INVALID		(null);

    private final String filename;

    FilenameEnum(String filename) {
        this.filename = filename;
    }

    /**
     * Converts the (old) filename to the corresponding (new) enum type.
     * @param filename string
     * @return the corresponding enum type
     */
    public static FilenameEnum getType(String filename) {
    	switch (filename) {
    		case "PENDING":
    			return PENDING;
    		case "EXPIRED":
    			return EXPIRED;
    		case "GENERAL":
    			return GENERAL;
    		case "DEADLINE":
    			return DEADLINE;
    		case "EVENT":
    			return EVENT;
    		case "COMPLETED":
    			return COMPLETED;
    		case "TAGS":
    			return TAGS;
    		default:
    			return INVALID;
    	}
    }

    public String getFilename() {
    	return filename;
    }
}
