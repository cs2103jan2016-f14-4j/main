package taskey.ui.content;

import java.util.ArrayList;

import javafx.geometry.Insets;

/**
 * Helper class for static initialization objects for grid, placed in UiConstants.
 *
 * @author JunWei
 */
public class UiGridSettings {

	private int gridHGap;
	private int gridVGap;
	private int paddings;
	private ArrayList<Integer> colPercents;
	
	/**
	 * Instantiates a new ui grid settings.
	 *
	 * @param _gridHGap - the horizontal gap between cells
	 * @param _gridVGap - the vertical gap between cells
	 * @param _paddings - the insets of cells
	 * @param _colPercents - how much space a column occupies
	 */
	public UiGridSettings( int _gridHGap, int _gridVGap, int _paddings, int ... _colPercents) {
		gridHGap = _gridHGap;
		gridVGap = _gridVGap;
		paddings = _paddings;
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
	
	public Insets getPaddings() {
		return new Insets(paddings);
	}
	
	public ArrayList<Integer> getColPercents() {
		return colPercents;
	}
}
