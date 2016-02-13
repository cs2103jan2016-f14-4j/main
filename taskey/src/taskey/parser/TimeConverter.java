package taskey.parser;

import java.text.ParseException;

public class TimeConverter {
	public static final long ONE_DAY = 86400; 
	public static final long ONE_WEEK = 604800; 
	
	//store curr time in seconds 
	private long currTime = System.currentTimeMillis()/1000;
	//======================================================
	
	/**
	 * Constructor 
	 */
	public TimeConverter() {
		
	}
	
	/**
	 * @return the value of the variable, currTime 
	 */
	public long getCurrTime() {
		return currTime; 
	}	
	
	/**
	 * Method to update currentTime in this class. 
	 */
	public void setCurrTime() {
		currTime = System.currentTimeMillis()/1000;
	}
	
	/**
	 * Convert a properly formatted date to its Epoch Format
	 * @param date: String in the format: dd MMM yyyy HH:mm:ss
	 * @return date in Epoch Time. 
	 */
	public long toEpochTime(String date) { 
		try {
			long epochTime = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss").parse(
					date).getTime() / 1000;
			return epochTime; 
		} catch (ParseException e) {
			System.out.println(e); 
			return -1; 
		}	
	}
	
	/**
	 * Convert an epoch time to human readable time. 
	 * @param epochTime: a long number that you want to convert to human readable format
	 * @return String: human readable time 
	 */
	public String toHumanTime(long epochTime) {
		String humanTime = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(
				new java.util.Date(epochTime*1000));
		
		return humanTime; 
	}
	
	/**
	 * Checks if 2 given times in Epoch Format are in the same day
	 * @param epochTime1
	 * @param epochTime2
	 * @return true if they are in the same day 
	 */
	public boolean isSameDay(long epochTime1, long epochTime2) {
		int yearDiff = Math.abs(getYear(epochTime1) - getYear(epochTime2)); 
		int monthDiff = Math.abs(getMonth(epochTime1) - getMonth(epochTime2));
		int dayDiff = Math.abs(getDay(epochTime1) - getDay(epochTime2));
		
		if (yearDiff == 0) {
			if (monthDiff == 0) {
				if (dayDiff == 0) {
					return true;
				}
			}
		} 
		return false; 
	}
	
	/**
	 * Checks if 2 given times in Epoch Format are in the same week
	 * @param epochTime1
	 * @param epochTime2
	 * @return true if they are in the same week 
	 */
	public boolean isSameWeek(long epochTime1, long epochTime2) {
		int yearDiff = Math.abs(getYear(epochTime1) - getYear(epochTime2)); 
		int monthDiff = Math.abs(getMonth(epochTime1) - getMonth(epochTime2));
		int dayDiff = Math.abs(getDay(epochTime1) - getDay(epochTime2));
		
		if (yearDiff == 0) {
			if (monthDiff == 0) {
				if (dayDiff <= 7) {
					return true;
				}
			}
		} 
		return false; 
	}
	
	/**
	 * @param epochTime
	 * @return Year as an integer 
	 */
	public int getYear(long epochTime) {
		String year = new java.text.SimpleDateFormat("yyyy").format(
				new java.util.Date(epochTime*1000));
		
		return Integer.parseInt(year); 
	}
	
	/**
	 * @param epochTime
	 * @return Month as an integer (0-12) 
	 */
	public int getMonth(long epochTime) {
		String month = new java.text.SimpleDateFormat("MM").format(
				new java.util.Date(epochTime*1000));
		
		return Integer.parseInt(month); 
	}
	
	/**
	 * @param epochTime
	 * @return day as an integer (0-31) 
	 */
	public int getDay(long epochTime) {
		String day = new java.text.SimpleDateFormat("dd").format(
				new java.util.Date(epochTime*1000));
		
		return Integer.parseInt(day); 
	}

}
