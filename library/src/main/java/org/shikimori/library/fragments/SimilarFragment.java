package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.fragments.base.AMBaseListFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.constpack.Constants;

import ru.altarix.basekit.library.tools.pagecontroller.Page;

import static org.shikimori.library.tool.constpack.Constants.*;

/**
 * Created by Владимир on 07.08.2015.
 */
@Page(key1 = TYPE, key2 = ITEM_ID)
public class SimilarFragment extends AMBaseListFragment {

    boolean isAnime;

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAnime = ((String) getParam(TYPE)).equals(Constants.ANIME);
    }

    @Override
    public int getActionBarTitle() {
        return isAnime ? R.string.similar_anime : R.string.similar_manga;
    }

    @Override
    protected String getLoadPath() {
        if(isAnime)
            return ShikiApi.getUrl(ShikiPath.MANGA_SIMILAR, (String)getParam(Constants.ITEM_ID));
        return ShikiApi.getUrl(ShikiPath.ANIME_SIMILAR, (String)getParam(Constants.ITEM_ID));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);

        activity.getPageController()
                .setLaunchActivity(ShowPageActivity.class)
                .addParam(Constants.ITEM_ID, item.id)
                .addParam(Constants.PAGE_FRAGMENT,
                        isAnime ? ShowPageActivity.ANIME_PAGE : ShowPageActivity.MANGA_PAGE)
                .startActivity();

    }
}
