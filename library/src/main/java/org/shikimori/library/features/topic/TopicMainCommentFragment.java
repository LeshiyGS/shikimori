package org.shikimori.library.features.topic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.adapters.TopicsAdapter;
import org.shikimori.library.adapters.holder.TopicHolder;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import ru.altarix.basekit.library.activities.BaseKitActivity;

/**
 * Created by Gars on 18.06.2015.
 */
public class TopicMainCommentFragment extends BaseFragment<BaseKitActivity<ShikiAC>> implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult> {

    private TopicHolder holder;
    private TopicsAdapter adapter;
    String itemCashed;
    private View llRoot;

    public static TopicMainCommentFragment newInstance(Bundle b) {
        TopicMainCommentFragment frag = new TopicMainCommentFragment();
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_topic_main, null);
        llRoot = hs.get(v, R.id.llRoot);
        hs.setVisibleGone(llRoot);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getLoaderController().show();
        itemCashed = getParam(Constants.ITEM_OBJECT);

        adapter = new TopicsAdapter(activity, null);
        holder = adapter.getViewHolder(getView());

        if(itemCashed==null){
            getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.TOPICS_ID) + getParam(Constants.TOPIC_ID))
                .setCache(true)
                .getResultObject(this);
        } else {
            try {
                buildView(new ItemTopicsShiki().create(new JSONObject(itemCashed)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        final ItemTopicsShiki item = new ItemTopicsShiki().create(res.getResultObject());
        buildView(item);
    }

    void buildView(final ItemTopicsShiki item){
        if(item.user == null)
            return;
        BodyBuild bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        bodyBuilder.parceAsync(item.htmlBody, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                if (activity == null)
                    return;
                activity.getLoaderController().hide();
                hs.setVisible(llRoot, true);
                item.parsedContent = view;
                adapter.setValues(holder, item, 0);
            }
        });
    }
}
