package taskey.logic;

import java.util.ArrayList;

/** @@author A0134177E
 * This class provides methods for creating deep copies of ArrayList<Task>, ArrayList<ArrayList<Task>>, and
 * ArrayList<TagCategory> objects.
 */
public class ListCloner {
	
	public static ArrayList<Task> cloneTaskList(ArrayList<Task> taskList) {
		ArrayList<Task> clone = new ArrayList<Task>();
		
		for (Task t : taskList) {
			clone.add(new Task(t));
		}
		
		return clone;
	}
	
	public static ArrayList<ArrayList<Task>> cloneTaskLists(ArrayList<ArrayList<Task>> taskLists) {
		ArrayList<ArrayList<Task>> clone = new ArrayList<ArrayList<Task>>();	
		
		for (int i = 0; i < taskLists.size(); i++) {
			clone.add(cloneTaskList(taskLists.get(i)));
		}
		
		return clone;
	}
	
	public static ArrayList<TagCategory> cloneTagCategoryList(ArrayList<TagCategory> tagCategoryList) {
		ArrayList<TagCategory> clone = new ArrayList<TagCategory>();
		
		for (TagCategory tc : tagCategoryList) {
			clone.add(new TagCategory(tc));
		}
		
		return clone;
	}
}
