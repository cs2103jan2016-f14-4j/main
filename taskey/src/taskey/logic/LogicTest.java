package taskey.logic;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import taskey.logic.LogicConstants.ListID;
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
		logic.executeCommand(ContentBox.PENDING, "clear");
		Task t = new Task("g2 a?b ,  ");
		ProcessedObject po = new ProcessedObject("FLOATING", t);
		LogicFeedback actual = logic.addFloating(t, po);
		
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		temp.get(ListID.PENDING.getIndex()).add(new Task("g2 a?b ,  "));
		temp.get(ListID.GENERAL.getIndex()).add(new Task("g2 a?b ,  "));
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertTrue(actual.equals(expected));
	}
}
