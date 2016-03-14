package taskey.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import taskey.logic.Task;

/**
 * @author Dylan
 */
class StorageWriter {
    /**
     * Generic write method. Serializes the given object of the specified type into its equivalent JSON representation.
     * @param dest the JSON file to be written
     * @param object of type T to be serialized
     * @param typeToken represents the generic type T of the given object; this is obtained from the Gson TypeToken class
     * @throws IOException
     */
    private <T> void writeToFile(File dest, T object, TypeToken<T> typeToken) throws IOException {
    	FileWriter writer = new FileWriter(dest);
    	Gson gson = new Gson();
    	String json = gson.toJson(object, typeToken.getType());
    	writer.write(json);
    	writer.close();
    }

    /*================*
     * Save directory *
     *================*/
    /**
     * Saves the given directory as a config file located in System.getProperty("user.dir").
     * @param toSave the File representing the directory to be saved
     * @param filename name of the destination file
     */
    void saveDirectory(File toSave, String filename) {
    	File dest = new File(filename);
    	try {
    		writeToFile(dest, toSave, new TypeToken<File>() {});
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    /*================*
     * Save task list *
     *================*/
    /**
     * Saves an ArrayList of Task objects to the File dest.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * @param tasks the tasklist to be saved
     * @param dest the destination file to be written to
     * @throws StorageException contains the last saved tasklists
     */
    void saveTasklist(ArrayList<Task> tasks, File dest) throws StorageException {
		try {
			writeToFile(dest, tasks, new TypeToken<ArrayList<Task>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			// When exception is encountered during write-after-modified, throw the last-modified superlist to Logic.
			throw new StorageException(e, History.getInstance().getLastSuperlist());
		}
    }

    /*===========*
     * Save tags *
     *===========*/
    /**
     * Saves the given HashMap containing user-defined tags to the File dest.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * @param tags HashMap that maps tag strings to their corresponding multiplicities
     * @param dest the destination file to be written to
     * @throws StorageException contains the last saved tagmap
     */
    void saveTags(HashMap<String, Integer> tags, File dest) throws StorageException {
    	try {
    		writeToFile(dest, tags, new TypeToken<HashMap<String, Integer>>() {});
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw new StorageException(e, History.getInstance().getLastTagmap());
    	}
    }
}