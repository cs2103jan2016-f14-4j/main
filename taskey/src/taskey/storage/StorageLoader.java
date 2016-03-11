package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.Task;
import taskey.storage.Storage.Constants;


class StorageLoader {

    /**
     * Generic read method.
     * Deserializes the JSON specified by the abstract file into an object of the specified type.
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
     * Load task list *
     *================*/
    /**
     * Returns an ArrayList of Task objects read from the file specified by the given filename String.
     * An empty ArrayList is returned if the file was not found.
     * This method is invoked by Logic.
     * @param filename name of the file to be read
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

    /*================*
     * Load directory *
     *================*/
    /**
     * Looks for, and loads, the last-saved directory from file in System.getProperty("user.dir")
     * @return true if the directory was successfully loaded; false otherwise.
     */
    File loadDirectory() {
    	try {
    		return readFromFile(new File(Constants.FILENAME_CONFIG), new TypeToken<File>() {});
    	} catch (FileNotFoundException e) {
    		return null;
    	}
    }

    /*===========*
     * Load tags *
     *===========*/
    /**
     * Returns the HashMap containing user-defined tags read from JSON file.
     * An empty HashMap is returned if the tags file was not found.
     * @return the HashMap read from file, or an empty HashMap if the file was not found.
     */
    HashMap<String, Integer> loadTags(File src) {
    	HashMap<String, Integer> tags;
    	try {
    		tags = readFromFile(src, new TypeToken<HashMap<String, Integer>>() {});
    		System.out.println("{Tags loaded} " + Constants.FILENAME_TAGS); //log info
    	} catch (FileNotFoundException e) {
    		System.out.println("{Tags not found} " + Constants.FILENAME_TAGS); //log info
    		tags = new HashMap<String, Integer>();
    	}
    	return tags;
    }
}
