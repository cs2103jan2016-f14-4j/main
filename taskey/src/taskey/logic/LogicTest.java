package taskey.logic;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import taskey.logic.LogicConstants.ListID;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;
import taskey.ui.UiConstants.ContentBox;

public class LogicTest {
	
	public static int NUM_SECONDS_1_DAY = 86400;
	public static int NUM_SECONDS_1_WEEK = 604800;
	
	@BeforeClass
	public static void testClear() {
		Logic logic = new Logic();
		LogicFeedback actual = logic.executeCommand(ContentBox.PENDING, "clear");
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, new ProcessedObject("CLEAR"), null);
		
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
		LogicFeedback actual = logic.addFloating(t, po);
		
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
		LogicFeedback actual = logic.addDeadline(t, po);
		
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
		LogicFeedback actual = logic.addDeadline(t, po);
		
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
		LogicFeedback actual = logic.addEvent(t, po);
		
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
		LogicFeedback actual = logic.addEvent(t, po);
		
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
	public void testDuplicateTaskName() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String input = "add g2 a?b ,  ";
		ProcessedObject po = parser.parseInput(input);
		Task t = po.getTask();
		logic.addFloating(t, po);
		LogicFeedback actual = logic.addFloating(t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.GENERAL.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("The task " 
		                                           + t.getTaskName() + " already exists!"));
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.THIS_WEEK, po, 0);
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, po, 0);
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByName(ContentBox.THIS_WEEK, po, t.getTaskName());
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByName(ContentBox.PENDING, po, t.getTaskName());
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.EXPIRED, po, 0);
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, po, 1);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("Index out of bounds!"));
		
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
		logic.addDeadline(t, po);
		LogicFeedback actual = logic.deleteByName(ContentBox.PENDING, po, "ayy lmao");
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("ayy lmao" 
				                                   + " not found in this list!"));
		
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String addInput = "add g2 a?b ,  ";
		ProcessedObject addProcessedObj = parser.parseInput(addInput);
		Task t = addProcessedObj.getTask();
		logic.addFloating(t, addProcessedObj);

		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String searchPhrase = "a?";
		String searchInput = "search " + searchPhrase;
		ProcessedObject searchProcessedObj = parser.parseInput(searchInput);
		LogicFeedback actual = logic.search(searchProcessedObj, searchPhrase);	
		temp.get(0).add(t);
		LogicFeedback expected = new LogicFeedback(temp, searchProcessedObj, null);
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseNotFound() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String addInput = "add g2 a?b ,  ";
		ProcessedObject addProcessedObj = parser.parseInput(addInput);
		Task t = addProcessedObj.getTask();
		logic.addFloating(t, addProcessedObj);

		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String searchPhrase = "2a";
		String searchInput = "search " + searchPhrase;
		ProcessedObject searchProcessedObj = parser.parseInput(searchInput);
		LogicFeedback actual = logic.search(searchProcessedObj, searchPhrase);
		LogicFeedback expected = new LogicFeedback(temp, searchProcessedObj, null);
		assertTrue(actual.equals(expected));
	}
	
	@Test
	public void testSearchPhraseEmpty() {
		Logic logic = new Logic();
		Parser parser = new Parser();
		logic.executeCommand(ContentBox.PENDING, "clear");
		String addInput = "add g2 a?b ,  ";
		ProcessedObject addProcessedObj = parser.parseInput(addInput);
		Task t = addProcessedObj.getTask();
		logic.addFloating(t, addProcessedObj);

		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String searchPhrase = "";
		String searchInput = "search " + searchPhrase;
		ProcessedObject searchProcessedObj = parser.parseInput(searchInput);
		LogicFeedback actual = logic.search(searchProcessedObj, searchPhrase);
		LogicFeedback expected = new LogicFeedback(logic.getAllTaskLists(), searchProcessedObj, 
				                     new Exception ("Search phrase cannot be empty!"));
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
		logic.addDeadline(t, po);
		po = parser.parseInput("done 0");
		LogicFeedback actual = logic.doneByIndex(ContentBox.THIS_WEEK, po, 0);
		
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
		logic.addDeadline(t, po);
		po = parser.parseInput("done 0");
		LogicFeedback actual = logic.doneByIndex(ContentBox.PENDING, po, 0);
		
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
		logic.addDeadline(t, po);
		po = parser.parseInput("done 0");
		LogicFeedback actual = logic.doneByIndex(ContentBox.EXPIRED, po, 0);
		
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
		logic.addDeadline(t, po);
		po = parser.parseInput("done 1");
		LogicFeedback actual = logic.doneByIndex(ContentBox.PENDING, po, 1);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(t);
		temp.get(ListID.THIS_WEEK.getIndex()).add(t);
		temp.get(ListID.DEADLINE.getIndex()).add(t);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception("Index out of bounds!"));
		
		assertTrue(actual.equals(expected));
	}
}
