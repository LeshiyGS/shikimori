package org.shikimori.library.fragments.base.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.json.JSONArray;
import org.shikimori.library.R;
import org.shikimori.library.interfaces.OnAdvancedCheck;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.loaders.BackGroubdLoader;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.LoadAsyncBuildHelper;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.ArrayList;
import java.util.List;

import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Владимир on 02.04.2015.
 */
public abstract class BaseListFragment<T extends AppCompatActivity> extends PullableFragment<T> implements Query.OnQuerySuccessListener,
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener,
        OnBaseListListener {
    public static final int DEFAULT_FIRST_PAGE = 1;
    public static final int LIMIT = 20;
    protected int page = DEFAULT_FIRST_PAGE;
    protected String search = "";
    protected boolean pauseOnScroll = false; // or true
    protected boolean pauseOnFling = true; // or false
    BaseAdapter adapter;
    private BackGroubdLoader<? extends JsonParseable> backBuilder;
    List<Object> allList = new ArrayList<>();
    private LoadAsyncBuildHelper lah;

    protected boolean isOptionsMenu() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(isOptionsMenu());
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lah = new LoadAsyncBuildHelper(activity, this);
    }

    public abstract void loadData();

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
    }

    public abstract void setAdapter(BaseAdapter adapter);

    public void setAdapter(BaseAdapter adapter, boolean saveState){
        setAdapter(adapter);
    }

    /**
     * Показываем лоадер если есть еще что подгружать
     *
     * @param more
     */
    public abstract void hasMoreItems(boolean more);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("test", "click");
    }

    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater) {
        if(isOptionsMenu()){
            inflater.inflate(R.menu.animes_menu, menu);
            inflateSearch(menu);
        }
        return menu;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(getActionBarMenu(menu, inflater), inflater);
//        if(activity!=null)
//            activity.supportInvalidateOptionsMenu();
    }

    protected void inflateSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.anime_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(activity.getString(R.string.search));
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

    public abstract BaseAdapter getAdapter(List<?> list);

    @Override
    public void prepareData(List<?> list, boolean removeLastItem, boolean limitOver) {

        if (activity == null)
            return;

        int size = list.size();
        // повышать на +1 или нет
        int limit = limitOver ? (LIMIT + 1) : LIMIT;
        // если предыдущее количество кратно limit+1
        // значит есть еще данные
        if (size != 0 && size % (limit) == 0) {
            hasMoreItems(true);
            // удаляем последний элемент
            if (removeLastItem)
                list.remove(size - 1);
        } else
            hasMoreItems(false);

        if (page == DEFAULT_FIRST_PAGE)
            allList.clear();

        for (int i = 0; i < list.size(); i++)
            allList.add(list.get(i));

        if (adapter == null) {
            adapter = getAdapter(allList);
            setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    protected void removeItem(int position) {
        if (allList != null) {
            allList.remove(position);
            adapter = getAdapter(allList);
            setAdapter(adapter, true);
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

    public void loadAsyncBuild(final BodyBuild bodyBuild, JSONArray array, Class<? extends JsonParseable> cl) {
        lah.loadAsyncBuild(bodyBuild, array, 0, cl, null);
    }

    public void loadAsyncBuild(final BodyBuild bodyBuild, JSONArray array, int maxLenght, Class<? extends JsonParseable> cl) {
        lah.loadAsyncBuild(bodyBuild, array, maxLenght, cl, null);
    }

    public void loadAsyncBuild(final BodyBuild bodyBuild, JSONArray array, int maxLenght, Class<? extends JsonParseable> cl, OnAdvancedCheck listener) {
        lah.loadAsyncBuild(bodyBuild, array, maxLenght, cl, listener);
    }

    public List<Object> getAllList() {
        return allList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backBuilder != null)
            backBuilder.cancelLoad();
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }
}
