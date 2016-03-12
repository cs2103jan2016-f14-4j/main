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
		String input = "add g2 a?b ,  on " + timeConverter.getDate(timeConverter.getCurrTime());
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
		logic.executeCommand(ContentBox.PENDING, "clear");
		String input = "add g2 a?b ,  on 12 mar 2017"; //Stub for now
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
}
