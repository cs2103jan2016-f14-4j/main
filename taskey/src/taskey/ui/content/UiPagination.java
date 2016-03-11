package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import taskey.ui.UiConstants;

public class UiPagination {
	private Pagination myPages;
	private ArrayList<GridPane> myGrids;
	private int currentPage;
	private int currentSelection;
	private int entriesPerPage;
	private ArrayList<ArrayList<StackPane>> totalEntries; // track for arrow key events
	
	public Pagination getPagination() {
		return myPages;
	}
	public UiPagination() {
		myPages = new Pagination(); // because there's no method to clear pages
		myGrids = new ArrayList<GridPane>();	
		totalEntries = new ArrayList<ArrayList<StackPane>>();
		myPages.setVisible(false);
	}
	public void initialize( int _entriesPerPage, int totalPages ) {
		currentSelection = 0;
		entriesPerPage = _entriesPerPage;
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
	public int processDeleteKey() {
		if ( totalEntries.size() == 0 ) {
			return -1;
		}
		return currentPage*entriesPerPage + currentSelection + 1;
	}
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
		myPane.getStyleClass().add(UiConstants.STYLE_GRAY_BOX);
	}

	public void addGridToPagination(GridPane theGrid, ArrayList<StackPane> pageEntries) {
		totalEntries.add(pageEntries);
		myGrids.add(theGrid);	
	}
	
	// Note this methods creates the whole page by default, therefore we modify it
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
