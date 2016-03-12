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
		System.out.println(input);
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
		LogicFeedback actual = logic.deleteByIndex(ContentBox.THIS_WEEK, po, 1);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
	
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
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, po, 1);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
}
