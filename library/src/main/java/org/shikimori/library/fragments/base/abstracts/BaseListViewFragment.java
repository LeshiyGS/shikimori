package org.shikimori.library.fragments.base.abstracts;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.paging.listview.PagingListView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import ru.altarix.basekit.library.actionmode.ActionDescription;
import ru.altarix.basekit.library.actionmode.ActionSelectCallBack;
import ru.altarix.basekit.library.actionmode.DestroyActionCallback;
import org.shikimori.library.tool.controllers.ShikiAC;

import java.util.List;

import ru.altarix.basekit.library.activities.BaseKitActivity;

/**
 * Created by Владимир on 31.03.2015.
 */
public abstract class BaseListViewFragment extends BaseListFragment<BaseKitActivity<ShikiAC>> {
    private PagingListView lvList;
    private Parcelable state;
    private ActionMode actionMode;

    @Override
    public int getLayoutId(){
        return R.layout.view_shiki_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), null);
        lvList = (PagingListView) v.findViewById(R.id.lvList);
        lvList.setHasMoreItems(true);
        lvList.setOnItemClickListener(this);
        lvList.setPagingableListener(endListListener);
        lvList.setOnScrollListener(pauseImageLoading);
        return v;
    }

    public PagingListView getListView(){
        return lvList;
    }

    /**
     * Достигаем последнего элемента, грузим новые данные
     */
    PagingListView.Pagingable endListListener = new PagingListView.Pagingable() {
        @Override
        public void onLoadMoreItems() {
            if(adapter == null || adapter.getCount() == 0)
                return;
            page++;
            loadData();
        }
    };

    /**
     * View id на которую привязываем pull to refresh
     * @return
     */
    @Override
    public int pullableViewId() {
        return R.id.lvList;
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        // save position
        setAdapter(adapter, false);
    }

    @Override
    public void setAdapter(BaseAdapter adapter, boolean saveState) {
        // save position
        if(saveState || page != DEFAULT_FIRST_PAGE)
            state = lvList.onSaveInstanceState();
        // set data
        lvList.setAdapter(adapter);
        // restore position
        if(state!=null)
            lvList.onRestoreInstanceState(state);
        state = null;
    }

    @Override
    public void prepareData(List<?> list, boolean removeLastItem, boolean limitOver) {
        state = lvList.onSaveInstanceState();
        super.prepareData(list, removeLastItem, limitOver);
        if(page == DEFAULT_FIRST_PAGE)
            lvList.setSelectionAfterHeaderView();
        else if (state!=null)
            lvList.onRestoreInstanceState(state);
        state = null;
    }

    /**
     * Показываем лоадер если есть еще что подгружать
     * @param more
     */
    @Override
    public void hasMoreItems(boolean more){
        try {
            lvList.onFinishLoading(more, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDeleteFromListInterface(ActionDescription confirmDeleteAction){
        confirmDeleteAction.setTitle(R.string.delete);
        confirmDeleteAction.setId(R.id.menu_delete);
        confirmDeleteAction.setOrder(1);
        confirmDeleteAction.setIconId(R.drawable.ic_action_delete);
        actionMode = activity.startSupportActionMode(new ActionSelectCallBack(activity, getListView(), getListView().getAdapter(),
                new DestroyActionCallback.DestroyAction() {
                    @Override
                    public void destroyActionMode() {
                    }
                }, confirmDeleteAction));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(actionMode!=null){
            actionMode.finish();
            actionMode = null;
        }
    }

}
