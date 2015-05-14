package org.shikimori.library.tool.actionmode;


import android.support.v7.widget.SearchView;

public class SearchDescription extends ActionDescription{
	
	private SearchView searchView;

	public void act(int[] selectedItems) {
	}

	public SearchView getSearchView() {
		return searchView;
	}

	public void setSearchView(SearchView searchView) {
		this.searchView = searchView;
	}

}
