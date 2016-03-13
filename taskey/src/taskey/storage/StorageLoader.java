package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.Task;

/**
 * @author Dylan
 */
class StorageLoader {
    /**
     * Generic read method.
     * Deserializes the JSON specified by the File into an object of the specified type.
     * @param file JSON file to be read
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @return An object of type T generated from the JSON file.
     * @throws FileNotFoundException
     */
    private <T> T readFromFile(File file, TypeToken<T> typeToken) throws FileNotFoundException {
    	FileReader reader = new FileReader(file);
    	Gson gson = new Gson();
		T object = gson.fromJson(reader, typeToken.getType()); //TODO Handle type safety
		return object;
    }

    /*================*
     * Load directory *
     *================*/
    /**
     * Attempts to load the last-saved directory from file in System.getProperty("user.dir")
     * @param filename name of the config file to be read
     * @return the File representing the last-saved directory, or null if it was not found.
     */
    File loadDirectory(String filename) {
    	File src = new File(filename);
    	try {
    		return readFromFile(src, new TypeToken<File>() {});
    	} catch (FileNotFoundException e) {
    		return null;
    	}
    }

    /*================*
     * Load task list *
     *================*/
    /**
     * Returns an ArrayList of Task objects read from the File src.
     * An empty ArrayList is returned if the file was not found.
     * @param src represents the file to be read
     * @return The task list loaded from file; or an empty list if the file does not exist.
     */
    ArrayList<Task> loadTasklist(File src) {
    	ArrayList<Task> tasks;
		try {
			tasks = readFromFile(src, new TypeToken<ArrayList<Task>>() {});
			System.out.println("{Tasklist loaded} " + src.getName()); //log info
		} catch (FileNotFoundException e) {
			System.out.println("{Tasklist not found} " + src.getName()); //log info
			tasks = new ArrayList<Task>();
		}
    	return tasks;
    }

    /*===========*
     * Load tags *
     *===========*/
    /**
     * Returns the HashMap containing the user-defined tags read from file.
     * An empty HashMap is returned if the tags file was not found.
     * @param src represents the file to be read
     * @return the HashMap read from file, or an empty HashMap if the file was not found.
     */
    HashMap<String, Integer> loadTags(File src) {
    	HashMap<String, Integer> tags;
    	try {
    		tags = readFromFile(src, new TypeToken<HashMap<String, Integer>>() {});
    		System.out.println("{Tags loaded} " + src.getName()); //log info
    	} catch (FileNotFoundException e) {
    		System.out.println("{Tags not found} " + src.getName()); //log info
    		tags = new HashMap<String, Integer>();
    	}
    	return tags;
    }
}
