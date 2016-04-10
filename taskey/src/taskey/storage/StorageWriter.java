package taskey.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;

/**
 * @@author A0121618M
 * This class provides methods to classes in the storage package
 * for writing tasklists, taglists and abstract paths to file in the JSON format.
 */
class StorageWriter {
	/**
	 * Generic write method. Serializes the given object of the specified type into its equivalent JSON representation.
	 * @param dest the abstract path of the JSON file to be written
	 * @param object of type T to be serialized
	 * @param typeToken represents the generic type T of the given object; this is obtained from the Gson TypeToken class
	 * @throws IOException thrown by FileWriter
	 */
	private <T> void writeToFile(File dest, T object, TypeToken<T> typeToken) throws IOException {
		FileWriter writer = new FileWriter(dest);
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		String jsonOutput = gson.toJson(object, typeToken.getType());
		writer.write(jsonOutput);
		writer.close();
	}

	/*============*
	 * Save tasks *
	 *============*/
	/**
	 * Writes an ArrayList of Task objects to the File dest.
	 * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
	 * Only non-empty tasklists are saved.
	 * If the given tasklist is empty, its respective file will be deleted.
	 * @param tasks the tasklist to be saved
	 * @param dest the destination file to be written to
	 * @throws IOException thrown by FileWriter
	 */
	void saveTasklist(ArrayList<Task> tasks, File dest) throws IOException {
		try {
			if (!tasks.isEmpty()) {
				writeToFile(dest, tasks, new TypeToken<ArrayList<Task>>() {});
			} else {
				dest.delete(); //can safely delete empty tasklist
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*===========*
	 * Save tags *
	 *===========*/
	/**
	 * Saves the given ArrayList of tag objects to the File dest.
	 * The file will be created if it doesn't exist; otherwise the existing file will be overwritten.
	 * Only non-empty taglists are saved.
	 * If the given taglist is empty, its respective file will be deleted.
	 * @param tags the taglist to be saved
	 * @param dest the destination file to be written to
	 * @throws IOException thrown by FileWriter
	 */
	void saveTaglist(ArrayList<TagCategory> tags, File dest) throws IOException {
		try {
			if (!tags.isEmpty()) {
				writeToFile(dest, tags, new TypeToken<ArrayList<TagCategory>>() {});
			} else { //can safely delete empty taglist
				dest.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*================*
	 * Save directory *
	 *================*/
	/**
	 * Writes the given abstract path to a config file located in System.getProperty("user.dir").
	 * @param dir the File object representing the directory path to be saved
	 * @param filename name of the destination file
	 */
	void saveDirectoryConfigFile(File dir, String filename) {
		File dest = new File(filename);
		try {
			writeToFile(dest, dir.getCanonicalFile(), new TypeToken<File>() {});
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("{Storage} Error saving new directory");
		}
	}
}