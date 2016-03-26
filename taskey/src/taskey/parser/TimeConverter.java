package taskey.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import taskey.constants.ParserConstants;

/* @@author A0107345L */ 
public class TimeConverter {
	
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
		currTime = System.currentTimeMillis()/1000;
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
	public long toEpochTime(String date) throws ParseException { 
		try {
			SimpleDateFormat format = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			format.setLenient(false); //disallow >=3 numbers for dd <-day of month
			long epochTime = format.parse(date).getTime() / 1000;
			return epochTime; 
		} catch (ParseException e) {
			try {
				//date came in dd MMM yyyy
				SimpleDateFormat format2 = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss");
				format2.setLenient(false);
				String date2 = date + " " + ParserConstants.DAY_END; 
				long epochTime = format2.parse(date2).getTime() / 1000;
				return epochTime; 
			} catch (ParseException e2) {
				try {
					SimpleDateFormat format3 = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss");
					format3.setLenient(false);
					String date3 = date + " 2016 " + ParserConstants.DAY_END; 
					long epochTime = format3.parse(date3).getTime() / 1000;
					return epochTime; 
				} catch (ParseException e3) {
					//System.out.println(e3); 
					throw e3; 
				}
			}
		}	
	}
	
	
	/**
	 * Convert an epoch time to human readable time. 
	 * @param epochTime: a long number that you want to convert to human readable format
	 * @return String: human readable time 
	 */
	public String toHumanTime(long epochTime) {
		String humanTime = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm").format(
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
		Calendar c1 = Calendar.getInstance();
		c1.setFirstDayOfWeek(Calendar.MONDAY);
		c1.set(getYear(epochTime1),
				getMonth(epochTime1)-1,
				getDay(epochTime1));
		int year1 = c1.get(c1.YEAR);
		int week1 = c1.get(c1.WEEK_OF_YEAR);

		Calendar c2 = Calendar.getInstance();
		c2.setFirstDayOfWeek(Calendar.MONDAY);
		c2.set(getYear(epochTime2),
				getMonth(epochTime2)-1,
				getDay(epochTime2));
		int year2 = c2.get(c2.YEAR);
		int week2 = c2.get(c2.WEEK_OF_YEAR);

		if(year1 == year2){
		       if (week1 == week2) {
		         return true; 
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
	
	/**
	 * converts an epoch time to date, without the hours:mins:seconds inside
	 * @param epochTime
	 * @return string format for date
	 */
	public String getDate(long epochTime) {
		String date = new java.text.SimpleDateFormat("dd MMM yyyy").format(
				new java.util.Date(epochTime*1000));
		
		return date; 
	}
	
	/**
	 * converts an epoch time to only HH:mm (hour and minute) of the day, 
	 * without the date.
	 * @param epochTime
	 * @return
	 */
	public String getTime(long epochTime) {
		String time = new java.text.SimpleDateFormat("HH:mm").format(
				new java.util.Date(epochTime*1000));
		
		return time;
	}
	
	/**
	 * Get human format day of the week.
	 * No params: get today's day
	 * eg. mon, tues... 
	 * @return
	 */
	public String getDayOfTheWeek() {
		//first day of the week starts from Sunday 
		String[] days = {"SUN","MON","TUE","WED","THU","FRI","SAT"}; 
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(currTime),
				getMonth(currTime)-1,
				getDay(currTime));
		int day = cal.get(cal.DAY_OF_WEEK); 
		
		return days[day-1]; 
	}
	
	/**
	 * Get human format day of the week.
	 * with an epoch time: get that epochTime's day
	 * eg. mon, tues... 
	 * @return
	 */
	public String getDayOfTheWeek(long epochTime) {
		//first day of the week starts from Sunday 
		String[] days = {"SUN","MON","TUE","WED","THU","FRI","SAT"}; 
		Calendar cal = Calendar.getInstance();
		cal.set(getYear(epochTime),
				getMonth(epochTime)-1,
				getDay(epochTime));
		int day = cal.get(cal.DAY_OF_WEEK); 
		
		return days[day-1]; 
	}
	
	/*
	public static void main(String[] args) {
		TimeConverter tc = new TimeConverter();
		try {
			long epochTime1 = tc.toEpochTime("11 Mar");
			long epochTime2 = tc.toEpochTime("21 Mar"); 
			System.out.println(tc.toHumanTime(epochTime1));
			System.out.println(tc.isSameWeek(epochTime1, epochTime2));
		} catch (Exception e) {
			System.out.println(e);
		}
	} */ 
}
