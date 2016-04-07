package taskey.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import taskey.parser.DateTimePatternMatcher;

/**
 * @@author A0107345L
 * Junit testing class for DateTimePatternMatcher
 * @author Xue Hui
 *
 */
public class PatternMatcherTest {
	DateTimePatternMatcher pm = new DateTimePatternMatcher(); 
	
	@Test
	/**
	 * Test that the hasPattern method returns true for dates
	 * that match its format 
	 */
	public void testPatternSet1() {
		
		String string1 = "add project meeting by 3 pm on 17 feb";
		String string2 = "add project meeting on 19 feb from 4pm to 5pm";
		String string3 = "add do homework at 3pm by tomorrow";
		
		assertTrue(pm.hasPattern(string1));
		assertTrue(pm.hasPattern(string2));
		assertTrue(pm.hasPattern(string3));
	}
	
	@Test 
	/**
	 * Test the TimeEdit method
	 */
	public void testPatternSet2() {
		String string4 = "add do homework by 23:00h";
		String string5 = "add do homework by tonight"; 
		
		assertTrue(pm.hasTimeEdit(string4));
		assertTrue(pm.hasTimeEdit(string5));	
	}
	
	@Test 
	/**
	 * Test that correct inputs return false
	 */
	public void testPatternSet3() {
		String string6 = "add do project meeting by 17 feb 3pm";
		assertFalse(pm.hasPattern(string6));
	}
	
	@Test
	/**
	 * Test that time format checkers for autocomplete
	 * works correctly 
	 */
	public void testAutoCompleteTime() {
		assertTrue(pm.hasAmPm("3pm"));
		assertFalse(pm.hasAmPm("feb"));
		
		assertTrue(pm.hasCorrectTimeFormat("3:29 pm")); 
		assertTrue(pm.hasTimeAC("3:30 p"));
		assertTrue(pm.hasTimeAC("08:30 h"));
	}
	
	@Test
	/**
	 * Test date format checkers for autocomplete works correctly
	 */
	public void testAutoCompleteDates() {
		assertTrue(pm.hasDateAC("23"));
		assertFalse(pm.hasDateAC("45")); 
		assertFalse(pm.hasDateAC("23 aug")); 
		
		assertTrue(pm.hasFullDateAC("23 aug")); 
		assertTrue(pm.hasFullDateAC("23")); 
		assertTrue(pm.hasFullDateAC("this friday")); 
		assertFalse(pm.hasFullDateAC("balh balh")); 
	}

}
