package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.Logic;
import taskey.logic.LogicConstants;
import taskey.logic.LogicConstants.ListID;
import taskey.logic.LogicFeedback;
import taskey.logic.LogicMemory;
import taskey.logic.ProcessedObject;
import taskey.logic.TagCategory;
import taskey.logic.Task;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;

/**
 * @@author A0134177E
 * @author Hubert
 */
public class LogicTest {
	public static final int NUM_SECONDS_1_DAY = 86400;
	public static final int NUM_SECONDS_1_WEEK = 604800;
	public static final int NUM_SECONDS_BUFFER_TIME = 100;
	public static final String STRING_ADD_FLOATING = "add g2 a?b ,  ";
	public static final String STRING_ADD_DEADLINE = "add g2 a?b ,   on %1$s";
	public static final String STRING_ADD_EVENT = "add g2 a?b ,   from %1$s to %2$s";
	public static final String STRING_DELETE_BY_INDEX = "del 1";
	public static final String STRING_DELETE_BY_INVALID_INDEX = "del 2";
	public static final String STRING_DELETE_BY_NAME = "del g2 a?b ,  ";
	public static final String STRING_DELETE_BY_INVALID_NAME = "del ayy lmao";
	public static final String STRING_SEARCH = "search ?B , ";
	public static final String STRING_SEARCH_NOT_FOUND = "search ayy lmao";
	public static final String STRING_SEARCH_EMPTY = "search ";
	public static final String STRING_DONE_BY_INDEX = "done 1";
	public static final String STRING_DONE_BY_INVALID_INDEX = "done 2";
	public static final String STRING_DONE_BY_NAME = "done g2 a?b ,  ";
	public static final String STRING_DONE_BY_INVALID_NAME = "del ayy lmao";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_NAME = "set 1 \"ayy lmao\"";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE = "set 1 [%1$s]";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_DATE_EVENT = "set 1 [%1$s, %2$s]";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_DATE_FLOATING = "set 1 [none]";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_BOTH = "set 1 \"ayy lmao\" [none]";
	public static final String STRING_UPDATE_BY_INVALID_INDEX = "set 2 \"ayy lmao\"";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_NAME = "set g2 a?b ,   \"ayy lmao\"";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_DATE_DEADLINE = "set add g2 a?b ,   [%1$s]";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_DATE_EVENT = "set add g2 a?b ,   [%1$s, %2$s]";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_DATE_FLOATING = "set add g2 a?b ,   [none]";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_BOTH = "set g2 a?b ,   \"ayy lmao\" [none]";
	public static final String STRING_UPDATE_BY_INVALID_NAME = "set g3 a?b ,  \"ayy lmao\"";
	public static final String STRING_UNDO = "undo";
	
	private Logic logic;
	private Parser parser;
	private TimeConverter timeConverter;
	
