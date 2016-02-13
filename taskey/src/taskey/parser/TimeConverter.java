package taskey.parser;

import java.text.ParseException;

public class TimeConverter {
	public static final long ONE_DAY = 86400; 
	public static final long ONE_WEEK = 604800; 
	
	//store curr time in seconds 
	private long currTime = System.currentTimeMillis()/1000;
	
	
	public long getCurrTime() {
		return currTime; 
	}
	
	public long toEpochTime(String date) {
		//note that date has to be of the format eg: "01 Jan 1970 01:00:00" 
		try {
			long epochTime = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss").parse(
					date).getTime() / 1000;
			return epochTime; 
		} catch (ParseException e) {
			System.out.println(e); 
			return -1; 
		}	
	}
	
	public String toHumanTime(long epochTime) {
		String humanTime = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(
				new java.util.Date(epochTime*1000));
		
		return humanTime; 
	}
	
	
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
	
	
	public int getYear(long epochTime) {
		String year = new java.text.SimpleDateFormat("yyyy").format(
				new java.util.Date(epochTime*1000));
		
		return Integer.parseInt(year); 
	}
	
	
	public int getMonth(long epochTime) {
		String month = new java.text.SimpleDateFormat("MM").format(
				new java.util.Date(epochTime*1000));
		
		return Integer.parseInt(month); 
	}
	
	public int getDay(long epochTime) {
		String day = new java.text.SimpleDateFormat("dd").format(
				new java.util.Date(epochTime*1000));
		
		return Integer.parseInt(day); 
	}

}
