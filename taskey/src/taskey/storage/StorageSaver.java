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
class StorageSaver {
    /**
     * Generic write method. Serializes the given object of the specified type into its equivalent JSON representation.
     * @param file to be written as JSON
     * @param object of type T to be serialized
     * @param typeToken represents the generic type T of the desired object; this is obtained from the Gson TypeToken class
     * @throws IOException
     */
    private <T> void writeToFile(File file, T object, TypeToken<T> typeToken) throws IOException {
    	FileWriter writer = new FileWriter(file);
    	Gson gson = new Gson();
    	String json = gson.toJson(object, typeToken.getType());
    	writer.write(json);
    	writer.close();
    }

    /*================*
     * Save directory *
     *================*/
    /**
     * Saves the current directory to disk in System.getProperty("user.dir").
     * @param src the directory File to be saved
     * @param filename name of the destination file
     * @return true if save was successful; false otherwise.
     */
    boolean saveDirectory(File src, String filename) {
    	File dest = new File(filename);
    	try {
    		writeToFile(dest, src, new TypeToken<File>() {});
    		System.out.println("{Storage directory saved}"); //debug info
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    /*================*
     * Save task list *
     *================*/
    /**
     * Saves an ArrayList of Task objects to the file specified by the given filename String.
     * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
     * This method is invoked by Logic.
     * @param tasks list of Task objects for saving
     * @param dest destination file that the tasklist will be saved as
     * @throws StorageException contains the last saved tasklist corresponding to the filename
     */
    void saveTasklist(ArrayList<Task> tasks, File dest) throws StorageException {
		try {
			writeToFile(dest, tasks, new TypeToken<ArrayList<Task>>() {});
		} catch (IOException e) {
			// When exception is encountered during write-after-modified, throw the last-modified superlist to Logic.
			throw new StorageException(e, History.getInstance().peek());
		}
    }

    /*===========*
     * Save tags *
     *===========*/
    /**
     * Saves the given HashMap containing user-defined tags to JSON file.
     * @param tags HashMap that maps the tag strings to their corresponding multiplicities
     * @param dest destination file that the tasklist will be saved as
     * @return true if successful; false otherwise.
     */
    boolean saveTags(HashMap<String, Integer> tags, File dest) {
    	try {
    		writeToFile(dest, tags, new TypeToken<HashMap<String, Integer>>() {});
    		return true;
    	} catch (IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}
