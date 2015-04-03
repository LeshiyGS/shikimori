package org.shikimori.library.fragments.base.abstracts;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.shikimori.library.R;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Владимир on 02.04.2015.
 */
public abstract class BaseListFragment<T extends ActionBarActivity> extends PullableFragment<T> implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    public static final int DEFAULT_FIRST_PAGE = 1;
    public static final int LIMIT = 20;
    protected int page = DEFAULT_FIRST_PAGE;
    protected String search = "";
    protected boolean pauseOnScroll = false; // or true
    protected boolean pauseOnFling = true; // or false
    ArrayAdapter adapter;

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

    public void StartFirstLoad() {
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
     *
     * @param more
     */
    public abstract void hasMoreItems(boolean more);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    public String getSearchText() {
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

    public abstract ArrayAdapter<?> getAdapter(List<?> list);

    protected void prepareData(List<?> list, boolean removeLastItem, boolean limitOver) {

        int size = list.size();
        // повешать на +1 или нет
        int limit = limitOver ? (LIMIT+1) : LIMIT;
        // если предыдущее количество кратно limit+1
        // значит есть еще данные
        if(size!=0 && size%(limit) == 0){
            hasMoreItems(true);
            // удаляем последний элемент
            if(removeLastItem)
                list.remove(size - 1);
        } else
            hasMoreItems(false);

        if (adapter == null) {
            adapter = getAdapter(list);
            setAdapter(adapter);
        } else {
            if (page == DEFAULT_FIRST_PAGE)
                adapter.clear();

            for (int i = 0; i < list.size(); i++) {
                adapter.add(list.get(i));
            }
        }
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
