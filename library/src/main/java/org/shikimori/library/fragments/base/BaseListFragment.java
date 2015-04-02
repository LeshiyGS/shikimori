package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.shikimori.library.R;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 02.04.2015.
 */
public abstract class BaseListFragment<T extends ActionBarActivity> extends PullableFragment<T> implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    public static final int DEFAULT_FIRST_PAGE = 1;
    protected String search = "";
    protected int page = DEFAULT_FIRST_PAGE;
    protected boolean pauseOnScroll = false; // or true
    protected boolean pauseOnFling = true; // or false

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * При быстрой прокрутке ставим паузу загрузки картинок, что б не тормозило
     */
    PauseOnScrollListener pauseImageLoading = new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling) {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    };

    public void StartFirstLoad(){
        showRefreshLoader();
        loadData();
    }

    @Override
    public void onStartRefresh() {
        page = DEFAULT_FIRST_PAGE;
    }

    public abstract void loadData();

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
    }

    public abstract void setAdapter(BaseAdapter adapter);

    /**
     * Показываем лоадер если есть еще что подгружать
     * @param more
     */
    public abstract void hasMoreItems(boolean more);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        h.showMsg(activity, "click");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_animes_filter) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.animes_menu, menu);
        inflateSearch(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void inflateSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.anime_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    public String getSearchText(){
        return search;
    }

    /**
     * Нажатие на кнопку поиска
     *
     * @param s
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String s) {
        page = DEFAULT_FIRST_PAGE;
        search = s;
        showRefreshLoader();
        loadData();
        return false;
    }

    /**
     * Вызываеться когда меняеться текст в строке поиска
     *
     * @param s
     * @return
     */
    @Override
    public boolean onQueryTextChange(String s) {
        if (s.length() == 0 && search.length() != 0) {
            onQueryTextSubmit("");
            return true;
        }
        return false;
    }
}
