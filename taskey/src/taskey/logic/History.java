package taskey.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;

/**
 * @@author A0121618M
 * This class is used by Logic for the undo and redo commands.
 * Every time the task lists are updated, they are pushed onto the taskStack.
 * At the same time, the tag list is also pushed onto the tagStack.
 * 
 * The undo command pops the most recently added task and tag data from the main stack
 * and pushes them onto the redo stack, to be held there until new data is added to History
 * (whereupon the redo stack is cleared), or the user invokes the redo command.
 * 
 * This class's redo method does the reverse of the undo routine: 
 * it pops the previously "undone" data from the redo stack and pushes them back onto the main stack.
 * Logic can then peek at the main stack to get the data for the redo command.
 */
class History {
	// The main History stack
	private ArrayDeque<ArrayList<ArrayList<Task>>> taskStack;
	private ArrayDeque<ArrayList<TagCategory>> tagStack;
	
	// The redo stack that temporarily holds data popped from the main stack
	private ArrayDeque<ArrayList<ArrayList<Task>>> redoTaskStack;
	private ArrayDeque<ArrayList<TagCategory>> redoTagStack;

	History() {
		taskStack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
		tagStack = new ArrayDeque<ArrayList<TagCategory>>();
		
		redoTaskStack = new ArrayDeque<ArrayList<ArrayList<Task>>>();
		redoTagStack = new ArrayDeque<ArrayList<TagCategory>>();
	}

	boolean taskStackIsEmpty() {
		return taskStack.isEmpty();
	}
	
	boolean tagStackIsEmpty() {
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
	void addTaskLists(ArrayList<ArrayList<Task>> taskLists) {
		taskStack.push(taskLists);
		redoTaskStack.clear();
	}
	
	/**
	 * Pushes the given tag category list to History.
	 * @param tagCategoryList
	 */
	void addTagCategoryList(ArrayList<TagCategory> tagCategoryList) {
		tagStack.push(tagCategoryList);
		redoTagStack.clear();
	}

	/*=========*
	 * Getters *
	 *=========*/
	/**
	 * Peeks at the last added task lists in History.
	 * @return
	 */
	ArrayList<ArrayList<Task>> peekTaskStack() {
		return taskStack.peek();
	}

	/**
	 * Peeks at the last added tag category list in History.
	 * @return
	 */
	ArrayList<TagCategory> peekTagStack() {
		return tagStack.peek();
	}


	/*==========*
	 * For undo *
	 *==========*/
	/**
	 * Pop the last added task lists from History.
	 * @return 
	 */
	ArrayList<ArrayList<Task>> popTaskStack() {
		ArrayList<ArrayList<Task>> popped = taskStack.pop();
		redoTaskStack.push(popped);
		return popped;
	}

	/**
	 * Pop the last added tag category list from History.
	 * @return 
	 */
	ArrayList<TagCategory> popTagStack() {
		ArrayList<TagCategory> popped = tagStack.pop();
		redoTagStack.push(popped);
		return popped;
	}

	/*==========*
	 * For redo *
	 *==========*/
	/**
	 * @@author A0121618M
	 * This method pops the last undid task lists and tag list from their respective redo stacks,
	 * and pushes them back onto the main history stacks.
	 * Logic can then peek at the main stack to redo the previously undid command.
	 * @return false if there is nothing to redo; true if successful
	 */
	boolean redo() {	
		try {
			ArrayList<ArrayList<Task>> undidTasks = redoTaskStack.pop();
			ArrayList<TagCategory> undidTags = redoTagStack.pop();
			taskStack.push(undidTasks);
			tagStack.push(undidTags);
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Clear history's stacks. This is done after Logic loads from a new directory.
	 * Logic must call its updateHistory() method immediately after History is cleared,
	 * to ensure that History isn't left empty.
	 */
	void clear() {
		taskStack.clear();
		tagStack.clear();
		redoTaskStack.clear();
		redoTagStack.clear();
	}
}