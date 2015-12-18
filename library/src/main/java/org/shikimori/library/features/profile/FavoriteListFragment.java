package org.shikimori.library.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.json.JSONObject;
import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public class FavoriteListFragment extends BaseGridViewFragment implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult>, AdapterView.OnItemClickListener {

    private int position;
    ObjectBuilder builder = new ObjectBuilder();

    public static FavoriteListFragment newInstance(int position){
        return newInstance(position, null);
    }

    public static FavoriteListFragment newInstance(int position, Bundle b){
        if(b == null)
            b = new Bundle();
        else
            b = (Bundle) b.clone();
        b.putInt(Constants.LIST_ID, position);
        FavoriteListFragment frag = new FavoriteListFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initParams();

        loadData();
    }

    private void initParams() {
        Bundle b = getArguments();
        if(b == null)
            return;

        position = b.getInt(Constants.LIST_ID);
    }

    @Override
   public void loadData() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.FAVOURITES, getFC().getUserId()))
                .setCache(true, QueryShiki.DAY)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {

        if(activity == null)
            return;
        JSONObject data = res.getResultObject();
        if(data == null)
            return;

        List<AMShiki> list = builder.getDataList(data.optJSONArray(getType()), AMShiki.class, new ObjectBuilder.AdvanceChecker<AMShiki>() {
            @Override
            public boolean check(AMShiki item, int position) {
                item.poster = item.poster.replace("x64", "preview");
                return false;
            }
        });

        prepareData(list, true, true);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.FAVOURITES, getFC().getUserId()));
        loadData();
    }

    @Override
    public ArrayAdapter<AMShiki> getAdapter(List<?> list) {
        return new AMAdapter(activity, (List<AMShiki>) list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
//        ProjectTool.TYPE type = ProjectTool.getTypeFromUrl(item.url);
//
//        Intent i = new Intent(activity, ShowPageActivity.class);
//        if(type == ProjectTool.TYPE.ANIME) {
//            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
//        }else if(type == ProjectTool.TYPE.MANGA){
//            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
//        }else if(type == ProjectTool.TYPE.CHARACTER)
//            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.CHARACTER_PAGE);
//        i.putExtra(Constants.ITEM_ID, item.id);

        LinkHelper.goToUrl(activity, item.url);

//        activity.startActivity(i);
    }

    public String getType() {
        String type;
        switch (position) {
            case 1: type = "mangas"; break;
            case 2: type = "characters"; break;
            case 3: type = "people"; break;
            case 4: type = "mangakas"; break;
            case 5: type = "seyu"; break;
            case 6: type = "producers"; break;
            default: type = "animes";
        }

        return type;
    }
}
