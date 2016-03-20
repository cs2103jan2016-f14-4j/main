package taskey.logic;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.LogicConstants.ListID;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;
import taskey.parser.UserTagDatabase;

/**
 * @author Hubert
 */
public class LogicTest {
	
	public static int NUM_SECONDS_1_DAY = 86400;
	public static int NUM_SECONDS_1_WEEK = 604800;
	public static int NUM_SECONDS_BUFFER_TIME = 100;
	
	@BeforeClass
	public static void testClear() {
		Logic logic = new Logic();
		LogicFeedback actual = logic.executeCommand(ContentBox.PENDING, "clear");
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, new ProcessedObject("CLEAR"), null); //Stub
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddFloating() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String input = "add g2 a?b ,  ";
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addFloating(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.GENERAL.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	 
	@Test
	public void testAddDeadlineForThisWeek() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addDeadline(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddDeadlineOutsideThisWeek() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " 
		                + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 10000);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addDeadline(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddEventForThisWeek() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  from " + timeConverter.getDate(currTime)
		                + " to " + timeConverter.getDate(currTime + 10000);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addEvent(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.EVENT.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddEventOutsideThisWeek() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  from " 
						+ timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 10000)
		                + " to " + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 50000);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addEvent(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.EVENT.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddExpiredDeadlineTask() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addDeadline(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("The date " + t.getDeadline()
				                                                           + " is already past!"));
		
		assertTrue(actual.equals(expected));
		
	}
	
	@Test
	public void testAddExpiredEventTask() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  from " + timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK)
		                + " to " + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		LogicFeedback actual = logic.addEvent(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("The date " + t.getEndDate()
				                                                           + " is already past!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testAddTags() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		UserTagDatabase utd = new UserTagDatabase();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  #tag1 #tag2";
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t, po);
		input = "add g2 a?b  ,  on " + timeConverter.getDate(currTime) + " #tag2";
		po = parser.parseInput(input);
		t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		input = "add g2 a?b ,  from " + timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK)
		         + " to " + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY)
		         + " #tag1 #tag3";
		po = parser.parseInput(input);
		t = po.getTask();
		logic.addEvent(logic.getAllTaskLists(), t, po);
		
