package org.shikimori.library.features.comminity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.adapters.RelationAdapter;
import org.shikimori.library.fragments.LinkedListFragment;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.one.Relation;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

import ru.altarix.basekit.library.tools.pagecontroller.Page;


/**
 * Created by Владимир on 27.03.2015.
 */
@Page(key1 = Constants.CUSTOM_URL)
public class LinkedListClubFragment extends LinkedListFragment {

    @Override
    public ArrayAdapter getAdapter(List<?> list) {
        return new AMAdapter(activity, (List<AMShiki>) list);
    }

    @Override
    protected List<? extends Object> getDataList(JSONArray data) {
        return builder.getDataList(data, AMShiki.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
        LinkHelper.goToUrl(activity, item.url);
    }
}
