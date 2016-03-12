package taskey.logic;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import taskey.ui.UiConstants.ContentBox;

public class LogicTest {
	
	@Test
	public void testClear() {
		Logic logic = new Logic();
		LogicFeedback actual = logic.executeCommand(ContentBox.PENDING, "clear");
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		LogicFeedback expected = new LogicFeedback(temp, new ProcessedObject("CLEAR"), null);
		
		assertTrue(actual.equals(expected));
	}
	
	/*@Test
	public void testAddFloating() {
		Logic logic = new Logic();
		Task t = new Task("oneword");
		ProcessedObject po = new ProcessedObject("FLOATING", t);
	}*/
}
