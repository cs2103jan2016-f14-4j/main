package taskey.junit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import taskey.parser.AutoComplete;

/**
 * @@author: A0107345L 
 * Tests the AutoComplete class
 * @author Xue Hui
 *
 */
public class AutoCompleteTest {
	AutoComplete ac = new AutoComplete(); 
	
	@Test
	/**
	 * Check that correcting wrong spellings for months
	 * gives the correct suggestions 
	 */
	public void testSpellChecker() {
		String[] months = {"feb","mar","apr"}; 
		ArrayList<String> correction = ac.correctDateError("fbr"); 
		
		assertEquals(months.length, correction.size()); 
		
		for(int i = 0; i < months.length; i++) {
			assertEquals(months[i], correction.get(i)); 
		}
		

		//TODO: implement correctDateError test 
	}

}
