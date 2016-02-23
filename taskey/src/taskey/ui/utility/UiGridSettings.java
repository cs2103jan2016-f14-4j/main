package taskey.ui.utility;

import java.util.ArrayList;

/**
 * Helper class for static initialization objects for grid, placed in Constants
 * @author JunWei
 *
 */
public class UiGridSettings {

	private int gridHGap;
	private int gridVGap;
	
	private ArrayList<Integer> colPercents;
	
	public UiGridSettings( int _gridHGap, int _gridVGap, int ... _colPercents) {
		gridHGap = _gridHGap;
		gridVGap = _gridVGap;
		colPercents = new ArrayList<Integer>();
		for ( int percent : _colPercents ) {
			colPercents.add(new Integer(percent));
		}
	}
	
	public int getHGap() {
		return gridHGap;
	}
	public int getVGap() {
		return gridVGap;
	}
	public ArrayList<Integer> getColPercents() {
		return colPercents;
	}
}