		/*
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
		assertTrue(actualTagSizes.equals(expectedTagSizes)); */ 
	}
	
	@Test
	public void testAddDuplicateTaskName() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String input = "add g2 a?b ,  ";
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t, po);
		LogicFeedback actual = logic.addFloating(logic.getAllTaskLists(), t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.GENERAL.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("The task \"" + t.getTaskName() 
		                                           + "\" already exists!"));
		
		assertTrue(actual.equals(expected));
	}
	
	
	@Test
	public void testDeleteTaskByIndexFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del 0");
		LogicFeedback actual = logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteTaskByIndexFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del 0");
		LogicFeedback actual = logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteTaskByNameFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,   on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del " + t.getTaskName());
		LogicFeedback actual = logic.deleteByName(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, t.getTaskName());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteTaskByNameFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,   on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del " + t.getTaskName());
		LogicFeedback actual = logic.deleteByName(logic.getAllTaskLists(), ContentBox.PENDING, po, t.getTaskName());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Ignore
	public void testDeleteTaskByIndexFromExpiredTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del 0");
		LogicFeedback actual = logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.EXPIRED, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Ignore
	public void testDeleteTaskByNameFromExpiredTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del " + t.getTaskName());
		LogicFeedback actual = logic.deleteByName(logic.getAllTaskLists(), ContentBox.EXPIRED, po, t.getTaskName());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteFromWrongTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del 0");
		LogicFeedback actual = logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.ACTION, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, 
				                                   new Exception("Cannot delete from this tab!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteTaskByIndexOutOfBounds() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del 2");
		LogicFeedback actual = logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, po.getIndex());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("\"2\" is not a valid index!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDeleteTaskNotFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,   on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del ayy lmao");
		LogicFeedback actual = logic.deleteByName(logic.getAllTaskLists(), ContentBox.PENDING, po, "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("\"ayy lmao\"" 
				                                   + " not found in this list!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		ProcessedObject po = parser.parseInput("add g2 a?b ,  ");
		Task t = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t, po);

		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String searchPhrase = "a?";
		po = parser.parseInput("search " + searchPhrase);
		LogicFeedback actual = logic.search(logic.getAllTaskLists(), po, searchPhrase);	
		temp.get(0).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseNotFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		ProcessedObject po = parser.parseInput("add g2 a?b ,  ");
		Task t = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t, po);

		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String searchPhrase = "2a";
		po = parser.parseInput("search " + searchPhrase);
		LogicFeedback actual = logic.search(logic.getAllTaskLists(), po, searchPhrase);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseEmpty() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		ProcessedObject po = parser.parseInput("add g2 a?b ,  ");
		Task t = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), t, po);

		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.GENERAL.getIndex()).add(t);
		
		String searchPhrase = "";
		po = parser.parseInput("search " + searchPhrase);
		LogicFeedback actual = logic.search(logic.getAllTaskLists(), po, searchPhrase);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception ("Search phrase cannot be empty!"));
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByIndexFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done 0");
		LogicFeedback actual = logic.doneByIndex(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.COMPLETED.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByIndexFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done 0");
		LogicFeedback actual = logic.doneByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.COMPLETED.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneFromWrongTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done 0");
		LogicFeedback actual = logic.doneByIndex(logic.getAllTaskLists(), ContentBox.EXPIRED, po, 0);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("Cannot use \"done\" command from this tab!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneByIndexOutOfBounds() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done 2");
		LogicFeedback actual = logic.doneByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, po.getIndex());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("\"2\" is not a valid index!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByNameFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done " + t.getTaskName());
		LogicFeedback actual = logic.doneByName(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, t.getTaskName());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.COMPLETED.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByNameFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done " + t.getTaskName());
		LogicFeedback actual = logic.doneByName(logic.getAllTaskLists(), ContentBox.PENDING, po, t.getTaskName());
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.COMPLETED.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testDoneTaskByNameNotFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done " + "ayy lmao");
		LogicFeedback actual = logic.doneByName(logic.getAllTaskLists(), ContentBox.PENDING, po, "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("ayy lmao does not exist in this tab!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeNameFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		Task afterUpdate = beforeUpdate.getDuplicate();
		afterUpdate.setTaskName("ayy lmao");
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 \"ayy lmao \"");
		LogicFeedback actual = logic.updateByIndexChangeName(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, 0, "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeNameFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		Task afterUpdate = beforeUpdate.getDuplicate();
		afterUpdate.setTaskName("ayy lmao");
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 \"ayy lmao \"");
		LogicFeedback actual = logic.updateByIndexChangeName(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexFromWrongTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("set 0 \"ayy lmao \"");
		LogicFeedback actual = logic.updateByIndexChangeName(logic.getAllTaskLists(), ContentBox.EXPIRED, po, 0, "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("Cannot use \"set\" command from this tab!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexOutOfBounds() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("set 2 \"ayy lmao \"");
		LogicFeedback actual = logic.updateByIndexChangeName(logic.getAllTaskLists(), ContentBox.PENDING, po, po.getIndex(), "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("\"2\" is not a valid index!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateSameWeekFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "[" + timeConverter.getDate(currTime + 10) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateSameWeekFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "[" + timeConverter.getDate(currTime + 10) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateDiffWeekFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "[" + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 10000) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateDiffWeekFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "[" + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 10000) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromFloatingToDeadline() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b";
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "[" + timeConverter.getDate(currTime) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromDeadlineToEvent() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "[" + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK 
				               + 10000) + ", " + timeConverter.getDate(currTime 
				               + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.EVENT.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromEventToFloating() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b from " + timeConverter.getDate(currTime) + " to "
				        + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addEvent(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 [none]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.GENERAL.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateEventTaskByIndexChangeDatePast() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b from " + timeConverter.getDate(currTime) + " to "
				        + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addEvent(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 [" + timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK) 
		                       + ", " + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName(beforeUpdate.getTaskName());
		temp.get(ListID.PENDING.getIndex()).add(beforeUpdate);
		temp.get(ListID.EVENT.getIndex()).add(beforeUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(beforeUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("The date " + afterUpdate.getEndDate()
		                                                                   + " is already past!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeNameFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		Task afterUpdate = beforeUpdate.getDuplicate();
		afterUpdate.setTaskName("ayy lmao");
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() +  " \"ayy lmao \"");
		LogicFeedback actual = logic.updateByNameChangeName(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, beforeUpdate.getTaskName(),
				                                            "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeNameFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		Task afterUpdate = beforeUpdate.getDuplicate();
		afterUpdate.setTaskName("ayy lmao");
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() +  " \"ayy lmao \"");
		LogicFeedback actual = logic.updateByNameChangeName(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(),
				                                            "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeNameFromWrongTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String input = "add g2 a?b";
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() +  " \"ayy lmao \"");
		LogicFeedback actual = logic.updateByNameChangeName(logic.getAllTaskLists(), ContentBox.EXPIRED, po, beforeUpdate.getTaskName(),
				                                            "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(beforeUpdate);
		temp.get(ListID.GENERAL.getIndex()).add(beforeUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("Cannot use \"set\" command from this tab!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameNotFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String input = "add g2 a?b";
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() +  " \"ayy lmao \"");
		LogicFeedback actual = logic.updateByNameChangeName(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, beforeUpdate.getTaskName(),
				                                            "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(beforeUpdate);
		temp.get(ListID.GENERAL.getIndex()).add(beforeUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(beforeUpdate.getTaskName() 
				                                   + " not found in this list!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateSameWeekFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime + 10) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, beforeUpdate.getTaskName(),
				                                            afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateDeadlineTaskByNameChangeDatePast() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime - NUM_SECONDS_1_DAY) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, beforeUpdate.getTaskName(),
				                                            afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(beforeUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(beforeUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(beforeUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("The date " + afterUpdate.getDeadline()
		                                                                   + " is already past!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateSameWeekFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime + 10) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(),
				                                            afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateDiffWeekFromThisWeekTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 10000) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.THIS_WEEK, po, beforeUpdate.getTaskName(), 
				                                            afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateDiffWeekFromPendingTab() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + 10000) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(), 
				                                            afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateFromFloatingToDeadline() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b";
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(), afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.DEADLINE.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateFromDeadlineToEvent() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [" + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK 
				               + 10000) + ", " + timeConverter.getDate(currTime 
				               + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(), afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.EVENT.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeDateFromEventToFloating() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b from " + timeConverter.getDate(currTime) + " to "
				        + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY);
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addEvent(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " [none]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeDate(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(), afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.GENERAL.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByIndexChangeBoth() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  ";
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set 0 " + "\"ayy lmao\" " + "[" + timeConverter.getDate(currTime) 
		                       + ", " + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK 
		                       + NUM_SECONDS_BUFFER_TIME) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByIndexChangeBoth(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, "ayy lmao",
				                                             afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName("ayy lmao");
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.EVENT.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUpdateTaskByNameChangeBoth() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  ";
		ProcessedObject po = parser.parseInput(input);
		Task beforeUpdate = po.getTask();
		logic.addFloating(logic.getAllTaskLists(), beforeUpdate, po);
		po = parser.parseInput("set " + beforeUpdate.getTaskName() + " \"ayy lmao\" " + "[" 
		                       + timeConverter.getDate(currTime)  + ", " 
				               + timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK 
		                       + NUM_SECONDS_BUFFER_TIME) + "]");
		Task afterUpdate = po.getTask();
		LogicFeedback actual = logic.updateByNameChangeBoth(logic.getAllTaskLists(), ContentBox.PENDING, po, beforeUpdate.getTaskName(), 
				                                            "ayy lmao", afterUpdate);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		afterUpdate.setTaskName("ayy lmao");
		temp.get(ListID.PENDING.getIndex()).add(afterUpdate);
		temp.get(ListID.THIS_WEEK.getIndex()).add(afterUpdate);
		temp.get(ListID.EVENT.getIndex()).add(afterUpdate);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoAdd() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("undo");
		LogicFeedback actual = logic.undo(po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoDelete() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("del 0");
		logic.deleteByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, 0);
		po = parser.parseInput("undo");
		LogicFeedback actual = logic.undo(po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.PENDING.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoUpdate() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("set 0 [none] \"ayy lmao\"");
		logic.updateByIndexChangeBoth(logic.getAllTaskLists(), ContentBox.PENDING, po, 0, "ayy lmao", po.getTask());
		po = parser.parseInput("undo");
		LogicFeedback actual = logic.undo(po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.PENDING.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testUndoDone() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		TimeConverter timeConverter = new TimeConverter();
		logic.executeCommand(ContentBox.PENDING, "clear");
		long currTime = timeConverter.getCurrTime();
		String input = "add g2 a?b ,  on " + timeConverter.getDate(currTime);
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addDeadline(logic.getAllTaskLists(), t, po);
		po = parser.parseInput("done 0");
		logic.doneByIndex(logic.getAllTaskLists(), ContentBox.PENDING, po, 0);
		po = parser.parseInput("undo");
		LogicFeedback actual = logic.undo(po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.PENDING.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
}
