package org.shikimori.library.tool;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.shikimori.library.R;

/**
 * Created by Владимир on 20.08.2015.
 */
public class SearchMenu implements SearchView.OnQueryTextListener {

    private final Menu menu;
    private final MenuInflater inflater;
    private String search;
    private SearchView.OnQueryTextListener searchListener;
    private int menuLayout;

    public SearchMenu(Menu menu, MenuInflater inflater){
        this.menu = menu;
        this.inflater = inflater;
    }

    public void onCreateOptionsMenu(int menuLayout, int idMenuSearch, SearchView.OnQueryTextListener searchListener) {
        this.searchListener = searchListener;
        inflater.inflate(menuLayout, menu);
        MenuItem searchItem = menu.findItem(idMenuSearch);
        if(searchItem!=null){
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchListener.onQueryTextSubmit(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() == 0 && search.length() != 0) {
            onQueryTextSubmit("");
            return true;
        }
        return false;
    }
}
