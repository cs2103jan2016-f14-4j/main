package taskey.junit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.Logic;
import taskey.logic.LogicConstants;
import taskey.logic.LogicConstants.ListID;
import taskey.logic.LogicFeedback;
import taskey.logic.ProcessedObject;
import taskey.logic.Task;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;

/**
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
	private ArrayList<ArrayList<Task>> originalCopy;
	private ArrayList<ArrayList<Task>> modifiedCopy;
	private Parser parser;
	private TimeConverter timeConverter;
	
	public static ArrayList<ArrayList<Task>> getEmptyLists() {
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		return temp;
	}
	
	public static ArrayList<ArrayList<Task>> addTaskToLists(Task task) {
		TimeConverter timeConverter = new TimeConverter();
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String taskType = task.getTaskType();
		long currTime = timeConverter.getCurrTime();
		
		if (taskType.equals("FLOATING")) {
			temp.get(ListID.GENERAL.getIndex()).add(task);
		} else if (taskType.equals("DEADLINE")) {
			long deadline = task.getDeadlineEpoch();
			
			if (deadline < currTime) {
				return temp;
			}
			
			temp.get(ListID.DEADLINE.getIndex()).add(task);
			
			if (timeConverter.isSameWeek(deadline, currTime)) {
				temp.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		} else if (taskType.equals("EVENT")) {
			long endDate = task.getEndDateEpoch();
			
			if (endDate < currTime) {
				return temp;
			}
			
			temp.get(ListID.EVENT.getIndex()).add(task);
			
			if (timeConverter.isSameWeek(task.getStartDateEpoch(), currTime)) {
				temp.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}
		
		temp.get(ListID.PENDING.getIndex()).add(task);
		
		return temp;
	}
	
	// Make sure clear command works because it is used in setUp().
	@BeforeClass
	public static void testClear() {
		Logic logic = new Logic();
		ArrayList<ArrayList<Task>> originalCopy = logic.getAllTaskLists();
		ArrayList<ArrayList<Task>> modifiedCopy = logic.getAllTaskLists();
		LogicFeedback actual = logic.clear(originalCopy, modifiedCopy);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, new ProcessedObject("CLEAR"), null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Before
	public void setUp() {
		logic = new Logic();
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		parser = new Parser();
		timeConverter = new TimeConverter();
		logic.clear(originalCopy, modifiedCopy);
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
	}
	
	@Test
	public void testAddFloating() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		LogicFeedback actual = logic.addFloating(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	 
	@Test
	public void testAddDeadlineForThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addDeadline(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddDeadlineOutsideThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addDeadline(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddEventForThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime),
				                     timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addEvent(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddEventOutsideThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK),
				                     timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addEvent(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddExpiredDeadlineTask() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime - NUM_SECONDS_1_DAY));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addDeadline(originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, task.getDeadline());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddExpiredEventTask() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK),
				                     timeConverter.getDate(currTime - NUM_SECONDS_1_DAY));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addEvent(originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, task.getEndDate());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddDuplicateTaskName() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		LogicFeedback actual = logic.addFloating(originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS, task.getTaskName());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	/*@Test
	public void testAddTasksWithTags() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		UserTagDatabase utd = new UserTagDatabase();
		
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  #tag1 #tag2";
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t, po);
		input = "add g3 a?b  ,  on " + timeConverter.getDate(currTime) + " #tag2";
		po = parser.parseInput(input);
		t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		input = "add g4 a?b ,  from " + timeConverter.getDate(currTime)
		         + " to " + timeConverter.getDate(currTime + NUM_SECONDS_1_DAY)
		         + " #tag1 #tag3";
		po = parser.parseInput(input);
		t = po.getTask();
		logic.addEvent(logic.getAllTaskLists(), t, po);
		ArrayList<String> actualTagList = logic.getTagList();
		ArrayList<String> expectedTagList = new ArrayList<String>();
		expectedTagList.add(new String("tag1"));
		expectedTagList.add(new String("tag2"));
		expectedTagList.add(new String("tag3"));
		assertTrue(actualTagList.equals(expectedTagList));
		
		ArrayList<Integer> actualTagSizes = logic.getTagSizes();
		ArrayList<Integer> expectedTagSizes = new ArrayList<Integer>();
		expectedTagSizes.add(2);
		expectedTagSizes.add(2);
		expectedTagSizes.add(1);
		assertTrue(actualTagSizes.equals(expectedTagSizes));
	}
	
	@Test
	public void testDeleteTasksWithTags() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		UserTagDatabase utd = new UserTagDatabase();
		
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  #tag1 #tag2";
		ProcessedObject po = parser.parseInput(input);
		Task t1 = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t1, po);
		input = "add g3 a?b  ,  on " + timeConverter.getDate(currTime) + " #tag2";
		po = parser.parseInput(input);
		Task t2 = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t2, po);
		input = "add g4 a?b ,  from " + timeConverter.getDate(currTime)
		         + " to " + timeConverter.getDate(currTime + NUM_SECONDS_1_DAY)
		         + " #tag1 #tag3";
		po = parser.parseInput(input);
		Task t3 = po.getTask();
		logic.addEvent(logic.getAllTaskLists(), t3, po);
		
		input = "del 2";
		po = parser.parseInput(input);
		logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, po.getIndex());
		ArrayList<String> actualTagList = logic.getTagList();
		ArrayList<String> expectedTagList = new ArrayList<String>();
		expectedTagList.add(new String("tag1"));
		expectedTagList.add(new String("tag2"));
		expectedTagList.add(new String("tag3"));
		assertTrue(actualTagList.equals(expectedTagList));
		
		ArrayList<Integer> actualTagSizes = logic.getTagSizes();
		ArrayList<Integer> expectedTagSizes = new ArrayList<Integer>();
		expectedTagSizes.add(2);
		expectedTagSizes.add(1);
		expectedTagSizes.add(1);
		assertTrue(actualTagSizes.equals(expectedTagSizes));
		
		input = "del " + t3.getTaskName();
		po = parser.parseInput(input);
		logic.deleteByName(logic.getAllTaskLists(), ContentBox.PENDING, po, t3.getTaskName());
		actualTagList = logic.getTagList();
		expectedTagList.remove("tag3");
		assertTrue(actualTagList.equals(expectedTagList));
		
		actualTagSizes = logic.getTagSizes();
		expectedTagSizes.set(0, 1);
		expectedTagSizes.remove(2);
		assertTrue(actualTagSizes.equals(expectedTagSizes));
		
		input = "del 1";
		po = parser.parseInput(input);
		logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, po.getIndex());
		actualTagList = logic.getTagList();
		expectedTagList.clear();
		assertTrue(actualTagList.equals(expectedTagList));
		
		actualTagSizes = logic.getTagSizes();
		expectedTagSizes.clear();
		assertTrue(actualTagSizes.equals(expectedTagSizes));
	}*/
	
	@Test
	public void testDeleteTaskByIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INDEX);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_NAME);
		LogicFeedback actual = logic.deleteByName(ContentBox.ACTION, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(LogicConstants.MSG_EXCEPTION_DELETE_INVALID_TAB));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteTaskByInvalidIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INVALID_INDEX);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, po.getIndex() + 1);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseFound() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);

		originalCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_SEARCH);
		LogicFeedback actual = logic.search(originalCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		temp.get(0).add(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseNotFound() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);

		originalCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_SEARCH_NOT_FOUND);
		LogicFeedback actual = logic.search(originalCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseEmpty() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);

		originalCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_SEARCH_EMPTY);
		LogicFeedback actual = logic.search(originalCopy, po);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_SEARCH_PHRASE_EMPTY;
		LogicFeedback expected = new LogicFeedback(modifiedCopy, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INDEX);
		LogicFeedback actual = logic.doneByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		temp.get(ListID.COMPLETED.getIndex()).add(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INDEX);
		LogicFeedback actual = logic.doneByIndex(ContentBox.EXPIRED, originalCopy, modifiedCopy, po);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID_TAB;
		LogicFeedback expected = new LogicFeedback(modifiedCopy, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByInvalidIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INVALID_INDEX);
		LogicFeedback actual = logic.doneByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, po.getIndex() + 1);
		LogicFeedback expected = new LogicFeedback(modifiedCopy, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_NAME);
		LogicFeedback actual = logic.doneByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		temp.get(ListID.COMPLETED.getIndex()).add(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByInvalidName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INVALID_NAME);
		LogicFeedback actual = logic.doneByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND;
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_NAME);
		LogicFeedback actual = logic.updateByIndexChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_NAME);
		LogicFeedback actual = logic.updateByIndexChangeName(ContentBox.EXPIRED, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexOutOfBounds() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INVALID_INDEX);
		LogicFeedback actual = logic.updateByIndexChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, po.getIndex() + 1);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromFloatingToDeadline() {
		long currTime = timeConverter.getCurrTime();
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		String taskName = po.getTask().getTaskName();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		String input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                        + NUM_SECONDS_1_WEEK));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromDeadlineToEvent() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_EVENT, timeConverter.getDate(currTime + NUM_SECONDS_1_DAY),
				              timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromEventToFloating() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime), 
				                     timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addEvent(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_DATE_FLOATING);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateEventTaskByIndexChangeDateExpired() {
		long currTime = timeConverter.getCurrTime();
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		String input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                        - NUM_SECONDS_1_DAY));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, po.getTask().getDeadline());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeBoth() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_BOTH);
		LogicFeedback actual = logic.updateByIndexChangeBoth(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
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
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoAdd() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoDelete() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INDEX);
		logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoUpdate() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_BOTH);
		logic.updateByIndexChangeBoth(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoDone() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INDEX);
		logic.doneByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
}