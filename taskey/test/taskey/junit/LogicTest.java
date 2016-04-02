package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.Logic;
import taskey.logic.LogicException;
import taskey.logic.LogicMemory;
import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;

/**
 * @@author A0134177E
 */
public class LogicTest {
	public static final int NUM_SECONDS_1_DAY = 86400;
	public static final int NUM_SECONDS_1_WEEK = 604800;
	public static final int NUM_SECONDS_BUFFER_TIME = 10; // Used for safety in dealing with boundary conditions
	
	private Logic logic;
	private Parser parser;
	private TimeConverter timeConverter;
	
	private static ArrayList<ArrayList<Task>> getEmptyLists() {
		ArrayList<ArrayList<Task>> lists = new ArrayList<ArrayList<Task>>();
		
		while (lists.size() < LogicMemory.NUM_TASK_LISTS) {
			lists.add(new ArrayList<Task>());
		}
		
		return lists;
	}
	
	private static void sortLists(ArrayList<ArrayList<Task>> lists) {
		for (ArrayList<Task> list : lists) {
			Collections.sort(list);
		}
	}
	
	private static void sortListsReversed(ArrayList<ArrayList<Task>> lists) {
		for (ArrayList<Task> list : lists) {
			Collections.sort(list, Collections.reverseOrder());
		}
	}
	
	private static void sortListReversed(ArrayList<Task> list) {
		Collections.sort(list, Collections.reverseOrder());
	}
	
	
	// Make sure "clear" command works because it is used in setUp().
	// "clear" command is supposed to clear all task and tag data in memory.
	@BeforeClass
	public static void testClear() {
		Logic logic = new Logic();
		logic.executeCommand(ContentBox.PENDING, "clear");
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
		assertTrue(logic.getTagCategoryList().isEmpty());
	}
	
	@Before
	public void setUp() {
		logic = new Logic();
		parser = new Parser();
		timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
	}
	
