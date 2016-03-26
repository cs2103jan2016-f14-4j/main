package taskey.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is to allow Storage to easily retrieve the last successfully saved tasklist/tagmap from memory
 * so that Storage can throw it to Logic when an error is encountered during saving.
 * This will in turn allow Logic to undo the last operation so that its data remains in sync with Storage.
 *
 * For now, this class is only meant to be used by Storage for the above purpose.
 * But, in the future, if more methods are added to this class, Logic could also use this for the undo command.
 * TODO: multiple undos/redos using stacks?
 *
 * @author Dylan
 */
public class History {
	private ArrayDeque<ArrayList<ArrayList<Task>>> taskStack;
	private ArrayDeque<ArrayList<TagCategory>> tagStack;

	public History() {
		taskStack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
		tagStack = new ArrayDeque<ArrayList<TagCategory>>();
	}

	public boolean taskStackIsEmpty() {
		return taskStack.isEmpty();
	}
	
	public boolean tagStackIsEmpty() {
		return tagStack.isEmpty();
	}

	/*========*
	 * Adders *
	 *========*/
	/**
	 * @@author A0134177E
	 * Pushes the given task lists to History.
	 * @param taskLists
	 */
	public void addTaskLists(ArrayList<ArrayList<Task>> taskLists) {
		taskStack.push(taskLists);
	}
	
	/**
	 * Pushes the given tag category list to History.
	 * @param tagCategoryList
	 */
	public void addTagCategoryList(ArrayList<TagCategory> tagCategoryList) {
		tagStack.push(tagCategoryList);

	}

	/*=========*
	 * Getters *
	 *=========*/
	/**
	 * Peeks at the last added task lists in History.
	 * @return
	 */
	public ArrayList<ArrayList<Task>> peekTaskStack() {
		return taskStack.peek();
	}

	/**
	 * Peeks at the last added tag category list in History.
	 * @return
	 */
	public ArrayList<TagCategory> peekTagStack() {
		return tagStack.peek();
	}


	/*==========*
	 * For undo *
	 *==========*/
	/**
	 * Pop the last added task lists from History.
	 * @return 
	 */
	public ArrayList<ArrayList<Task>> popTaskStack() {
		return taskStack.pop();
	}

	/**
	 * Pop the last added tag category list from History.
	 * @return 
	 */
	public ArrayList<TagCategory> popTagStack() {
		return tagStack.pop();
	}

}