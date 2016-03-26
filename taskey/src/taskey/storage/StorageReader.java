package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.TagCategory;
import taskey.logic.Task;

/**
 * @@author A0121618M
 */
class StorageReader {
	@SuppressWarnings("serial")
	public class InvalidTaskException extends Exception {
	}
	
    /**
     * Generic read method. Deserializes the JSON specified by src into an object of the specified type.
     * @param src JSON file to be read
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @return An object of type T generated from the JSON file.
     * @throws FileNotFoundException if FileReader constructor fails
     */
    private <T> T readFromFile(File src, TypeToken<T> typeToken) throws FileNotFoundException {
    	FileReader reader = new FileReader(src);
    	Gson gson = new Gson();
		T object = gson.fromJson(reader, typeToken.getType()); //TODO Handle type safety
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
    File loadDirectory(String filename) {
    	File src = new File(filename);
    	try {
    		//TODO: buggyPath will somehow always have user.dir prefixed in its absolute path
    		File buggyPath = readFromFile(src, new TypeToken<File>() {});
    		File fixedPath = new File(buggyPath.getPath()); //hackey solution
    		return fixedPath;
    	} catch (FileNotFoundException e) {
    		return null;
    	}
    }
    

    /*================*
     * Load task list *
     *================*/
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
			System.out.println("{At least one tasklist file was not found} " + src.getName());
			throw e;
		} catch (InvalidTaskException e) {
			System.out.println("{Tasklist invalid} " + src.getName());
			throw e;
		}
    	return tasklist;
    }
    
    /**
     * Sanity check for when reading tasklists.
     * @param tasklist
     * @throws StorageException
     */
    void verifyTasklist(ArrayList<Task> tasklist) throws InvalidTaskException {
    	for (Task t : tasklist) {
    		if (t.getTaskType() == null) {
    			throw new InvalidTaskException();
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
}