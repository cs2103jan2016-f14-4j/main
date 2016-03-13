package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * This class is used to provide pagination support with extensions.
 * Such as selecting of elements
 * 
 * @author junwei
 */
public class UiPagination {
	private Pagination myPages;
	private ArrayList<GridPane> myGrids;
	private int currentPage;
	private int currentSelection;
	private ArrayList<ArrayList<StackPane>> totalEntries; // track for arrow key events
	private String selectionStyle;
	
	public Pagination getPagination() {
		return myPages;
	}
	
	public UiPagination(String _selectionStyle) {
		selectionStyle = _selectionStyle;
		myPages = new Pagination(); 
		myGrids = new ArrayList<GridPane>();	
		totalEntries = new ArrayList<ArrayList<StackPane>>();
		myPages.setVisible(false);
	}
	
	/**
	 * Initialize, sets the display for Pagination with totalPages.
	 * Then binds the method for create pages
	 * @param totalPages - the total pages
	 */
	public void initialize( int totalPages ) {
		currentSelection = 0;
		if ( totalPages == 0 ) {// for formatting
			myPages.setPageCount(1);
			myPages.setMaxPageIndicatorCount(1);
		} else {
			myPages.setPageCount(totalPages);
			myPages.setMaxPageIndicatorCount(totalPages);
		}
		myPages.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
		myPages.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                return createPage(pageIndex);
            }
        });
		myPages.setVisible(true);
	}
	
	public int getSelection() { 
		if ( totalEntries.size() == 0 ) {
			return -1;
		}
		int currentIndex = 0; 
		for ( int i = 0; i < totalEntries.size(); i++ ) {
			if ( i != currentPage ) {
				currentIndex += totalEntries.get(i).size();
			} else {
				currentIndex += currentSelection;
				break;
			}
		}
		return currentIndex;
	}
	
	/**
	 * Process arrow key, for the selection to be handled
	 * Needs to be called by the UiFormatters in order to interact with the page
	 *
	 * @param - event the event
	 */
	public void processArrowKey(KeyEvent event) {
		if ( totalEntries.size() == 0 ) {
			return;
		}
		if ( event.getCode() == KeyCode.DOWN) {
			select(currentSelection + 1);
		} else if ( event.getCode() == KeyCode.UP) {
			select(currentSelection - 1);
		} else if ( event.getCode() == KeyCode.RIGHT) {
			myPages.setCurrentPageIndex(currentPage+1); 
		} else {
			myPages.setCurrentPageIndex(currentPage-1); // note Pagination handles negative values
		}
	}	
	
	private void select(int selection) {
		if ( totalEntries.size() == 0 ) {
			return;
		}
		ArrayList<StackPane> pageContent = totalEntries.get(currentPage);
		if ( pageContent.size() == 0 ) { // division by 0
			return;
		}
		selection %= pageContent.size();
		if ( selection < 0 ) {
			selection = pageContent.size()-1;
		}
		// remove previous selection's style
		 StackPane myPane = pageContent.get(currentSelection);
		if ( myPane.getStyleClass().size() > 1) {
			myPane.getStyleClass().remove(1);
		}
		currentSelection = selection;
		myPane = (StackPane) pageContent.get(currentSelection);
		myPane.getStyleClass().add(selectionStyle);
	}

	/**
	 * Adds the grid to pagination, and provides an arraylist of stackpanes
	 * Which will be handled by UiPagination to do selection
	 *
	 * @param theGrid - the grid
	 * @param pageEntries - the page entries
	 */
	public void addGridToPagination(GridPane theGrid, ArrayList<StackPane> pageEntries) {
		totalEntries.add(pageEntries);
		myGrids.add(theGrid);	
	}
	
	/**
	 * Creates the page.
	 * Note this method creates the whole page by default, therefore we keep an arraylist of grids instead
	 * @param pageIndex the page index
	 * @return the grid pane
	 */
	private GridPane createPage(int pageIndex) {
		if ( pageIndex >= myGrids.size() ) {
			return null; // grid has not been initialized
		} 
		if ( currentSelection != 0 ) {
			select(0); // de-select previous selection
		}
		currentSelection = 0;
		currentPage = pageIndex;
		select(0); // only after arraylist has been initialized
		return myGrids.get(pageIndex);
    }	
	
	public void clear() {
		myGrids.clear();
		totalEntries.clear();
	}
}