	public static ArrayList<ArrayList<Task>> getEmptyLists() {
		ArrayList<ArrayList<Task>> lists = new ArrayList<ArrayList<Task>>();
		
		while (lists.size() < 8) {
			lists.add(new ArrayList<Task>());
		}
		
		return lists;
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
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.GENERAL.getIndex()).add(task);
		expected.get(ListID.PENDING.getIndex()).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingDeadlineTaskEndingThisWeekShouldUpdateOnlyPendingAndDeadlineAndThisWeekLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime);
		String input = "add task on " + deadline;
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.DEADLINE.getIndex()).add(task);
		expected.get(ListID.THIS_WEEK.getIndex()).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingDeadlineTaskNotEndingThisWeekShouldUpdateOnlyPendingAndDeadlineLists() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK);
		String input = "add task on " + deadline;
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.DEADLINE.getIndex()).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingEventTaskStartingThisWeekShouldUpdateOnlyPendingAndEventAndThisWeekLists() {
		long currTime = timeConverter.getCurrTime();
		String startDate = timeConverter.getDate(currTime);
		String endDate = timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK);
		String input = "add task from " + startDate + " to " + endDate;
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.EVENT.getIndex()).add(task);
		expected.get(ListID.THIS_WEEK.getIndex()).add(task);
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.EVENT.getIndex()).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingExpiredDeadlineTaskShouldThrowExceptionMessage() {
		long currTime = timeConverter.getCurrTime();
		String deadline = timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		String input = "add task on " + deadline;
		String expected = LogicConstants.MSG_EXCEPTION_DATE_EXPIRED;
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
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_DATE_EXPIRED;
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
		assertEquals(LogicConstants.MSG_ADD_SUCCESSFUL, e.getMessage());
		e = logic.executeCommand(ContentBox.PENDING, "add task from 30 dec 5pm to 31 dec 6pm").getException();
		assertEquals(LogicConstants.MSG_ADD_SUCCESSFUL, e.getMessage());
	}
	
	@Test
	public void tasksWithSameNameButDifferentDatesShouldBeAddedToTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.GENERAL.getIndex()).add(task);
		expected.get(ListID.PENDING.getIndex()).add(task);
		
		input = "add task on 31 dec 3pm";
		logic.executeCommand(ContentBox.PENDING, input);
		task = parser.parseInput(input).getTask();
		expected.get(ListID.DEADLINE.getIndex()).add(task);
		expected.get(ListID.PENDING.getIndex()).add(task);
		
		input = "add task from 30 dec 5pm to 31 dec 6pm";
		logic.executeCommand(ContentBox.PENDING, input);
		task = parser.parseInput(input).getTask();
		expected.get(ListID.EVENT.getIndex()).add(task);
		expected.get(ListID.PENDING.getIndex()).add(task);
		
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
	
	/*
	@Ignore
	public void testDeleteTaskByName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_NAME);
		LogicFeedback actual = logic.deleteByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}*/
	
	// Test inputs: 2 is out of range, 0 is an impossible index, -1 is a negative index and should not be allowed.
	@Test
	public void deletingTaskByInvalidIndexShouldThrowException() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		Exception actual = logic.executeCommand(ContentBox.PENDING, "del 2").getException();
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_INVALID_INDEX;
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
		logic.executeCommand(ContentBox.PENDING, "del 2");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "del 0");
		assertEquals(expected, logic.getAllTaskLists());
		
		logic.executeCommand(ContentBox.PENDING, "del -1");
		assertEquals(expected, logic.getAllTaskLists());
	}
	
	/*
	@Ignore
	public void testDeleteTaskByInvalidName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INVALID_NAME);
		LogicFeedback actual = logic.deleteByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, po.getTask().getTaskName());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}*/
	
	// This test might fail once PowerSearch is implemented.
	@Test
	public void searchShouldReturnAllTasksWhoseNameContainsSearchPhrase() {
		String input = "add task1";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task1 = parser.parseInput(input).getTask();
		ArrayList<Task> expected = new ArrayList<Task>();
		expected.add(task1);
		input = "add task2 on 31 dec";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task2 = parser.parseInput(input).getTask();
		expected.add(task2);
		// Both task1 and task2 match
		logic.executeCommand(ContentBox.PENDING, "search t");
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
		
		// Only task2 matches
		logic.executeCommand(ContentBox.PENDING, "search 2");
		expected.remove(task1);
		assertEquals(expected, logic.getAllTaskLists().get(LogicMemory.INDEX_ACTION));
	}
	
	@Test
	public void searchShouldThrowExceptionMessageIfSearchPhraseNotFound() {
		String input = "add task1";
		logic.executeCommand(ContentBox.PENDING, input);
		Exception actual = logic.executeCommand(ContentBox.PENDING, "search t ask").getException();
		assertEquals(LogicConstants.MSG_EXCEPTION_SEARCH_NOT_FOUND, actual.getMessage());
	}
	
	// The completed task should be removed from all lists and then inserted into the COMPLETED list.
	@Test
	public void doneTaskByIndexShouldUpdateTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		logic.executeCommand(ContentBox.PENDING, "done 1");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.COMPLETED.getIndex()).add(task);
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
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, 2);
		Exception expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "done 0").getException();
		exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, 0);
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
		
		actual = logic.executeCommand(ContentBox.PENDING, "done -1").getException();
		exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, -1);
		expected = new Exception(exceptionMsg);
		assertEquals(expected.getMessage(), actual.getMessage());
	}
	
	@Test
	public void doneTaskByInvalidIndexShouldNotChangeTaskLists() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		Task task = parser.parseInput(input).getTask();
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.DEADLINE.getIndex()).add(task);
		expected.get(ListID.THIS_WEEK.getIndex()).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void updateTaskByInvalidIndexShouldThrowExceptionMessage() {
		logic.executeCommand(ContentBox.PENDING, "add task");
		Exception actual = logic.executeCommand(ContentBox.PENDING, "set 2 \"new name\"").getException();
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_INVALID_INDEX;
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
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
	}
	
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
		expected.get(ListID.DEADLINE.getIndex()).add(newTask);
		expected.get(ListID.PENDING.getIndex()).add(newTask);
		expected.get(ListID.THIS_WEEK.getIndex()).add(newTask);
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
		expected.get(ListID.EVENT.getIndex()).add(newTask);
		expected.get(ListID.PENDING.getIndex()).add(newTask);
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
		expected.get(ListID.GENERAL.getIndex()).add(newTask);
		expected.get(ListID.PENDING.getIndex()).add(newTask);
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
		String expected = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, deadline);
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
		expected.get(ListID.GENERAL.getIndex()).add(oldTask);
		expected.get(ListID.PENDING.getIndex()).add(oldTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	

	@Ignore
	public void testUpdateTaskByNameChangeName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_NAME_CHANGE_NAME);
		LogicFeedback actual = logic.updateByNameChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Ignore
	public void testUpdateTaskByNameFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_NAME_CHANGE_NAME);
		LogicFeedback actual = logic.updateByNameChangeName(ContentBox.EXPIRED, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Ignore
	public void testUpdateTaskByInvalidName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INVALID_NAME);
		LogicFeedback actual = logic.updateByNameChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, po.getTask().getTaskName());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
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
		expected.get(ListID.DEADLINE.getIndex()).add(newTask);
		expected.get(ListID.PENDING.getIndex()).add(newTask);
		expected.get(ListID.THIS_WEEK.getIndex()).add(newTask);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Ignore
	public void testUpdateTaskByNameChangeBoth() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_NAME_CHANGE_BOTH);
		LogicFeedback actual = logic.updateByNameChangeBoth(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
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
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
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
	}*/
	
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
	public void deletingTagCategoryShouldOnlyRemoveAllTasksWithThatTag() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "del #tag3");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		Task task = parser.parseInput("add task #tag1").getTask();
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
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
	/*
	// The order of displayed tasks is not tested here.
	// This test also checks that there are no duplicate tasks in the displayed list.
	@Test
	public void viewingTagsShouldOnlyDisplayAllTasksWithAtLeastOneOfThoseTags() {
		logic.executeCommand(ContentBox.PENDING, "add task1 #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task4 #tag2 #tag4");
		LogicFeedback lf = logic.executeCommand(ContentBox.PENDING, "view #tag1 #tag3 #tag5");
		ArrayList<Task> viewList = lf.getTaskLists().get(ListID.ACTION.getIndex());
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
		Exception actual = logic.executeCommand(ContentBox.ACTION, "set 1 \"new name\"").getException();
		assertEquals(LogicConstants.MSG_EXCEPTION_UPDATE_INVALID, actual.getMessage());
	}
	
	@Test
	public void doneCompletedTasksShouldThrowExceptionMessage() {
		String input = "add task";
		logic.executeCommand(ContentBox.PENDING, input);
		logic.executeCommand(ContentBox.PENDING, "done 1");
		logic.executeCommand(ContentBox.PENDING, "view archive");
		Exception actual = logic.executeCommand(ContentBox.ACTION, "done 1").getException();
		assertEquals(LogicConstants.MSG_EXCEPTION_DONE_INVALID, actual.getMessage());
	}*/
}