	@Test
	public void addingFloatingTaskShouldUpdateOnlyGeneralAndPendingLists() {
		String input = "add task";
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, input);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingDeadlineTaskEndingThisWeekShouldUpdateOnlyPendingAndDeadlineAndThisWeekLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime + NUM_SECONDS_BUFFER_TIME);
		String input = "add task on " + deadline;
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, input);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_DEADLINE).add(task);
		expected.get(LogicMemory.INDEX_THIS_WEEK).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingDeadlineTaskNotEndingThisWeekShouldUpdateOnlyPendingAndDeadlineLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_BUFFER_TIME);
		String input = "add task on " + deadline;
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, input);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_DEADLINE).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingEventTaskStartingThisWeekShouldUpdateOnlyPendingAndEventAndThisWeekLists() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime + NUM_SECONDS_BUFFER_TIME);
		String endDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_BUFFER_TIME);
		String input = "add task from " + startDate + " to " + endDate;
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_EVENT).add(task);
		expected.get(LogicMemory.INDEX_THIS_WEEK).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingEventTaskNotStartingThisWeekShouldUpdateOnlyPendingAndEventLists() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK);
		String endDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY);
		String input = "add task from " + startDate + " to " + endDate;
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_EVENT).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingExpiredDeadlineTaskShouldThrowExceptionMessage() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		String input = "add task on " + deadline;
		String expected = LogicException.MSG_ERROR_DATE_EXPIRED;
		String actual = logic.executeCommand(ContentBox.PENDING, input).getException().getMessage();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingExpiredDeadlineTaskShouldNotUpdateAnyLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		String input = "add task on " + deadline;
		logic.executeCommand(ContentBox.PENDING, input);
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	@Test
	public void addingExpiredEventTaskShouldThrowException() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK);
		String endDate = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		String input = "add task from " + startDate + " to " + endDate;
		String exceptionMsg = LogicException.MSG_ERROR_DATE_EXPIRED;
		Exception expected = new Exception(exceptionMsg);
		Exception actual = logic.executeCommand(ContentBox.PENDING, input).getException();
		assertEquals(expected.getMessage(), actual.getMessage());
	}
	
	@Test
	public void addingExpiredEventTaskShouldNotUpdateAnyLists() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK);
		String endDate = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		String input = "add task from " + startDate + " to " + endDate;
		logic.executeCommand(ContentBox.PENDING, input);
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	// Equivalence partitions: floating tasks, deadline tasks, event tasks with the same name but different dates
	@Test
	public void addingTasksWithSameNameButDifferentDatesShouldNotThrowException() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		Exception e = logic.executeCommand(ContentBox.PENDING, "add task on 31 dec 3pm").getException();
		assertEquals(LogicException.MSG_SUCCESS_ADD, e.getMessage());
		e = logic.executeCommand(ContentBox.PENDING, "add task from 30 dec 5pm to 31 dec 6pm").getException();
		assertEquals(LogicException.MSG_SUCCESS_ADD, e.getMessage());
	}
	
	@Test
	public void tasksWithSameNameButDifferentDatesShouldBeAddedToTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		
		input = "add task on 31 dec 3pm";
		logic.executeCommand(ContentBox.PENDING, input);
		task = parser.parseInput(input).getTask();
		expected.get(LogicMemory.INDEX_DEADLINE).add(task);
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		
		input = "add task from 30 dec 5pm to 31 dec 6pm";
		logic.executeCommand(ContentBox.PENDING, input);
		task = parser.parseInput(input).getTask();
		expected.get(LogicMemory.INDEX_EVENT).add(task);
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		
		sortListsReversed(expected);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void deletingFloatingTaskByIndexShouldUpdatePendingAndGeneralLists() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		logic.executeCommand(ContentBox.PENDING, "del 1");
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	@Test
	public void deletingDeadlineTaskEndingThisWeekByIndexShouldUpdatePendingdAndDeadlineAndThisWeekLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime);
		logic.executeCommand(ContentBox.PENDING, "add task on " + deadline);
		logic.executeCommand(ContentBox.PENDING, "del 1");
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	@Test
	public void deletingDeadlineTaskNotEndingThisWeekByIndexShouldUpdatePendingAndDeadlineLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK);
		logic.executeCommand(ContentBox.PENDING, "add task on " + deadline);
		logic.executeCommand(ContentBox.PENDING, "del 1");
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	@Test
	public void deletingEventTaskStartingThisWeekByIndexShouldUpdatePendingAndEventAndThisWeekLists() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime);
		String endDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK);
		logic.executeCommand(ContentBox.PENDING, "add task from " + startDate + " to " + endDate);
		logic.executeCommand(ContentBox.PENDING, "del 1");
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	@Test
	public void deletingEventTaskNotStartingThisWeekByIndexShouldUpdatePendingAndEventLists() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK);
		String endDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY);
		logic.executeCommand(ContentBox.PENDING, "add task from " + startDate + " to " + endDate);
		logic.executeCommand(ContentBox.PENDING, "del 1");
		assertEquals(getEmptyLists(), logic.getAllTaskLists());
	}
	
	// Test inputs: 2 is out of range, 0 is an impossible index, -1 is a negative index and should not be allowed.
	@Test
	public void deletingTaskByInvalidIndexShouldThrowException() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		Exception actual = logic.executeCommand(ContentBox.PENDING, "del 2").getException();
		String exceptionMsg = LogicException.MSG_ERROR_INVALID_INDEX;
		Exception expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "del 0").getException();
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "del -1").getException();
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
	}
	
	@Test
	public void deletingTaskByInvalidIndexShouldNotChangeTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		logic.executeCommand(ContentBox.PENDING, "del 2");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "del 0");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "del -1");
		assertEquals(expected, logic.getAllTaskLists());
	}
	
	// This test might fail once PowerSearch is implemented.
	@Ignore
	public void searchShouldReturnAllTasksWhoseNameContainsSearchPhrase() {
		String input = "add task1";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task1 = parser.parseInput(input).getTask();

		input = "add task2 on 31 dec";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task2 = parser.parseInput(input).getTask();

		// Both task1 and task2 match
		logic.executeCommand(ContentBox.PENDING, "search t");
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(task1);
		expected.add(task2);
		sortListReversed(expected);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		// Only task2 matches
		logic.executeCommand(ContentBox.PENDING, "search 2");
		expected.remove(task1);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
	}
	
	@Test
	public void testSearch() {
		String input = "add I will initialize it before the eve of xmas";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task1 = parser.parseInput(input).getTask();
		
		input = "add In the end, it doesn't even matter";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task2 = parser.parseInput(input).getTask();
		
		logic.executeCommand(ContentBox.PENDING, "search eVe");
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(task1);
		expected.add(task2);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		logic.executeCommand(ContentBox.PENDING, "search tHe eve");
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		logic.executeCommand(ContentBox.PENDING, "search evE THe");
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		logic.executeCommand(ContentBox.PENDING, "search tHe IN");
		expected.remove(task1);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		logic.executeCommand(ContentBox.PENDING, "search iN");
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		logic.executeCommand(ContentBox.PENDING, "search e");
		expected.remove(task2);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		logic.executeCommand(ContentBox.PENDING, "search i");
		expected.add(task1);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
	}
	
	@Test
	public void searchShouldThrowExceptionMessageIfSearchPhraseNotFound() {
		String input = "add task1";
		logic.executeCommand(ContentBox.PENDING, input);
		Exception actual = logic.executeCommand(ContentBox.PENDING, "search t ask").getException();
		assertEquals(LogicException.MSG_ERROR_SEARCH_NOT_FOUND, actual.getMessage());
	}
	
	// The completed task should be removed from all lists and then inserted into the COMPLETED list.
	@Test
	public void doneTaskByIndexShouldUpdateTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "done 1");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_COMPLETED).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	// The completed task's tags should be removed from the tag database.
	// No other tags should be affected.
	@Test
	public void doneTaskByIndexShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1 #tag2");
		logic.executeCommand(ContentBox.PENDING, "add task2 #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "done 1");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag3"));
		assertEquals(expected, logic.getTagCategoryList());
	}
	
	@Test
	public void doneTaskByInvalidIndexShouldThrowException() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		Exception actual = logic.executeCommand(ContentBox.PENDING, "done 2").getException();
		String exceptionMsg = String.format(LogicException.MSG_ERROR_INVALID_INDEX, 2);
		Exception expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "done 0").getException();
		exceptionMsg = String.format(LogicException.MSG_ERROR_INVALID_INDEX, 0);
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "done -1").getException();
		exceptionMsg = String.format(LogicException.MSG_ERROR_INVALID_INDEX, -1);
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
	}
	
	@Test
	public void doneTaskByInvalidIndexShouldNotChangeTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		logic.executeCommand(ContentBox.PENDING, "done 2");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "done 0");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "done -1");
		assertEquals(expected, logic.getAllTaskLists());
	}
	
	// Every list that contains the updated task should also be updated.
	@Test
	public void updateTaskByIndexChangeNameShouldUpdateTaskLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime);
		String input = "add task on " + deadline;
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "set 1 \"new name\"");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		task.setTaskName("new name");
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_DEADLINE).add(task);
		expected.get(LogicMemory.INDEX_THIS_WEEK).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void updateTaskByInvalidIndexShouldThrowExceptionMessage() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		Exception actual = logic.executeCommand(ContentBox.PENDING, "set 2 \"new name\"").getException();
		String exceptionMsg = LogicException.MSG_ERROR_INVALID_INDEX;
		Exception expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "set 0 \"new name\"").getException();
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "set -1 \"new name\"").getException();
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
	}
	
	@Test
	public void updateTaskByInvalidIndexShouldNotChangeTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		logic.executeCommand(ContentBox.PENDING, "set 2 \"new name\"");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "set 0 \"new name\"");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "set -1 \"new name\"");
		assertEquals(expected, logic.getAllTaskLists());
	}
	
	/*
	@Ignore
	public void testUpdateTaskByIndexChangeDateSameWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                 + NUM_SECONDS_BUFFER_TIME));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Ignore
	public void testUpdateTaskByIndexChangeDateDiffWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                 + NUM_SECONDS_1_WEEK
				                                                                                 + NUM_SECONDS_BUFFER_TIME));
		                                                                                         // Boundary value
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}*/
	
	@Test
	public void changingTaskDateFromFloatingToDeadlineShouldUpdateTaskLists() {
		long currTime = timeConverter.getCurrTime();
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		String oldTaskName = parser.parseInput(input).getTask().getTaskName();
		String deadline = timeConverter.getDate(currTime);
		input = "set 1 [" + deadline + "]";
		logic.executeCommand(ContentBox.PENDING, input);
		Task newTask = parser.parseInput(input).getTask();
		newTask.setTaskName(oldTaskName);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_DEADLINE).add(newTask);
		expected.get(LogicMemory.INDEX_PENDING).add(newTask);
		expected.get(LogicMemory.INDEX_THIS_WEEK).add(newTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void changingTaskDateFromDeadlineToEventShouldUpdateTaskLists() {
		long currTime = timeConverter.getCurrTime();
		String input = "add task on " + timeConverter.getDate(currTime);
		logic.executeCommand(ContentBox.PENDING, input);
		String oldTaskName = parser.parseInput(input).getTask().getTaskName();
		input = "set 1 [30 dec 5pm, 31 dec 3pm]";
		logic.executeCommand(ContentBox.PENDING, input);
		Task newTask = parser.parseInput(input).getTask();
		newTask.setTaskName(oldTaskName);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_EVENT).add(newTask);
		expected.get(LogicMemory.INDEX_PENDING).add(newTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void changingTaskDateFromEventToFloatingShouldUpdateTaskLists() {
		long currTime = timeConverter.getCurrTime();
		String input = "add task from " + timeConverter.getDate(currTime) + " to 31 dec 11pm";
		logic.executeCommand(ContentBox.PENDING, input);
		String oldTaskName = parser.parseInput(input).getTask().getTaskName();
		input = "set 1 [none]";
		logic.executeCommand(ContentBox.PENDING, input);
		Task newTask = parser.parseInput(input).getTask();
		newTask.setTaskName(oldTaskName);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_FLOATING).add(newTask);
		expected.get(LogicMemory.INDEX_PENDING).add(newTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void changingTaskDateToExpiredShouldThrowException() {
		long currTime = timeConverter.getCurrTime();
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		String deadline = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		input = "set 1 [" + deadline + "]";
		String actual = logic.executeCommand(ContentBox.PENDING, input).getException().getMessage();
		String expected = LogicException.MSG_ERROR_DATE_EXPIRED;
		assertEquals(expected, actual);
	}
	
	@Test
	public void changingTaskDateToExpiredShouldNotChangeTaskLists() {
		long currTime = timeConverter.getCurrTime();
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task oldTask = parser.parseInput(input).getTask();
		String deadline = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		input = "set 1 [" + deadline + "]";
		logic.executeCommand(ContentBox.PENDING, input);
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_FLOATING).add(oldTask);
		expected.get(LogicMemory.INDEX_PENDING).add(oldTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	

	@Test
	public void changingTaskNameAndDateShouldUpdateTaskLists() {
		long currTime = timeConverter.getCurrTime();
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		String deadline = timeConverter.getDate(currTime);
		input = "set 1 [" + deadline + "] \"new name\"";
		logic.executeCommand(ContentBox.PENDING, input);
		Task newTask = parser.parseInput(input).getTask();
		newTask.setTaskName("new name");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_DEADLINE).add(newTask);
		expected.get(LogicMemory.INDEX_PENDING).add(newTask);
		expected.get(LogicMemory.INDEX_THIS_WEEK).add(newTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Ignore
	public void changingTaskNameShouldThrowExceptionMessageIfThereIsDuplicateTask() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		logic.executeCommand(ContentBox.PENDING, "add task 2");
		Exception actual = logic.executeCommand(ContentBox.PENDING, "set 2 \"task\"").getException();
		String expected = LogicException.MSG_ERROR_DUPLICATE_TASKS;
		assertEquals(expected, actual.getMessage());
	}
	
	@Ignore
	public void changingTaskNameShouldNotChangeTaskListsIfThereIsDuplicateTask() {
		String input = "add task";
		Task task1 = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, input);

		input = "add task 2";
		Task task2 = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "add task 2");

		input = "set 2 \"task\"";
		logic.executeCommand(ContentBox.PENDING, input); // Should fail
		
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task1);
		expected.get(LogicMemory.INDEX_PENDING).add(task2);
		expected.get(LogicMemory.INDEX_FLOATING).add(task1);
		expected.get(LogicMemory.INDEX_FLOATING).add(task2);
		assertEquals(expected, logic.getAllTaskLists());
	}
	
	
	@Test
	public void undoAddShouldUpdateTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "undo");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void undoAddShouldUpdateTagDatabase() {
		String input = "add task #tag1 #tag2";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "undo");
		assertTrue(logic.getTagCategoryList().isEmpty());
	}
	
	@Test
	public void undoDeleteShouldUpdateTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "del 1");
		logic.executeCommand(ContentBox.PENDING, "undo");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void undoDeleteShouldUpdateTagDatabase() {
		String input = "add task #tag1 #tag2";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "del 1");
		logic.executeCommand(ContentBox.PENDING, "undo");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagCategoryList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void undoUpdateShouldUpdateTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "set 1 \"new name\" [30 dec 5pm, 31 dec 3pm]");
		logic.executeCommand(ContentBox.PENDING, "undo");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void undoDoneShouldUpdateTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "done 1");
		logic.executeCommand(ContentBox.PENDING, "undo");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void undoDoneShouldUpdateTagDatabase() {
		String input = "add task #tag1 #tag2";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "done 1");
		logic.executeCommand(ContentBox.PENDING, "undo");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagCategoryList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingTaggedFloatingTaskShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1 #tag2");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagCategoryList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingTaggedDeadlineTaskShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task on 31 dec 5pm #tag1 #tag2");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagCategoryList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingTaggedEventTaskShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task from 30 dec 5pm to 31 dec 5pm #tag1 #tag2");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagCategoryList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void deletingTaggedTaskByIndexShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1 #tag2");
		logic.executeCommand(ContentBox.PENDING,"del 1");
		assertTrue(logic.getTagCategoryList().isEmpty());
	}
	
	@Test
	public void deletingTagCategoryShouldUpdateTaskLists() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "del #tag3");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		Task task = parser.parseInput("add task #tag1").getTask();
		expected.get(LogicMemory.INDEX_PENDING).add(task);
		expected.get(LogicMemory.INDEX_FLOATING).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void deletingTagCategoryShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "del #tag3");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1")); // #tag2 and #tag3 should not be in tag database
		assertEquals(expected, logic.getTagCategoryList());
	}
	
	@Test
	public void deletingNonExistentTagCategoryShouldThrowException() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		LogicException expected = new LogicException(LogicException.MSG_ERROR_TAG_NOT_FOUND);
		LogicException actual = logic.executeCommand(ContentBox.PENDING, "del #tag4").getException();
		assertEquals(expected, actual);
	}
	
	// The order of displayed tasks is not tested here.
	// This test also checks that there are no duplicate tasks in the displayed list.
	@Test
	public void viewingTagsShouldOnlyDisplayAllTasksWithAtLeastOneOfThoseTags() {
		logic.executeCommand(ContentBox.PENDING, "add task1 #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task4 #tag2 #tag4");
		logic.executeCommand(ContentBox.PENDING, "view #tag1 #tag3 #tag5");
		ArrayList<Task> viewList = logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION);
		Task task1 = parser.parseInput("add task1 #tag1").getTask();
		assertTrue(viewList.contains(task1));
		Task task2 = parser.parseInput("add task2 on 31 dec 3pm #tag2 #tag3").getTask();
		assertTrue(viewList.contains(task2));
		Task task3 = parser.parseInput("add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3").getTask();
		assertTrue(viewList.contains(task3));
		assertTrue(viewList.size() == 3); // Should not contain task4
	}
	
	@Test
	public void updatingCompletedTasksShouldThrowExceptionMessage() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "done 1");
		logic.executeCommand(ContentBox.PENDING, "view archive");
		LogicException expected = new LogicException(LogicException.MSG_ERROR_UPDATE_INVALID);
		LogicException actual = logic.executeCommand(ContentBox.ACTION, "set 1 \"new name\"").getException();
		assertEquals(expected, actual);
	}
	
	@Test
	public void doneCompletedTasksShouldThrowExceptionMessage() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "done 1");
		logic.executeCommand(ContentBox.PENDING, "view archive");
		LogicException expected = new LogicException(LogicException.MSG_ERROR_DONE_INVALID);
		LogicException actual = logic.executeCommand(ContentBox.ACTION, "done 1").getException();
		assertEquals(expected, actual);
	}
	
	// This test is currently dependent on the order of the lists, which in turn depends on the compareTo() method
	// in Task.java. Until the sorting method is finalized, this test may occasionally fail.
	@Test
	public void updatingTaskPriorityByIndexShouldNotAffectAnyOtherTasks() {
		String input = "add task1";
		Task task1 = parser.parseInput(input).getTask(); // Should go into PENDING and FLOATING lists
		logic.executeCommand(ContentBox.PENDING, input);

		input = "add task2";
		Task task2 = parser.parseInput(input).getTask(); // Should go into PENDING and FLOATING lists
		logic.executeCommand(ContentBox.PENDING, input);
		
		logic.executeCommand(ContentBox.PENDING, "set 2 !!"); // Should only modify task2
		
		ArrayList<Task> pendingList = logic.getAllTaskLists().get(LogicMemory.INDEX_PENDING);
		ArrayList<Task> floatingList = logic.getAllTaskLists().get(LogicMemory.INDEX_FLOATING);
		assertTrue(pendingList.get(0).getPriority() == 2);
		assertTrue(pendingList.get(1).getPriority() == 1);
		assertTrue(floatingList.get(0).getPriority() == 2);
		assertTrue(floatingList.get(1).getPriority() == 1);
	}
}